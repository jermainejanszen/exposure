package com.exposure.user;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds information about the current user including the other users they
 * have connections with.
 */
public class CurrentUser extends User {

   private List<ConnectionItem> connections = new ArrayList<>();

    /**
     * The current user
     * @param uid the id of the current user
     */
    public CurrentUser(String uid) {
        super(uid);
    }

    /**
     * Returns all the connections that the current user has accrued
     * @return all the connections that the current user has accrued
     */
    public List<ConnectionItem> getConnections() {
        return connections;
    }

    /**
     * Sets the connections for the current user
     * @param connections the connections to set for the current user
     */
    public void setConnections(List<ConnectionItem> connections) {
        this.connections = connections;
    }

    /**
     * Adds a connection to the list of the current user's current connections
     * @param uid the user id of the new connection
     */
    public void addConnection(String uid) {
        if (!isConnected(uid)) {
            this.connections.add(new ConnectionItem(uid, new ArrayList<String>()));
        }
    }

    /**
     * Checks to see whether the current user has connected with another user
     * @param otherUid the user id of the other user
     * @return true if already connected, else false
     */
    public boolean isConnected(String otherUid) {
        for (ConnectionItem connection : this.connections) {
            if (connection.getUid().equals(otherUid)) {
                return true;
            }
        }
        return false;
    }

}
