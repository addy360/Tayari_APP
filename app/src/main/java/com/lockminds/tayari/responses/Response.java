package com.lockminds.tayari.responses;

public class Response {

    Boolean status;
    String uid;

    public Response() {
    }

    public Response(Boolean status, String uid) {
        this.status = status;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }


}
