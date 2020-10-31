package com.exposure.user;

import java.util.List;

public class ConnectionItem {

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
