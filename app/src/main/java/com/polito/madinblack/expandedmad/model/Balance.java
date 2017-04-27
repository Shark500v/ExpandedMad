package com.polito.madinblack.expandedmad.model;



public class Balance {

    private String Usaername;
    private String UserId;
    private String Surname;
    private Double Balance;

    public Balance(String usaername, String surname, String userId, Double balance) {
        Usaername = usaername;
        UserId = userId;
        Balance = balance;
        Surname = surname;
    }

    public String getUsaername() {
        return Usaername;
    }

    public void setUsaername(String usaername) {
        Usaername = usaername;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public Double getBalance() {
        return Balance;
    }

    public void setBalance(Double balance) {
        Balance = balance;
    }

    public String getSurname() {
        return Surname;
    }

    public void setSurname(String surname) {
        Surname = surname;
    }
}
