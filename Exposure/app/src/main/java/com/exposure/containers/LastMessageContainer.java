package com.exposure.containers;

public class LastMessageContainer {

    private String message;
    private Long time;

    public String getMessage() {
        return message;
    }

    public Long getTime() {
        return time;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
