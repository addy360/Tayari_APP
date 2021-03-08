package com.lockminds.tayari.model;

public class FirebaseUserModel {
    String display_name,photo_url,email,password,phonenumber;

    public FirebaseUserModel() {
    }

    public FirebaseUserModel(String password, String email, String phonenumber, String display_name, String photo_url) {
        this.display_name = display_name;
        this.photo_url = photo_url;
        this.password = password;
        this.phonenumber =phonenumber;
        this.email =email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }
}
