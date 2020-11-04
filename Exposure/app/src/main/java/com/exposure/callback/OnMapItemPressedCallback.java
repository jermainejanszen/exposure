package com.exposure.callback;

import java.io.Serializable;

/**
 * Callback interface for handling the case where a user map item is clicked on
 */
public interface OnMapItemPressedCallback extends Serializable {

    /**
     * Called upon clicking upon a map item
     * @param uid the uid corresponding to the user represented by the map item
     */
    void onPress(String uid);
}
