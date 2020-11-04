package com.exposure.user;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to store information about other users that the current user may have a connection with
 */
public class OtherUser extends User {

    private List<String> exposedInfo;

    /**
     * Constructor for the other user
     * @param uid
     */
    public OtherUser(String uid) {
        super(uid);
        exposedInfo = new ArrayList<>();
        exposedInfo.add(UserField.NICKNAME.toString());
        exposedInfo.add(UserField.BIRTHDAY.toString());
        exposedInfo.add(UserField.PREFERENCES.toString());
        exposedInfo.add(UserField.PROFILE_IMAGE.toString());
    }

    public OtherUser(ConnectionItem connectionItem) {
        super(connectionItem.getUid());
        this.exposedInfo = connectionItem.getExposedInfo();
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
    public ConnectionItem exposeDetail(UserField field) {
        if(!checkDetailExposed(field)) {
            this.exposedInfo.add(field.toString());
        }
        return this.toConnectionItem();
    }

    /**
     * Creates new connection item for this other user
     * @return the connection item
     */
    public ConnectionItem toConnectionItem() {
        return new ConnectionItem(this.getUid(), exposedInfo);
    }

}
