package com.goeey.backend.handler;

import com.goeey.backend.model.entity.socket.ClientSocketMessage;
import com.goeey.backend.model.entity.socket.ServerSocketMessage;
import com.goeey.backend.model.entity.socket.ServerSocketMessageType;
import com.goeey.backend.service.PlayerService;
import com.goeey.backend.service.RoomService;
import com.goeey.backend.util.SerializationUtil;
import com.google.gson.JsonIOException;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.Arrays;

public class SocketHandler implements WebSocketHandler {
    private final ClientMessageHandler handler;
    private final RoomService roomService;
    private final PlayerService playerService;

    public SocketHandler(ClientMessageHandler clientMessageHandler, RoomService roomService, PlayerService playerService) {
        this.handler = clientMessageHandler;
        this.roomService = roomService;
        this.playerService = playerService;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(session.receive()
                .map(msg -> {
                    try {
                        ClientSocketMessage clientSocketMessage = SerializationUtil.deserialize(msg.getPayloadAsText().getBytes(), ClientSocketMessage.class);
                        return session.textMessage(handler.handle(clientSocketMessage));
                    }
                    catch (StreamCorruptedException sce) {
                        ServerSocketMessage<String> message = new ServerSocketMessage<>(ServerSocketMessageType.ERROR, "Error: " + sce.getMessage());

                        try {
                            return session.textMessage(SerializationUtil.serializeString(message));
                        } catch (JsonIOException e) {
                            return session.textMessage("Error: " + e.getMessage());
                        }
                    }
                    catch (Exception e) {
                        return session.textMessage("Error: " + e.getMessage());
                    }
                }));
    }
}