package com.polito.madinblack.expandedmad.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Expense {


    public enum State{CONTEST, ACCEPTED}
    public enum Tag{FOOD, WATER_BILL, GAS_BILL, LIGHT_BILL, FLIGHT, HOTEL, FUEL, DRINK, OTHER}
    public enum Currency{YEN, EURO, DOLLAR, GBP}

    private String name;
    private Tag tag;
    private float cost;
    private String description;
    private Currency currency;
    private State state;
    private Group group;
    private User paying;
    private Long id;


    private int year;
    private int month;
    private int day;


    private static long counter = 1;

    //a map showing for each user the cost of the Payment
    private Map<String, Payment> userCost = new HashMap<>();

    public Expense(){

    }

    public Expense(String name, Tag tag, float cost, String description, Currency currency, Group group, User paying, int year, int month, int day){
        this.name        = name;
        this.tag         = tag;
        this.cost        = cost;
        this.description = description;
        this.currency    = currency;
        this.group       = group;
        this.paying      = paying;
        this.year        = year;
        this.month       = month;
        this.day         = day;
        this.id          = counter++;
        this.state       = State.ACCEPTED;

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

    public String dateToString(){
        return day+"/"+month+"/"+year;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public void setYear(int year) { this.year = year; }

    public void setMonth(int month) { this.month = month; }

    public void setDay(int day) { this.day = day; }

    public void setGroup(Group group) { this.group = group; }

    public void setPaying(User user) {this.paying = user; }


    public static int dateCompare(Expense e1, Expense e2){
        /*if(e1.getYear()>e2.getYear())
            return 1;
        else if(e1.getMonth())
    */
        return 1;
    }

    public Long getId() {
        return id;
    }

    public Payment getPayment(Long id){ return userCost.get(id); }

    public Double getMyBalance(){
        if(!userCost.containsKey(MyApplication.myself.getId()))
            return -777d;
        return userCost.get(MyApplication.myself.getId()).getBalance();
    }



    public User getPaying() {
        return paying;
    }

    public String toString(){
        return userCost.get(MyApplication.myself.getId()).toString();
    }

}
