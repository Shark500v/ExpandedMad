package com.polito.madinblack.expandedmad.model;



public class Balance {

    private String userPhoneNumber;
    private String userName;
    private String userSurname;
    private Double balance;
    private Currency.CurrencyISO currencyISO;
    private String parentUserPhoneNumber;

    public Balance(){


    }

    public Balance(String userPhoneNumber, String userName, String userSurname, Double balance, Currency.CurrencyISO currencyISO) {
        this.userPhoneNumber = userPhoneNumber;
        this.userName = userName;
        this.userSurname = userSurname;
        this.balance = CostUtil.round(balance, 2);
        this.currencyISO = currencyISO;
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
        this.balance = CostUtil.round(balance, 2);
    }


    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public Currency.CurrencyISO getCurrencyISO() {
        return currencyISO;
    }

    public void setCurrencyISO(Currency.CurrencyISO currencyISO) {
        this.currencyISO = currencyISO;
    }

    public String getParentUserPhoneNumber() {
        return parentUserPhoneNumber;
    }

    public void setParentUserPhoneNumber(String parentUserPhoneNumber) {
        this.parentUserPhoneNumber = parentUserPhoneNumber;
    }
}
