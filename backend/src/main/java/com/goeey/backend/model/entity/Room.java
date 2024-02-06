package com.goeey.backend.model.entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Room {
    private String id;
    private Map<Integer, Player> players = new ConcurrentHashMap<>();
    // Add game state properties here (e.g., deck of cards, current turn)

    public Room(String id) {
        this.id = id;
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
}
