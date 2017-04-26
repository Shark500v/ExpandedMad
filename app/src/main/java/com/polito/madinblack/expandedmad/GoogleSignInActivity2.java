package com.polito.madinblack.expandedmad;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.polito.madinblack.expandedmad.GroupManaging.GroupListActivity;
import com.polito.madinblack.expandedmad.model.MyApplication;
import com.polito.madinblack.expandedmad.model.User;


/**
 * Created by Ale on 13/04/2017.
 */

public class GoogleSignInActivity2 extends BaseActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    // [START declare_auth_listener]
    private FirebaseAuth.AuthStateListener mAuthListener;
    // [END declare_auth_listener]

    private GoogleApiClient mGoogleApiClient;
    private TextView mStatusTextView;
    private TextView mDetailTextView;
    /*added by Ale*/
    private EditText mTelephoneTextView;

    /*added by Ale*/
    private DatabaseReference mDatabase;

    private String phoneNumber;

    private MyApplication ma;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_registration2);

        ma = MyApplication.getInstance();

        // Views
        mStatusTextView = (TextView) findViewById(R.id.status);
        mDetailTextView = (TextView) findViewById(R.id.detail);
        /*added by Ale to insert telephone*/
        mTelephoneTextView = (EditText) findViewById(R.id.telephone);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // Button listeners: to change and load only sign in of google and confirm
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);
        /*added by Ale to confirm telephone number*/
        findViewById(R.id.confirm_number_button).setOnClickListener(this);

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // [START initialize_auth]
        //
        // [END initialize_auth]

        /*added by Ale to set index*/
        //mDatabase = FirebaseDatabase.getInstance().getReference();


        // [START auth_state_listener]
        //mAuthListener = new FirebaseAuth.AuthStateListener() {
           // @Override
         //   public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
          //      FirebaseUser user = firebaseAuth.getCurrentUser();
            //    if (user != null) {
                    // User is signed in
              //      Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                //    firebaseId      = user.getUid();
                  //  userEmail       = user.getEmail();
                 //   String[] items  = user.getDisplayName().split(" ");
                   // userName        = items[0];
                    //userSurname     = items[1];

               //     mDatabase.child("userId").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                 //       @Override
                   //     public void onDataChange(DataSnapshot dataSnapshot) {
                     //       if (dataSnapshot.exists()) {

                       //         /*to memorize phone number of logged user*/
                         //       userPhoneNumber = dataSnapshot.getValue(String.class);
                           //     GroupListActivity.logged = true;
                                /*google login and number yet inserted jump to group page*/
                             //   Intent intent = new Intent(GoogleSignInActivity2.this, GroupListActivity.class);
                               // startActivity(intent);
                                //finish();

//                            }
  //                      }

    //                    @Override
      //                  public void onCancelled(DatabaseError databaseError) {

        //                }
          //          });



         //       } else {
                    // User is signed out
           //         Log.d(TAG, "onAuthStateChanged:signed_out");

             //   }

        //    }
      //  };
        // [END auth_state_listener]


        updateUI(ma.getFirebaseUser());



    }

    // [START on_start_add_listener]
    @Override
    public void onStart() {
        super.onStart();
        //Adds a listener that will be called when the connection becomes authenticated or unauthenticated.
       // mAuth.addAuthStateListener(mAuthListener);

    }
    // [END on_start_add_listener]

    // [START on_stop_remove_listener]
    @Override
    public void onStop() {
        super.onStop();
       // if (mAuthListener != null) {
         //   mAuth.removeAuthStateListener(mAuthListener);
        //}
    }
    // [END on_stop_remove_listener]

    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);

            } else {
                // Google Sign In failed, update UI appropriately
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(GoogleSignInActivity2.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        updateUI(null);
                    }
                });
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access (delete the information that your app obtained from the Google APIs)
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        updateUI(null);
                    }
                });
    }

    private void confirmNumber() {
        /*confirm number and add user detail*/
        phoneNumber = mTelephoneTextView.getText().toString();
        User.writeNewUser(mDatabase, ma.getFirebaseId(), ma.getUserName(),  ma.getUserSurname(), phoneNumber, ma.getUserEmail());
        mDatabase.child("userId").child(ma.getFirebaseId()).setValue(phoneNumber);
        finish();
        /*
        Intent intent = new Intent(GoogleSignInActivity2.this, GroupListActivity.class);
        startActivity(intent);
        */
    }




    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            /*
            mStatusTextView.setText("Google User: " + user.getEmail() + user.getDisplayName());
            mDetailTextView.setText("Firebase User: " + user.getUid());


            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        */
            mStatusTextView.setText("Sign in as: " + user.getDisplayName());
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.confirm_number_button).setVisibility(View.VISIBLE);
            findViewById(R.id.telephone).setVisibility(View.VISIBLE);
            findViewById(R.id.detail).setVisibility(View.VISIBLE);


        } else {

            mStatusTextView.setText(R.string.signed_out);
            //mDetailTextView.setText(null);

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);

            //findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
            findViewById(R.id.confirm_number_button).setVisibility(View.GONE);
            findViewById(R.id.telephone).setVisibility(View.GONE);
            findViewById(R.id.detail).setVisibility(View.GONE);


        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_in_button) {
            signIn();
        } else if (i == R.id.sign_out_button) {
            signOut();
        } else if (i == R.id.disconnect_button) {
            revokeAccess();
        }
        else if (i == R.id.confirm_number_button) {
            confirmNumber();
        }
    }


}
