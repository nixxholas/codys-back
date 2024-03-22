package com.goeey.backend.entity;

public class PlayerResultData {
    // Can be negative. Shows the amount of money the player has won or lost.
    public int earnings;
    // Balance after earnings are added.
    public int balance;

    public PlayerResultData(int earnings, int balance) {
        this.earnings = earnings;
        this.balance = balance;
    }
}
