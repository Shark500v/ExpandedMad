package com.polito.madinblack.expandedmad.groupManaging;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.login.BaseActivity;
import com.polito.madinblack.expandedmad.model.MyApplication;
import com.polito.madinblack.expandedmad.model.UserForGroup;

import java.util.HashMap;
import java.util.Map;


public class EditGroupName extends BaseActivity {
    private EditText editGroupName;
    private CoordinatorLayout coordinatorLayout;
    private String groupName;
    private String groupId;
    private String newGroupName;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_group_name);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.modify_group_name));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.snackbar_no_modify);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            groupName = extras.getString("groupName");
            groupId = extras.getString("groupIndex");
        }



        editGroupName = (EditText)findViewById(R.id.edit_group_name);
        editGroupName.setText(groupName);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_modify_group_name, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, GroupSettings.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            navigateUpTo(intent);
            return true;
        }
        if (id == R.id.confirm_group_name){
            if(!editGroupName.getText().toString().equals(groupName) && !editGroupName.getText().toString().isEmpty()) {
                showProgressDialog();
                //devo aggiornare il nome del gruppo nel database
                newGroupName = editGroupName.getText().toString();

                FirebaseDatabase.getInstance().getReference().child("groups/"+groupId+"/name").runTransaction(new Transaction.Handler() {

                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {

                        FirebaseDatabase.getInstance().getReference().child("groups/"+groupId+"/users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Map<String,Object> nameUpdate = new HashMap<>();

                                for(DataSnapshot child : dataSnapshot.getChildren()){
                                    UserForGroup userForGroup = child.getValue(UserForGroup.class);
                                    nameUpdate.put("/users/" + userForGroup.getPhoneNumber() + "/" + userForGroup.getFirebaseId() + "/groups/" + groupId + "/name", newGroupName);
                                }
                                nameUpdate.put("/groups/" + groupId + "/name", newGroupName);
                                FirebaseDatabase.getInstance().getReference().updateChildren(nameUpdate);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });



                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                        hideProgressDialog();
                        Intent intent = new Intent(getApplicationContext(), GroupSettings.class);
                        intent.putExtra("groupName", newGroupName);
                        intent.putExtra("groupIndex", groupId);
                        navigateUpTo(intent);

                    }
                });


            }else{
                Snackbar.make(coordinatorLayout, getString(R.string.name_not_modified), Snackbar.LENGTH_LONG).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
