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

    public ConnectionItem getConnection(String uid) {
        for (ConnectionItem connection : connections) {
            if (0 == connection.getUid().compareTo(uid)) {
                return connection;
            }
        }
        return null;
    }

    /* Setters */
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

    public boolean isConnected(String otherUid) {
        return null != getConnection(otherUid);
    }

}
