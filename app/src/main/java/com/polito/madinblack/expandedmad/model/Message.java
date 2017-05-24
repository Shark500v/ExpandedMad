package com.polito.madinblack.expandedmad.model;

import java.util.Date;

public class Message {

    private String sentByName;
    private String sentById;
    private String message;
    private Date date;

    public Message() {
    }

    public Message(String sentByName, String sentById, String message){
        this.sentByName = sentByName;
        this.sentById = sentById;
        this.message = message;
        date = new Date();
    }

    public String getSentByName() {
        return sentByName;
    }

    public void setSentByName(String sentByName) {
        this.sentByName = sentByName;
    }

    public String getSentById() {
        return sentById;
    }

    public void setSentById(String sentById) {
        this.sentById = sentById;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
