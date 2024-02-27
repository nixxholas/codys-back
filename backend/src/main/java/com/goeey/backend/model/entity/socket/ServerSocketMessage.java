package com.goeey.backend.model.entity.socket;

import com.goeey.backend.model.entity.EntityTarget;

public class ServerSocketMessage<E> extends BaseSocketMessage<E, ServerSocketMessageType> {
    public ServerSocketMessage(ServerSocketMessageType type, E message) {
        super();
        setType(type);
        setMessage(message);
    }

    public ServerSocketMessage(ServerSocketMessageType type, E message, EntityTarget target) {
        this(type, message);
        setTarget(target);
    }
}
