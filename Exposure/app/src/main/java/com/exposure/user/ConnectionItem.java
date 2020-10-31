package com.exposure.user;

import java.util.List;
import java.util.Set;

public class ConnectionItem {

    private String uid;
    private Set<String> exposedInfo;

    public ConnectionItem(String uid, Set<String> exposedInfo){
        this.uid = uid;
        this.exposedInfo = exposedInfo;
    }

    public void setExposedInfo(Set<String> exposedInfo){
        this.exposedInfo = exposedInfo;
    }

    public void addExposedField(String exposedField){
        this.exposedInfo.add(exposedField);
    }

    public Set<String> getExposedInfo(){
        return exposedInfo;
    }

    public String getUid(){
        return uid;
    }
}
