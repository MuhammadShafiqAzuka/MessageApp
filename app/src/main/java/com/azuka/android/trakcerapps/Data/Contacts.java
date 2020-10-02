package com.azuka.android.trakcerapps.Data;

public class Contacts {
    private String name, status, image, email, device_token;

    public Contacts() {
    }

    public Contacts(String name, String status, String image, String email, String device_token) {
        this.name = name;
        this.status = status;
        this.image = image;
        this.email = email;
        this.device_token = device_token;
    }

    public Contacts(String messageSenderID, int ic_launcher, String image, String new_message, String messageSenderID1) {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }
}
