package com.exposure.user;

import java.io.Serializable;
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

    public void addConnection(String uid) {
        if (!isConnected(uid)) {
            this.connections.add(new ConnectionItem(uid, new ArrayList<String>()));
        }
    }

    public boolean isConnected(String otherUid) {
        for (ConnectionItem connection : this.connections) {
            if (connection.getUid().equals(otherUid)) {
                return true;
            }
        }
        return false;
    }

}
