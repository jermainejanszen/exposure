package com.exposure.user;

import java.io.Serializable;
import java.util.List;

public class ConnectionItem implements Serializable {

    private final String uid;
    private final List<String> exposedInfo;

    public ConnectionItem(String uid, List<String> exposedInfo){
        this.uid = uid;
        this.exposedInfo = exposedInfo;
    }

    public String getUid() {
        return uid;
    }

    public List<String> getExposedInfo() {
        return exposedInfo;
    }

}
