package com.polito.madinblack.expandedmad.model;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MyApplication {

    private static MyApplication myApplication = null;

    private static User myself;
    private static FirebaseUser firebaseUser;
    private static String userPhoneNumber;
    private static String firebaseId;
    private static String userName;
    private static String userSurname;
    private static String userEmail;
    private static Boolean logged = false;
    private static Boolean isPhone = false;
    private static GroupForUser groupForUser;
    private static Currency.CurrencyISO currencyISOFavorite = Currency.CurrencyISO.USD;


    /*groupId --> group*/
    private Map<Long, Group> groups = new LinkedHashMap<>();

    /*list of all users, can be seen as phonebook*/
    private Map<Long, User> users = new LinkedHashMap<>();

    private Map<String, String> imagesUrl = new LinkedHashMap<>();

    private DatabaseReference mDatabase;


    private MyApplication(){


    }

    public static MyApplication getInstance(){
        if(myApplication==null)
            myApplication = new MyApplication();
        return myApplication;
    }

    public void addGroup(Group g){
        /*groups.put(g.getId(), g);
    */
    }

    public List<Group> getGroup(){ return new ArrayList<Group>(groups.values()); }

    public void putImageurl (String firebaseId, String imageUrl){
        imagesUrl.put(firebaseId, imageUrl);
    }

    public String getImageUrl (String firebaseId){
        return imagesUrl.get(firebaseId);
    }

    public Group getSingleGroup(Long id) {
        return groups.get(id);
    }

    public User getMyself() {
        return myself;
    }

    public  void setMyself(User myself) {
        MyApplication.myself = myself;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        MyApplication.userPhoneNumber = userPhoneNumber;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        MyApplication.firebaseId = firebaseId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        MyApplication.userName = userName;
    }

    public String getUserSurname() {
        return userSurname;
    }

    public void setUserSurname(String userSurname) {
        MyApplication.userSurname = userSurname;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        MyApplication.userEmail = userEmail;
    }

    public Boolean getLogged() {
        return logged;
    }

    public void setLogged(Boolean logged) {
        MyApplication.logged = logged;
    }

    public Boolean getIsPhone() {
        return isPhone;
    }

    public void setIsPhone(Boolean isPhone) {
        MyApplication.isPhone = isPhone;
    }

    public FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    public void setFirebaseUser(FirebaseUser firebaseUser) {
        MyApplication.firebaseUser = firebaseUser;
    }

    public GroupForUser getGroupForUser() {
        return groupForUser;
    }

    public void setGroupForUser(GroupForUser groupForUser) {
        MyApplication.groupForUser = groupForUser;
    }

    public static Currency.CurrencyISO getCurrencyISOFavorite() {
        return currencyISOFavorite;
    }

    public static void setCurrencyISOFavorite(Currency.CurrencyISO currencyISOFavorite) {
        MyApplication.currencyISOFavorite = currencyISOFavorite;
    }
}
