package com.polito.madinblack.expandedmad.model;

/**
 * Created by Ale on 04/04/2017.
 */

public class Payment {

    private User user;
    private Float paid;
    private Float toPaid;

    public Payment(User user, Float paid, Float toPaid){
        this.user   = user;
        this.paid   = paid;
        this.toPaid = toPaid;


    }



    public Float getPaid() {
        return paid;
    }

    public void setPaid(Float paid) {
        this.paid = paid;
    }

    public Float getToPaid() {
        return toPaid;
    }

    public void setToPaid(Float toPaid) {
        this.toPaid = toPaid;
    }



    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
