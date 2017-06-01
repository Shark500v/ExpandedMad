package com.polito.madinblack.expandedmad.model;


import android.content.Intent;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.polito.madinblack.expandedmad.groupManaging.GroupSettings;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class Group {

    private String  name;
    private String  id;
    private Long    size;
    private String  urlImage;
    private String  createdByFullName;
    private String  createdByPhoneNumber;


    //image attribute will be combination of url and group id


    private Map<String, UserForGroup> users = new LinkedHashMap<>();

    private Map<String, Boolean> expenses = new LinkedHashMap<>();

    public Group(){

    }

    public Group(String name, String createdByFullName, String createdByPhoneNumber){
        this.name                   = name;
        this.size                   = 0L;
        this.createdByFullName      = createdByFullName;
        this.createdByPhoneNumber   = createdByPhoneNumber;



    }

    public Group(String name, String createdByFullName, String createdByPhoneNumber, List<UserForGroup> usersForGroup){
        this.name                   = name;
        this.createdByFullName      = createdByFullName;
        this.size                   = ((Integer)usersForGroup.size()).longValue();
        this.createdByPhoneNumber   = createdByPhoneNumber;

        for(UserForGroup userForGroup : usersForGroup){
            this.users.put(userForGroup.getFirebaseId(), userForGroup);
        }


    }

    public String getCreatedByFullName() {
        return createdByFullName;
    }

    public void setCreatedByFullName(String createdByFullName) {
        this.createdByFullName = createdByFullName;
    }

    public String getCreatedByPhoneNumber() {
        return createdByPhoneNumber;
    }

    public void setCreatedByPhoneNumber(String createdByPhoneNumber) {
        this.createdByPhoneNumber = createdByPhoneNumber;
    }

    /*return of group id*/
    public static String writeNewGroup(DatabaseReference mDatabaseRootReference, String name, List<UserForGroup> usersForGroup, String userName, String userPhoneNumber) {

        Group group = new Group(name, userName, userPhoneNumber, usersForGroup);

        DatabaseReference myGroupRef = mDatabaseRootReference.child("groups").push();
        String groupKey = myGroupRef.getKey();

        group.setId(groupKey);

        myGroupRef.setValue(group);

        GroupForUser groupForUser = new GroupForUser(group);
        groupForUser.setTimestamp();
        for(UserForGroup userForGroup : usersForGroup) {
            mDatabaseRootReference.child("users/"+userForGroup.getPhoneNumber()+"/"+userForGroup.getFirebaseId()+"/groups/"+groupKey).setValue(groupForUser);
        }

        HistoryInfo historyInfo = new HistoryInfo(userName, null, 3l, 0d, null, null);
        mDatabaseRootReference.child("history/"+groupKey).push().setValue(historyInfo);

        return groupKey;

    }

    /*to add single member to a group created yet*/
    public static void writeUserToGroup(final DatabaseReference mDatabaseRootReference, final String groupId, final String groupName, final String userFirebaseId, final String userPhoneNumber, final String userName, final String userSurname){


        mDatabaseRootReference.child("groups/"+groupId+"/users").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final UserForGroup newUserForGroup = new UserForGroup(userPhoneNumber, userFirebaseId, userName, userSurname);
                Balance balance = new Balance(userPhoneNumber, userName, userSurname, 0D, MyApplication.getCurrencyISOFavorite());

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
                                           boolean committed, DataSnapshot dataSnapshot){
                        if(committed){
                            final Long size = dataSnapshot.getValue(Long.class);

                            FirebaseDatabase.getInstance().getReference().child("groups/"+groupId+"/name").runTransaction(new Transaction.Handler() {

                                @Override
                                public Transaction.Result doTransaction(MutableData name) {
                                    return Transaction.success(name);
                                }

                                @Override
                                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot2) {
                                    if(b){
                                        String name = dataSnapshot2.getValue(String.class);
                                        GroupForUser newGroupForUser = new GroupForUser(name, groupId, size, 0L);
                                        newGroupForUser.setTimestamp();
                                        mDatabaseRootReference.child("users/"+userPhoneNumber+"/"+userFirebaseId+"/groups/"+groupId).setValue(newGroupForUser);

                                        /*update the history*/
                                        HistoryInfo historyInfo = new HistoryInfo(userName+" "+userSurname, null, 2L, 0D, null, null);
                                        mDatabaseRootReference.child("history/"+groupId).push().setValue(historyInfo);



                                    }

                                }
                            });




                        }

                    }

                });



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }



    public String getName(){ return name; }

    public String getId(){
        return id;
    }

    public Long   getSize(){ return size; }

    public String getUrlImage() { return urlImage; }

    public Map<String, UserForGroup> getUsers() { return users; }

    public void   setName(String name) { this.name = name; }

    public void   setId(String id) { this.id = id; }

    public void   setSize(Long size) { this.size = size; }

    public void   setUrlImage(String urlImage) {  this.urlImage = urlImage; }
}
