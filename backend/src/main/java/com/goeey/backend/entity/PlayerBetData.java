package com.goeey.backend.entity;

public class PlayerBetData {
    private String playerId;
    private int betAmount;

    public PlayerBetData(String playerId, int betAmount) {
        this.playerId = playerId;
        this.betAmount = betAmount;
    }
}
