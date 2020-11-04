package com.exposure.adapters;

/**
 * Represents a message in a conversation between two users
 */
public class MessageListItem {

    private final String message;
    private final String sender;
    private final Long time;

    /**
     * Constructor for the MessageListItem, initialising the message and the sender of the message
     * @param message the message content
     * @param sender the sender of the message
     */
    public MessageListItem(String message, String sender) {
        this.message = message;
        this.sender = sender;
        this.time = System.currentTimeMillis();
    }

    /**
     * Returns the content of the message
     * @return the content of the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the sender of the message
     * @return the sender of the message
     */
    public String getSender() {
        return sender;
    }
}
