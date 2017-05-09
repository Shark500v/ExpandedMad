package com.polito.madinblack.expandedmad.groupMembers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.polito.madinblack.expandedmad.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.polito.madinblack.expandedmad.model.Balance;
import com.polito.madinblack.expandedmad.utility.RecyclerViewAdapterUsers;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class UserDebts extends AppCompatActivity {


    private String name = "";
    private String surname = "";
    private String groupId = "";
    private String firebaseId = "";
    private RecyclerViewAdapterUsers adapter;
    private RecyclerView recyclerView;
    private DatabaseReference mDatabaseBalancesReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_debts);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            /*Bundle arguments = new Bundle();
            arguments.putString(ItemDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(ItemDetailFragment.ARG_ITEM_ID));
            ItemDetailFragment fragment = new ItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit();*/
            //Mostra il parametro passato come titolo
            Bundle extras = getIntent().getExtras();
            name = extras.getString("NAME");
            surname = extras.getString("SURNAME");
            groupId = extras.getString("GROUP_ID");
            firebaseId = extras.getString("FIREBASE_ID");

            actionBar.setTitle(name + " " + surname + " " + getString(R.string.balance));

            mDatabaseBalancesReference = FirebaseDatabase.getInstance().getReference().child("groups/"+groupId+"/users/"+firebaseId+"/balances");

        }

    }

    @Override
    public void onStart(){
        super.onStart();

        adapter = new RecyclerViewAdapterUsers(this, mDatabaseBalancesReference);

        recyclerView = (RecyclerView) findViewById(R.id.item_list);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onStop() {
        super.onStop();

        // Clean up comments listener
        if(adapter!=null)
            adapter.cleanupListener();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, GroupMemebersActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            navigateUpTo(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
