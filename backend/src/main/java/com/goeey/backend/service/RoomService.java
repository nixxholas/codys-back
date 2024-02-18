package com.goeey.backend.service;

import com.goeey.backend.model.entity.Player;
import com.goeey.backend.model.entity.Room;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomService {
    private final TaskScheduler taskScheduler;
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();

    public RoomService(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    public Room createRoom() {
        String roomId = UUID.randomUUID().toString();
        Room room = new Room(roomId, taskScheduler);
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
        Room room = getRoom(roomId);

        if (room != null) {
            try {
                room.addPlayer(player, room.getNextAvailableSeat());
            } catch (IllegalArgumentException e) {
                // Handle exception
                System.out.println("Player could not be added to room: " + e.getMessage());
            }
        }
    }

    // Methods to remove players from rooms, delete rooms, etc.
}
