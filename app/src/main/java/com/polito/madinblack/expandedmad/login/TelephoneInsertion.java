package com.polito.madinblack.expandedmad.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.groupManaging.GroupListActivity;
import com.polito.madinblack.expandedmad.model.CostUtil;
import com.polito.madinblack.expandedmad.model.Group;
import com.polito.madinblack.expandedmad.model.MyApplication;
import com.polito.madinblack.expandedmad.model.User;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Ale on 01/05/2017.
 */

public class TelephoneInsertion extends AppCompatActivity{


    private static final String TAG = "TelephoneInsertionActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;




    private TextView mStatusTextView;
    private EditText mTelephoneEditText;
    private EditText mInputInvitationEditText;

    private TextInputLayout mPhoneLayout;
    private TextInputLayout mInvitationLayout;
    private TextInputLayout mPrefixLayout;

    private Button mConfirmNumberButton;
    private AutoCompleteTextView mCountryAutoCompleteTextView;
    private EditText mPrefixTextView;

    private MyApplication ma;

    private DatabaseReference mDatabaseRoot;
    private DatabaseReference mDatabaseGroupReference;

    private String mPhoneNumber;
    private String mInvitationCode;
    private String mPrefix;
    private boolean isFirst;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_registration);


        final String [] countryPrefix = getResources().getStringArray(R.array.countries_prefix);
        final int countryPrefixSize = countryPrefix.length;
        String [] countries = new String[countryPrefixSize];
        final Map<String, String> countryPrefixMap = new LinkedHashMap<String, String>();
        final Map<String, String> prefixCountryMap = new HashMap<String, String>();
        for(int i=0; i<countryPrefixSize; i++){
            String [] splitted = countryPrefix[i].split(" ");
            countries[i] = splitted[0];
            countryPrefixMap.put(splitted[0], splitted[1]);
            prefixCountryMap.put(splitted[1], splitted [0]);
        }





        ma = MyApplication.getInstance();

        mDatabaseRoot = FirebaseDatabase.getInstance().getReference();



        mStatusTextView                 = (TextView) findViewById(R.id.status);
        mTelephoneEditText              = (EditText) findViewById(R.id.telephone);
        mInputInvitationEditText        = (EditText)findViewById(R.id.input_invitation);
        mPhoneLayout                    = (TextInputLayout)findViewById(R.id.telephone_layout);
        mInvitationLayout               = (TextInputLayout)findViewById(R.id.invitation_layout);
        mPrefixLayout                   = (TextInputLayout)findViewById(R.id.prefix_layout);
        mConfirmNumberButton            = (Button) findViewById(R.id.confirm_number_button);
        mCountryAutoCompleteTextView    = (AutoCompleteTextView) findViewById(R.id.country);
        mPrefixTextView                 = (EditText) findViewById(R.id.prefix);

        isFirst = true;

        mStatusTextView.setText(getString(R.string.signin_as) + " " + ma.getUserName() + " " + ma.getUserSurname());
        findViewById(R.id.fields).setVisibility(View.VISIBLE);
        findViewById(R.id.button_field).setVisibility(View.VISIBLE);
        findViewById(R.id.sign_in_button).setVisibility(View.GONE);
        mConfirmNumberButton.setVisibility(View.VISIBLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.list_element, countries);

        mCountryAutoCompleteTextView.setAdapter(adapter);

        mCountryAutoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountryAutoCompleteTextView.showDropDown();
            }
        });

        mCountryAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPrefixTextView.setText(countryPrefixMap.get((String)parent.getItemAtPosition(position)));
            }
        });

        mCountryAutoCompleteTextView.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if (s.length() != 0 && isFirst) {
                    if (countryPrefixMap.containsKey(s.toString())) {
                        isFirst = false;
                        mPrefixTextView.setText(countryPrefixMap.get(s.toString()));

                    }
                    else{
                        isFirst = false;
                        mPrefixTextView.setText("-");

                    }
                }else if(!isFirst){
                    isFirst = true;
                }



            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });

        mPrefixTextView.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if(s.length() != 0 && isFirst){
                    if(prefixCountryMap.containsKey(s.toString())){
                        isFirst = false;
                        mCountryAutoCompleteTextView.setText(prefixCountryMap.get(s.toString()));

                    }
                    else{
                        isFirst = false;
                        mCountryAutoCompleteTextView.setText("-");

                    }
                }else if(!isFirst){
                    isFirst = true;
                }

            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });



        mConfirmNumberButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                /*confirm number and add user detail*/
                mPhoneNumber = mTelephoneEditText.getText().toString();
                mInvitationCode = mInputInvitationEditText.getText().toString();
                mPrefix = mPrefixTextView.getText().toString();

                if(!CostUtil.validateTelFaxNumber(mPhoneNumber)) {

                    mPhoneLayout.setError(getString(R.string.err_msg_telephone));
                    requestFocus(mTelephoneEditText);
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    // Vibrate for 250 milliseconds
                    vibrator.vibrate(250);
                    return;
                }else if((mPrefix!=null && !mPrefix.isEmpty()) && (mPhoneNumber.toLowerCase().contains(mPrefixTextView.getText().toString().toLowerCase()) || mPhoneNumber.contains("+"))) {
                    mPhoneLayout.setError(getString(R.string.err_msg_prefix_number));
                    requestFocus(mTelephoneEditText);
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    // Vibrate for 250 milliseconds
                    vibrator.vibrate(250);
                    return;
                }
                else{
                    mPhoneLayout.setErrorEnabled(false);
                }

                if(!CostUtil.validateTelFaxNumber(mPrefix) || mPrefix.charAt(0)!='+'){
                    mPrefixLayout.setError(getString(R.string.err_msg_prefix));
                    requestFocus(mPrefixLayout);
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    // Vibrate for 250 milliseconds
                    vibrator.vibrate(250);
                    return;
                }



                mPhoneNumber = mPrefix + mPhoneNumber;
                ma.setUserPhoneNumber(mPhoneNumber);

                User.writeNewUser(mDatabaseRoot, ma.getFirebaseId(), ma.getUserName(),  ma.getUserSurname(), ma.getUserPhoneNumber(), ma.getUserEmail());

                ma.setIsPhone(true);


                if(mInvitationCode!=null && !mInvitationCode.isEmpty()){

                    mDatabaseGroupReference = mDatabaseRoot.child("groups/"+mInvitationCode);

                    mDatabaseGroupReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                String groupName = dataSnapshot.child("name").getValue(String.class);
                                Group.writeUserToGroup(mDatabaseRoot, mInvitationCode, groupName, ma.getFirebaseId(), ma.getUserPhoneNumber(), ma.getUserName(), ma.getUserSurname());
                                Intent intent = new Intent(TelephoneInsertion.this, GroupListActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                mInvitationLayout.setError(getString(R.string.err_msg_invitation));
                                requestFocus(mInputInvitationEditText);
                                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                // Vibrate for 250 milliseconds
                                vibrator.vibrate(250);

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else{
                    Intent intent = new Intent(TelephoneInsertion.this, GroupListActivity.class);
                    startActivity(intent);
                    finish();

                }

            }
        });

    }



    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }



}
