package com.goeey.backend.model.entity.socket;

/*
 * The ServerSocketMessageType enum is used to define the type of message
 * that is being sent from the server to the client.
 */
public enum ServerSocketMessageType {
    CONNECT,
    DISCONNECT,
    ERROR,
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
    DEAL,
    DEALER_HIT,
    DEALER_STAND,
    DEALER_BUST,
    DEALER_BLACKJACK,
    DEALER_WIN,
    DEALER_LOSE,
    DEALER_PUSH,
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
    PLAYER_JOIN,
    PLAYER_LEAVE,
    PLAYER_SIT,
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
}
