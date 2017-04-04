package com.polito.madinblack.expandedmad.model;

public class User {
    private String name;
    private String surname;
    private float globalBalance;

    public User(String name, String surname){
        this.name = name;
        this.surname = surname;
        globalBalance = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public float getGlobalBalance() {
        return globalBalance;
    }

    public void setGlobalBalance(float globalBalance) {
        this.globalBalance = globalBalance;
    }
}
