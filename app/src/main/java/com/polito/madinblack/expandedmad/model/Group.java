package com.polito.madinblack.expandedmad.model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class Group {

    private String name;
    private Long id;
    private Float totCredit;
    private Float totDebit;



    private static long counter = 0;
    //add image attribute

    private Map<Long, User>users = new HashMap<>();
    private Map<Long, Expense>expenses = new LinkedHashMap<>();

    public Group(String name){
        this.name = name;
        this.id = counter++;
        totCredit = 0F;
        totDebit = 0F;

    }

    public void addExpense(Expense e){
        expenses.put(e.getId(), e);
        totDebit += e.getPayment(MyApplication.myself.getId()).getDebit();
        totCredit += e.getPayment(MyApplication.myself.getId()).getCredit();

    }

    public void addUser(User user){
        users.put(user.getId(), user);
    }

    public List<User> getUsers(){ return new ArrayList<>(users.values());}



    public Long getId(){
        return id;
    }

    public Float getTotCredit() {
        return totCredit;
    }

    public Float getTotDebit() {
        return totDebit;
    }


    public int getSize(){ return expenses.size();}

    public String getName(){ return name; }



}
