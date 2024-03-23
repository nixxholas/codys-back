package com.goeey.game.socket;

import com.goeey.backend.util.SerializationUtil;
import com.gooey.base.socket.ClientEvent;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.concurrent.LinkedBlockingQueue;

public class WebSocket extends WebSocketClient{

    private static LinkedBlockingQueue<ClientEvent> serverQueue;

    public WebSocket(URI serverUri) {
        super(serverUri);
        serverQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("Socket is opened");
        System.out.println(Thread.currentThread());
        try {
            this.sentEvents();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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

    public void addToQueue(ClientEvent event) throws InterruptedException {
        serverQueue.put(event);
    }

    public void sentEvents() throws InterruptedException {
        while(this.isOpen()) {
            if(serverQueue.peek() != null) {
                this.send(SerializationUtil.serializeString(serverQueue.take()));
            }

            Thread.sleep(1000);
        }
    }
}
