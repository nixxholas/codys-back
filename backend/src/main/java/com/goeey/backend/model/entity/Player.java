package com.goeey.backend.model.entity;

import java.util.UUID;

public class Player {
    private String id;
    private String name;
    // Add player-specific properties here (e.g., hand of cards, current bet)

    // Constructor, getters, and setters
    public Player(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
