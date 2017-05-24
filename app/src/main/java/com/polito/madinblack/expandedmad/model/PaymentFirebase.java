package com.polito.madinblack.expandedmad.model;

import java.util.Locale;

public class PaymentFirebase {
    private String id;
    private String userFirebaseId;
    private String userPhoneNumber;
    private String userFullName;
    private Double paid;
    private Double toPay;
    private String sortingField;
    private Double newToPay;
    private String motivation;

    //costruttore per il database
    public PaymentFirebase(){

    }

    public PaymentFirebase(Payment payment){
        this.userPhoneNumber    = payment.getUserPhoneNumber();
        this.userFirebaseId     = payment.getUserFirebaseId();
        this.paid               = CostUtil.round(payment.getPaid(), 2);
        this.toPay             = CostUtil.round(payment.getToPay(), 2);
        this.userFullName       = payment.getUserFullName();

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

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getPaid() {
        return paid;
    }

    public void setPaid(Double paid) {
        this.paid = CostUtil.round(paid,2);
    }

    public Double getToPay() {
        return toPay;
    }


    public void setToPay(Double toPay) {
        this.toPay = CostUtil.round(toPay,2);
    }

    public Double getDebit(){


        if(paid < toPay)
            return CostUtil.roundDown(toPay-paid, 2);
        else
            return 0d;
    }

    public Double getCredit(){
        if(paid > toPay)
            return CostUtil.roundDown(paid-toPay, 2);
        else
            return 0d;

    }

    public Double getBalance(){
        return CostUtil.roundDown((paid-toPay), 2);
    }

    public String getSortingField() {
        return sortingField;
    }

    public void setSortingField(String sortingField) {
        this.sortingField = sortingField;
    }

    public Double getNewToPay() {
        return newToPay;
    }

    public void setNewToPay(Double newToPay) {
        this.newToPay = newToPay;
    }

    public String getMotivation() {
        return motivation;
    }

    public void setMotivation(String motivation) {
        this.motivation = motivation;
    }

    @Override
    public String toString() {

        return String.format(Locale.getDefault(), "%.2f",(paid))+"/"+String.format(Locale.getDefault(), "%.2f",(toPay));
    }
}
