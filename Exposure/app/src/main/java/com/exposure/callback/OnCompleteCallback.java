package com.exposure.callback;

/**
 * Callback interface for handling the success or failure of a given operation
 */
public interface OnCompleteCallback {
    void update(boolean success, String message);
}
