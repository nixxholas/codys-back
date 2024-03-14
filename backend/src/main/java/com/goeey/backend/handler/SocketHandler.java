package com.goeey.backend.handler;

import com.gooey.base.BasePlayer;
import com.gooey.base.Player;
import com.gooey.base.Room;
import com.gooey.base.socket.ClientEvent;
import com.gooey.base.socket.ServerEvent;
import com.goeey.backend.util.SerializationUtil;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SocketHandler implements WebSocketHandler {
    private final Sinks.Many<ServerEvent> broadcastSink; // For broadcasting to all players in the lobby
    // broadcast messages to all players within a specific room.
    private final Map<String, Sinks.Many<ServerEvent>> roomSinkMap = new ConcurrentHashMap<>();
    // Runtime datastore for player data
    private final Map<String, BasePlayer> players = new ConcurrentHashMap<>();
    // Store all players in a lobby session first
    private final Map<String, Player> lobbySessions = new ConcurrentHashMap<>();
    // Runtime datastore for rooms
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();

    public SocketHandler() {
        this.broadcastSink = Sinks.many().multicast().onBackpressureBuffer();
    }

    // START OF PLAYER MANAGEMENT METHODS

    public Player getPlayerById(String id) {
        // Find through the lobby first
        if (lobbySessions.containsKey(id)) {
            return lobbySessions.get(id);
        }

        // Then by room
        for (Room room : rooms.values()) {
            if (room.hasPlayerById(id)) {
                return room.getPlayerById(id);
            }
        }

        // Finally, by player
        BasePlayer playerData = players.get(id);
        return new Player(playerData.getId(), playerData.getName());
    }

    public Player createPlayer(String id, String name) {
        Player player = new Player(id, name);
        players.put(player.getId(), player);
        return player;
    }

    public Mono<Void> joinLobby(WebSocketSession session, String playerId) {
        Player player = getPlayerById(playerId);
        if (player == null)
            return null;
        if (rooms.values().stream().anyMatch(r -> r.hasPlayerById(playerId)))
            return null;

        // Notify all players in the lobby
        if (lobbySessions.size() > 0) {
            ServerEvent joinEvent = new ServerEvent<>(ServerEvent.Type.PLAYER_JOIN, "Player " + player.getName() + " joined the lobby");
            broadcastSink.tryEmitNext(joinEvent);
        }
        // Add the joining player to the lobby
        lobbySessions.put(playerId, player);
        subscribePlayerToLobbyBroadcasts(session).subscribe();

        return session.send(broadcastSink.asFlux()
                .map(event -> session.textMessage(SerializationUtil.serializeString(event))));
    }

    public Mono<Void> subscribePlayerToLobbyBroadcasts(WebSocketSession session) {
        Flux<ServerEvent> eventsFlux = broadcastSink.asFlux();

        return session.send(eventsFlux.map(event ->
                session.textMessage(SerializationUtil.serializeString(event))
        ));
    }

    // END OF PLAYER MANAGEMENT METHODS

    // START OF ROOM MANAGEMENT METHODS

    public Room createRoom() {
        String roomId = UUID.randomUUID().toString();
        Room room = new Room(roomId);
        rooms.put(roomId, room);
        return room;
    }

    public Room getRoom(String roomId) {
        return rooms.get(roomId);
    }

    public int getRoomCount() {
        return rooms.size();
    }

    public Room getPlayerRoomByPlayerId(String playerId) {
        for (Room room : rooms.values()) {
            if (room.hasPlayerById(playerId)) {
                return room;
            }
        }
        return null;
    }

    public Flux<ClientEvent> removePlayerFromRoom(String playerId, String roomId) {
        Room room = getRoom(roomId);
        if (room != null) {
            room.removePlayer(room.getPlayerSeatNumber(playerId));
        }

        return null;
    }

    // END OF ROOM MANAGEMENT METHODS

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.receive()
                .map(webSocketMessage -> {
                    try {
                        return SerializationUtil.deserializeString(webSocketMessage.getPayloadAsText(), ClientEvent.class);
                    } catch (Exception e) {
                        // Handle deserialization error
                        return new ClientEvent("error", ClientEvent.Type.MESSAGE, "Invalid message format");
                    }
                })
                .flatMap(event -> {
                    // Process event to determine if it's for the lobby or a specific room
                    switch (event.getType()) {
                        case CONNECT, REGISTER, CREATE_AND_JOIN_ROOM:
                            return processLobbyEvent(session, event); // Implement this method for lobby-specific actions
                        case DISCONNECT:
                            return processDisconnectEvent(session, event); // Implement this method for disconnect-specific actions
                        case PING: // Send back a ping
                            return session.send(Mono.just(session.textMessage(SerializationUtil.serializeString(new ServerEvent(ServerEvent.Type.PONG, "Pong")))));
                        default:
                            // Assume event contains roomId for room-specific actions
                            return processRoomEvent(session, event);
                    }
                }).then();
    }

    private Mono<Void> processDisconnectEvent(WebSocketSession session, ClientEvent event) {
        // Handle disconnect
        return Mono.empty();
    }

    private Mono<Void> processLobbyEvent(WebSocketSession session, ClientEvent event) {
        System.out.println("Received message: " + SerializationUtil.serializeString(event) + " from " + session.getId());

        // Handle lobby events
        switch (event.getType()) {
            case CONNECT:
                // Find the player
                Player connectingPlayer = getPlayerById(event.getClientId());
                if (connectingPlayer == null)
                    return session.send(Mono.just(session.textMessage(SerializationUtil.serializeString(new ServerEvent(ServerEvent.Type.ERROR, "Invalid player")))));

                // Join the player to the lobby
                joinLobby(session, event.getClientId());
                // Send a welcome message to the player
                return session.send(Mono.just(session.textMessage(SerializationUtil.serializeString(new ServerEvent(ServerEvent.Type.JOIN, "Welcome to the lobby " + connectingPlayer.getName() + "!")))));
            case REGISTER:
                // Create a new player
                Player newPlayer = createPlayer(event.getClientId(), event.getMessage());
                // Join the player to the lobby
                joinLobby(session, event.getClientId());
                // Register the player
                return session.send(Mono.just(session.textMessage(SerializationUtil.serializeString(new ServerEvent(ServerEvent.Type.REGISTERED, "Welcome to the lobby " + newPlayer.getName() + "!")))));
            case CREATE_AND_JOIN_ROOM:
                Player joiningAndCreatingPlayer = getPlayerById(event.getClientId());
                if (joiningAndCreatingPlayer == null)
                    return session.send(Mono.just(session.textMessage(SerializationUtil.serializeString(new ServerEvent(ServerEvent.Type.ERROR, "Invalid player")))));

                // Create a new room
                Room room = createRoom();
                // Join the player to the room
                room.playerJoin(joiningAndCreatingPlayer, session);
                // Send a welcome message to the player
                return session.send(Mono.just(session.textMessage(SerializationUtil.serializeString(new ServerEvent(ServerEvent.Type.JOIN, "Welcome to room " + room.getRoomId())))));
            default:
                return session.send(Mono.just(session.textMessage(SerializationUtil.serializeString(new ServerEvent(ServerEvent.Type.ERROR, "Invalid event")))));
        }
    }

    private Mono<Void> processRoomEvent(WebSocketSession session, ClientEvent event) {
        // Retrieve player data
        Player player = getPlayerById(event.getClientId());
        if (player == null)
            return session.send(Mono.just(session.textMessage(SerializationUtil.serializeString(new ServerEvent(ServerEvent.Type.ERROR, "Invalid player")))));
        // Retrieve the player's room
        Room room = getPlayerRoomByPlayerId(event.getClientId());
        if (room == null) {
            if (event.getType() == ClientEvent.Type.JOIN) {
                // Create a new room
                room = getRoom(event.getMessage());
                if (room == null)
                    return session.send(Mono.just(session.textMessage(SerializationUtil.serializeString(new ServerEvent(ServerEvent.Type.ERROR, "Invalid room")))));
                // Join the player to the room
                room.playerJoin(player, session);
                return session.send(Mono.just(session.textMessage(SerializationUtil.serializeString(new ServerEvent(ServerEvent.Type.JOIN, "Welcome to room " + room.getRoomId())))));
            } else {
                // Player is not in a room
                return session.send(Mono.just(session.textMessage(SerializationUtil.serializeString(new ServerEvent(ServerEvent.Type.ERROR, "You are not in a room")))));
            }
        }
        Sinks.Many<ServerEvent> roomSink = roomSinkMap.computeIfAbsent(room.getRoomId(), id -> Sinks.many().multicast().onBackpressureBuffer());
        ServerEvent responseEvent;
        switch (event.getType()) {
            case JOIN:
                // TODO: Check if from lobby or another room

                // Join the room
                room.playerJoin(player, session);
                responseEvent = new ServerEvent(ServerEvent.Type.JOIN, "You have joined room " + room.getRoomId());
                break;
            case LEAVE:
                // Leave the room
                Player leftPlayer = room.playerLeave(event.getClientId());
                // Bring the player back to the lobby
                lobbySessions.put(leftPlayer.getId(), leftPlayer);
                responseEvent = new ServerEvent(ServerEvent.Type.LEAVE, "You have left room " + room.getRoomId());
            case SIT:
                // Sit the player in a seat;
                return session.send(Mono.just(session.textMessage(
                        SerializationUtil.serializeString(
                                room.sit(player, Integer.parseInt(event.getMessage()))
                        ))
                ));
            case LEAVE_SEAT:
                // Leave the seat
                return session.send(Mono.just(session.textMessage(SerializationUtil.serializeString(room.standUp(player)))));
//            case BET:
//                // Place a bet
//                return processBetEvent(session, event, room, roomSink);
            default:
                responseEvent = new ServerEvent(ServerEvent.Type.ERROR, "Invalid event");
        }

//         = new ServerEvent(ServerEvent.Type.BET, "Processed event for room: " + room.getRoomId());
        roomSink.tryEmitNext(responseEvent);
        return session.send(roomSink.asFlux().map(evt -> session.textMessage(SerializationUtil.serializeString(evt))));
    }
}