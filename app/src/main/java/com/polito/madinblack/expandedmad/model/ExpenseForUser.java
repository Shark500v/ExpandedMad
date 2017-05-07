package com.polito.madinblack.expandedmad.model;


public class ExpenseForUser {
    private String name;
    private String paidByName;
    private String paidBySurname;
    private String paidByFirebaseId;
    private String paidByPhoneNumber;
    private String id;
    private Double myBalance;
    private String  currencyName;
    private String  currencySymbol;


    public ExpenseForUser(){

    }

    public ExpenseForUser(Expense expense, Double myBalance){
        this.name               = expense.getName();
        this.myBalance          = myBalance;
        this.paidByName         = expense.getPaidByName();
        this.paidBySurname      = expense.getPaidBySurname();
        this.paidByFirebaseId   = expense.getPaidByFirebaseId();
        this.paidByPhoneNumber  = expense.getPaidByPhoneNumber();
        this.currencyName       = expense.getCurrencyName();
        this.currencySymbol     = expense.getCurrencySymbol();
        this.id                 = expense.getId();
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




    public String getPaidByName() {
        return paidByName;
    }

    public void setPaidByName(String paidByName) {
        this.paidByName = paidByName;
    }



    public String getPaidBySurname() {
        return paidBySurname;
    }

    public void setPaidBySurname(String paidBySurname) {
        this.paidBySurname = paidBySurname;
    }


    public String getPaidByFirebaseId() {
        return paidByFirebaseId;
    }

    public void setPaidByFirebaseId(String paidByFirebaseId) {
        this.paidByFirebaseId = paidByFirebaseId;
    }

    public String getPaidByPhoneNumber() {
        return paidByPhoneNumber;
    }

    public void setPaidByPhoneNumber(String paidByPhoneNumber) {
        this.paidByPhoneNumber = paidByPhoneNumber;
    }

    public Double getMyBalance() {
        return myBalance;
    }

    public void setMyBalance(Double myBalance) {
        this.myBalance = myBalance;
    }




    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }




    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }


}
