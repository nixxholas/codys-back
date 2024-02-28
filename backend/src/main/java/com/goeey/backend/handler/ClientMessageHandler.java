package com.goeey.backend.handler;

import com.goeey.backend.model.entity.Player;
import com.goeey.backend.model.entity.Room;
import com.goeey.backend.model.entity.socket.ClientSocketMessage;
import com.goeey.backend.model.entity.socket.ClientSocketMessageType;
import com.goeey.backend.model.entity.socket.ServerSocketMessage;
import com.goeey.backend.model.entity.socket.ServerSocketMessageType;
import com.goeey.backend.service.PlayerService;
import com.goeey.backend.service.RoomService;
import com.goeey.backend.util.SerializationUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ClientMessageHandler {
    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final RoomService roomService;
    private final PlayerService playerService;

    public ClientMessageHandler(RoomService roomService, PlayerService playerService) {
        this.roomService = roomService;
        this.playerService = playerService;
    }

    private void addSession(String clientId, WebSocketSession session) {


        sessions.put(clientId, session);
    }

    private void removeSession(String clientId) {
        sessions.remove(clientId);
    }

    public void sendMessage(String clientId, String message) {
        WebSocketSession session = sessions.get(clientId);
        if (session != null && session.isOpen()) {
            session.textMessage(message);
        }
    }

    private String validateCurrentSession(WebSocketSession session, ClientSocketMessage message) {
        // If the message has an ID, it means the connection was to reconnect the player
        if (message.getClientId() == null) {
            session.textMessage(SerializationUtil.serializeString(new ClientSocketMessage(message.getClientId(),
                    ClientSocketMessageType.DISCONNECT,
                    "Invalid client ID!")));
            session.close(CloseStatus.NORMAL);
            return "Invalid client ID!";
        }

        return null;
    }

    public String handle(WebSocketSession session, ClientSocketMessage message) throws IOException {
        String validationResult;
        switch (message.getType()) {
            // TODO: Introduce types where auth is not required
            default:
                validationResult = validateCurrentSession(session, message);
                if (validationResult != null)
                    return validationResult;
                break;
        }

        switch (message.getType()) {
            case CONNECT:
                // If the message contains an ID, it means the connection is a reconnect
                if (message.getMessage() != null) {
                    Player player = playerService.getPlayerById(message.getClientId());
                    addSession(message.getClientId(), session);

                    if (player == null) {
                        return "Player not found!";
                    }

                    return Arrays.toString(SerializationUtil.serialize(player));
                } else {
                    // If the message does not have an ID, it means the connection is a new connection
                    addSession(message.getClientId(), session);
                }

                return "Connected!";
            case DISCONNECT:
                // Handle disconnects from a user.
                Player player = playerService.getPlayerById(message.getClientId());

                // 1. Find out if the user is in a room
                Room room = roomService.getPlayerRoomByPlayerId(message.getClientId());

                // 2. If the user is in a room, remove the user from the room
                if (room != null) {
                    room.removePlayer(room.getPlayerSeatNumber(player.getId()));

                    // 3. Inform the other players in the room that the user has left
                    // Retrieve all sessions in the room from sessions map
                    String[] players = room.getPlayerIds();
                    for (String playerId : players) {
                        sendMessage(playerId, SerializationUtil.serializeString(new ClientSocketMessage(message.getClientId(),
                                ClientSocketMessageType.DISCONNECT,
                                "Player " + player.getId() + " has left the room!")));
                    }
                }

                // 4. Remove the user from the list of sessions
                removeSession(message.getClientId());
                return "Disconnected!";
            case JOIN:
                if (roomService.getPlayerRoomByPlayerId(message.getClientId()) == null) {
                    int seatNumber = 1;
                    String roomId = message.getMessage();
                    roomService.getRoom(roomId)
                            .addPlayer(playerService.getPlayerById(message.getClientId()), seatNumber);
                }

                break;
            case CREATE_AND_JOIN_ROOM:
                // Check if there are any rooms available
                if (roomService.getRoomCount() == 0) {
                    // Retrieve the player
                    Player currentPlayer = playerService.getPlayerById(message.getClientId());
                    // Check if the player exists
                    if (currentPlayer == null) {
                        // Player not found, warn the user and abort.
                        sendMessage(message.getClientId(), SerializationUtil.serializeString(
                                new ClientSocketMessage(message.getClientId(), ClientSocketMessageType.MESSAGE,
                                "Player not found!")));
                        return "Player not found!";
                    }

                    // Check if the player is in any room first
                    Room playerRoom = roomService.getPlayerRoomByPlayerId(currentPlayer.getId());
                    if (playerRoom != null) {
                        // Player is already in a room, warn the user and abort.
                        sendMessage(currentPlayer.getId(), SerializationUtil.serializeString(
                                new ClientSocketMessage(message.getClientId(), ClientSocketMessageType.MESSAGE,
                                "Player is already in a room!")));
                        return "Player is already in a room!";
                    }

                    // Create a new room
                    Room newRoom = roomService.createRoom();
                    // Add the player to the room
                    roomService.addPlayerToRoom(newRoom.getRoomId(), currentPlayer);
                    // Inform the player that they have been added to the room
                    sendMessage(currentPlayer.getId(), SerializationUtil.serializeString(
                            new ServerSocketMessage<>(ServerSocketMessageType.JOIN,
                                    "You have been added to a new room!")));
                }
                break;
            case LEAVE:
                // When a player leaves the room
                if (message.getClientId() != null) {

                } else {

                }
                break;
            case SIT:
                break;
            case LEAVE_SEAT:
                break;
            case BET:
                break;
            case MODIFY:
                break;
            case HIT:
                break;
            case STAND:
                break;
            case DOUBLE:
                break;
            case SPLIT:
                break;
            case INSURANCE:
                break;
            case SURRENDER:
                break;
        }

        return "Hello, world!";
    }
}