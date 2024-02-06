package com.goeey.backend.model.entity.socket;

import com.goeey.backend.model.entity.EntityTarget;

public class ServerSocketMessage<T> extends BaseSocketMessage<T, ServerSocketMessageType> {
    public ServerSocketMessage(ServerSocketMessageType type, T message) {
        super();
        setType(type);
        setMessage(message);
    }

    public ServerSocketMessage(ServerSocketMessageType type, T message, EntityTarget target) {
        this(type, message);
        setTarget(target);
    }
}
