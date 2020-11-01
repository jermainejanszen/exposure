package com.exposure.callback;

import java.io.Serializable;

/**
 * Callback interface for handling the case where a user map item is clicked on
 */
public interface OnMapItemPressedCallback extends Serializable {
    void onPress(String uid);
}
