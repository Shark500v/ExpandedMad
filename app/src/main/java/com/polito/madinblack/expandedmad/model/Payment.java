package com.polito.madinblack.expandedmad.model;

public class Payment {

    private User user;
    private Expense expense;
    private Float paid;
    private Float toPaid;
    private int weight;
    private boolean isWeightEnabled;
    private boolean isModified;

    //costruttore per il database
    public Payment(){}

    public Payment(User user, Expense expense, Float paid, Float toPaid){
        this.user               = user;
        this.expense            = expense;
        this.paid               = paid;
        this.toPaid             = CostUtil.round(toPaid, 2);
        this.weight             = 1;
        this.isWeightEnabled    = false;
        this.isModified         = false;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }



    public Expense getExpense() {
        return expense;
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
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

        this.toPaid = CostUtil.round(toPaid, 2);
    }

    public Float getDebit(){


        if(paid < toPaid)
            return (toPaid-paid);
        else
            return 0f;
    }

    public Float getCredit(){
        if(paid > toPaid)
            return (paid-toPaid);
        else
            return 0f;

    }

    public Float getBalance(){
        return (paid-toPaid);
    }


    @Override
    public String toString() {

        return String.format("%.2f",(paid))+"/"+String.format("%.2f",(toPaid));
    }




}
