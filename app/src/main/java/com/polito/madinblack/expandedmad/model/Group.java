package com.polito.madinblack.expandedmad.model;


import android.content.Intent;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.polito.madinblack.expandedmad.GoogleSignInActivity2;
import com.polito.madinblack.expandedmad.GroupManaging.GroupListActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class Group {

    private String  name;
    private String  id;
    private Long    size;
    private Long    newExpenses;

    /*
    private Double totPersonalCredit;
    private Double totPersonalDebit;
    */
    //image attribute will be combination of url and group id


    private Map<String, Boolean> users = new LinkedHashMap<>();

    private Map<String, Boolean> expenses = new LinkedHashMap<>();

    //map containing how money myself have to paid/received by users. + = received - = give 0 nothing
    private Map<String, Float> myCreditsDebits = new HashMap<>();

    public Group(){

    }

    public Group(String name){
        this.name        = name;
        this.size        = 0L;
        this.newExpenses = 0L;

    }

    public Group(String name, Map<String, Boolean> users){
        this.name        = name;
        this.size        = Long.valueOf(users.size());
        this.newExpenses = 0L;
        this.users       = users;

    }

    public static void writeNewGroup(DatabaseReference mDatabase, String name, Map<String, Boolean> users) {

        Group group = new Group(name, users);


        DatabaseReference myGroupRef = mDatabase.child("groups").push();
        String groupKey = myGroupRef.getKey();

        group.setId(groupKey);

        GroupForUser groupForUser = new GroupForUser(group);

        myGroupRef.setValue(group);

        for(String s : users.keySet()) {
            mDatabase.child("users").child(s).child("groups").child(groupKey).setValue(groupForUser);
        }




    }

    /*to add single member to a group created yet*/
    public static void writeUserToGroup(final DatabaseReference mDatabase, final String groupId, final String userId){

        mDatabase.child("groups").child(groupId).child("users").child(userId).setValue(true);

        mDatabase.child("groups").child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Group group = dataSnapshot.getValue(Group.class);
                    mDatabase.child("users").child(userId).child("groups").child(groupId).setValue(group);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        mDatabase.child("groups/size").runTransaction( new Transaction.Handler(){

            @Override
            public Transaction.Result doTransaction(MutableData currentData){
                if(currentData.getValue() == null){
                    //no default value for data, set one
                    currentData.setValue(1);
                    mDatabase.child("users").child(userId).child("groups").child(groupId).child("size").setValue(1);
                }else{
                    // perform the update operations on data
                    currentData.setValue((Long) currentData.getValue() + 1);
                    mDatabase.child("users").child(userId).child("groups").child(groupId).child("size").setValue(currentData.getValue());
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError databaseError,
                                   boolean committed, DataSnapshot currentData){
                //This method will be called once with the results of the transaction.
                //Update remove the user from the group
            }
        });



    }



    /*be carrefour: for now do this operation only after add all initial expenses*/
    public void addExpense(Expense e){
        //expenses.put(e.getId(), e);

    }

    public void addUser(String userId){
        users.put(userId, true);
    }

    public List<User> getUsersCreditsDebits (){

        List<User> usersCD = new ArrayList<User>();

        Iterator<String> it = myCreditsDebits.keySet().iterator();

        while(it.hasNext()) {
            //User u = users.get(it.next());
            //usersCD.add(u);
        }

        return usersCD;
    }

    public Map<String, Float> getMyCreditsDebits() {
        return myCreditsDebits;
    }


    /*
    public Double getTotCredit() {
        return totPersonalCredit;
    }

    public Double getTotDebit() {
        return totPersonalDebit;
    }
*/

   // public User getUser(String id) { return users.get(id);}

    public String getName(){ return name; }

    public String getId(){
        return id;
    }

    public Long   getSize(){ return size; }

    public Long   getNewExpenses() { return newExpenses; }

    public Map<String, Boolean> getUsers(){ return users; }

    public void   setName(String name) { this.name = name; }

    public void   setId(String id) { this.id = id; }

    public void   setSize(Long size) { this.size = size; }

    public void   setNewExpenses(Long newExpenses) { this.newExpenses = newExpenses; }

    public void setUsers(Map<String, Boolean> users) { this.users = users; }

/*
    public List<Expense> getExpenses() {
        return new ArrayList<>(expenses.values());
    }
*/
/*
    public String getDetails(){
        return "Insert here the Details of the Group.";
    }
*/
    /*
    public Expense getSingleExpense(Long id) {
        return expenses.get(id);
    }
    */
    public void uplMyCreditDebit(User user, Float debitCredit){
        if(!(myCreditsDebits.containsKey(user.getId()))){
            myCreditsDebits.put(user.getId(), debitCredit);
        }else{
            Float previous = myCreditsDebits.get(user.getId());
            myCreditsDebits.put(user.getId(), previous+debitCredit);
        }

    }


}
