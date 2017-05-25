package com.polito.madinblack.expandedmad.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.groupManaging.GroupListActivity;
import com.polito.madinblack.expandedmad.model.MyApplication;
import com.polito.madinblack.expandedmad.notification.Config;
import com.polito.madinblack.expandedmad.tabViewGroup.TabView;

import static com.polito.madinblack.expandedmad.notification.NotificationUtils.saveTokenOnDb;

/**
 * Created by Ale on 01/05/2017.
 */

public class CheckTelephone extends BaseActivity {

    private static final String TAG = "CheckTelephone";

    private ValueEventListener  mValueListener;
    private DatabaseReference   mDatabaseTelephoneReference;
    private MyApplication       ma;
    private TextView            mStatusTextView;
    private String groupIndex;
    private String groupName;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showProgressDialog();
        setContentView(R.layout.google_registration);

        ma = MyApplication.getInstance();

        mStatusTextView = (TextView) findViewById(R.id.status);

        mStatusTextView.setText(getString(R.string.signin_as) + " " + ma.getUserName() + " " +ma.getUserSurname());

        mDatabaseTelephoneReference = FirebaseDatabase.getInstance().getReference().child("registration/"+ma.getFirebaseId());

        /*maybe better to know if user is logged yet*/
        mValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ma.setUserPhoneNumber(dataSnapshot.getValue(String.class));
                    ma.setIsPhone(true);

                    //added for user token, useful for notification
                    String token = getUserToken();
                    saveTokenOnDb(token, ma.getUserPhoneNumber());

                    groupIndex = getIntent().getExtras().getString("groupIndex");
                    groupName  = getIntent().getExtras().getString("groupName");
                    index = getIntent().getExtras().getInt("request");
                    if(index == 1 || index == 2){
                        Intent intent = new Intent(CheckTelephone.this, TabView.class);
                        intent.putExtra("groupIndex", groupIndex);
                        intent.putExtra("groupName", groupName);
                        intent.putExtra("request", index);
                        startActivity(intent);
                    }else{
                        /*google login and number yet inserted jump to group page*/
                        Intent intent = new Intent(CheckTelephone.this, GroupListActivity.class);
                        startActivity(intent);
                    }
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

    private String getUserToken() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);

        //da spostare quando si fa login
        //saveTokenOnDb(regId);
        if (!TextUtils.isEmpty(regId))
            return regId;
        else
            return null;
    }


}
