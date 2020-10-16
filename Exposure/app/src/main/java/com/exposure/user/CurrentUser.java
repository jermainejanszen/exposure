package com.exposure.user;

import java.util.ArrayList;

/**
 * Holds information about the current user including the other users they
 * have connections with.
 */
public class CurrentUser extends User {

    private ArrayList<OtherUser> connections = new ArrayList<>();

    public CurrentUser(String uid) {
        super(uid);

        // TODO: Load connections from Firebase
    }

    /* Getters */
    public ArrayList<OtherUser> getConnections() {
        return connections;
    }

}
