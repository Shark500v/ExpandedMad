package com.polito.madinblack.expandedmad.model;

import java.util.HashMap;
import java.util.Map;

public class UserForGroup {

    private String name;
    private String surname;
    private String id;
    private Map<String, Double> balance = new HashMap<String, Double>();


    public UserForGroup(){

    }

    public UserForGroup(User user){
        this.name        = user.getName();
        this.surname     = user.getSurname();
        this.id          = user.getId();
    }

    public UserForGroup(String id, String name, String surname){
        this.name        = name;
        this.surname     = surname;
        this.id          = id;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Double> getBalance() {
        return balance;
    }

    public void setDebts(Map<String, Double> balance) {
        this.balance = balance;
    }

}
