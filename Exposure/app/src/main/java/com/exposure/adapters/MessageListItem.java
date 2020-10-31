package com.exposure.adapters;

public class MessageListItem {

    private final String message;
    private final String sender;
    private final Long time;

    public MessageListItem(String message, String sender) {
        this.message = message;
        this.sender = sender;
        this.time = System.currentTimeMillis();
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public Long getTime() {
        return time;
    }
}
