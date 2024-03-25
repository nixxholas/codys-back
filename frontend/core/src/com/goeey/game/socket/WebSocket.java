package com.goeey.game.socket;

import com.goeey.game.GameManager;
import com.gooey.base.socket.ClientEvent;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.net.URI;
import java.util.concurrent.LinkedBlockingQueue;

public class WebSocket extends WebSocketClient{
    private CountDownLatch latch = new CountDownLatch(1);

    private LinkedBlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();

    private static final long RECONNECT_DELAY_MS = 1000; // 1 second delay
    private boolean reconnectScheduled = false;
    private Timer reconnectTimer;

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
        return  this.latch;
    }

    public LinkedBlockingQueue<String> getMessageQueue(){
        return this.messageQueue;
    }
    @Override
    public void onClose(int i, String s, boolean b) {
        System.out.println("Socket is closed");
        //scheduleReconnect();
    }
    @Override
    public void onError(Exception e) {
        System.out.println("Socket has an error");
        //scheduleReconnect();
    }

    private void scheduleReconnect(){
        if (!reconnectScheduled) {
            reconnectScheduled = true;
            reconnectTimer = new Timer();
            reconnectTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    reconnect();
                }
            }, RECONNECT_DELAY_MS);
        }
    }

    public void reconnect() {
        System.out.println("Attempting to reconnect...");
        try {
            reconnectScheduled = false;
            reconnectTimer.cancel();
            reconnectTimer.purge();
            reconnectTimer = null;

            //Connect To Socket Again
            GameManager.socketHandler = new SocketHandler("ws://localhost:8080/ws");

        } catch (Exception e) {
            System.err.println("Failed to reconnect: " + e.getMessage());
            scheduleReconnect(); // Retry reconnecting
        }
    }

}
