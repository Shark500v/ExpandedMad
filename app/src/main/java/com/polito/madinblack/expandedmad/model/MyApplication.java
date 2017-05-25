package com.polito.madinblack.expandedmad.model;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MyApplication {


    private static String userPhoneNumber;
    private static String firebaseId;
    private static String userName;
    private static String userSurname;
    private static String userEmail;
    private static Currency.CurrencyISO currencyISOFavorite = Currency.CurrencyISO.EUR;




    public static String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public static void setUserPhoneNumber(String userPhoneNumber) {
        MyApplication.userPhoneNumber = userPhoneNumber;
    }

    public static  String getFirebaseId() {
        return firebaseId;
    }

    public static  void setFirebaseId(String firebaseId) {
        MyApplication.firebaseId = firebaseId;
    }

    public static  String getUserName() {
        return userName;
    }

    public static  void setUserName(String userName) {
        MyApplication.userName = userName;
    }

    public static  String getUserSurname() {
        return userSurname;
    }

    public static  void setUserSurname(String userSurname) {
        MyApplication.userSurname = userSurname;
    }

    public static  String getUserEmail() {
        return userEmail;
    }

    public static  void setUserEmail(String userEmail) {
        MyApplication.userEmail = userEmail;
    }


    public static Currency.CurrencyISO getCurrencyISOFavorite() {
        return currencyISOFavorite;
    }

    public static void setCurrencyISOFavorite(Currency.CurrencyISO currencyISOFavorite) {
        MyApplication.currencyISOFavorite = currencyISOFavorite;
    }

    public static boolean isVariablesAvailable(){
        return (userPhoneNumber!=null && firebaseId!=null && !userPhoneNumber.isEmpty() && !firebaseId.isEmpty());
    }



}
