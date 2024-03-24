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
    private boolean settled = false;
    private boolean doubleDown = false;
    private boolean split = false;
    private boolean insurance = false;
    private int balance;
    private int currentBet;
    private Sinks.Many<ServerEvent> sink;

    public Player(String id, String name) {
        super(id, name);
        this.balance = 1000; // Default balance
    }

    public void reset() {
        hand.clear();
        standing = false;
        settled = false;
        doubleDown = false;
        split = false;
        insurance = false;
        currentBet = 0;
    }

    public int getBalance() {
        return balance;
    }

    public int getCurrentBet() {
        return currentBet;
    }

    public int getNumCards() {
        return this.hand.size();
    }

    public Sinks.Many<ServerEvent> getSink() {
        return sink;
    }

    public void setSink(Sinks.Many<ServerEvent> sink) {
        this.sink = sink;
    }

    public void addCard(Card card) {
        hand.add(card);
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

    public boolean hasAce() {
        for (Card card : hand) {
            if (card.getRank() == Rank.ACE) {
                return true;
            }
        }
        return false;
    }

    public boolean shouldStillDraw() {
        return calculateHandValue() <= 21 || isStanding() || isDoubleDown();
    }

    public boolean isSettled() {
        return settled;
    }

    public void setSettled(boolean settled) {
        this.settled = settled;
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

    public boolean isBlackjack() {
        return (this.calculateHandValue() == 21 && this.getNumCards() == 2 && !this.isSplit());
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

    public int winBet() {
        int winnings = currentBet;
        if (isBlackjack()) {
            winnings = (int) (currentBet * 2.5);
            this.balance += winnings; // Blackjack pays 3 to 2
            this.currentBet = 0;
        } else {
            winnings = currentBet * 2;
            this.balance += winnings; // Winner gets double their bet
            this.currentBet = 0;
        }
        return winnings;
    }

    public int loseBet() {
        int loss = currentBet;
        this.currentBet = 0; // Loss already accounted for when bet was placed
        return loss;
    }

    public void push() { // In case of a tie
        this.balance += currentBet; // Return the bet to the player
        this.currentBet = 0;
    }
}
