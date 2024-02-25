package com.goeey.backend.model.entity.socket;

import com.goeey.backend.model.entity.EntityTarget;

import java.io.*;

public abstract class BaseSocketMessage<E, T> implements Serializable {
    private T type;
    private E message;
    private EntityTarget target;

    protected BaseSocketMessage() {
    }

    public T getType() {
        return type;
    }

    public E getMessage() {
        return message;
    }

    public EntityTarget getTarget() {
        return target;
    }

    public void setType(T type) {
        this.type = type;
    }

    public void setMessage(E message) {
        this.message = message;
    }

    public void setTarget(EntityTarget target) {
        this.target = target;
    }
}