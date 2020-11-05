package com.exposure.callback;

import android.graphics.Bitmap;
import java.io.Serializable;

/**
 * Callback interface for handling the information of a chat item clicked on by the current user
 */
public interface OnChatItemPressedCallback extends Serializable {

    /**
     * Defines behaviour when pressing an item in the chats list.
     * @param uid uid of the pressed user
     * @param name name of the pressed user
     * @param profileImage profile image of the pressed user
     */
    void onPress(String uid, String name, Bitmap profileImage);
}
