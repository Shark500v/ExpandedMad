package com.polito.madinblack.expandedmad.model;



public class Balance {

    private String userPhoneNumber;
    private String fullName;
    private Double balance;
    private Currency.CurrencyISO currencyISO;
    private String parentUserPhoneNumber;


    public Balance(){


    }

    public Balance(String userPhoneNumber, String userName, String userSurname, Double balance, Currency.CurrencyISO currencyISO) {
        this.userPhoneNumber = userPhoneNumber;
        this.fullName = userName + " " + userSurname;
        this.balance = CostUtil.round(balance, 2);
        this.currencyISO = currencyISO;
    }

    public String getUserPhoneNumber() { return userPhoneNumber; }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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
