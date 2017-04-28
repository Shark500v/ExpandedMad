package com.polito.madinblack.expandedmad.new_group;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.polito.madinblack.expandedmad.GroupManaging.GroupListActivity;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.model.MyApplication;

import java.io.Serializable;
import java.util.List;

public class InviteActivity extends AppCompatActivity {

    List<SelectUser> invite;
    private MyApplication ma;
    TaskStackBuilder ts = TaskStackBuilder.create(this);
    Intent intent1;
    String groupCode;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        //toolbar settings
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar. (bottone indietro nella pagina 2 che è quella che mostra la lista delle spese)
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ma = MyApplication.getInstance();
        invite = (List<SelectUser>) getIntent().getSerializableExtra("InviteList");
        groupCode = getIntent().getStringExtra("Code");

        //in questo punto il codice prende la lista principale e la mostra come recyclerview
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.invite_list);
        assert recyclerView != null;
        setupRecyclerView(recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override   //lo uso per le icone in alto a destra che posso selezionare
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.confirm_group:
                //manage here the invitation email, after some checks
                //prima di poter procedere devo verificare che tutte le email siano state inserite
                boolean flag = true;

                for(int i=0; i<invite.size(); i++){
                    if(invite.get(i).getEmail().compareTo("") == 0){
                        flag=false;         //viene settato a false se almeno una email non viene inserita
                    }
                }

                if(flag){
                    //posso procedere con l'activity successiva, ma prima devo inviare le email

                    //dovrei andare alla pagina del gruppo
                    intent1 = new Intent(InviteActivity.this, GroupListActivity.class);

                    sendEmail();

                    return true;
                }else{
                    //devo prima settare tutte le email
                    View mv = findViewById(R.id.frameLayout);
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    // Vibrate for 250 milliseconds
                    v.vibrate(250);
                    Snackbar.make(mv, "Please, compile all fields", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    return true;
                }

            case R.id.home:
                navigateUpTo(new Intent(this, SelectContact.class));    //definisco il parente verso cui devo tornare indietro
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void sendEmail(){

        String userName = ma.getUserName() + " " + ma.getUserSurname();         //devo passargli il nome dell'utente loggato
        String code = groupCode;                //devo passargli il codice da inviare nella mail
        String[] to = new String[invite.size()];//prendo la mail del/dei destinatari
        for(int i=0; i<invite.size();i++) {
            to[i] = invite.get(i).getEmail();
        }
        String subject = "[MadExpenses] " + userName + " has sent you an invite";
        String body = "<h1>" + userName + " has invited you on MadExpenses!<br></h1>" +
                "Hi,<br><br>" +
                userName + " would like to add you in his/her group.<br><br>" +
                "To join your friend " +
                //"<a href=\"%%APPINVITE_LINK_PLACEHOLDER%%\">install</a>" +
                "install" +
                " our app now using this code: " + code +".<br><br>" +
                "We are waiting for you on MadExpenses,<br><br>" +
                "Your MadExpenses Team";

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/html");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, fromHtml(body));

        try {
            //startActivity(Intent.createChooser(emailIntent, "Choose an Email client:"));
            startActivityForResult(Intent.createChooser(emailIntent, "Choose an Email client:"), 1);
        }catch (ActivityNotFoundException e){
            //non ci sono email clients sul telefono
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //lanciata quando l'activity precedente ritorna
        startActivity(intent1);
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    @Override   //questo serve per il search button
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_group, menu);

        return super.onCreateOptionsMenu(menu);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new InviteRecyclerViewAdapter(invite));
    }
}
