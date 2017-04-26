package com.polito.madinblack.expandedmad.model;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Map;

public class User {

    private String name;
    private String surname;
    private String phoneNumber;
    private float globalBalance;
    private static long counter = 1;    /*counter to assign an id*/
    private final Long id;              //unique Id for each user
    Bitmap thumb;                       //used for the icon account

    /*all groups for user*/
    private Map<Long, Group>groups = new HashMap<>();

    //serve questo costruttore per il database (aggiunto anche setter e getter su phoneNumber)
    public User()
    {
        this.id = counter++;
        globalBalance = 0;
    }

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

    public String toString(){
        return this.getName() + " " + this.getSurname();
    }

    public String getPhoneNumber() { return phoneNumber; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Bitmap getThumb() {
        return thumb;
    }

    public void setThumb(Bitmap thumb) {
        this.thumb = thumb;
    }
}
