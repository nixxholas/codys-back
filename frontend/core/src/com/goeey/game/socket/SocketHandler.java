package com.goeey.game.socket;

import com.badlogic.gdx.utils.Json;
import com.goeey.backend.util.SerializationUtil;
import com.gooey.base.socket.ClientEvent;
import com.gooey.base.socket.ServerEvent;

import java.net.URI;
import java.net.URISyntaxException;

public class SocketHandler {
    // Glenn is here
    private WebSocket ws;

    public SocketHandler(String uriStr) {

        URI uri = null;
        try {
            uri = new URI(uriStr);

        } catch (URISyntaxException e) {
            System.out.println("invalid server uri");
        }

        ws = new WebSocket(uri);
        startListening();
        //ws.connect();
    }

    public void startListening(){
        Thread listenerThread = new Thread(() -> {
            try {
                System.out.println(ws.getReadyState());
                ws.connectBlocking();
                System.out.println(ws.getReadyState());

                //Listen to all socket events
                while (ws.isOpen()){
                    if(ws.getMessageQueue() != null){
                        String message = ws.getMessageQueue().take();
                        var severEvent =  SerializationUtil.deserializeString(message, ServerEvent.class);
                        System.out.println(severEvent.getType());
                        System.out.println(severEvent.getMessage());
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        listenerThread.start();
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

    public void createandjoin(String clientId){
        ClientEvent createAndJoinEvent = new ClientEvent(clientId, ClientEvent.Type.CREATE_AND_JOIN_ROOM, clientId);
        try{
            ws.send(SerializationUtil.serializeString(createAndJoinEvent));
            ws.getLatch().await();
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }

    public void sit(String clientId){
        ClientEvent sitEvent = new ClientEvent(clientId, ClientEvent.Type.SIT, "1");
        try{
            ws.send(SerializationUtil.serializeString(sitEvent));
            ws.getLatch().await();
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }

    public void bet(String clientId){
        ClientEvent betEvent = new ClientEvent(clientId, ClientEvent.Type.BET, "100");
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
