package com.exposure.user;

import java.util.List;

public class ConnectionItem {

    private String uid;
    private List<String> exposedInfo;

    public ConnectionItem(String uid, List<String> exposedInfo){
        this.uid = uid;
        this.exposedInfo = exposedInfo;
    }

    public void setExposedInfo(List<String> exposedInfo){
        this.exposedInfo = exposedInfo;
    }

    public void addExposedField(String exposedField){
        this.exposedInfo.add(exposedField);
    }

    public List<String> getExposedInfo(){
        return exposedInfo;
    }

    public String getUid(){
        return uid;
    }
}
