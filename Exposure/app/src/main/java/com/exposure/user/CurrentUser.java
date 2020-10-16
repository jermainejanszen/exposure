package com.exposure.user;

import java.util.ArrayList;

public class CurrentUser extends User {

    private ArrayList<OtherUser> connections = null;

    public CurrentUser(String uid) {
        super(uid);
    }

    /* Getters */
    public ArrayList<OtherUser> getConnections() {
        if(null == connections) {
            // TODO: Load connections from Firebase
        }
        return connections;
    }

}
