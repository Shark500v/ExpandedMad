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



    public PaymentInfo(String userFirebaseId, String userPhoneNumber, Double paidBefore, Double paidNow, Double balance) {
        this.userFirebaseId = userFirebaseId;
        this.userPhoneNumber = userPhoneNumber;
        this.paidNow = paidNow;
        this.balance = balance;
        this.paidBefore = paidBefore;
    }

    public PaymentInfo(PaymentFirebase paymentFirebase, Double paidNow){
        this.userFirebaseId = paymentFirebase.getUserFirebaseId();
        this.userPhoneNumber = paymentFirebase.getUserPhoneNumber();
        this.paidNow = paidNow;
        this.balance = paymentFirebase.getBalance();
        this.paidBefore = paymentFirebase.getPaid();
        this.userNameDisplayed = paymentFirebase.getUserNameDisplayed();

    }

    public PaymentInfo(PaymentInfo paymentInfo) {
        this.userFirebaseId = paymentInfo.getUserFirebaseId();
        this.userPhoneNumber = paymentInfo.getUserPhoneNumber();
        this.paidNow = paymentInfo.getPaidNow();
        this.balance = paymentInfo.getBalance();
        this.paidBefore = paymentInfo.getPaidBefore();
        this.id = paymentInfo.getId();
        this.userNameDisplayed = paymentInfo.getUserNameDisplayed();
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
}
