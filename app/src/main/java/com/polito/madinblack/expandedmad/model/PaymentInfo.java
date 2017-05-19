package com.polito.madinblack.expandedmad.model;

/**
 * Created by Ale on 12/05/2017.
 */

public class PaymentInfo {
    private String userFirebaseId;
    private String userPhoneNumber;
    private Double paidNow;
    private Double balance;
    private Double paidBefore;
    private String id;
    private String userNameDisplayed;
    private Double toPaid;


    public PaymentInfo(){}


    public PaymentInfo(PaymentFirebase paymentFirebase, Double paidNow){
        this.userFirebaseId     = paymentFirebase.getUserFirebaseId();
        this.userPhoneNumber    = paymentFirebase.getUserPhoneNumber();
        this.paidNow            = paidNow;
        this.balance            = paymentFirebase.getBalance();
        this.paidBefore         = paymentFirebase.getPaid();
        this.userNameDisplayed  = paymentFirebase.getUserFullName();
        this.toPaid             = paymentFirebase.getToPaid();
        this.id                 = paymentFirebase.getId();

    }


    public String getUserFirebaseId() {
        return userFirebaseId;
    }

    public void setUserFirebaseId(String userFirebaseId) {
        this.userFirebaseId = userFirebaseId;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }


    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getPaidNow() {
        return paidNow;
    }

    public void setPaidNow(Double paidNow) {
        this.paidNow = paidNow;
    }

    public Double getPaidBefore() {
        return paidBefore;
    }

    public void setPaidBefore(Double paidBefore) {
        this.paidBefore = paidBefore;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserNameDisplayed() {
        return userNameDisplayed;
    }

    public void setUserNameDisplayed(String userNameDisplayed) {
        this.userNameDisplayed = userNameDisplayed;
    }

    public Double getToPaid() {
        return toPaid;
    }

    public void setToPaid(Double toPaid) {
        this.toPaid = toPaid;
    }
}
