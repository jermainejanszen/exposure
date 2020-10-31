package com.exposure.user;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Used to store information about other users that the current user
 * may have a connection with.
 */
public class OtherUser extends User {

    private final Set<String> exposedInfo = new HashSet<>();

    /* Constructor */
    public OtherUser(String uid) {
        super(uid);

        // TODO: Load exposed info from Firebase
    }

    /**
     * Checks whether a given detail is exposed to the user.
     * @param field The detail to check.
     * @return True if the detail is exposed, otherwise false.
     */
    public boolean checkDetailExposed(UserField field) {
        return this.exposedInfo.contains(field.toString());
    }

    /**
     * Exposes the given field.
     * @param field Field to expose.
     */
    public void exposeDetail(UserField field) {
        this.exposedInfo.add(field.toString());
    }

    /**
     * Hides the given field.
     * @param field Field to hide.
     */
    public void hideDetail(UserField field) {
        this.exposedInfo.remove(field.toString());
    }

    public ConnectionItem toConnectionItem() {
        return new ConnectionItem(this.getUid(), exposedInfo);
    }

}
