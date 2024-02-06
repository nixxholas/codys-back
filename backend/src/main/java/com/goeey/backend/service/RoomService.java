package com.goeey.backend.service;

import com.goeey.backend.model.entity.Player;
import com.goeey.backend.model.entity.Room;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomService {
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();

    public Room createRoom() {
        String roomId = UUID.randomUUID().toString();
        Room room = new Room(roomId);
        rooms.put(roomId, room);
        return room;
    }
    
    public Room getRoom(String roomId) {
        return rooms.get(roomId);
    }

    public Room getPlayerRoomByPlayerId(String playerId) {
        for (Room room : rooms.values()) {
            if (room.hasPlayerById(playerId)) {
                return room;
            }
        }
        return null;
    }

    public void addPlayerToRoom(String roomId, Player player) {
        // Add player to the specified room
        // You might need to send updates to other players in the room
    }

    // Methods to remove players from rooms, delete rooms, etc.
}
