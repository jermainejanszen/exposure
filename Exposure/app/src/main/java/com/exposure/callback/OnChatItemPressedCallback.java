package com.exposure.callback;

import android.graphics.Bitmap;

import java.io.Serializable;

public interface OnChatItemPressedCallback extends Serializable {

    void onPress(String uid, String name, Bitmap profileImage);

}
