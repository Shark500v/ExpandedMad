package com.polito.madinblack.expandedmad.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.polito.madinblack.expandedmad.model.MyApplication;

/**
 * Created by Ale on 26/04/2017.
 */

public class CheckLogIn extends BaseActivity {



    private FirebaseAuth                    mAuth;
    private FirebaseAuth.AuthStateListener  mAuthListener;
    private MyApplication                   ma;

    private static final String TAG = "GoogleActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showProgressDialog();
        //setContentView(R.layout.google_registration);

        //Local variable of logged user
        ma = MyApplication.getInstance();
        //Set persistence true to database
        mAuth = FirebaseAuth.getInstance();



    }


    @Override
    public void onStart() {
        super.onStart();
        //Check if user is logged or not
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null)
            logged(user);
        else
            notLogged();
        finish();
    }


    // [START on_stop_remove_listener]
    @Override
    public void onStop() {
        super.onStop();
    }

    private void logged(FirebaseUser user){
        //User is logged
        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

        //[START] Take all user data
        ma.setFirebaseId(user.getUid());
        ma.setUserEmail(user.getEmail());

        String[] items = user.getDisplayName().split(" ");
        if (items[0] != null)
            ma.setUserName(items[0]);
        else
            ma.setUserName("");

        if (items[1] != null)
            ma.setUserSurname(items[1]);
        else
            ma.setUserSurname("");

        ma.setFirebaseUser(user);
        ma.setLogged(true);
        //[END] Take all user data

        Intent intent = new Intent(CheckLogIn.this, CheckTelephone.class);
        startActivity(intent);
        //finish();



    }

    private void notLogged(){
        // User is signed out
        Log.d(TAG, "onAuthStateChanged:signed_out");
        ma.setLogged(false);
        Intent intent = new Intent(CheckLogIn.this, GoogleSignInActivity.class);
        startActivity(intent);
        //finish();

    }

}
