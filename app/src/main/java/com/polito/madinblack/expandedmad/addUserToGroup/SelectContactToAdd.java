package com.polito.madinblack.expandedmad.addUserToGroup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.groupManaging.GroupSettings;
import com.polito.madinblack.expandedmad.model.Group;
import com.polito.madinblack.expandedmad.model.User;
import com.polito.madinblack.expandedmad.model.UserForGroup;
import com.polito.madinblack.expandedmad.newGroup.SelectUser;
import com.polito.madinblack.expandedmad.tabViewGroup.TabView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class SelectContactToAdd extends AppCompatActivity {

    private List<SelectUser> groupM = new ArrayList<>();
    private List<SelectUser> invite = new ArrayList<>();    //used to invite new membres to join the app
    private List<String> usersInGroup = new ArrayList<>();
    private final Map<String,String> usersInDatabase = new HashMap<>();
    private Map<String, SelectUser> selectUserToAdd = new HashMap<>();
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabaseReferenceUserInGroup;
    private ValueEventListener mValueEventListener;
    private String groupId;
    private String groupName;
    private AtomicInteger counter;
    private ContactsToAddFragment fragment;                              //used to show the contact list

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact_to_add);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            groupId = extras.getString("groupIndex");
            groupName = extras.getString("groupName");
        }

        mDatabaseReferenceUserInGroup = FirebaseDatabase.getInstance().getReference().child("groups").child(groupId).child("users");
        mDatabaseReferenceUserInGroup.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usersInGroup.clear();
                for(DataSnapshot userInGroupSnapshot : dataSnapshot.getChildren()){
                    UserForGroup userForGroup = userInGroupSnapshot.getValue(UserForGroup.class);
                    String userPhoneNumber = userForGroup.getPhoneNumber();
                    usersInGroup.add(userPhoneNumber);
                }

                //preparo il fragment che dovrà mostrare a schermo i contatti di cui ho fatto il retrieve dalla rubbrica
                Bundle arguments = new Bundle();
                arguments.putSerializable("LIST", (Serializable) groupM);
                arguments.putSerializable("USERS_LIST", (Serializable) usersInGroup);
                arguments.putString("groupIndex", groupId);
                fragment = new ContactsToAddFragment();
                fragment.setArguments(arguments);
                //lancio il fragment
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).commit();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //if(mValueEventListener != null){
         //   mDatabaseReferenceUserInGroup.addValueEventListener(mValueEventListener);
        //}
    }

    @Override
    protected void onStop() {
        super.onStop();
        //if(mValueEventListener != null){
          //  mDatabaseReferenceUserInGroup.removeEventListener(mValueEventListener);
        //}
    }

    @Override
    protected void onResume() {
        super.onResume();
        invite.clear();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, GroupSettings.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            navigateUpTo(intent);
            return true;
        }else if(id == R.id.confirm_group){

            if (groupM.isEmpty()) {

                View mv = findViewById(R.id.frameLayout);
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 250 milliseconds
                v.vibrate(250);
                Snackbar.make(mv, getString(R.string.at_least_one_member), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                return true;
            }else{
                //prima di lanciare la nuova activity devo verificare che tutti i contatti selezionati fanno parte del DB
                //verifico contatti e riempo la lista degli invite (contatti da invitare)!

                counter = new AtomicInteger(groupM.size());
                invite.clear();
                selectUserToAdd.clear();

                for(SelectUser selectUser : groupM){
                    selectUserToAdd.put(selectUser.getPhone(), selectUser);
                }

                for(SelectUser selectUser : groupM){

                    mDatabaseReference.child("users").child(selectUser.getPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String key = dataSnapshot.getKey();
                            if(!dataSnapshot.exists()){
                                //groupM.remove(selectUser);
                                invite.add(selectUserToAdd.get(key));
                            } else{
                                for(DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()){
                                    //int index = groupM.indexOf(selectUser);
                                    String userFirebaseId = dataSnapshotChild.getKey();
                                    User user = dataSnapshotChild.getValue(User.class);
                                    String name = user.getName();
                                    String surname = user.getSurname();
                                    selectUserToAdd.get(key).setFirebaseId(userFirebaseId);
                                    selectUserToAdd.get(key).setName(name);
                                    selectUserToAdd.get(key).setSurname(surname);
                                    //groupM.set(index, selectUser);
                                }
                            }

                            if(counter.decrementAndGet()==0){
                                //if(invite.isEmpty()){
                                //se la lista è vuota non ci sono inviti da fare e posso andare oltre, altrimenti devo procedere ad invitare le persone mancanti prima di aggiungere le persone al gruppo
                                List<UserForGroup> userForGroupList = new ArrayList<>();

                                for(SelectUser selectUser : groupM) {
                                    if (!invite.contains(selectUser)){

                                        String name = selectUser.getName();
                                        if(name==null)
                                            name ="";
                                        String surname = selectUser.getSurname();
                                        if(surname==null)
                                            surname ="";


                                        if (selectUser.getFirebaseId() != null) {
                                            UserForGroup userForGroup = new UserForGroup(selectUser.getPhone(), selectUser.getFirebaseId(), name, surname);
                                            for (int i = 0; i < userForGroupList.size(); i++) {
                                                userForGroupList.get(i).connect(userForGroup);
                                                userForGroup.connect(userForGroupList.get(i));

                                            }
                                            userForGroupList.add(userForGroup);
                                        }
                                    }
                                }

                                if(invite.isEmpty()) {
                                    Group.writeUsersToGroup(mDatabaseReference, groupId, groupName, userForGroupList);
                                    Intent intent1 = new Intent(SelectContactToAdd.this, TabView.class);
                                    intent1.putExtra("groupIndex", groupId);
                                    intent1.putExtra("groupName", groupName);
                                    intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startActivity(intent1);
                                }else {


                                    //invito le persone che non sono ancora nel DB
                                    Bundle arguments = new Bundle();
                                    arguments.putSerializable("invite", (Serializable) invite);
                                    arguments.putSerializable("usersToAdd", (Serializable) userForGroupList);
                                    arguments.putString("groupIndex", groupId);
                                    arguments.putString("groupName", groupName);
                                    //arguments.putSerializable("Group Members", (Serializable) groupM);  //lista di utenti già inscritti
                                    InviteContactToAdd fragment = new InviteContactToAdd();
                                    fragment.setArguments(arguments);
                                    fragment.show(getSupportFragmentManager(), "InviteContacts");
                                }

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_adding_members, menu);

        return super.onCreateOptionsMenu(menu);
    }
}