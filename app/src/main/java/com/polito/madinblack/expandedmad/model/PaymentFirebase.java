package com.polito.madinblack.expandedmad.model;

public class PaymentFirebase {
    private String userId;
    private Expense expense;
    private Double paid;
    private Double toPaid;

    //costruttore per il database
    public PaymentFirebase(){

    }

    public PaymentFirebase(String userId, Expense expense, Double paid, Double toPaid){
        this.userId     = userId;
        this.expense    = expense;
        this.paid       = paid;
        this.toPaid     = CostUtil.round(toPaid, 2);
    }

    public String getUser() {
        return userId;
    }

    public void setUser(String userId) {
        this.userId = userId;
    }

    public Expense getExpense() {
        return expense;
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
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
