package com.polito.madinblack.expandedmad.GroupManaging;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.polito.madinblack.expandedmad.ExpenseDetailFragment;
import com.polito.madinblack.expandedmad.ExpenseListActivity;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.dummy.Group;

/**
 * Created by Francesco on 04/04/2017.
 */

public class GroupDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(GroupDetailFragment.ARG_G_ID, getIntent().getStringExtra(GroupDetailFragment.ARG_G_ID));
            GroupDetailFragment fragment = new GroupDetailFragment();
            fragment.setArguments(arguments);
            //getSupportFragmentManager().beginTransaction().addToBackStack(null).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.group_detail_container, fragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, ExpenseListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            navigateUpTo(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
