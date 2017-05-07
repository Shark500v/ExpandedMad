package com.polito.madinblack.expandedmad.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.groupManaging.GroupListActivity;
import com.polito.madinblack.expandedmad.model.MyApplication;

/**
 * Created by Ale on 01/05/2017.
 */

public class CheckTelephone extends BaseActivity {

    private ValueEventListener  mValueListener;
    private DatabaseReference   mDatabaseTelephoneReference;
    private MyApplication       ma;
    private TextView            mStatusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showProgressDialog();
        setContentView(R.layout.google_registration);

        ma = MyApplication.getInstance();

        mStatusTextView = (TextView) findViewById(R.id.status);

        mStatusTextView.setText("Sign in as: " + ma.getUserName() + " " +ma.getUserSurname());



        mDatabaseTelephoneReference = FirebaseDatabase.getInstance().getReference().child("registration/"+ma.getFirebaseId());

        /*maybe better to know if user is logged yet*/
        mValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ma.setUserPhoneNumber(dataSnapshot.getValue(String.class));
                    ma.setIsPhone(true);

                    /*google login and number yet inserted jump to group page*/
                    Intent intent = new Intent(CheckTelephone.this, GroupListActivity.class);
                    startActivity(intent);
                    finish();

                }else{

                    ma.setIsPhone(false);
                    Intent intent = new Intent(CheckTelephone.this, TelephoneInsertion.class);
                    startActivity(intent);
                    finish();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


    }


    @Override
    public void onStart() {
        super.onStart();
        mDatabaseTelephoneReference.addListenerForSingleValueEvent(mValueListener);
    }


}
