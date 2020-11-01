package com.exposure.callback;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Callback interface for handling the information of a chat item clicked on by the current user
 */
public interface OnChatItemPressedCallback extends Serializable {
    void onPress(String uid, String name, Bitmap profileImage);
}
