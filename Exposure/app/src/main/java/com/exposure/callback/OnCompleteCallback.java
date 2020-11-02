package com.exposure.callback;

/**
 * Callback interface for handling the success or failure of a given operation
 */
public interface OnCompleteCallback {

    /**
     * Notifies the calling class that the task has been executed
     * @param success indicates the success or failure of the task
     * @param message contains message regarding the task
     */
    void update(boolean success, String message);
}
