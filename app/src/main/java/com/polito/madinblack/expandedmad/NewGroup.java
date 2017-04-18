package com.polito.madinblack.expandedmad;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.polito.madinblack.expandedmad.GroupManaging.GroupListActivity;
import com.polito.madinblack.expandedmad.model.Group;
import com.polito.madinblack.expandedmad.model.MyApplication;

public class NewGroup extends AppCompatActivity {

    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mGroupsDatabase;
    private DatabaseReference mUsersDatabase;
    private MyApplication ma;
    String phoneId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolB);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Recupero il numero di telefono
        Bundle extras=getIntent().getExtras();
        if(extras!=null)
            phoneId=extras.getString("phoneId");
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.confirm_group) {

            boolean isNull = false;
            Intent intent;

            EditText inputGroupName = (EditText) findViewById(R.id.group_name);
            String groupName = inputGroupName.getText().toString();
            if (groupName.isEmpty() || groupName == null) {
                intent = new Intent(this, NewGroup.class);
                startActivity(intent);
                return true;
            }



            addGroup(groupName);

            Intent intent1=new Intent(NewGroup.this, GroupListActivity.class); //da cambiare (dovra' andare alla pagina del gruppo creato)
            startActivity(intent1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addGroup(String groupName){
        // creo il gruppo e setto il nome del gruppo ricevuto dalla EditText
        Group group = new Group();
        group.setName(groupName);

        //creo il gruppo sotto "Groups" e gli assegno una chiave univoca
        mGroupsDatabase=FirebaseDatabase.getInstance().getReference("Groups");
        String groupId =mGroupsDatabase.push().getKey();
        mGroupsDatabase.child(groupId).setValue(group);

        //creo il gruppo sotto l'utente che lo crea (bisognerà aggiungere il gruppo ad ogni utente che partecipa al gruppo)
        mUsersDatabase = FirebaseDatabase.getInstance().getReference("Users");
        mUsersDatabase.child(phoneId).setValue(groupId);
        mUsersDatabase.child(phoneId).child(groupId).setValue(group);

        //da togliere penso
        ma = MyApplication.getInstance();
        ma.addGroup(group);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_new_group, menu);
        return true;
    }
}
