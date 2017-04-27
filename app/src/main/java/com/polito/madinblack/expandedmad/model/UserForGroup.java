package com.polito.madinblack.expandedmad.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserForGroup {

    private String name;
    private String surname;
    private String id;
    private List<Balance> balances = new ArrayList<>();

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

    public List<Balance> getBalances() {
        return balances;
    }

    public void setBalances(List<Balance> balances) {
        this.balances = balances;
    }

}
