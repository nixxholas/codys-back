package com.gooey.base.socket;

import com.gooey.base.EntityTarget;

public class ServerEvent<E> extends BaseEvent<E> {
    /*
     * The ServerSocketMessageType enum is used to define the type of message
     * that is being sent from the server to the client.
     */
    public enum Type {
        CONNECT,
        DISCONNECT,
        ROOM_LIST,
        ROOM_PLAYERS,
        REGISTERED,
        ERROR,
        COUNTDOWN,
        UPDATE,
        JOINED,
        LEAVE,
        SIT,
        STOOD_UP,
        BET,
        MODIFY,
        HIT,
        STAND,
        DOUBLE,
        SPLIT,
        INSURANCE,
        SURRENDER,
        DEAL,
        DEALER_DRAW,
        DEALER_HIT,
        DEALER_STAND,
        DEALER_BUST,
        DEALER_BLACKJACK,
        DEALER_WIN,
        DEALER_LOSE,
        DEALER_PUSH,
        PLAYER_DRAW,
        PLAYER_WIN,
        PLAYER_LOSE,
        PLAYER_PUSH,
        PLAYER_BLACKJACK,
        PLAYER_BUST,
        PLAYER_SURRENDER,
        PLAYER_DOUBLE,
        PLAYER_SPLIT,
        PLAYER_INSURANCE,
        PLAYER_HIT,
        PLAYER_STAND,
        PLAYER_BET,
        PLAYER_JOINED,
        PLAYER_LEFT,
        PLAYER_SAT,
        PLAYER_LEAVE_SEAT,
        PLAYER_MODIFY,
        PLAYER_CONNECT,
        PLAYER_DISCONNECT,
        PLAYER_READY,
        PLAYER_NOT_READY,
        PLAYER_RECONNECT,
        PLAYER_RECONNECTED,
        PLAYER_DISCONNECTED,
        PLAYER_RECONNECTING,
        PONG
    }

    private Type type;

    public ServerEvent(Type type, E message) {
        super();
        this.type = type;
        setMessage(message);
    }

    public ServerEvent(Type type, E message, EntityTarget target) {
        this(type, message);
        setTarget(target);
    }

    public Type getType() {
        return type;
    }

    public boolean shouldBroadcast() {
        switch (type) {
            case CONNECT, PONG, UPDATE, ERROR, DISCONNECT -> {
                return false;
            }
        }

        return true;
    }
}
