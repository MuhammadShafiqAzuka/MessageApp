package com.azuka.android.trakcerapps.Data;

public class Messages {
    private String date, message, sender, time, type, receiver, messageID;

    public Messages(String date, String message, String sender, String time, String type, String receiver, String messageID) {
        this.date = date;
        this.message = message;
        this.sender = sender;
        this.time = time;
        this.type = type;
        this.receiver = receiver;
        this.messageID = messageID;
    }
    public Messages() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }
}

