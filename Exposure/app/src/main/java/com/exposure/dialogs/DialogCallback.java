package com.exposure.dialogs;

import com.exposure.user.UserField;

public interface DialogCallback {
    void send(UserField userField, String fieldValue);
}
