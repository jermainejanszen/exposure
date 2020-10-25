package com.exposure.adapters;

import android.graphics.Bitmap;

public class MessageListItem {

    private final String message;
    private final boolean sender;

    public MessageListItem(String message, boolean sender) {
        this.message = message;
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public boolean getSender() {
        return sender;
    }
}
