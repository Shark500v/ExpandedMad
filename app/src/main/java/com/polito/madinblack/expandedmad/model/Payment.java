package com.polito.madinblack.expandedmad.model;

import android.graphics.drawable.PaintDrawable;

public class Payment {

    private String userId;
    private String userName;
    private String expenseId;
    private Double paid;
    private double toPaid;
    private int weight;
    private boolean isWeightEnabled;
    private boolean isModified;

    public Payment(String userId, String userName, String expenseId, Double paid, Double toPaid){
        this.userId             = userId;
        this.userName           = userName;
        this.expenseId          = expenseId;
        this.paid               = paid;
        this.toPaid             = CostUtil.round(toPaid, 2);
        this.weight             = 1;
        this.isWeightEnabled    = false;
        this.isModified         = false;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
        this.paid = paid;
    }

    public double getToPaid() {
        return toPaid;
    }

    public void setToPaid(double toPaid) {

        this.toPaid = CostUtil.round(toPaid, 2);
    }

    public Double getDebit(){


        if(paid < toPaid)
            return (toPaid-paid);
        else
            return 0d;
    }

    public Double getCredit(){
        if(paid > toPaid)
            return (paid-toPaid);
        else
            return 0d;

    }

    public Double getBalance(){
        return (paid-toPaid);
    }


    @Override
    public String toString() {

        return String.format("%.2f",(paid))+"/"+String.format("%.2f",(toPaid));
    }




}
