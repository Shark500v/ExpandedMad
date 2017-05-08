package com.polito.madinblack.expandedmad.model;



public class Balance {

    private String userPhoneNumber;
    private String userName;
    private String userSurname;
    private Double balance;
    private String currencyName;
    private String currencySymbol;

    public Balance(){


    }

    public Balance(String userPhoneNumber, String userName, String userSurname, Double balance, String currencyName, String currencySymbol) {
        this.userPhoneNumber = userPhoneNumber;
        this.userName = userName;
        this.userSurname = userSurname;
        this.balance = balance;
        this.currencyName = currencyName;
        this.currencySymbol = currencySymbol;
    }

    public String getUserPhoneNumber() { return userPhoneNumber; }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserSurname() {
        return userSurname;
    }

    public void setUserSurname(String userSurname) {
        this.userSurname = userSurname;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
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
