package com.gooey.base;

public class BasePlayer {
    private String id;
    private String name;

    public BasePlayer(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() { return name; }
}
