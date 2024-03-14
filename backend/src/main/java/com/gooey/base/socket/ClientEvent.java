package com.gooey.base.socket;

public class ClientEvent extends BaseEvent<String> {
    /*
     * This enum represents the different types of messages that
     * can be sent from the client to the server.
     */
    public enum Type {
        CONNECT,
        REGISTER,
        DISCONNECT,
        JOIN,
        LEAVE,
        SIT,
        LEAVE_SEAT,
        BET,
        MODIFY,
        HIT,
        STAND,
        DOUBLE,
        SPLIT,
        INSURANCE,
        SURRENDER,
        CREATE_AND_JOIN_ROOM,
        MESSAGE,
        PING
    }
    private final String clientId;
    private Type type;
    public ClientEvent(String clientId, Type type) {
        super();
        this.clientId = clientId;
        this.type = type;
    }

    public ClientEvent(String clientId, Type type, String message) {
        this(clientId, type);
        setMessage(message);
    }

    public String getClientId() {
        return clientId;
    }

    public ClientEvent.Type getType() {
        return type;
    }
}
