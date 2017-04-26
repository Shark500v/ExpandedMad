package com.polito.madinblack.expandedmad.model;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.polito.madinblack.expandedmad.GoogleSignInActivity;
import com.polito.madinblack.expandedmad.GoogleSignInActivity2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MyApplication {

    public static MyApplication myApplication = null;

    public static User myself;
    private static FirebaseUser firebaseUser;
    private static String userPhoneNumber;
    private static String firebaseId;
    private static String userName;
    private static String userSurname;
    private static String userEmail;
    private static Boolean logged;
    private static Boolean isPhone;




    /*groupId --> group*/
    private Map<Long, Group> groups = new LinkedHashMap<>();

    /*list of all users, can be seen as phonebook*/
    private Map<Long, User> users = new LinkedHashMap<>();

    private DatabaseReference mDatabase;






    private MyApplication(){









        /*
        myself = new User("Mario", "Rossi");
        int i;
        

        Group g1 = new Group("Group1");
        Group g2 = new Group("Group2");
        Group g3 = new Group("Group3");

        groups.put(g1.getId(), g1);
        groups.put(g2.getId(), g2);
        groups.put(g3.getId(), g3);


        User u1 = new User("Francesco", "Pipito'");
        User u2 = new User("Alessio", "Sacco");
        User u3 = new User("Alessandro", "Catto");
        User u4 = new User("Alessandro", "Nicoletta");
        User u5 = new User("Lucia", "De Simone");
        User u6 = new User("Bill", "Gates");

        users.put(u1.getId(), u1);
        users.put(u2.getId(), u2);
        users.put(u3.getId(), u3);
        users.put(u4.getId(), u4);
        users.put(u5.getId(), u5);
        users.put(u6.getId(), u6);


        g1.addUser(u1);
        g1.addUser(u2);
        g1.addUser(u3);
        g1.addUser(myself);


        g2.addUser(u1);
        g2.addUser(u2);
        g2.addUser(u3);
        g2.addUser(u5);
        g2.addUser(myself);


        g3.addUser(u5);
        g3.addUser(u6);
        g3.addUser(myself);

        Expense e1 = new Expense("Flight", Expense.Tag.FLIGHT, 89.6f, "Fligth to New York!", Expense.Currency.EURO, g1, u1, 2017, 3, 16);
        e1.addPayment(u1,    89.6f, 22.4f);
        e1.addPayment(u2,     0f, 22.4f);
        e1.addPayment(u3,     0f, 22.4f);
        e1.addPayment(myself, 0f, 22.4f);
        g1.addExpense(e1);

        Expense e2 = new Expense("Light Bill", Expense.Tag.LIGHT_BILL, 128f, "Torino house light bill", Expense.Currency.EURO, g1, myself, 2017, 1, 15);
        e2.addPayment(u1,       0f, 32f);
        e2.addPayment(u2,       0f, 32f);
        e2.addPayment(u3,       0f, 32f);
        e2.addPayment(myself, 128f, 32f);
        g1.addExpense(e2);

        Expense e3 = new Expense("Cake", Expense.Tag.FOOD, 100f, "Cake for Ale birthday", Expense.Currency.EURO, g2, u5, 2017, 2, 26);
        e3.addPayment(u1,       20f, 20f);
        e3.addPayment(u2,       10f, 20f);
        e3.addPayment(u3,       20f, 20f);
        e3.addPayment(myself,    0f, 20f);
        e3.addPayment(u5,       50f, 20f);
        g2.addExpense(e3);

        Expense e4 = new Expense("Pizza", Expense.Tag.FOOD, 50f, "Pizza saturday dinner", Expense.Currency.EURO, g3, u5, 2017, 3, 16);
        e4.addPayment(myself,    0f, 16.67f);
        e4.addPayment(u5,       50f, 16.67f);
        e4.addPayment(u6,        0f, 16.67f);
        g3.addExpense(e4);

        for(i=0; i<15; i++){
            e4 = new Expense("Pizza", Expense.Tag.FOOD, 50f, "Pizza saturday dinner", Expense.Currency.EURO, g3, u5, 2017, 3, 16);
            e4.addPayment(myself,    0f, 16.67f);
            e4.addPayment(u5,       50f, 16.67f);
            e4.addPayment(u6,        0f, 16.67f);
            g3.addExpense(e4);
        }

        Expense e5 = new Expense("Lunch", Expense.Tag.FOOD, 145f, "Lunch at Ciro's restaurant", Expense.Currency.EURO, g3, myself, 2017, 4, 4);
        e5.addPayment(myself,  145f, 48.3f);
        e5.addPayment(u5,        0f, 48.3f);
        e5.addPayment(u6,        0f, 48.3f);
        g3.addExpense(e5);
        */
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

    public Group getSingleGroup(Long id) {
        return groups.get(id);
    }

    public static User getMyself() {
        return myself;
    }

    public static void setMyself(User myself) {
        MyApplication.myself = myself;
    }

    public static String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public static void setUserPhoneNumber(String userPhoneNumber) {
        MyApplication.userPhoneNumber = userPhoneNumber;
    }

    public static String getFirebaseId() {
        return firebaseId;
    }

    public static void setFirebaseId(String firebaseId) {
        MyApplication.firebaseId = firebaseId;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        MyApplication.userName = userName;
    }

    public static String getUserSurname() {
        return userSurname;
    }

    public static void setUserSurname(String userSurname) {
        MyApplication.userSurname = userSurname;
    }

    public static String getUserEmail() {
        return userEmail;
    }

    public static void setUserEmail(String userEmail) {
        MyApplication.userEmail = userEmail;
    }

    public static Boolean getLogged() {
        return logged;
    }

    public static void setLogged(Boolean logged) {
        MyApplication.logged = logged;
    }

    public static Boolean getIsPhone() {
        return isPhone;
    }

    public static void setIsPhone(Boolean isPhone) {
        MyApplication.isPhone = isPhone;
    }

    public static FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    public static void setFirebaseUser(FirebaseUser firebaseUser) {
        MyApplication.firebaseUser = firebaseUser;
    }
}
