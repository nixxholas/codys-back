package com.gooey.base.socket;

import com.gooey.base.EntityTarget;

import java.io.*;

public abstract class BaseEvent<E> implements Serializable {
    private E message;
    private EntityTarget target;

    protected BaseEvent() {
    }

    public E getMessage() {
        return message;
    }

    public EntityTarget getTarget() {
        return target;
    }

    public void setMessage(E message) {
        this.message = message;
    }

    public void setTarget(EntityTarget target) {
        this.target = target;
    }
}