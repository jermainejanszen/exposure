package com.exposure.user;

import java.io.Serializable;
import java.util.List;

/**
 * Represents a connection with another user and tracks how much the current user has exposed
 * about them
 */
public class ConnectionItem implements Serializable {

    private final String uid;
    private final List<String> exposedInfo;

    /**
     * Represents a connection with another user
     * @param uid the id of the user other
     * @param exposedInfo the information that has been exposed about them so far
     */
    public ConnectionItem(String uid, List<String> exposedInfo){
        this.uid = uid;
        this.exposedInfo = exposedInfo;
    }

    /**
     * Returns the user id of the other user that has been connected with
     * @return the user id of the other user that has been connected with
     */
    public String getUid() {
        return uid;
    }

    /**
     * Returns the fields of the profile that has been exposed to the current user for this match
     * @return the fields of the profile that has been exposed to the current user for this match
     */
    public List<String> getExposedInfo() {
        return exposedInfo;
    }

}
