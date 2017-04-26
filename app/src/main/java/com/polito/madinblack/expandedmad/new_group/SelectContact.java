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

import com.polito.madinblack.expandedmad.ExpenseListActivity;
import com.polito.madinblack.expandedmad.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SelectContact extends AppCompatActivity {

    List<SelectUser> groupM = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);

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
            Intent intent = new Intent(this, ExpenseListActivity.class);
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
                Intent intent1=new Intent(SelectContact.this, NewGroup.class);
                intent1.putExtra("Group Members", (Serializable) groupM);
                startActivity(intent1);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_group, menu);

        return super.onCreateOptionsMenu(menu);
    }
}
