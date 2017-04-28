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



    /*
    private Double totPersonalCredit;
    private Double totPersonalDebit;
    */
    //image attribute will be combination of url and group id


    private Map<String, UserForGroup> users = new LinkedHashMap<>();

    private Map<String, Boolean> expenses = new LinkedHashMap<>();

    //map containing how money myself have to paid/received by users. + = received - = give 0 nothing
    //private Map<String, Float> myCreditsDebits = new HashMap<>();

    public Group(){

    }

    public Group(String name){
        this.name        = name;
        this.size        = 0L;


    }

    public Group(String name, List<UserForGroup> usersForGroup){
        this.name        = name;
        this.size        = Long.valueOf(usersForGroup.size());


        for(UserForGroup userForGroup : usersForGroup){
            this.users.put(userForGroup.getId(), userForGroup);
        }


    }


    /*return of group id*/
    public static String writeNewGroup(DatabaseReference mDatabase, String name, List<UserForGroup> usersForGroup) {

        for(UserForGroup userForGroup : usersForGroup) {
            userForGroup.initializeBalance(usersForGroup);
        }


        Group group = new Group(name, usersForGroup);


        DatabaseReference myGroupRef = mDatabase.child("groups").push();
        String groupKey = myGroupRef.getKey();

        group.setId(groupKey);

        myGroupRef.setValue(group);


        GroupForUser groupForUser = new GroupForUser(group);

        for(UserForGroup userForGroup : usersForGroup) {
            mDatabase.child("users").child(userForGroup.getId()).child("groups").child(groupKey).setValue(groupForUser);
        }


        return groupKey;

    }

    /*to add single member to a group created yet*/
    public static void writeUserToGroup(final DatabaseReference mDatabase, final String groupId, final String userId, final String userName, final String userSurname){


        mDatabase.child("groups").child(groupId).child("users").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<UserForGroup> userForGroupList = new ArrayList<UserForGroup>();

                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                    userSnapshot.getRef().child("balance").updateChildren(userId)


                    userForGroupList.add(userSnapshot.getValue(UserForGroup.class));
                }
                UserForGroup userForGroup = new UserForGroup(userId, userName, userSurname);
                userForGroup.initializeBalance(userForGroupList);
                mDatabase.child("groups").child(groupId).child("users").child(userId).setValue(userForGroup);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        mDatabase.child("groups").child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    GroupForUser groupForUser = new GroupForUser(dataSnapshot.getValue(Group.class));
                    mDatabase.child("users").child(userId).child("groups").child(groupId).setValue(groupForUser);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        mDatabase.child("groups").child(groupId).child("size").runTransaction( new Transaction.Handler(){

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
   // public void addExpense(Expense e){
        //expenses.put(e.getId(), e);

    //}


/*
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
*/

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



    public Map<String, UserForGroup> getUsers() { return users; }

    public void   setName(String name) { this.name = name; }

    public void   setId(String id) { this.id = id; }

    public void   setSize(Long size) { this.size = size; }





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
    /*
    public void uplMyCreditDebit(User user, Float debitCredit){
        if(!(myCreditsDebits.containsKey(user.getId()))){
            myCreditsDebits.put(user.getId(), debitCredit);
        }else{
            Float previous = myCreditsDebits.get(user.getId());
            myCreditsDebits.put(user.getId(), previous+debitCredit);
        }

    }
*/

}
