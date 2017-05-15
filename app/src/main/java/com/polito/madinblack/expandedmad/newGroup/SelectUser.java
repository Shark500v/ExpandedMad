package com.polito.madinblack.expandedmad.newGroup;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

//used to save the contant values each time I retrive a contact
public class SelectUser implements Serializable{
    String name;

    public Bitmap getThumb() {
        //return thumb;
        if (byteImg != null){
            return BitmapFactory.decodeByteArray(byteImg, 0, byteImg.length);
        }
        return null;
    }

    public void setThumb(Bitmap img) throws IOException {
        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG, 100, blob);
        byteImg = blob.toByteArray();
        //thumb = img;
    }

    //Bitmap thumb;
    byte[] byteImg = null;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    String phone;

    public Boolean getCheckedBox() {
        return checkedBox;
    }

    public void setCheckedBox(Boolean checkedBox) {
        this.checkedBox = checkedBox;
    }

    Boolean checkedBox = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    String firebaseId;

    public String getFirebaseId() { return firebaseId;  }

    public void setFirebaseId(String firebaseId) {  this.firebaseId = firebaseId;   }
}