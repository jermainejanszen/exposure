package com.exposure.user;

import java.util.HashMap;
import java.util.Map;

public class OtherUser extends User {

    private final Map<UserField, Boolean> exposedInfo = new HashMap<UserField, Boolean>();

    public OtherUser(String uid) {
        super(uid);

        for(UserField field : UserField.values()) {
            // TODO: Load exposed info from Firebase
            this.exposedInfo.put(field, false);
        }
    }

    /**
     * Checks whether a given detail is exposed to the user.
     * @param field The detail to check.
     * @return True if the detail is exposed, otherwise false.
     */
    public boolean checkDetailExposed(UserField field) {
        if(null == this.exposedInfo.get(field)) {
            return false;
        } else {
            return this.exposedInfo.get(field);
        }
    }

    /**
     * Exposes the given field.
     * @param field Field to expose.
     */
    public void exposeDetail(UserField field) {
        this.exposedInfo.put(field, true);
    }

    /**
     * Hides the given field.
     * @param field Field to hide.
     */
    public void hideDetail(UserField field) {
        this.exposedInfo.put(field, false);
    }

}
