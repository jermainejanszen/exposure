package com.exposure.adapters;

import android.graphics.Bitmap;

public class MapListItem {

    private String uid;
    private Bitmap profileImage;
    private String name;

    public MapListItem(String uid) {
        this.uid = uid;
        this.profileImage = null;
        this.name = "First";
    }

    public Bitmap getProfileImage() {
        return profileImage;
    }

    public String getName() {
        return name;
    }

}
