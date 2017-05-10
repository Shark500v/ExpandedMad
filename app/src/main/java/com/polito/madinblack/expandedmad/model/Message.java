package com.polito.madinblack.expandedmad.model;

import java.util.Date;

public class Message {

    private String sentBy;
    private String message;
    private Date date;

    public Message() {
    }

    public Message(String sentBy, String message){
        this.sentBy = sentBy;
        this.message = message;
    }

    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
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
