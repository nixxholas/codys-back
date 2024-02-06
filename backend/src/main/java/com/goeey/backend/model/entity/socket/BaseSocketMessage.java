package com.goeey.backend.model.entity.socket;

import com.goeey.backend.model.entity.EntityTarget;

import java.io.*;

public abstract class BaseSocketMessage<T, E> implements Serializable {
    private E type;
    private T message;
    private EntityTarget target;

    protected BaseSocketMessage() {
    }

    public E getType() {
        return type;
    }

    public T getMessage() {
        return message;
    }

    public EntityTarget getTarget() {
        return target;
    }

    public void setType(E type) {
        this.type = type;
    }

    public void setMessage(T message) {
        this.message = message;
    }

    public void setTarget(EntityTarget target) {
        this.target = target;
    }
}