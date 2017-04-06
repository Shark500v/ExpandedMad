package com.polito.madinblack.expandedmad.model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class Group {

    private String name;
    private Long id;
    private Float totCredit;
    private Float totDebit;



    private static long counter = 1;
    //add image attribute

    private Map<Long, User>users = new HashMap<>();

    private Map<Long, Expense>expenses = new LinkedHashMap<>();

    //map containing how money myself have to paid/received by users. + = received - = give 0 nothing
    private Map<Long, Float> myCreditsDebits = new HashMap<>();

    public Group(String name){
        this.name = name;
        this.id = counter++;
        totCredit = 0F;
        totDebit = 0F;

    }

    /*be carrefour: for now do this operation only after add all initial expenses*/
    public void addExpense(Expense e){
        expenses.put(e.getId(), e);

    }

    public void addUser(User user){
        users.put(user.getId(), user);
    }

    public List<User> getUsersCreditsDebits (){

        List<User> usersCD = new ArrayList<User>();

        Iterator<Long> it = myCreditsDebits.keySet().iterator();

        while(it.hasNext()) {
            User u = users.get(it.next());
            usersCD.add(u);
        }

        return usersCD;
    }

    public Map<Long, Float> getMyCreditsDebits() {
        return myCreditsDebits;
    }

    public List<User> getUsers(){ return new ArrayList<>(users.values());}


    public Long getId(){
        return id;
    }

    public Float getTotCredit() {
        return totCredit;
    }

    public Float getTotDebit() {
        return totDebit;
    }

    public User getUser(Long id) { return users.get(id);}


    public int getSize(){ return expenses.size();}

    public String getName(){ return name; }

    public List<Expense> getExpenses() {
        return new ArrayList<>(expenses.values());
    }

    public String getDetails(){
        return "Insert here the Details of the Group.";
    }

    public Expense getSingleExpense(Long id) {
        return expenses.get(id);
    }

    public void uplMyCreditDebit(User user, Float debitCredit){
        if(!(myCreditsDebits.containsKey(user.getId()))){
            myCreditsDebits.put(user.getId(), debitCredit);
        }else{
            Float previous = myCreditsDebits.get(user.getId());
            myCreditsDebits.put(user.getId(), previous+debitCredit);
        }

    }


}
