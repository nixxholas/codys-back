package com.goeey.game.socket;

import com.goeey.backend.util.SerializationUtil;
import com.goeey.game.entity.GameState;
import com.goeey.game.utils.ProcessServerMessage;
import com.gooey.base.socket.ServerEvent;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

public class WebSocket extends WebSocketClient {
    private final CountDownLatch latch = new CountDownLatch(1);
    private CompletableFuture<ServerEvent<?>> messageFuture;

    public WebSocket(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        GameState.getGameState().setConnected(true);
        System.out.println("Socket open:" + Thread.currentThread());
    }

    @Override
    public void onMessage(String message) {
        ServerEvent<?> serverEvent =  SerializationUtil.deserializeString(message, ServerEvent.class);

        if (isReplyToClientMessage(serverEvent.getType())) {
            System.out.println("Reply: " + message + " on " + Thread.currentThread());
            if(messageFuture != null && !messageFuture.isDone()) {
                messageFuture.complete(serverEvent);
            }

        } else {
            System.out.println("Received: " + message + " on " + Thread.currentThread());
            ProcessServerMessage.callMethod(serverEvent, GameState.getGameState());
            this.latch.countDown();
        }
    }

    public CompletableFuture<ServerEvent<?>> sendAsyncMessage(String message) {
        messageFuture = new CompletableFuture<>();
        System.out.println("Sent: " + message + " on " + Thread.currentThread());
        this.send(message);
        return messageFuture;
    }

    public boolean isReplyToClientMessage(ServerEvent.Type eventType) {
        return switch (eventType) {
            case ROOM_PLAYERS, ROOM_LIST, PLAYER_SAT, JOINED_ROOM -> true;
            default -> false;
        };
    }

    public CountDownLatch getLatch(){
        return this.latch;
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        GameState.getGameState().setConnected(false);
        System.out.println("Socket closed: " + Thread.currentThread());
    }
    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }
}
