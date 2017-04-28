package com.polito.madinblack.expandedmad.model;



public class Balance {

    private String userId;
    private String userName;
    private String userSurname;
    private Double balance;

    public Balance(){


    }

    public Balance(String userId, String userName, String userSurname, Double balance) {
        this.userName = userName;
        this.userId = userId;
        this.userSurname = userSurname;
        this.balance = balance;
    }

    public String getUserId() {return userId;}

    public void setUserId(String userId) {
        this.userId = userId;
    }

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
}
