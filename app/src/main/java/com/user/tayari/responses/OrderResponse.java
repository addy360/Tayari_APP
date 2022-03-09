package com.user.tayari.responses;

import com.user.tayari.model.Order;

public class OrderResponse {

    Boolean status;
    String uid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;
    String message;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    Order order;
    public OrderResponse() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public OrderResponse(Boolean status, String uid, String message, String id, Order order) {
        this.status = status;
        this.message = message;
        this.uid = uid;
        this.id = id;
        this.order = order;
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
