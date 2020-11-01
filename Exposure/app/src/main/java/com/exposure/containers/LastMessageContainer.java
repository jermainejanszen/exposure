package com.exposure.containers;

/**
 * Container used to store details of the most recent message sent between two users
 */
public class LastMessageContainer {

    private String message;
    private Long time;

    /**
     * Retrieve the content of the most recent message sent between two users
     * @return the content of the most recent message sent between two users
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the time the message was retrieved
     * @return the time the message was retrieved
     */
    public Long getTime() {
        return time;
    }

    /**
     * Sets the content of the most recent message sent between two users
     * @param message the content of the most recent message sent between two users
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Sets the time the most recent message between two users was sent
     * @param time the time the most recent message between two users was sent 
     */
    public void setTime(Long time) {
        this.time = time;
    }
}
