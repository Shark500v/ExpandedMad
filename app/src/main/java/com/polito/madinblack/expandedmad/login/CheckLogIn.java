package com.polito.madinblack.expandedmad.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.model.Currency;
import com.polito.madinblack.expandedmad.model.MyApplication;

import java.util.Locale;

/**
 * Created by Ale on 26/04/2017.
 */

public class CheckLogIn extends BaseActivity {


    private FirebaseAuth                    mAuth;
    static boolean calledAlready            = false;

    private String groupIndex;
    private String groupName;
    private int index;

    private static final String TAG = "GoogleActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showProgressDialog();
        //setContentView(R.layout.google_registration);
        if (!calledAlready) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        }


        if(getIntent().getExtras()!=null){
            groupIndex = getIntent().getExtras().getString("groupIndex");
            groupName  = getIntent().getExtras().getString("groupName");
            index = getIntent().getExtras().getInt("request");
        }

        //for the preferences
        setPreferences();

        //Set persistence true to database
        mAuth = FirebaseAuth.getInstance();
    }

    private void setPreferences(){

        PreferenceManager.setDefaultValues(this, R.xml.pref_fragmentsettings, false);

        //recupero le impostazioni dell'utente
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String curValue = preferences.getString("pref_key_currency", "error");

        if (curValue.compareTo("error")!=0){
            //inserisci quì il codice
            MyApplication.setCurrencyISOFavorite(Currency.getCurrencyISO(Integer.valueOf(curValue)));
        }

        languageSettings(preferences);
    }

    private void languageSettings (SharedPreferences pref){

        String LangValue = pref.getString("pref_key_language", "error");

        if(LangValue.compareTo("0") == 0){
            //codice eseguito solo al primo avvio
            String lan;
            lan = Locale.getDefault().getDisplayLanguage();

            if(lan.compareTo("italiano") == 0){
                //trattando solo due lingue analizzo il caso italiano, tutti gli altri casi saranno in inglese
                pref.edit().putString("pref_key_language", "1").apply();    //setto la lingua di default del device come default
            }else {
                pref.edit().putString("pref_key_language", "2").apply();
            }
        } else {
            //in questo caso avrà sicuramente il valore 1 o 2, quindi devo settare la lingua corretta
            if (LangValue.compareTo("1") == 0){
                setLocale("it");
            }else {
                setLocale("en");
            }
        }
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
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
        MyApplication.setFirebaseId(user.getUid());
        MyApplication.setUserEmail(user.getEmail());

        String[] items = user.getDisplayName().split(" ");
        if (items[0] != null)
            MyApplication.setUserName(items[0]);
        else
            MyApplication.setUserName("");

        if (items[1] != null)
            MyApplication.setUserSurname(items[1]);
        else
            MyApplication.setUserSurname("");

        //[END] Take all user data

        Intent intent = new Intent(CheckLogIn.this, CheckTelephone.class);
        if(groupIndex!= null){
            intent.putExtra("groupIndex", groupIndex);
            intent.putExtra("groupName", groupName);
            intent.putExtra("request", index);
        }
        startActivity(intent);
    }

    private void notLogged(){
        // User is signed out
        Log.d(TAG, "onAuthStateChanged:signed_out");
        Intent intent = new Intent(CheckLogIn.this, GoogleSignInActivity.class);
        if(groupIndex!= null){
            intent.putExtra("groupIndex", groupIndex);
            intent.putExtra("groupName", groupName);
            intent.putExtra("request", index);
        }
        startActivity(intent);
    }

}
