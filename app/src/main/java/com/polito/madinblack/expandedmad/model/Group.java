package com.polito.madinblack.expandedmad.model;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
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
            this.users.put(userForGroup.getFirebaseId(), userForGroup);
        }


    }


    /*return of group id*/
    public static String writeNewGroup(DatabaseReference mDatabaseRootReference, String name, List<UserForGroup> usersForGroup) {

        Group group = new Group(name, usersForGroup);

        DatabaseReference myGroupRef = mDatabaseRootReference.child("groups").push();
        String groupKey = myGroupRef.getKey();

        group.setId(groupKey);

        myGroupRef.setValue(group);

        GroupForUser groupForUser = new GroupForUser(group);

        for(UserForGroup userForGroup : usersForGroup) {
            mDatabaseRootReference.child("users/"+userForGroup.getPhoneNumber()+"/"+userForGroup.getFirebaseId()+"/groups/"+groupKey).setValue(groupForUser);
        }
        return groupKey;

    }

    /*to add single member to a group created yet*/
    public static void writeUserToGroup(final DatabaseReference mDatabaseRootReference, final String groupId, final String groupName, final String userFirebaseId, final String userPhoneNumber, final String userName, final String userSurname){


        mDatabaseRootReference.child("groups/"+groupId+"/users").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final UserForGroup newUserForGroup = new UserForGroup(userPhoneNumber, userFirebaseId, userName, userSurname);
                Balance balance = new Balance(userPhoneNumber, userName, userSurname, 0D, "Euro", "€");

                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){

                    userSnapshot.getRef().child("balances").child(userFirebaseId).setValue(balance);
                    UserForGroup userForGroup = userSnapshot.getValue(UserForGroup.class);
                    newUserForGroup.connect(userForGroup);
                    mDatabaseRootReference.child("users/"+userForGroup.getPhoneNumber()+"/"+userForGroup.getFirebaseId()+"/groups/"+groupId+"/size").runTransaction(new Transaction.Handler(){
                        @Override
                        public Transaction.Result doTransaction(MutableData currentData){
                            if(currentData.getValue() == null){
                                //no default value for data, set one
                                currentData.setValue(1L);
                            }else{
                                // perform the update operations on data
                                currentData.setValue(currentData.getValue(Long.class) + 1);
                            }
                            return Transaction.success(currentData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError,
                                               boolean committed, DataSnapshot currentData){

                        }





                    });



                }
                mDatabaseRootReference.child("groups/"+groupId+"/users/"+userFirebaseId).setValue(newUserForGroup);

                mDatabaseRootReference.child("groups/"+groupId+"/size").runTransaction(new Transaction.Handler(){
                    @Override
                    public Transaction.Result doTransaction(MutableData currentData){
                        if(currentData.getValue() == null){
                            //no default value for data, set one
                            currentData.setValue(1L);
                        }else{
                            // perform the update operations on data
                            currentData.setValue(currentData.getValue(Long.class) + 1);
                        }
                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError,
                                           boolean committed, DataSnapshot currentData){
                        if(committed){
                            GroupForUser newGroupForUser = new GroupForUser(groupName, groupId, (Long) currentData.getValue(), 0L);
                            mDatabaseRootReference.child("users"+userPhoneNumber+"/"+userFirebaseId+"/groups/"+groupId).setValue(newGroupForUser);


                        }

                    }

                });






            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
