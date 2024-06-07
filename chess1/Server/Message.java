package org.example.chess1.Server;

import java.io.Serializable;

public class Message implements Serializable {
    private final MessageType type;
    private final Object data;

    public Message(MessageType type) {
        this.type = type;
        this.data = null;
    }

    public Message(MessageType type, Object data) {
        this.type = type;
        this.data = data;
    }


    //Get
    public Object getData() {
        return data;
    }

    public MessageType getType() {
        return type;
    }
}
