package com.lockminds.tayari.responses;

public class UploadImageResponse {

    String image, status,thumbnail,error,message,ext,path,fullPath;

    public UploadImageResponse() {
    }

    public UploadImageResponse(String path, String fullPath,String image, String status, String thumbnail, String error, String message, String ext) {
        this.image = image;
        this.status = status;
        this.thumbnail = thumbnail;
        this.error = error;
        this.message = message;
        this.ext = ext;
        this.fullPath = fullPath;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }
}
