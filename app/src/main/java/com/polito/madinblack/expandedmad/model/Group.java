package com.polito.madinblack.expandedmad.model;

import java.util.HashMap;
import java.util.Map;


public class Group {

    private String name;
    private Long id;

    private static long counter = 0;
    //add image attribute

    private Map<Long, User>users = new HashMap<>();
    private Map<Long, Expense>expenses = new HashMap<>();

    public Group(String name){
        this.name = name;
        this.id = counter++;


    }

    public void addExpense(Expense e){
        expenses.put(e.getId(), e);



    }

    public void addUser(User user){
        users.put(user.getId(), user);


    }

    public Long getId(){
        return id;

    }

    public String getName(){ return name; }



}
