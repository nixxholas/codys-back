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

    }

    @Override
    public void onMessage(String s) {
        SerializationUtil.serializeString(s);
    }

    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception e) {

    }
}
