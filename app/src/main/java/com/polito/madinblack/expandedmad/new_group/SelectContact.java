package com.polito.madinblack.expandedmad.new_group;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.polito.madinblack.expandedmad.ExpenseListActivity;
import com.polito.madinblack.expandedmad.GroupManaging.GroupListActivity;
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

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            /*
            Bundle arguments = new Bundle();
            arguments.putString(GroupDetailFragment.ARG_G_ID, getIntent().getStringExtra(GroupDetailFragment.ARG_G_ID));    //id del gruppo come stringa
            GroupDetailFragment fragment = new GroupDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().add(R.id.group_detail_container, fragment).commit();
            */
            Bundle arguments = new Bundle();
            arguments.putSerializable("LIST", (Serializable) groupM);
            ContactsFragment fragment = new ContactsFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, fragment).commit();

        }
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

                View mv = findViewById(R.id.frameLayout);
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 250 milliseconds
                v.vibrate(250);
                Snackbar.make(mv, "Please, select at least one member!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                return true;
            }else{
                //prima di lanciare la nuova activity devo verificare che tutti i contatti selezionati facciano parte dell'applicazione

                /*
                *   verifica contatti e riempo la lista degli invite!
                * */
                //se acuni contatti non sono presenti dentro il DB allora li devo invitare
                invite.clear();

                counter = new AtomicInteger(groupM.size());

                for(final SelectUser selectUser : groupM){

                    mDatabaseReference.child("users").child(selectUser.getPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.exists()){
                                groupM.remove(selectUser);
                                invite.add(selectUser);
                            }


                            if(counter.decrementAndGet()==0){
                                if(invite.isEmpty()){
                                    //se la lista è vuota non ci sono inviti da fare e posso andare oltre, altrimenti devo procedere ad invitare le persone mancanti prima di creare il gruppo
                                    Intent intent1=new Intent(SelectContact.this, NewGroup.class);
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

                //invite.addAll(groupM);  //funzione di prova

                /*
                if(invite.isEmpty()){
                    //se la lista è vuota non ci sono inviti da fare e posso andare oltre, altrimenti devo procedere ad invitare le persone mancanti prima di creare il gruppo
                    Intent intent1=new Intent(SelectContact.this, NewGroup.class);
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
                */

            }
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_group, menu);

        return super.onCreateOptionsMenu(menu);
    }
}
