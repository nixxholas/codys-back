package com.goeey.game.socket;

import com.badlogic.gdx.ScreenAdapter;
import com.goeey.backend.util.SerializationUtil;
import com.goeey.game.utils.ProcessServerMessage;
import com.gooey.base.socket.ClientEvent;
import com.gooey.base.socket.ServerEvent;
import org.java_websocket.enums.ReadyState;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.concurrent.CountDownLatch;

public class SocketHandler {
    private final WebSocket ws;

    private CountDownLatch latch = new CountDownLatch(1);

    public SocketHandler(String uriStr) {

        URI uri = null;
        try {
            uri = new URI(uriStr);
        } catch (URISyntaxException e) {
            System.out.println("invalid server uri");
        }

        ws = new WebSocket(uri);
        startListening();
    }

    public void startListening(){
        System.out.println("Starting to Listen");
        Thread listenerThread = new Thread(() -> {
            try {
                System.out.println(ws.getReadyState());
                ws.connectBlocking();
                System.out.println(ws.getReadyState());

                //Listen to all socket events
                while (ws.isOpen()){
                    if(ws.getMessageQueue() != null){
                        String message = ws.getMessageQueue().take();
                        ServerEvent<?> serverEvent =  SerializationUtil.deserializeString(message, ServerEvent.class);
                        //System.out.println(serverEvent.getType());
                        //System.out.println(serverEvent.getMessage());
                        ProcessServerMessage.callMethod(serverEvent);
                        if (serverEvent.getType() == ServerEvent.Type.ERROR || serverEvent.getType() == ServerEvent.Type.PLAYER_SAT) {
                            this.latch.countDown();
                        }
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        listenerThread.start();
    }

    public ReadyState getState() {
        return ws.getReadyState();
    }

    public WebSocket getWebSocket(){
        return this.ws;
    }

    public void resetLatch(int num){
        this.latch = new CountDownLatch(num);
   }

    public void awaitPlayer() throws InterruptedException {
        latch.await(); // Wait until the latch count becomes zero
    }

    public void register(String clientId){
        ClientEvent registerEvent = new ClientEvent(clientId, ClientEvent.Type.REGISTER, clientId);
        try{
            ws.send(SerializationUtil.serializeString(registerEvent));
            ws.getLatch().await();
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }

    public void connect(String clientId){
        ClientEvent connectEvent = new ClientEvent(clientId, ClientEvent.Type.CONNECT, clientId);
        try{
            ws.send(SerializationUtil.serializeString(connectEvent));
            ws.getLatch().await();
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }

    public void createAndJoin(String clientId){
        ClientEvent createAndJoinEvent = new ClientEvent(clientId, ClientEvent.Type.CREATE_AND_JOIN_ROOM, clientId);
        try{
            ws.send(SerializationUtil.serializeString(createAndJoinEvent));
            ws.getLatch().await();
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }

    public void sit(String clientId, int seatNum){
        ClientEvent sitEvent = new ClientEvent(clientId, ClientEvent.Type.SIT, Integer.toString(seatNum));
        try{
            ws.send(SerializationUtil.serializeString(sitEvent));
            ws.getLatch().await();
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }

    public void bet(String clientId, double amount){
        DecimalFormat df = new DecimalFormat("#.#");
        String amt = df.format(amount);
        ClientEvent betEvent = new ClientEvent(clientId, ClientEvent.Type.BET, amt);
        try{
            ws.send(SerializationUtil.serializeString(betEvent));
            ws.getLatch().await();
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }

    public void closeWebSocket() {
        if(ws.isOpen()){
            ws.close();
        }
    }
}
