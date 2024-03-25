package com.goeey.game.socket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

public class WebSocket extends WebSocketClient{
    private CountDownLatch latch = new CountDownLatch(1);
    private LinkedBlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();

    public WebSocket(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("Socket is opened");
        System.out.println(Thread.currentThread());
    }
    @Override
    public void onMessage(String message) {
        System.out.println("Received: " + message);
        try{
            this.messageQueue.put(message);
            this.latch.countDown();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public CountDownLatch getLatch(){
        return this.latch;
    }

    public LinkedBlockingQueue<String> getMessageQueue(){
        return this.messageQueue;
    }
    @Override
    public void onClose(int i, String s, boolean b) {
        System.out.println("Socket is closed");
    }
    @Override
    public void onError(Exception e) {
        System.out.println("Socket has an error");
    }
}
