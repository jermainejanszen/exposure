package com.exposure.adapters;

import android.graphics.Bitmap;

public class ChatListItem {

    private String uid;
    private Bitmap profileImage;
    private String name;
    private String lastMessage;
    private String date;

    public ChatListItem(String uid) {
        this.uid = uid;
        this.profileImage = null;
        this.name = "First";
        this.lastMessage = "This was the last message that was sent.";
        this.date = "24/10/20";
    }

    public Bitmap getProfileImage() {
        return profileImage;
    }

    public String getName() {
        return name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getDate() {
        return date;
    }
}
