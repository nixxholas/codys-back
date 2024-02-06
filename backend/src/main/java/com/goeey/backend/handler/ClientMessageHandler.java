package com.goeey.backend.handler;

import com.goeey.backend.model.entity.Player;
import com.goeey.backend.model.entity.socket.ClientSocketMessage;
import com.goeey.backend.service.PlayerService;
import com.goeey.backend.service.RoomService;
import com.goeey.backend.util.SerializationUtil;

import java.io.IOException;
import java.util.Arrays;

public class ClientMessageHandler {
    private final RoomService roomService;
    private final PlayerService playerService;
    private final ClientSocketMessage message;

    public ClientMessageHandler(RoomService roomService, PlayerService playerService, ClientSocketMessage message) {
        this.roomService = roomService;
        this.playerService = playerService;
        this.message = message;
    }

    public String handle() throws IOException {
        switch (message.getType()) {
            case CONNECT:
                // If the message has an ID, it means the connection was to reconnect the player
                if (message.getMessage() != null) {
                    Player player = playerService.getPlayerById(message.getClientId());

                    if (player == null) {
                        return "Player not found!";
                    }

                    return Arrays.toString(SerializationUtil.serialize(player));
                }

                return "Connected!";
            case DISCONNECT:
                return "Disconnected!";
            case JOIN:
                if (roomService.getPlayerRoomByPlayerId(message.getClientId()) == null) {
                    int seatNumber = 1;
                    String roomId = message.getMessage();
                    roomService.getRoom(roomId)
                            .addPlayer(
                                    playerService.getPlayerById(message.getClientId()),
                                    seatNumber);
                }

                break;
            case LEAVE:
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