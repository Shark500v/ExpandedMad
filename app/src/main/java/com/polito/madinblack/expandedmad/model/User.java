package com.polito.madinblack.expandedmad.model;


import com.google.firebase.database.DatabaseReference;
import android.graphics.Bitmap;
import java.util.HashMap;
import java.util.Map;

public class User {

    private String name;
    private String surname;
    private String firebaseId;
    private String phoneNumber;
    private String id;
    private String email;
    private String urlImage;

    /*all groups for user*/
    private Map<String, Boolean> groups = new HashMap<>();

    public User(){

    }

    public User(String name, String surname, String phoneNumber, String email){
        this.name        = name;
        this.surname     = surname;
        this.phoneNumber = phoneNumber;
        this.email       = email;
        this.id          = phoneNumber;
    }

    public static void writeNewUser(DatabaseReference mDatabase, String firebaseId, String name, String surname, String phoneNumber, String email) {
        User user = new User(name, surname, phoneNumber, email);
        user.setFirebaseId(firebaseId);
        mDatabase.child("users/"+phoneNumber+"/"+firebaseId).setValue(user);
        mDatabase.child("registration/"+firebaseId).setValue(phoneNumber);
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

    public String getFirebaseId() { return firebaseId; }

    public void setFirebaseId(String firebaseId) { this.firebaseId = firebaseId; }

    public String getPhoneNumber() { return phoneNumber; }

    public void setPhoneNumber(String telephone) { this.phoneNumber = phoneNumber; }

    public String getId() { return id; }

    public String toString(){
        return this.getName() + " " + this.getSurname();
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }
}
