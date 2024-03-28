package com.goeey.backend.entity;

import com.gooey.base.*;
import com.gooey.base.socket.ServerEvent;
import com.goeey.backend.util.SerializationUtil;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import static com.goeey.backend.util.SerializationUtil.gson;

/**
 * Represents a room in the casino where players can play blackjack.
 * A room can have up to 6 players and a dealer.
 * The room is responsible for managing the game state and the deck of cards.
 * It also schedules the next round to start after a certain delay.
 */
public class Room {
    private boolean noMoreBets;
    private final Sinks.Many<ServerEvent> broadcastSink; // For broadcasting to all players in the room
    private final Map<String, Disposable> playerBroadcastDisposables = new ConcurrentHashMap<>();
    private final String id;
    private Map<String, Player> unseatedPlayers = new ConcurrentHashMap<>();
    private Map<Integer, Player> players = new ConcurrentHashMap<>(6);
    private transient List<Card> deck = new ArrayList<>();
    private transient Player dealer = new Player(UUID.randomUUID().toString(), "Dealer");
    private String currentTurnPlayerId = null;
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
        noMoreBets = false;
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

    public boolean hasAPlayerSeated() {
        return !players.isEmpty();
    }

    public boolean isSeated(String playerId) {
        for (Player player : players.values()) {
            if (player.getId().equals(playerId)) {
                return true;
            }
        }
        return false;
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

        for (Player player : unseatedPlayers.values()) {
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

        for (Player player : unseatedPlayers.values()) {
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

    public ServerEvent placeBet(Player player, int amount) {
        return placeBet(player.getId(), amount);
    }

    public ServerEvent placeBet(String playerId, int amount) {
        if (!gameState.equals(GameState.WAITING_FOR_BETS)) {
            throw new IllegalStateException("Can't place bets, game not started.");
        }
        if (!isSeated(playerId)) {
            throw new IllegalStateException("Player is not seated.");
        }

        Player player = getPlayerById(playerId);
        if (player != null) {
            player.placeBet(amount);

            // Inform everyone in the room that the player has placed a bet
            ServerEvent betEvent = new ServerEvent<>(ServerEvent.Type.PLAYER_BET, new PlayerBetData(playerId, amount));
            broadcastSink.tryEmitNext(betEvent);
        }

        return new ServerEvent<>(ServerEvent.Type.BET, amount);
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
        unseatedPlayers.put(player.getId(), player);
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

    /**
     * Sits a player at a seat in the room.
     * If the seat is already taken, returns an error message.
     * If the seat number is invalid, returns an error message.
     * If the player is already sitting, returns an error message.
     * Otherwise, broadcasts a message to the room indicating that the player has sat down.
     * @param player
     * @param seatNumber
     * @return
     */
    public ServerEvent sit(Player player, int seatNumber) {
        if (seatNumber < 1 || seatNumber > 6) {
            return new ServerEvent<>(ServerEvent.Type.ERROR, "Invalid seat number.");
        }
        if (players.containsKey(seatNumber)) {
            return new ServerEvent<>(ServerEvent.Type.ERROR, "Seat is already taken.");
        }
        if (getPlayerSeatNumber(player.getId()) > 0) {
            return new ServerEvent<>(ServerEvent.Type.ERROR, "You are already sitting.");
        }

        // Broadcast a message to the room indicating that the player has sat down
        ServerEvent sitEvent = new ServerEvent<>(ServerEvent.Type.PLAYER_SAT, player.getId() + " sat down at seat " + seatNumber);
        broadcastSink.tryEmitNext(sitEvent);
        unseatedPlayers.remove(player.getId());
        players.put(seatNumber, player);

        // if the player is the first to sit down, start the game
        if (players.size() == 1) {
            run();
        }

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
        unseatedPlayers.put(player.getId(), player);

        // Return a message to the stander
        return new ServerEvent(ServerEvent.Type.STOOD_UP, seatNumber);
    }

    public void startTimer() {
        if (this.atLeastOneHasPlacedBet()) {
            // Start the round if at least one player has placed a bet
            if (this.atLeastOneHasPlacedBet()) {
                System.out.println("Sending a timer to wait for bets.");
                Thread countdownThread = new Thread(() -> {
                    for (int i = 0; i < 10; i++) {
                        ServerEvent timerEvent = new ServerEvent<>(ServerEvent.Type.COUNTDOWN, 10 - i);
                        broadcastSink.tryEmitNext(timerEvent);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (gameState == GameState.WAITING_FOR_BETS) {
                        gameState = GameState.DEALING;
                        noMoreBets = true;
                    }
                });
                countdownThread.start();

                // Wait for the countdown to finish
                while (countdownThread.isAlive()) {
                    // If all players stand up, end the countdown
                    if (!this.atLeastOneHasPlacedBet()) {
                        // Reset the entire state back to waiting for players
                        countdownThread.interrupt();
                        gameState = hasAPlayerSeated() ? GameState.WAITING_FOR_BETS : GameState.WAITING_FOR_PLAYERS;
                        noMoreBets = false;
                    }
                }
            }
        } else {
            System.out.println("Waiting for players to place bets...");
        }
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

    public ServerEvent hit(int seatNumber) {
        if (gameState != GameState.PLAYER_TURN) {
            return new ServerEvent<>(ServerEvent.Type.ERROR, "Not the right time to hit.");
        }
        if (!noMoreBets) {
            return new ServerEvent<>(ServerEvent.Type.ERROR, "Game not started.");
        }
        Player player = players.get(seatNumber);
        if (currentTurnPlayerId == null || !currentTurnPlayerId.equals(player.getId())) {
            return new ServerEvent<>(ServerEvent.Type.ERROR, "Not your turn.");
        }
        if (player.isSettled()) {
            return new ServerEvent<>(ServerEvent.Type.ERROR, "Player has already settled.");
        }
        if (player.getCurrentBet() <= 0) {
            return new ServerEvent<>(ServerEvent.Type.ERROR, "You're not in this round.");
        }

        Card card;
        if (!player.isStanding()) {
            card = deck.remove(0);
            player.addCard(card);

            // Broadcast the card to all players
            ServerEvent<Card> cardEvent = new ServerEvent<>(ServerEvent.Type.PLAYER_DRAW, card, getEntityTarget(player.getId()));
            broadcastSink.tryEmitNext(cardEvent);

            // Check if the player busts
            if (player.calculateHandValue() > 21) {
                player.setStanding(false); // Player busts

                // Settle with the player
                int lossAmount = player.loseBet();
                ServerEvent bustEvent = new ServerEvent<>(ServerEvent.Type.PLAYER_BUST,
                        new PlayerResultData(lossAmount, player.getBalance()), getEntityTarget(player.getId()));
                broadcastSink.tryEmitNext(bustEvent);

                player.setSettled(true); // Player has settled

                return new ServerEvent<>(ServerEvent.Type.PLAYER_BUST, card, getEntityTarget(player.getId()));
            }
        } else {
            return new ServerEvent<>(ServerEvent.Type.ERROR, "Player is standing.");
        }

        return new ServerEvent(ServerEvent.Type.PLAYER_HIT, card, getEntityTarget(player.getId()));
    }

    public ServerEvent stand(int seatNumber) {
        if (gameState != GameState.PLAYER_TURN) {
            return new ServerEvent<>(ServerEvent.Type.ERROR, "Not the right time to hit.");
        }
        if (!noMoreBets) {
            return new ServerEvent<>(ServerEvent.Type.ERROR, "Game not started.");
        }
        Player player = players.get(seatNumber);
        if (currentTurnPlayerId == null || !currentTurnPlayerId.equals(player.getId())) {
            return new ServerEvent<>(ServerEvent.Type.ERROR, "Not your turn.");
        }
        if (player.isSettled()) {
            return new ServerEvent<>(ServerEvent.Type.ERROR, "Player has already settled.");
        }
        if (player.getCurrentBet() <= 0) {
            return new ServerEvent<>(ServerEvent.Type.ERROR, "You're not in this round.");
        }

        if (!player.isStanding()) {
            player.setStanding(true);

            ServerEvent standEvent = new ServerEvent<>(ServerEvent.Type.PLAYER_STAND, null, getEntityTarget(player.getId()));
            broadcastSink.tryEmitNext(standEvent);
        }

        return new ServerEvent<>(ServerEvent.Type.PLAYER_STAND, null, getEntityTarget(player.getId()));
    }

    // Double down method
    public ServerEvent doubleDown(int seatNumber) {
        // If game state is not player turn, return an error, wrong time to double down
        if (gameState != GameState.PLAYER_TURN) {
            return new ServerEvent<>(ServerEvent.Type.ERROR, "Not the right time to hit.");
        }
        // If the game has not started, return an error, game not started
        if (!noMoreBets) {
            return new ServerEvent<>(ServerEvent.Type.ERROR, "Game not started.");
        }
        // Retrieve the player from the seat number
        Player player = players.get(seatNumber);
        // If the current turn player is not the player, return an error, not your turn
        if (currentTurnPlayerId == null || !currentTurnPlayerId.equals(player.getId())) {
            return new ServerEvent<>(ServerEvent.Type.ERROR, "Not your turn.");
        }
        if (player.getCurrentBet() <= 0) {
            return new ServerEvent<>(ServerEvent.Type.ERROR, "You're not in this round.");
        }
        if (player.isSettled()) {
            return new ServerEvent<>(ServerEvent.Type.ERROR, "Player has already settled.");
        }
        if (player.getNumCards() > 2) {
            return new ServerEvent<>(ServerEvent.Type.ERROR, "Can only double down on the first two cards.");
        }

        if (!player.isStanding()) {
            player.setDoubleDown(true);
            player.placeBet(player.getCurrentBet()); // Additional bet
            Card card = deck.remove(0);
            player.addCard(card);
            // Broadcast the card to all players
            ServerEvent<Card> cardEvent = new ServerEvent<>(ServerEvent.Type.PLAYER_DOUBLE, card, getEntityTarget(player.getId()));
            broadcastSink.tryEmitNext(cardEvent);

            player.setStanding(true); // Player is forced to stand as he can only take one card after doubling down

            if (player.calculateHandValue() > 21) {
                // Settle with the player
                int lossAmount = player.loseBet();
                ServerEvent bustEvent = new ServerEvent<>(ServerEvent.Type.PLAYER_BUST, player.getBalance(),
                        getEntityTarget(player.getId()));
                broadcastSink.tryEmitNext(bustEvent);
                player.setSettled(true); // Player has settled

                return new ServerEvent<>(ServerEvent.Type.PLAYER_LOSE,
                        new PlayerResultData(lossAmount, player.getBalance()), getEntityTarget(player.getId()));
            } else {
                // Wait for verdict
                return new ServerEvent<>(ServerEvent.Type.PLAYER_STAND, null, getEntityTarget(player.getId()));
            }
        }

        return new ServerEvent<>(ServerEvent.Type.ERROR, "Player is standing.");
    }

    /*
    // Split method
    public void split(int seatNumber) {
        if (gameState != GameState.PLAYER_TURN) {
            throw new IllegalStateException("Not the right time to split.");
        }
        if (!gameStarted) {
            throw new IllegalStateException("Game not started.");
        }
        Player player = players.get(seatNumber);
        if (player != null && !player.isStanding()) {
            player.setSplit(true);
            // TODO
        }
    }
    */

    /*
    // Insurance method
    public void insurance(int seatNumber) {
        if (gameState != GameState.PLAYER_TURN) {
            throw new IllegalStateException("Not the right time to take insurance.");
        }
        if (!gameStarted) {
            throw new IllegalStateException("Game not started.");
        }
        Player player = players.get(seatNumber);
        if (player != null && !player.isStanding()) {
            player.setInsurance(true);
            // TODO
        }
    }
    */

    private void dealerPlay() {
        currentTurnPlayerId = "dealer";
        // Reveal the dealer's second card
        ServerEvent revealEvent = new ServerEvent<>(ServerEvent.Type.DEALER_REVEAL, dealer.getHand().get(1),
                getEntityTarget("dealer"));
        broadcastSink.tryEmitNext(revealEvent);

        // If the dealer's hand value is less than 17, the dealer must draw cards until the hand value is at least 17
        // Includes soft 17 as well.
        while (dealer.calculateHandValue() < 17 ||
                (dealer.calculateHandValue() == 17 && dealer.hasAce() && dealer.getNumCards() == 2)) {
            Card nextCard = deck.remove(0);
            dealer.addCard(nextCard);

            ServerEvent dealerDrawEvent = new ServerEvent<>(ServerEvent.Type.DEALER_DRAW, nextCard, getEntityTarget("dealer"));
            broadcastSink.tryEmitNext(dealerDrawEvent);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        determineOutcome(); // Determine the outcome after the dealer has played
    }

    private EntityTarget getEntityTarget(String target) {
        if (target.equals("dealer")) {
            return EntityTarget.DEALER;
        } else {
            // Loop all seated players and return the corresponding seat
            for (Map.Entry<Integer, Player> entry : players.entrySet()) {
                if (entry.getValue().getId().equals(target)) {
                    switch (entry.getKey()) {
                        case 1:
                            return EntityTarget.PLAYER_1;
                        case 2:
                            return EntityTarget.PLAYER_2;
                        case 3:
                            return EntityTarget.PLAYER_3;
                        case 4:
                            return EntityTarget.PLAYER_4;
                        case 5:
                            return EntityTarget.PLAYER_5;
                        case 6:
                            return EntityTarget.PLAYER_6;
                    }
                }
            }
        }

        return EntityTarget.ALL;
    }

    private void dealInitialCards() {
        try {
            // Deal two cards to each player and the dealer at the start of each round
            for (int i = 0; i < 2; i++) {
                for (Player player : players.values()) {
                    Card playerCard = deck.remove(0);
                    player.addCard(playerCard);

                    // Broadcast the card to all players
                    ServerEvent playerCardEvent = new ServerEvent<>(ServerEvent.Type.PLAYER_DRAW, playerCard, getEntityTarget(player.getId()));
                    broadcastSink.tryEmitNext(playerCardEvent);
                    Thread.sleep(2000);
                }

                // Deal to dealer
                Card card = deck.remove(0);
                // Broadcast the card to all players
                if (i == 0) {
                    ServerEvent cardEvent = new ServerEvent<>(ServerEvent.Type.DEALER_DRAW, card, getEntityTarget("dealer"));
                    broadcastSink.tryEmitNext(cardEvent);
                    Thread.sleep(2000);
                } else {
                    // Hide the second card
                    ServerEvent cardEvent = new ServerEvent<>(ServerEvent.Type.DEALER_DRAW, null, getEntityTarget("dealer"));
                    broadcastSink.tryEmitNext(cardEvent);
                    Thread.sleep(2000);
                }

                dealer.addCard(card);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // Determine the outcome of the round, and distribute the winnings to the players
    // Also broadcast balance updates to all players
    private void determineOutcome() {
        int dealerValue = dealer.calculateHandValue();
        for (Player player : players.values()) {
            // Skip players who have already settled
            if (player.isSettled()) {
                continue;
            }

            int playerValue = player.calculateHandValue();

            // Blackjack scenarios
            if (player.isBlackjack() && !dealer.isBlackjack()) {
                System.out.println(player.getId() + " wins!");
                int playerEarning = player.winBet();

                // Broadcast the player win event
                ServerEvent playerWinEvent = new ServerEvent<>(ServerEvent.Type.PLAYER_WIN,
                        new PlayerResultData(playerEarning, player.getBalance()), getEntityTarget(player.getId()));
                broadcastSink.tryEmitNext(playerWinEvent);
                // Move to the next player
                continue;
            } else if (player.isBlackjack() && dealer.isBlackjack()) {
                System.out.println(player.getId() + " pushes (ties).");
                player.push();

                // Broadcast the player win event
                ServerEvent playerPushEvent = new ServerEvent<>(ServerEvent.Type.PLAYER_PUSH,
                        new PlayerResultData(0, player.getBalance()), getEntityTarget(player.getId()));
                broadcastSink.tryEmitNext(playerPushEvent);
                // Move to the next player
                continue;
            } else if (!player.isBlackjack() && dealer.isBlackjack()) {
                System.out.println(player.getId() + " loses.");
                int playerLosses = player.loseBet();

                // Broadcast the player loss event
                ServerEvent playerLossEvent = new ServerEvent<>(ServerEvent.Type.PLAYER_LOSE,
                        new PlayerResultData(playerLosses, player.getBalance()), getEntityTarget(player.getId()));
                broadcastSink.tryEmitNext(playerLossEvent);
                continue;
            }

            // Regular scenarios
            if (playerValue > 21) {
                System.out.println(player.getId() + " busts and loses.");
                int playerLosses = player.loseBet();

                // Broadcast the player loss event
                ServerEvent playerLossEvent = new ServerEvent<>(ServerEvent.Type.PLAYER_LOSE,
                        new PlayerResultData(playerLosses, player.getBalance()), getEntityTarget(player.getId()));
                broadcastSink.tryEmitNext(playerLossEvent);
            } else if (playerValue > dealerValue || dealerValue > 21) {
                System.out.println(player.getId() + " wins!");
                int playerEarning = player.winBet();

                // Broadcast the player win event
                ServerEvent playerWinEvent = new ServerEvent<>(ServerEvent.Type.PLAYER_WIN,
                        new PlayerResultData(playerEarning, player.getBalance()), getEntityTarget(player.getId()));
                broadcastSink.tryEmitNext(playerWinEvent);
            } else if (playerValue == dealerValue) {
                System.out.println(player.getId() + " pushes (ties).");
                player.push();

                // Broadcast the player win event
                ServerEvent playerPushEvent = new ServerEvent<>(ServerEvent.Type.PLAYER_PUSH,
                        new PlayerResultData(0, player.getBalance()), getEntityTarget(player.getId()));
                broadcastSink.tryEmitNext(playerPushEvent);
                // Move to the next player
            } else {
                System.out.println(player.getId() + " loses.");
                int playerLosses = player.loseBet();

                // Broadcast the player loss event
                ServerEvent playerLossEvent = new ServerEvent<>(ServerEvent.Type.PLAYER_LOSE,
                        new PlayerResultData(playerLosses, player.getBalance()), getEntityTarget(player.getId()));
                broadcastSink.tryEmitNext(playerLossEvent);
            }
        }
        gameState = GameState.ROUND_ENDED; // The round has ended, prepare for a new round
    }

    public void run() {
        // Make sure we're not overriding the thread
        if (thread != null && thread.isAlive()) {
            return;
        }

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
                            if (!players.isEmpty() && hasAPlayerSeated() && atLeastOneHasPlacedBet()) { // At least one player
                                gameState = GameState.WAITING_FOR_BETS; // Start the round

                                ServerEvent startEvent = new ServerEvent<>(ServerEvent.Type.STARTING, this.id);
                                broadcastSink.tryEmitNext(startEvent);
                            } else {
                                // Send out alive
                                ServerEvent aliveEvent = new ServerEvent<>(ServerEvent.Type.PONG, this.id);
                                broadcastSink.tryEmitNext(aliveEvent);
                            }
                            break;
                        case WAITING_FOR_BETS:
                            startTimer();
                            break;
                        case PLAYER_TURN:
                            // Time for players to take their turns in order
                            for (Player player : players.values()) {
                                currentTurnPlayerId = player.getId();
                                // Retrieve which N player we're targeting
                                EntityTarget entityTarget = getEntityTarget(player.getId());

                                // Broadcast the player's turn
                                ServerEvent playerTurnEvent = new ServerEvent<>(ServerEvent.Type.PLAYER_TURN,
                                        null, entityTarget);
                                broadcastSink.tryEmitNext(playerTurnEvent);

                                // Wait for the player to take their turn, provide 10 seconds to make a decision
                                // If the player is standing, skip their turn
                                // If the player is not standing, they can hit, reset the timer, and wait for them again
                                AtomicReference<List<Card>> playerInitialHand = new AtomicReference<>(player.getHand());
                                while (player.shouldStillDraw() && !player.isStanding()) {
                                    Thread playerTurnCountdown = new Thread(() -> {
                                        // Send timer to wait for player to take action
                                        int countdown = 10;
                                        // While the player still can draw cards
                                        while (player.shouldStillDraw()) {
                                            // While the player has not taken any action
                                            while (playerInitialHand.get().size() == player.getHand().size()) {
                                                if (countdown == 0) {
                                                    // Player has not taken any action, force stand
                                                    player.setStanding(true);
                                                    ServerEvent standEvent = new ServerEvent<>(ServerEvent.Type.PLAYER_STAND,
                                                            null, entityTarget);
                                                    broadcastSink.tryEmitNext(standEvent);
                                                    break;
                                                }

                                                ServerEvent timerEvent = new ServerEvent<>(ServerEvent.Type.COUNTDOWN, countdown);
                                                broadcastSink.tryEmitNext(timerEvent);

                                                try {
                                                    Thread.sleep(1000);
                                                } catch (InterruptedException e) {
                                                    throw new RuntimeException(e);
                                                }
                                                countdown--;
                                            }

                                            if (player.isStanding()) {
                                                break;
                                            } else {
                                                // Reset the countdown
                                                countdown = 10;

                                                // Deal with the draw broadcast elsewhere
                                                playerInitialHand.set(player.getHand());
                                            }
                                        }
                                    });
                                    playerTurnCountdown.start();

                                    // Wait for the countdown to finish
                                    while (playerTurnCountdown.isAlive()) {
                                        if (player.isStanding() || !player.shouldStillDraw()) {
                                            playerTurnCountdown.interrupt();
                                        }
                                    }
                                }

                                if (!player.isStanding()) {
                                    Thread.sleep(1000);
                                }
                            }
                            gameState = GameState.DEALER_TURN;
                            // All players have taken their turns, emit dealer reveal event
                            ServerEvent dealerTurnEvent = new ServerEvent<>(ServerEvent.Type.DEALER_REVEAL, this.id);
                            broadcastSink.tryEmitNext(dealerTurnEvent);
                            break;
                        case DEALING:
                            // Clear hands and prepare for a new round
                            dealer.getHand().clear();
                            players.values().forEach(player -> {
                                player.getHand().clear(); // Clear hands before dealing new cards
                                player.setStanding(false);  // Reset standing status
                            });
                            initializeDeck(); // Reinitialize the deck each round
                            currentTurnPlayerId = "dealer";

                            // Broadcast the deal event
                            ServerEvent dealEvent = new ServerEvent<>(ServerEvent.Type.DEAL, this.id);
                            broadcastSink.tryEmitNext(dealEvent);

                            // Deal initial cards to players and the dealer
                            dealInitialCards();
                            gameState = GameState.PLAYER_TURN; // Players can now take their turns
                            break;
                        case DEALER_TURN:
                            Thread.sleep(2000);
                            dealerPlay();
                            break;
                        case ROUND_ENDED:
                            // Reset game state for the next round
                            // Remove players who have insufficient balance
                            for (Player player : players.values()) {
                                if (player.getBalance() < 1) {
                                    standUp(player);
                                }

                                // Reset internal player state
                                player.reset();
                            }

                            dealer.reset();
                            currentTurnPlayerId = null;

                            if (players.isEmpty()) {
                                gameState = GameState.WAITING_FOR_PLAYERS;
                            } else {
                                gameState = GameState.WAITING_FOR_BETS;
                            }

                            ServerEvent serverEvent = new ServerEvent<>(ServerEvent.Type.UPDATE,
                                    "Room state has been reset.");
                            broadcastSink.tryEmitNext(serverEvent);

                            break;
                        case PLAYER_DISCONNECTED:
                            // Handle player disconnection
                            break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            System.out.println("[Room " + this.id + "] Game state has stopped running as there are no players sitting.");
        });
        thread.start();
    }
}
