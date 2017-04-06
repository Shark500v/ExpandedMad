package com.polito.madinblack.expandedmad.model;

/**
 * Created by Ale on 04/04/2017.
 */

public class Payment {

    private User user;
    private Expense expense;
    private Float paid;
    private Float toPaid;

    public Payment(User user, Expense expense, Float paid, Float toPaid){
        this.user    = user;
        this.expense = expense;
        this.paid    = paid;
        this.toPaid  = toPaid;


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
        if(paid > toPaid)
            return "+"+paid.toString()+"/"+toPaid.toString();
        else
            return paid.toString()+"/"+toPaid.toString();
    }




}
