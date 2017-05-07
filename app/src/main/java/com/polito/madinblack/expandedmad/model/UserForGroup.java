package com.polito.madinblack.expandedmad.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserForGroup {

    private String phoneNumber;
    private String name;
    private String surname;
    private String firebaseId;
    private Map<String, Balance> balances = new HashMap<>();

    public UserForGroup(){

    }

    public UserForGroup(User user){
        this.name        = user.getName();
        this.surname     = user.getSurname();
        this.firebaseId  = user.getFirebaseId();
        this.phoneNumber = user.getPhoneNumber();

    }

    public UserForGroup(String phoneNumber, String firebaseId, String name, String surname){
        this.name        = name;
        this.surname     = surname;
        this.phoneNumber  = phoneNumber;
        this.firebaseId = firebaseId;
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


    public String getPhoneNumber() { return phoneNumber; }

    public Map<String, Balance> getBalances() {
        return balances;
    }

    public void setBalances(Map<String, Balance> balances) {
        this.balances = balances;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public void initializeBalance(List<UserForGroup> usersForGroup){

        for(UserForGroup userForGroup : usersForGroup){
            if(!userForGroup.getFirebaseId().equals(this.getFirebaseId())){
                Balance balance = new Balance(userForGroup.getFirebaseId(), userForGroup.getName(), userForGroup.getSurname(), 0D);
                balances.put(userForGroup.getFirebaseId(), balance);

            }

        }

    }
    //add new user to balance with 0
    public void connect(UserForGroup userForGroup){
        Balance balance = new Balance(userForGroup.getFirebaseId(), userForGroup.getName(), userForGroup.getSurname(), 0D);
        if(!balances.containsKey(userForGroup.getFirebaseId()))
            balances.put(userForGroup.getFirebaseId(), balance);
    }




}
