package com.polito.madinblack.expandedmad.model;


public class ExpenseForUser {
    private String name;
    private String paidBy;
    private String id;
    private Double myBalance;


    public ExpenseForUser(){

    }

    public ExpenseForUser(Expense expense, Double myBalance){
        this.name       = expense.getName();
        this.myBalance  = myBalance;
        this.paidBy     = expense.getPaidBy();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }




    public String getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(String paidBy) {
        this.paidBy = paidBy;
    }




    public Double getMyBalance() {
        return myBalance;
    }

    public void setMyBalance(Double myBalance) {
        this.myBalance = myBalance;
    }



}
