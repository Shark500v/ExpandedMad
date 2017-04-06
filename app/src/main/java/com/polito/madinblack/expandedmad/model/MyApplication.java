package com.polito.madinblack.expandedmad.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Ale on 04/04/2017.
 */

public class MyApplication {

    public static MyApplication myApplication = null;



    public static User myself;

    /*groupId --> group*/
    private Map<Long, Group> groups = new LinkedHashMap<>();

    /*list of all users, can be seen as phonebook*/
    private Map<Long, User> users = new LinkedHashMap<>();



    private MyApplication(){
        myself = new User("MyName", "MySurname");
        

        Group g1 = new Group("Group1");
        Group g2 = new Group("Group2");
        Group g3 = new Group("Group3");

        groups.put(g1.getId(), g1);
        groups.put(g2.getId(), g1);
        groups.put(g3.getId(), g1);


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

        g1.addUser(u1);
        g1.addUser(u2);
        g1.addUser(u3);

        g2.addUser(u1);
        g2.addUser(u2);
        g2.addUser(u3);
        g2.addUser(u4);
        g2.addUser(u5);

        g3.addUser(u5);
        g3.addUser(u6);

        Expense e1 = new Expense("Fly", Expense.Tag.FLY, 67.2f, null, Expense.Currency.EURO, g1, u1, 2017, 3, 16);
        e1.addPayment(u1, 67.2f, 22.4f);
        e1.addPayment(u2,  0f, 22.4f);
        e1.addPayment(u3,  0f, 22.4f);
        g1.addExpense(e1);

        Expense e2 = new Expense("Light Bill", Expense.Tag.LIGHT_BILL, 96f, null, Expense.Currency.EURO, g1, u2, 2017, 1, 15);
        e2.addPayment(u1,  0f, 32f);
        e2.addPayment(u2, 96f, 32f);
        e2.addPayment(u3,  0f, 32f);
        g1.addExpense(e2);

        Expense e3 = new Expense("Cake", Expense.Tag.FOOD, 100f, null, Expense.Currency.EURO, g2, u5, 2017, 2, 26);
        e3.addPayment(u1,  20f, 20f);
        e3.addPayment(u2,  10f, 20f);
        e3.addPayment(u3,  20f, 20f);
        e3.addPayment(u4,   0f, 20f);
        e3.addPayment(u5,  50f, 20f);
        g2.addExpense(e3);




    }


    public static MyApplication getInstance(){
        if(myApplication==null)
            myApplication = new MyApplication();
        return myApplication;
    }

    public void addGroup(Group g){
        groups.put(g.getId(), g);
    }

    public List<Group> getGroup(){ return new ArrayList<Group>(groups.values()); }







}
