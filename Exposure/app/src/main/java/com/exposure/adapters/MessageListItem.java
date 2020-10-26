package com.exposure.adapters;

public class MessageListItem {

    private final String message;
    private final String sender;

    public MessageListItem(String message, String sender) {
        this.message = message;
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }
}
