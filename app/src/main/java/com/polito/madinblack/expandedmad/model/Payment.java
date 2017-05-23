package com.polito.madinblack.expandedmad.model;


import java.util.Locale;

public class Payment {

    private String userFirebaseId;
    private String userPhoneNumber;
    private String userFullName;
    private String expenseId;
    private Double paid;
    private Double toPay;
    private int weight;
    private boolean isWeightEnabled;
    private boolean isModified;

    public Payment(String userFirebaseId, String userPhoneNumber, String userName, String userSurname, String expenseId, Double paid, Double toPay){
        this.userFirebaseId     = userFirebaseId;
        this.userPhoneNumber    = userPhoneNumber;
        this.userFullName       = userName + " " + userSurname;
        this.expenseId          = expenseId;
        this.paid               = CostUtil.round(paid, 2);;
        this.toPay             = CostUtil.round(toPay, 2);
        this.weight             = 1;
        this.isWeightEnabled    = false;
        this.isModified         = false;
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



    public String getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(String expenseId) {
        this.expenseId = expenseId;
    }



    public int getWeight() { return weight; }

    public void setWeight(int weight) { this.weight = weight; }



    public boolean isWeightEnabled() {
        return isWeightEnabled;
    }

    public void setWeightEnabled(boolean weightEnabled) {
        isWeightEnabled = weightEnabled;
    }



    public boolean isModified() {
        return isModified;
    }

    public void setModified(boolean modified) {
        isModified = modified;
    }





    public Double getPaid() {
        return paid;
    }

    public void setPaid(Double paid) {
        this.paid = CostUtil.round(paid,2);
    }

    public double getToPay() {
        return toPay;
    }

    public void setToPay(double toPay) {

        this.toPay = CostUtil.round(toPay, 2);
    }


    public Double getDebit(){


        if(paid < toPay)
            return (toPay-paid);
        else
            return 0d;
    }

    public Double getCredit(){
        if(paid > toPay)
            return (paid-toPay);
        else
            return 0d;

    }

    public Double getBalance(){
        return (paid-toPay);
    }



    @Override
    public String toString() {

        return String.format(Locale.getDefault(), "%.2f",(paid))+"/"+String.format(Locale.getDefault(), "%.2f",(toPay));
    }




}
