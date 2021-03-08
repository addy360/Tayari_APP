package com.lockminds.tayari.responses;

public class FCMResponse {
    Boolean success, failure;

    public FCMResponse(boolean success, boolean failure) {
        this.success = success;
        this.failure = failure;
    }

    public FCMResponse() {
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean getFailure() {
        return failure;
    }

    public void setFailure(boolean failure) {
        this.failure = failure;
    }
}
