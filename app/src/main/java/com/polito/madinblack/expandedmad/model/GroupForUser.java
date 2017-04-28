package com.polito.madinblack.expandedmad.model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ale on 26/04/2017.
 */

public class GroupForUser {


    private String  name;
    private String  id;
    private Long    size;
    private Long    newExpenses;


    public GroupForUser(){

    }

    public GroupForUser(Group group){
        this.name        = group.getName();
        this.size        = group.getSize();
        this.id          = group.getId();
        this.newExpenses = 0L;

    }



    public String getName(){ return name; }

    public String getId(){
        return id;
    }

    public Long   getSize(){ return size; }

    public Long   getNewExpenses() { return newExpenses; }


    public void   setName(String name) { this.name = name; }

    public void   setId(String id) { this.id = id; }

    public void   setSize(Long size) { this.size = size; }

    public void   setNewExpenses(Long newExpenses) { this.newExpenses = newExpenses; }







}
