package com.polito.madinblack.expandedmad.new_group;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.polito.madinblack.expandedmad.GroupManaging.GroupListActivity;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.model.Group;
import com.polito.madinblack.expandedmad.model.MyApplication;
import com.polito.madinblack.expandedmad.model.UserForGroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NewGroup extends AppCompatActivity {


    private DatabaseReference mDatabaseReference;
    private MyApplication ma;
    String phoneId;
    List<SelectUser> groupM;
    List<SelectUser> invite;
    ListView lv;
    GroupMembersAdapter adapter;
    String groupCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        groupM = (List<SelectUser>) getIntent().getSerializableExtra("Group Members");
        invite = (List<SelectUser>) getIntent().getSerializableExtra("invite");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        lv = (ListView) findViewById(R.id.list1);
        adapter = new GroupMembersAdapter(groupM, this);

        // Show the Up button in the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        lv.setAdapter(adapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.confirm_group) {

            Intent intent;

            EditText inputGroupName = (EditText) findViewById(R.id.group_name);
            String groupName = inputGroupName.getText().toString();
            if (groupName.isEmpty() || groupName == null) {
                /*
                intent = new Intent(this, NewGroup.class);
                startActivity(intent);
                */
                View mv = findViewById(R.id.main_content);
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                v.vibrate(250);
                Snackbar.make(mv, "Please, insert a valid group name!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                return true;
            }


            /*
            //queste 4 righe sono ancora da modificare, devono creare il gruppo sotto l'utente, e gli utenti all'interno del gruppo
            mFirebaseInstance=FirebaseDatabase.getInstance();
            mFirebaseDatabase = mFirebaseInstance.getReference("Groups");
            mFirebaseDatabase.child(group.getId()+": "+groupName).child("Group").setValue(group);
            mFirebaseDatabase.child(group.getId()+": "+groupName).child("Users").setValue(phoneId);
            */


            List<UserForGroup> userForGroupList = new ArrayList<>();
            for(SelectUser selectUser : groupM){
                String[] items = selectUser.getName().split(" ");
                if(items[0]==null)
                    items[0] = "";
                if(items[1]==null)
                    items[1] = "";

                UserForGroup userForGroup = new UserForGroup(selectUser.getPhone(), items[0], items[1]);
                userForGroupList.add(userForGroup);

            }
            UserForGroup userForGroup = new UserForGroup(ma.getUserPhoneNumber(), ma.getUserName(), ma.getUserSurname());
            userForGroupList.add(userForGroup);


            groupCode = Group.writeNewGroup(mDatabaseReference, groupName, userForGroupList);


            if(invite==null || invite.isEmpty()){
                Intent intent1=new Intent(NewGroup.this, GroupListActivity.class); //da cambiare (dovra' andare alla pagina del gruppo creato)
                startActivity(intent1);
            }else{
                if(invite.isEmpty()){
                    Intent intent1=new Intent(NewGroup.this, GroupListActivity.class); //da cambiare (dovra' andare alla pagina del gruppo creato)
                    startActivity(intent1);
                }else {
                    //devo invitare i nuovi membri del gruppo
                    Intent intent2 = new Intent(NewGroup.this, InviteActivity.class);
                    intent2.putExtra("InviteList", (Serializable) invite);
                    intent2.putExtra("Code", groupCode);
                    startActivity(intent2);
                }
            }

            return true;

        }else if (id == android.R.id.home){
            Intent intent = new Intent(this, SelectContact.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            navigateUpTo(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_new_group, menu);
        return true;
    }
}
