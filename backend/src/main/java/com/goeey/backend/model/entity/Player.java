package com.goeey.backend.model.entity;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String id;
    private List<Card> hand = new ArrayList<>();
    private boolean standing = false;
    private int balance;
    private int currentBet;

    public Player(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public int getBalance() {
        return balance;
    }

    public int getNumCards() {
        return this.value;
    }
    
    public void setNumCards(int value) {
        this.value = value;
    }

    public void addCard(Card card) {
        int num = getNumCards();
        hand.add(card);
        setNumCards(num + 1); // Increases number of cards by 1
    }

    public List<Card> getHand() {
        return hand;
    }

    public int calculateHandValue() {
        int value = 0;
        int aces = 0;
        for (Card card : hand) {
            if (card.getRank() == Rank.ACE) {
                aces++;
                value += 11;
            } else {
                value += card.getValue();
            }
        }
        while (value > 21 && aces > 0) {
            value -= 10; // Convert an ace from 11 to 1
            aces--;
        }
        return value;
    }

    public void setStanding(boolean standing) {
        this.standing = standing;
    }

    public boolean isStanding() {
        return standing;
    }

    public void placeBet(int amount) {
        if (amount > balance) {
            throw new IllegalArgumentException("Bet amount exceeds balance.");
        }
        this.currentBet = amount;
        this.balance -= amount;
    }

    public void winBet() {

        /* Blackjack scenarios. */
        if (this.calculateHandValue() == 21 && this.getNumCards() == 2) {
            this.balance += (currentBet * 2.5); // Blackjack pays 3 to 2
            this.currentBet = 0;
        } else {
            this.balance += (currentBet * 2); // Winner gets double their bet
            this.currentBet = 0;
        }
    }

    public void loseBet() {
        this.currentBet = 0; // Loss already accounted for when bet was placed
    }

    public void push() { // In case of a tie
        this.balance += currentBet; // Return the bet to the player
        this.currentBet = 0;
    }
}
