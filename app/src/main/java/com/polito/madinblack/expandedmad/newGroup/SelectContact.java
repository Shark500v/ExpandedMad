package com.polito.madinblack.expandedmad.newGroup;

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
import com.polito.madinblack.expandedmad.groupManaging.GroupListActivity;
import com.polito.madinblack.expandedmad.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class SelectContact extends AppCompatActivity {

    private List<SelectUser> groupM = new ArrayList<>();
    private List<SelectUser> invite = new ArrayList<>();    //used to invite new membres to join the app
    private DatabaseReference mDatabaseReference;
    private AtomicInteger counter;
    private ContactsFragment fragment; //used to show the contact list
    private Map<String, SelectUser> selectUserToAdd = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //preparo il fragment che dovrà mostrare a schermo i contatti di cui ho fatto il retrieve dalla rubbrica
        Bundle arguments = new Bundle();
        arguments.putSerializable("LIST", (Serializable) groupM);
        fragment = new ContactsFragment();
        fragment.setArguments(arguments);

        //lancio il fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_2, fragment).commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        invite.clear();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, GroupListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            navigateUpTo(intent);
            return true;
        }else if(id == R.id.confirm_group){

            if (groupM.isEmpty()) {

                View mv = findViewById(R.id.frame_layout_2);
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
                            }
                            else{
                                for(DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()){
                                    //int index = groupM.indexOf(selectUser);
                                    String userFirebaseId = dataSnapshotChild.getKey();
                                    String name = dataSnapshotChild.child("name").getValue(String.class);
                                    String surname = dataSnapshotChild.child("surname").getValue(String.class);
                                    selectUserToAdd.get(key).setFirebaseId(userFirebaseId);
                                    selectUserToAdd.get(key).setName(name);
                                    selectUserToAdd.get(key).setSurname(surname);
                                    //groupM.set(index, selectUser);
                                }
                            }

                            if(counter.decrementAndGet()==0){
                                if(invite.isEmpty()){
                                    //se la lista è vuota non ci sono inviti da fare e posso andare oltre, altrimenti devo procedere ad invitare le persone mancanti prima di creare il gruppo
                                    Intent intent1=new Intent(SelectContact.this, NewGroup.class);
                                    intent1.putExtra("invite", (Serializable) invite);                  //in questo caso la lista sarà vuota
                                    intent1.putExtra("Group Members", (Serializable) groupM);
                                    startActivity(intent1);
                                }else{
                                    //invito le persone che non sono ancora nel DB
                                    Bundle arguments = new Bundle();
                                    arguments.putSerializable("invite", (Serializable) invite);
                                    arguments.putSerializable("Group Members", (Serializable) groupM);  //lista di utenti già inscritti
                                    InviteContact fragment = new InviteContact();
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
        getMenuInflater().inflate(R.menu.menu_new_group, menu);

        return super.onCreateOptionsMenu(menu);
    }
}