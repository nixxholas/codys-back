package com.goeey.backend.entity;

import com.gooey.base.Card;
import com.gooey.base.Player;
import com.gooey.base.Rank;
import com.gooey.base.Suit;
import com.gooey.base.socket.ServerEvent;
import com.goeey.backend.util.SerializationUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.goeey.backend.util.SerializationUtil.gson;

/**
 * Represents a room in the casino where players can play blackjack.
 * A room can have up to 6 players and a dealer.
 * The room is responsible for managing the game state and the deck of cards.
 * It also schedules the next round to start after a certain delay.
 */
public class Room {
    private final Sinks.Many<ServerEvent> broadcastSink; // For broadcasting to all players in the room
    private final Map<String, Disposable> playerBroadcastDisposables = new ConcurrentHashMap<>();
    private final String id;
    private final Map<Integer, Player> players = new ConcurrentHashMap<>(6);
    private transient List<Card> deck = new ArrayList<>();
    private transient Player dealer = new Player(UUID.randomUUID().toString(), "Dealer");
    private boolean gameStarted = false;
    private transient Timer timer;
    private transient Thread thread;

    private enum GameState {
        WAITING_FOR_PLAYERS,
        WAITING_FOR_BETS,
        DEALING,
        PLAYER_TURN,
        DEALER_TURN,
        ROUND_ENDED,
        PLAYER_DISCONNECTED,
    }

    private GameState gameState = GameState.WAITING_FOR_BETS;

    public Room(String id) {
        this.id = id;
        this.broadcastSink = Sinks.many().multicast().onBackpressureBuffer();
//        this.playerSinks = new ConcurrentHashMap<>();
        timer = new Timer();
        initializeDeck();
    }

    public int getNextAvailableSeat() {
        for (int i = 1; i <= 6; i++) {
            if (!players.containsKey(i)) {
                return i;
            }
        }
        return -1; // No available seats
    }

    public int getPlayerSeatNumber(String playerId) {
        for (Map.Entry<Integer, Player> entry : players.entrySet()) {
            if (entry.getValue().getId().equals(playerId)) {
                return entry.getKey();
            }
        }
        return -1;
    }

    public String[] getPlayerIds() {
        String[] playerIds = new String[players.size()];
        int i = 0;
        for (Player player : players.values()) {
            playerIds[i] = player.getId();
            i++;
        }
        return playerIds;
    }

    public String getRoomId() {
        return id;
    }

    public Map<Integer, Player> getPlayers() {
        return players;
    }

    public Player getPlayerById(String playerId) {
        for (Player player : players.values()) {
            if (player.getId().equals(playerId)) {
                return player;
            }
        }
        return null;
    }

    public boolean hasPlayerById(String playerId) {
        for (Player player : players.values()) {
            if (player.getId().equals(playerId)) {
                return true;
            }
        }
        return false;
    }

    public boolean atLeastOneHasPlacedBet() {
        for (Player player : players.values()) {
            if (player.getCurrentBet() > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean notAllHavePlacedBet() {
        for (Player player : players.values()) {
            if (player.getCurrentBet() == 0) {
                return true;
            }
        }
        return false;
    }

    public Mono<Void> subscribePlayerToRoomBroadcasts(WebSocketSession session) {
        Flux<ServerEvent> eventsFlux = broadcastSink.asFlux();

        return session.send(eventsFlux.map(event ->
            session.textMessage(SerializationUtil.serializeString(event))
        ));
    }

    public Mono<Void> playerJoin(Player player, WebSocketSession session) {
        // CAVEAT: Check if the player is in a room or in this room before adding them
        if (players.size() >= 6) {
            return session.send(Mono.just(session.textMessage(
                    gson.toJson(new ServerEvent<>(ServerEvent.Type.ERROR, "Room is full.")))));
        }
        if (hasPlayerById(player.getId())) {
            return session.send(Mono.just(session.textMessage(
                    gson.toJson(new ServerEvent<>(ServerEvent.Type.ERROR, "You are already in a room.")))));
        }

        if (!players.isEmpty()) {
            // Create and emit the join event
            ServerEvent joinEvent = new ServerEvent<>(ServerEvent.Type.PLAYER_JOINED, "Player " + player.getName() + " joined the room.");
            this.broadcastSink.tryEmitNext(joinEvent);
        }
        players.put(getNextAvailableSeat(), player);
        playerBroadcastDisposables.put(player.getId(), subscribePlayerToRoomBroadcasts(session).subscribe());

        return session.send(broadcastSink.asFlux()
                .map(event -> session.textMessage(SerializationUtil.serializeString(event))));
    }

    public Player playerLeave(String playerId) {
        // Get the seat number of the player
        int seatNumber = getPlayerSeatNumber(playerId);
        // Stand up the player if they are sitting
        if (seatNumber > 0) {
            standUp(getPlayerById(playerId));
        }

        // Remove the player from the room
        Player leavingPlayer = players.get(seatNumber);
        if (leavingPlayer != null) {
            // Broadcast a message to the room indicating that the player has left
            // Assuming you have a method to convert Player object or playerId to a String that identifies the player to other clients
            ServerEvent leaveEvent = new ServerEvent<>(ServerEvent.Type.PLAYER_LEFT, leavingPlayer.getId());
            broadcastSink.tryEmitNext(leaveEvent);

            // Remove the player from the room's player map
            players.remove(seatNumber);
            playerBroadcastDisposables.get(playerId).dispose();
            playerBroadcastDisposables.remove(playerId);

            return leavingPlayer;
        }

        return null;
    }

    public ServerEvent sit(Player player, int seatNumber) {
        if (seatNumber < 1 || seatNumber > 6) {
            return new ServerEvent<>(ServerEvent.Type.ERROR, "Invalid seat number.");
        }
        if (players.containsKey(seatNumber)) {
            return new ServerEvent<>(ServerEvent.Type.ERROR, "Seat is already taken.");
        }

        // Broadcast a message to the room indicating that the player has sat down
        ServerEvent sitEvent = new ServerEvent<>(ServerEvent.Type.PLAYER_SAT, player.getId() + " sat down at seat " + seatNumber);
        broadcastSink.tryEmitNext(sitEvent);

        players.put(seatNumber, player);

        // Return a message to the sitter
        return new ServerEvent(ServerEvent.Type.PLAYER_SAT, seatNumber);
    }

    public ServerEvent standUp(Player player) {
        int seatNumber = getPlayerSeatNumber(player.getId());
        if (seatNumber < 0) {
            return new ServerEvent<>(ServerEvent.Type.ERROR, "Player not found.");
        }

        // Broadcast a message to the room indicating that the player has stood up
        ServerEvent standEvent = new ServerEvent<>(ServerEvent.Type.STOOD_UP, player.getId() + " stood up from seat " + seatNumber);
        broadcastSink.tryEmitNext(standEvent);

        players.remove(seatNumber);

        // Return a message to the stander
        return new ServerEvent(ServerEvent.Type.STOOD_UP, seatNumber);
    }

    public void removePlayer(int seatNumber) throws NullPointerException {
        Player player = players.get(seatNumber);
        if (player == null)
            throw new NullPointerException("Player not found.");
        players.remove(seatNumber);
    }

    private void initializeDeck() {
        deck.clear();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                deck.add(new Card(suit, rank));
            }
        }
        Collections.shuffle(deck);
    }

    public void hit(int seatNumber) {
        if (gameState != GameState.PLAYER_TURN) {
            throw new IllegalStateException("Not the right time to hit.");
        }
        if (!gameStarted) {
            throw new IllegalStateException("Game not started.");
        }
        Player player = players.get(seatNumber);
        if (player != null && !player.isStanding()) {
            player.addCard(deck.remove(0));
            if (player.calculateHandValue() > 21) {
                player.setStanding(false); // Player busts
            }
        }
    }

    public void stand(int seatNumber) {
        if (gameState != GameState.PLAYER_TURN) {
            throw new IllegalStateException("Not the right time to stand.");
        }
        Player player = players.get(seatNumber);
        if (player != null) {
            player.setStanding(true);
            checkGameOver();
        }
    }

    /*
    // Double down method
    public void doubleDown(int seatNumber) {
        if (gameState != GameState.PLAYER_TURN) {
            throw new IllegalStateException("Not the right time to double down.");
        }
        if (!gameStarted) {
            throw new IllegalStateException("Game not started.");
        }
        Player player = players.get(seatNumber);
        if (player != null && !player.isStanding()) {
            player.setDoubleDown(true);
            player.placeBet(player.getCurrentBet()); // Additional bet
            player.addCard(deck.remove(0));
        }
        if (player.calculateHandValue() > 21) {
            player.setStanding(false); // Player busts
        } else {
            player.setStanding(true); // Player is forced to stand as he can only take one additional card
        }
    }
    */

    private void checkGameOver() {
        for (Player player : players.values()) {
            if (!player.isStanding()) {
                return; // Game is not over if any player is not standing
            }
        }
        dealerPlay(); // All players are standing, now it's dealer's turn
    }

    private void dealerPlay() {
        gameState = GameState.DEALER_TURN;
        while (dealer.calculateHandValue() < 17) {
            dealer.addCard(deck.remove(0));
        }

        determineOutcome(); // Determine the outcome after the dealer has played
    }

    private void dealInitialCards() {
        // Deal two cards to each player and the dealer at the start of each round
        for (int i = 0; i < 2; i++) {
            dealer.addCard(deck.remove(0));
            for (Player player : players.values()) {
                player.addCard(deck.remove(0));
            }
        }
    }

    private void determineOutcome() {
        dealerPlay(); // Dealer takes their turn
        int dealerValue = dealer.calculateHandValue();
        for (Player player : players.values()) {
            int playerValue = player.calculateHandValue();

            // Blackjack scenarios
            if ((playerValue == 21 && player.getNumCards() == 2) && (dealerValue != 21 || dealerValue == 21 && dealer.getNumCards() != 2)) {
                System.out.println(player.getId() + " wins!");
                player.winBet();
            } else if ((playerValue == 21 && player.getNumCards() == 2) && (dealerValue == 21 && dealer.getNumCards() == 2)) {
                System.out.println(player.getId() + " pushes (ties).");
                player.push();
            } else if ((playerValue != 21 || playerValue == 21 && player.getNumCards() != 2) && (dealerValue == 21 && dealer.getNumCards() == 2)) {
                System.out.println(player.getId() + " loses.");
                player.loseBet();
            }

            if (playerValue > 21) {
                System.out.println(player.getId() + " busts and loses.");
                player.loseBet();
            } else if (playerValue > dealerValue || dealerValue > 21) {
                System.out.println(player.getId() + " wins!");
                player.winBet();
            } else if (playerValue == dealerValue) {
                System.out.println(player.getId() + " pushes (ties).");
                player.push();
            } else {
                System.out.println(player.getId() + " loses.");
                player.loseBet();
            }
        }
        gameStarted = false; // Reset the game state for the next round
        gameState = GameState.ROUND_ENDED; // The round has ended, prepare for a new round

    }

    // Add a method for players to place bets at the beginning of each round
    public void placeBet(int seatNumber, int amount) {
        if (!gameStarted) {
            throw new IllegalStateException("Can't place bets, game not started.");
        }
        Player player = players.get(seatNumber);
        if (player != null) {
            player.placeBet(amount);
        }
    }

    /**
     * Schedules the next round to start after a certain delay.
     */
    @Scheduled(fixedRate = 500)
    private void syncState() {
        Instant timeNow = Instant.now();
        System.out.println("Room: " + this.id + "is syncing state.");

        if (gameState == GameState.WAITING_FOR_BETS) {
            if (this.atLeastOneHasPlacedBet()) {
                // Start the round if at least one player has placed a bet
                if (this.notAllHavePlacedBet() && this.atLeastOneHasPlacedBet()) {
                    System.out.println("Not all players have placed bets. Sending a timer to wait for bets.");
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (gameState == GameState.WAITING_FOR_BETS) {
                                gameState = GameState.DEALING;
                            }
                        }
                    }, 10000);
                }
            } else {
                System.out.println("Waiting for players to place bets...");
            }
        }
    }

    public void run() {
        thread = new Thread(() -> {
            while (!players.isEmpty()) {
                System.out.println("[Room " + this.id + "] Game state is still running!");
                try {
                    switch (gameState) {
                    /*
                      Add a case for WAITING_FOR_PLAYERS to check if there are enough players to start the game.
                      If there are enough players, start the game by calling startRound().

                      Cases:
                      - No players: Do nothing.
                      - At least one player: Check if at least one player has placed a bet. If so, start the round.
                      - All players have placed bets: Start the round.
                     */
                        case WAITING_FOR_PLAYERS:
                            break;
                        case WAITING_FOR_BETS:
                            if (!players.isEmpty() && this.atLeastOneHasPlacedBet()) {
                                // Start the round if at least one player has placed a bet
                                if (this.notAllHavePlacedBet()) {
                                    System.out.println("Not all players have placed bets. Sending a timer to wait for bets.");
                                }
                            } else {
                                System.out.println("Waiting for players to place bets...");
                            }
                            break;
                        case PLAYER_TURN:
                            for (Player player : players.values()) {
                                if (!player.isStanding()) {
                                    Thread.sleep(1000);
//                                hit(player.getSeatNumber());
                                }
                            }
                            break;
                        case DEALING:
                            // Clear hands and prepare for a new round
                            dealer.getHand().clear();
                            players.values().forEach(player -> {
                                player.getHand().clear(); // Clear hands before dealing new cards
                                player.setStanding(false);  // Reset standing status
                            });
                            initializeDeck(); // Reinitialize the deck each round
                            dealInitialCards();
                            gameState = GameState.PLAYER_TURN; // Players can now take their turns
                            break;
                        case DEALER_TURN:
                            Thread.sleep(1000);
                            dealerPlay();
                            break;
                        case ROUND_ENDED:
                            // Reset game state for the next round
                            gameState = GameState.WAITING_FOR_BETS;

                            // Remove players who have insufficient balance
                            players.values().removeIf(player -> player.getBalance() < 10);
                            break;
                        case PLAYER_DISCONNECTED:
                            // Handle player disconnection
                            break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
