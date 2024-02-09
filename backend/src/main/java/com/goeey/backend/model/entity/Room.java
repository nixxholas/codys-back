package com.goeey.backend.model.entity;

import org.springframework.scheduling.TaskScheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

public class Room {
    private String id;
    private Map<Integer, Player> players = new ConcurrentHashMap<>();
    private List<Card> deck = new ArrayList<>();
    private Player dealer = new Player("Dealer");
    private boolean gameStarted = false;

    private enum GameState {
        WAITING_FOR_BETS,
        DEALING,
        PLAYER_TURN,
        DEALER_TURN,
        ROUND_ENDED
    }

    private GameState gameState = GameState.WAITING_FOR_BETS;

    private final TaskScheduler taskScheduler;
    private ScheduledFuture<?> nextRoundTask;

    public Room(String id, TaskScheduler taskScheduler) {
        this.id = id;
        this.taskScheduler = taskScheduler;
        initializeDeck();
    }

    public String getId() {
        return id;
    }

    public Map<Integer, Player> getPlayers() {
        return players;
    }

    public boolean hasPlayerById(String playerId) {
        for (Player player : players.values()) {
            if (player.getId().equals(playerId)) {
                return true;
            }
        }
        return false;
    }

    public void addPlayer(Player player, int seatNumber) {
        players.put(seatNumber, player);
    }

    public void removePlayer(int seatNumber) {
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

    public void startRound() {
        if (gameState != GameState.WAITING_FOR_BETS) {
            throw new IllegalStateException("Previous round not completed.");
        }
        gameState = GameState.DEALING;
        // Clear hands and prepare for a new round
        dealer.getHand().clear();
        players.values().forEach(player -> {
            player.getHand().clear(); // Clear hands before dealing new cards
            player.setStanding(false);  // Reset standing status
        });
        initializeDeck(); // Reinitialize the deck each round
        dealInitialCards();
        gameState = GameState.PLAYER_TURN; // Players can now take their turns
    }

    // Method to schedule the next round
    public void scheduleNextRound(long delayInSeconds) {
        if (nextRoundTask != null && !nextRoundTask.isDone()) {
            nextRoundTask.cancel(false); // Cancel previous task if it's still pending
        }
        nextRoundTask = taskScheduler.schedule(this::startNextRound,
                java.time.Instant.now().plusSeconds(delayInSeconds));
    }

    public void cancelScheduledNextRound() {
        if (nextRoundTask != null && !nextRoundTask.isDone()) {
            nextRoundTask.cancel(false);
        }
    }

    private void startNextRound() {
        if (!players.isEmpty() && gameState == GameState.ROUND_ENDED) {
            prepareForNextRound(); // Resets the game state and waits for bets
        }
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

    private void checkGameOver() {
        for (Player player : players.values()) {
            if (!player.isStanding()) {
                return; // Game is not over if any player is not standing
            }
        }
        dealerPlay(); // All players are standing, now it's dealer's turn
        determineOutcome(); // Determine the outcome after the dealer has played
        gameStarted = false; // Reset the game state
    }

    private void dealerPlay() {
        while (dealer.calculateHandValue() < 17) {
            dealer.addCard(deck.remove(0));
        }
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
        gameState = GameState.DEALER_TURN;
        dealerPlay(); // Dealer takes their turn
        int dealerValue = dealer.calculateHandValue();
        for (Player player : players.values()) {
            int playerValue = player.calculateHandValue();
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
        scheduleNextRound(30); // Schedule the next round to start after 30 seconds
    }

    public void prepareForNextRound() {
        if (gameState != GameState.ROUND_ENDED) {
            throw new IllegalStateException("Round not ended yet.");
        }
        // Reset game state for the next round
        gameState = GameState.WAITING_FOR_BETS;

        // Remove players who have insufficient balance
        players.values().removeIf(player -> player.getBalance() < 10);
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
}