package com.polito.madinblack.expandedmad;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.polito.madinblack.expandedmad.GroupManaging.GroupListActivity;
import com.polito.madinblack.expandedmad.model.MyApplication;
import com.polito.madinblack.expandedmad.model.User;

/**
 * Created by Ale on 26/04/2017.
 */

public class CheckLogIn extends AppCompatActivity {


    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    // [START declare_auth_listener]
    private FirebaseAuth.AuthStateListener mAuthListener;
    // [END declare_auth_listener]

    private ValueEventListener mValueListener;


    /*added by Ale*/
    private DatabaseReference mDatabase;

    private MyApplication ma;

    private static final String TAG = "GoogleActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ma = MyApplication.getInstance();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // [START auth_state_listener]
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    ma.setFirebaseId(user.getUid());
                    ma.setUserEmail(user.getEmail());
                    String[] items = user.getDisplayName().split(" ");
                    ma.setUserName(items[0]);
                    ma.setUserSurname(items[1]);
                    ma.setFirebaseUser(user);
                    ma.setLogged(true);

                    mValueListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {

                                ma.setUserPhoneNumber(dataSnapshot.getValue(String.class));
                                ma.setIsPhone(true);


                                /*google login and number yet inserted jump to group page*/
                                Intent intent = new Intent(CheckLogIn.this, GroupListActivity.class);
                                startActivity(intent);
                                finish();

                            }else{

                                ma.setIsPhone(false);
                                Intent intent = new Intent(CheckLogIn.this, GoogleSignInActivity2.class);
                                startActivity(intent);
                                finish();

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };


                    mDatabase.child("userId").child(user.getUid()).addListenerForSingleValueEvent(mValueListener);



                } else {

                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    ma.setLogged(false);
                    Intent intent = new Intent(CheckLogIn.this, GoogleSignInActivity2.class);
                    startActivity(intent);

                }
            }


        };


    }

    @Override
    public void onStart() {
        super.onStart();
        //Adds a listener that will be called when the connection becomes authenticated or unauthenticated.
        mAuth.addAuthStateListener(mAuthListener);
    }
    // [END on_start_add_listener]

    // [START on_stop_remove_listener]
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        if(mValueListener!=null && mDatabase!=null){
            mDatabase.child("userId").child(ma.getFirebaseId()).removeEventListener(mValueListener);
        }
    }





}
