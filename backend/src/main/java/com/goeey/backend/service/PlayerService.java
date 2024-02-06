package com.goeey.backend.service;

import com.goeey.backend.model.entity.Player;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PlayerService {
    private final Map<String, Player> players = new ConcurrentHashMap<>();

    public Player getPlayerById(String id) {
        return players.get(id);
    }

    public Player createPlayer(String name) {
        Player player = new Player(name);
        players.put(player.getId(), player);
        return player;
    }
}
