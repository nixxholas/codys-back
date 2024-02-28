package com.goeey.backend.model.entity.socket;

/*
 * This enum represents the different types of messages that
 * can be sent from the client to the server.
 */
public enum ClientSocketMessageType {
    CONNECT,
    CREATE_ACCOUNT,
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
}
