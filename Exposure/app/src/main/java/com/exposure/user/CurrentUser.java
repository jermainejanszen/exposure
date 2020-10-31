package com.exposure.user;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds information about the current user including the other users they
 * have connections with.
 */
public class CurrentUser extends User {

   private List<ConnectionItem> connections = new ArrayList<>();

    public CurrentUser(String uid) {
        super(uid);
    }

    /* Getters */
    public List<ConnectionItem> getConnections() {
        return connections;
    }

    /* Setters */
    public void setConnections(List<ConnectionItem> connections) {
        this.connections = connections;
    }

}
