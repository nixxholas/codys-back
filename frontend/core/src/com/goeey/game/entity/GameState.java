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

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
        
        switch (seatNumber) {
            case 1:
                playerEntityTarget = EntityTarget.PLAYER_1;
                break;
            case 2:
                playerEntityTarget = EntityTarget.PLAYER_2;
                break;
            case 3:
                playerEntityTarget = EntityTarget.PLAYER_3;
                break;
            case 4:
                playerEntityTarget = EntityTarget.PLAYER_4;
                break;
            case 5:
                playerEntityTarget = EntityTarget.PLAYER_5;
                break;
            default:
                playerEntityTarget = null;
                break;
        }
        
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
            throw new ArithmeticException("Insufficient balance");
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
