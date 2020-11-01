package com.exposure.callback;

import java.io.Serializable;

public interface OnMapItemPressedCallback extends Serializable {

    void onPress(String uid);

}
