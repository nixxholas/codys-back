package com.goeey.game.entity;

import com.gooey.base.EntityTarget;

public class GameState {
    private static GameState instance;
    private boolean isRegistered;
    private boolean isConnected;
    private int seatNumber;
    private boolean isSeated;
    private boolean isInLobby;
    private boolean isInRoom;
    private boolean hasBet;
    private boolean gameEnded;
    private boolean gameRestCalled;
    private boolean isFirstCountDown;
    private boolean playerLeft;
    private EntityTarget playerEntityTarget;
    private int playerBalance = 1000;

    private GameState() {
    }

    public static GameState getGameState() {
        if(instance == null) {
            instance = new GameState();
        }

        return instance;
    }

    public EntityTarget getPlayerEntityTarget() {
        return playerEntityTarget;
    }

    public int getSeatNumber() {
        return seatNumber;
    }
    public static EntityTarget getEntityTargetGivenSeatNumber (int seatNumber) {
        return switch (seatNumber) {
            case 1 -> EntityTarget.PLAYER_1;
            case 2 -> EntityTarget.PLAYER_2;
            case 3 -> EntityTarget.PLAYER_3;
            case 4 -> EntityTarget.PLAYER_4;
            case 5 -> EntityTarget.PLAYER_5;
            default -> null;
        };
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
        this.playerEntityTarget = getEntityTargetGivenSeatNumber(seatNumber);
    }

    public boolean isFirstCountDown() {
        return isFirstCountDown;
    }

    public void setFirstCountDown(boolean firstCountDown) {
        isFirstCountDown = firstCountDown;
    }

    public int getPlayerBalance() {
        return playerBalance;
    }

    public void addToPlayerBalance(int winning) {
        this.playerBalance += winning;
    }

    public void deductPlayerBalance(int betAmount) throws ArithmeticException {
        if(playerBalance < betAmount) {
            throw new ArithmeticException("Insufficient balance"); // Not enough money to make a bet
        }

        this.playerBalance -= betAmount;
    }

    public boolean getHasBet() {
        return hasBet;
    }

    public void setHasBet(boolean hasBet) {
        this.hasBet = hasBet;
    }

    public boolean hasGameEnded() {
        return gameEnded;
    }

    public void setGameEnded(boolean gameEnded) {
        this.gameEnded = gameEnded;
    }

    public boolean isGameRestCalled() {
        return gameRestCalled;
    }

    public void setGameRestCalled(boolean gameRestCalled) {
        this.gameRestCalled = gameRestCalled;
    }

    public boolean hasPlayerLeft() {
        return playerLeft;
    }

    public void setPlayerLeft(boolean playerLeft) {
        this.playerLeft = playerLeft;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setRegistered(boolean registered) {
        isRegistered = registered;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public boolean isSeated() {
        return isSeated;
    }

    public void setSeated(boolean seated) {
        isSeated = seated;
    }

    public boolean isInLobby() {
        return isInLobby;
    }

    public void setInLobby(boolean inLobby) {
        isInLobby = inLobby;
    }

    public boolean isInRoom() {
        return isInRoom;
    }

    public void setInRoom(boolean inRoom) {
        this.isInRoom = inRoom;
    }

}
