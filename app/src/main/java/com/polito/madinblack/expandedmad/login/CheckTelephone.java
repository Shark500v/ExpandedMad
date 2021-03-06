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

public class CheckTelephone extends BaseActivity {

    private static final String TAG = "CheckTelephone";

    private ValueEventListener  mValueListener;
    private DatabaseReference   mDatabaseTelephoneReference;
    private TextView            mStatusTextView;
    private String groupIndex;
    private String groupName;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showProgressDialog();
        setContentView(R.layout.google_registration);

        mStatusTextView = (TextView) findViewById(R.id.status);
        mStatusTextView.setText(getString(R.string.signin_as) + " " + MyApplication.getUserName() + " " +MyApplication.getUserSurname());


    }


    @Override
    public void onStart() {
        super.onStart();
        if(getUserPhone()!=null){
            MyApplication.setUserPhoneNumber(getUserPhone());


            //added for user token, useful for notification
            String token = getUserToken();
            saveTokenOnDb(token, MyApplication.getUserPhoneNumber());

                    /*google login and number yet inserted jump to group page*/
            Intent intent = new Intent(CheckTelephone.this, GroupListActivity.class);
            if(getIntent().getExtras()!=null){
                groupIndex = getIntent().getExtras().getString("groupIndex");
                groupName  = getIntent().getExtras().getString("groupName");
                index = getIntent().getExtras().getInt("request");
                if(index == 1 || index == 2){
                    intent.putExtra("groupIndex", groupIndex);
                    intent.putExtra("groupName", groupName);
                    intent.putExtra("request", index);
                    startActivity(intent);
                }
            }
            startActivity(intent);
            finish();

        }else{
            Intent intent = new Intent(CheckTelephone.this, TelephoneInsertion.class);
            startActivity(intent);
            finish();



        }
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

    private String getUserPhone(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        return pref.getString("phone", null);
    }


}
