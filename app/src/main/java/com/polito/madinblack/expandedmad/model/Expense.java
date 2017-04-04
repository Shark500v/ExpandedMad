package com.polito.madinblack.expandedmad.model;

import java.util.HashMap;
import java.util.Map;


public class Expense {

    public enum State{CONTEST, ACCEPTED}
    public enum Tag{FOOD, WATER_BILL, GAS_BILL, LIGHT_BILL, OTHER}
    public enum Currency{YEN, EURO, DOLLAR, GBP}

    private String name;
    private Tag tag;
    private float cost;
    private String description;
    private Currency currency;
    private State state;
    private Group group;



    private Long id;


    private static long counter = 0;


    //list of all user partecipating to the Expensive
    private Map<Long, User> users = new HashMap<>();

    //a map showing for each user the cost of the Payment
    private Map<Long, Payment> userCost = new HashMap<>();


    public Expense(String name, Tag tag, float cost, Group group){
        this.name = name;
        this.tag = tag;
        this.cost = cost;
        this.group = group;

    }

    public void addUser(User u){

        users.put(u.getId(), u);

    }

    public void addUserCost(User u, Payment payment){
        userCost.put(u.getId(), payment);

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
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

    public Currency getCurrency() {
        return currency;
    }

    /*to modify in order to change cost accordly to new currency*/
    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Long getId() {
        return id;
    }


}
