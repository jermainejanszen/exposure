package com.exposure.callback;

import java.io.Serializable;

public interface OnItemPressedCallback extends Serializable {

    void onPress(String uid);

}
