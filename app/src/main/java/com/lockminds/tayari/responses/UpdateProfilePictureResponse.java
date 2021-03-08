package com.lockminds.tayari.responses;

public class UpdateProfilePictureResponse {

    String status, message, error, picture;

    public UpdateProfilePictureResponse() {
    }

    public UpdateProfilePictureResponse(String picture,String status, String message, String error) {
        this.status = status;
        this.message = message;
        this.error = error;
        this.picture =picture;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
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
