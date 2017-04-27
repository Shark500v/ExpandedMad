package com.polito.madinblack.expandedmad.model;

import java.util.HashMap;
import java.util.Map;

public class UserForGroup {

    private String name;
    private String surname;
    private String id;
    private Map<String, Double> debts = new HashMap<String, Double>();


    public UserForGroup(){

    }

    public UserForGroup(User user){
        this.name        = user.getName();
        this.surname     = user.getSurname();
        this.id          = user.getId();
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

    public Map<String, Double> getDebts() {
        return debts;
    }

    public void setDebts(Map<String, Double> debts) {
        this.debts = debts;
    }

}
