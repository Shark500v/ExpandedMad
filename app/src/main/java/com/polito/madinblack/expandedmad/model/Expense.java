package com.polito.madinblack.expandedmad.model;

import java.util.Map;

enum State{constest, accepted}

//ne creo una nuova con le regole date dal layoute che stiamo segurendo

public class Expense {
    private String name;
    private String tag;
    private float cost;
    private String description;
    private String currency;
    private State state;
    //a map showing for each user the cost of the expense, maybe to change
    private Map<User,Float> userCost;

    public Expense(String name, String tag, float cost){
        this.name = name;
        this.tag = tag;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Map<User, Float> getUserCost() {
        return userCost;
    }

    public void setUserCost(Map<User, Float> userCost) {
        this.userCost = userCost;
    }
}
