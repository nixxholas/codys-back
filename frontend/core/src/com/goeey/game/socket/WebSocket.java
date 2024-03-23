package com.goeey.game.socket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

import com.goeey.backend.util.SerializationUtil;
import com.gooey.base.socket.BaseEvent;

public class WebSocket extends WebSocketClient{

    public WebSocket(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("Socket is opened");
    }

    @Override
    public void onMessage(String s) {
        System.out.println("Received:");
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        System.out.println("Socket is closed");
    }

    @Override
    public void onError(Exception e) {

    }
    @Override
    public void run() {

        for(int i = 0; i < 100; i++) {
            System.out.println(Thread.currentThread().getId());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
