package com.polito.madinblack.expandedmad.model;

import android.graphics.drawable.PaintDrawable;

/**
 * Created by Ale on 04/04/2017.
 */

public class Payment {

    private User user;
    private Expense expense;
    private Float paid;
    private Float toPaid;
    private int weight;
    private float cost;


    public Payment(User user, Expense expense, Float paid, Float toPaid){
        this.user    = user;
        this.expense = expense;
        this.paid    = paid;
        this.toPaid  = toPaid;
        this.weight  = 0;
        if(this.expense!=null)
            cost=this.expense.getCost();



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

    public void setCost(float cost){
        if(expense!=null) {
            expense.setCost(cost);
        }
        this.cost = cost;

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

    public void setWeight (int weight){

        this.weight = weight;

    }

    public float getCost() {
        return cost;
    }


    @Override
    public String toString() {

        return String.format("%.2f",(paid))+"/"+String.format("%.2f",(toPaid));
    }



}
