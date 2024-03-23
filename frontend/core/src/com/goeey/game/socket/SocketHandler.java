package com.goeey.game.socket;

import com.gooey.base.socket.ClientEvent;

import java.net.URI;
import java.net.URISyntaxException;

public class SocketHandler {
    private WebSocket ws;

    public SocketHandler(String uriStr) {

        URI uri = null;
        try {
            uri = new URI(uriStr);

        } catch (URISyntaxException e) {
            System.out.println("invalid server uri");
        }

        ws = new WebSocket(uri);
        ws.connect();
    }

    public void register(String clientId) {
        try {
            ws.addToQueue(new ClientEvent(clientId, ClientEvent.Type.REGISTER));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeWebSocket() {
        if(ws.isOpen()){
            ws.close();
        }
    }
}
