package com.exposure.callback;

import com.exposure.user.UserField;

public interface DialogCallback {
    void send(UserField userField, String fieldValue);
}
