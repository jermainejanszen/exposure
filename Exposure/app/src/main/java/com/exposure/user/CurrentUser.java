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
     * Gets a ConnectionItem for the given uid
     * @param uid uid of the user to get the ConnectionItem of
     * @return the ConnectionItem of the user with the given uid. Null if the current
     * user is not connected to the user with the given uid
     */
    public ConnectionItem getConnection(String uid) {
        for (ConnectionItem connection : connections) {
            if (0 == connection.getUid().compareTo(uid)) {
                return connection;
            }
        }
        return null;
    }

    /**
     * Sets the current user's connections
     * @param connections connections to set
     */
    public void setConnections(List<ConnectionItem> connections) {
        this.connections = connections;
    }

    /**
     * Adds the given connection if the current user is not already connected to the given user
     * @param connectionItem new ConnectionItem to add
     */
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
