package com.lockminds.tayari.responses;

public class UserVerifyResponse {
    String status;

    public UserVerifyResponse() {
    }

    public UserVerifyResponse(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
