package com.lockminds.tayari.responses;

public class UpdateResponse {

    String user_id,status, message, error;

    public UpdateResponse() {
    }

    public UpdateResponse(String user_id,String status, String message, String error) {
        this.user_id = user_id;
        this.status = status;
        this.message = message;
        this.error = error;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
