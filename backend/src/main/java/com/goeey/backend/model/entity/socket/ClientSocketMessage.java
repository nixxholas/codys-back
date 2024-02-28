package com.goeey.backend.model.entity.socket;

public class ClientSocketMessage extends BaseSocketMessage<String, ClientSocketMessageType> {
    private final String clientId;
    public ClientSocketMessage(String clientId) {
        super();
        this.clientId = clientId;
    }

    public ClientSocketMessage(String clientId, ClientSocketMessageType type, String message) {
        super();
        this.clientId = clientId;
        setType(type);
        setMessage(message);
    }

    public String getClientId() {
        return clientId;
    }
}
