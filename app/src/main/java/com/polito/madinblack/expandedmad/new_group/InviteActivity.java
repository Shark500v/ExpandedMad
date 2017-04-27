package com.polito.madinblack.expandedmad.new_group;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.polito.madinblack.expandedmad.R;

import java.io.Serializable;
import java.util.List;

public class InviteActivity extends AppCompatActivity {

    List<SelectUser> invite;
    List<SelectUser> groupM;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        //toolbar settings
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar. (bottone indietro nella pagina 2 che è quella che mostra la lista delle spese)
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        invite = (List<SelectUser>) getIntent().getSerializableExtra("InviteList");
        groupM = (List<SelectUser>) getIntent().getSerializableExtra("Group Members");

        //in questo punto il codice prende la lista principale e la mostra come recyclerview
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.invite_list);
        assert recyclerView != null;
        setupRecyclerView(recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override   //lo uso per le icone in alto a destra che posso selezionare
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.confirm_group:
                //manage here the invitation email, after some checks
                Intent intent1=new Intent(InviteActivity.this, NewGroup.class);
                intent1.putExtra("Group Members", (Serializable) groupM);
                startActivity(intent1);
                return true;

            case R.id.home:
                navigateUpTo(new Intent(this, SelectContact.class));    //definisco il parente verso cui devo tornare indietro
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override   //questo serve per il search button
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_group, menu);

        return super.onCreateOptionsMenu(menu);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new InviteRecyclerViewAdapter(invite));
    }
}
