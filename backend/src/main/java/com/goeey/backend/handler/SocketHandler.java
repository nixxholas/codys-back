package com.goeey.backend.handler;

import com.goeey.backend.model.entity.socket.ClientSocketMessage;
import com.goeey.backend.model.entity.socket.ServerSocketMessage;
import com.goeey.backend.model.entity.socket.ServerSocketMessageType;
import com.goeey.backend.service.PlayerService;
import com.goeey.backend.service.RoomService;
import com.goeey.backend.util.SerializationUtil;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.Arrays;

public class SocketHandler implements WebSocketHandler {
    private final RoomService roomService;
    private final PlayerService playerService;

    public SocketHandler(RoomService roomService, PlayerService playerService) {
        this.roomService = roomService;
        this.playerService = playerService;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(session.receive()
                .map(msg -> {
                    try {
                        ClientSocketMessage clientSocketMessage = SerializationUtil.deserialize(msg.getPayloadAsText().getBytes(), ClientSocketMessage.class);
                        ClientMessageHandler handler = new ClientMessageHandler(roomService, playerService, clientSocketMessage);
                        return session.textMessage(handler.handle());
                    }
                    catch (StreamCorruptedException sce) {
                        ServerSocketMessage<String> message = new ServerSocketMessage<>(ServerSocketMessageType.ERROR, "Error: " + sce.getMessage());

                        try {
                            return session.textMessage(Arrays.toString(SerializationUtil.serialize(message)));
                        } catch (IOException e) {
                            return session.textMessage("Error: " + e.getMessage());
                        }
                    }
                    catch (Exception e) {
                        return session.textMessage("Error: " + e.getMessage());
                    }
                }));
    }
}