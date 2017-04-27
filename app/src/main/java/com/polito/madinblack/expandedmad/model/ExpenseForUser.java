package com.polito.madinblack.expandedmad.model;


public class ExpenseForUser {
    private String name;
    private String paidBy;
    private Long id;
    private Double myBalance;


    public ExpenseForUser(){

    }

    public ExpenseForUser(Expense expense){
        this.name       = expense.getName();
        this.myBalance  = expense.getMyBalance();
        this.paidBy     = expense.getPaying().getName();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
