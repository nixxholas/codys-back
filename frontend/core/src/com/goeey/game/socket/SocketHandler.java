package com.goeey.game.socket;

import com.goeey.backend.util.SerializationUtil;
import com.gooey.base.socket.ClientEvent;
import org.java_websocket.client.WebSocketClient;

import java.net.URI;
import java.net.URISyntaxException;

public class SocketHandler {
    private WebSocketClient ws;
    public SocketHandler(String uriStr) {

        URI uri = null;
        try {
            uri = new URI(uriStr);
        } catch (URISyntaxException e) {
            System.out.println("invalid server uri");
        }

        ws = new WebSocket(uri);

        try {
            System.out.println(ws.getReadyState());
            ws.connectBlocking();
            System.out.println(ws.getReadyState());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isRegistered() {
        return true;
    }

    public void register(String clientId) {
        ClientEvent registerEvent = new ClientEvent(clientId, ClientEvent.Type.REGISTER);
        ws.send(SerializationUtil.serializeString(registerEvent));
    }

    public void closeSocket() {
        if(ws.isOpen()){
            ws.close();
        }
    }
}
