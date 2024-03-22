package com.gooey.base;

import com.gooey.base.socket.ServerEvent;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.List;

public class Player extends BasePlayer {
    private List<Card> hand = new ArrayList<>();
    private boolean standing = false;
    private boolean doubleDown = false;
    private boolean split = false;
    private boolean insurance = false;
    private int balance;
    private int currentBet;
    private int numCards;
    private Sinks.Many<ServerEvent> sink;

    public Player(String id, String name) {
        super(id, name);
        this.balance = 1000; // Default balance
    }

    public int getBalance() {
        return balance;
    }

    public int getCurrentBet() {
        return currentBet;
    }

    public int getNumCards() {
        return this.numCards;
    }

    public Sinks.Many<ServerEvent> getSink() {
        return sink;
    }

    public void setSink(Sinks.Many<ServerEvent> sink) {
        this.sink = sink;
    }
    
    public void setNumCards(int num_cards) {
        this.numCards = numCards;
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

    public void setDoubleDown(boolean doubleDown) {
        this.doubleDown = doubleDown;
    }

    public boolean isDoubleDown() {
        return doubleDown;
    }

    public void setSplit(boolean split) {
        this.split = split;
    }

    public boolean isSplit() {
        return split;
    }

    public void setInsurance(boolean insurance) {
        this.insurance = insurance;
    }

    public boolean isInsurance() {
        return insurance;
    }

    public boolean placeBet(int amount) {
        if (amount > balance) {
//            throw new IllegalArgumentException("Bet amount exceeds balance.");
            return false;
        }
        if (isDoubleDown()) {
            this.currentBet = 2 * amount;
        } else {
        this.currentBet = amount;
        }
        this.balance -= amount;
        return true;
    }

    public void winBet() {

        // Blackjack scenarios
        if (this.calculateHandValue() == 21 && this.getNumCards() == 2 && this.isSplit() == false) {
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
