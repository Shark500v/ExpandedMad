package com.polito.madinblack.expandedmad.model;

public class PaymentFirebase {
    private String id;
    private String userId;
    private String expenseId;
    private Double paid;
    private Double toPaid;

    //costruttore per il database
    public PaymentFirebase(){

    }

    public PaymentFirebase(Payment payment){
        this.userId     = payment.getUserId();
        this.expenseId  = payment.getExpenseId();
        this.paid       = CostUtil.round(payment.getPaid(), 2);
        this.toPaid     = CostUtil.round(payment.getToPaid(), 2);
    }

    public String getUser() {
        return userId;
    }

    public void setUser(String userId) {
        this.userId = userId;
    }

    public String getExpenseId() {
        return expenseId;
    }

    public void setExpense(String expenseId) {
        this.expenseId = expenseId;
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
