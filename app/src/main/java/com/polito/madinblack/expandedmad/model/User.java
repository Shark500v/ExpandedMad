package com.polito.madinblack.expandedmad.model;

import java.util.HashMap;
import java.util.Map;

public class User {

    private String name;
    private String surname;
    private float globalBalance;
    private static long counter = 1; /*counter to assign an id*/
    private final Long id;          //unique Id for each user

    /*all groups for user*/
    private Map<Long, Group>groups = new HashMap<>();


    public User(String name, String surname){
        this.name = name;
        this.surname = surname;
        this.id = counter++;
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

    public Long getId() { return id; }
}
