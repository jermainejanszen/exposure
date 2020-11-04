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

    //TODO
    public ConnectionItem getConnection(String uid) {
        for (ConnectionItem connection : connections) {
            if (0 == connection.getUid().compareTo(uid)) {
                return connection;
            }
        }
        return null;
    }

    public void setConnections(List<ConnectionItem> connections) {
        this.connections = connections;
    }

    public void addConnection(ConnectionItem connectionItem) {
        ConnectionItem connection = getConnection(connectionItem.getUid());
        if (null != connection) {
            connections.remove(connection);
        }
        this.connections.add(connectionItem);
    }

    /**
     * Checks to see whether the current user has connected with another user
     * @param otherUid the user id of the other user
     * @return true if already connected, else false
     */
    public boolean isConnected(String otherUid) {
        return null != getConnection(otherUid);
    }

}
