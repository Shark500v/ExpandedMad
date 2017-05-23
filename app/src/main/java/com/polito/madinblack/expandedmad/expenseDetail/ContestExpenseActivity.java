package com.polito.madinblack.expandedmad.expenseDetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.login.BaseActivity;
import com.polito.madinblack.expandedmad.model.Balance;
import com.polito.madinblack.expandedmad.model.Currency;
import com.polito.madinblack.expandedmad.model.HistoryInfo;
import com.polito.madinblack.expandedmad.model.MyApplication;
import com.polito.madinblack.expandedmad.model.PaymentFirebase;
import com.polito.madinblack.expandedmad.model.PaymentInfo;
import com.polito.madinblack.expandedmad.tabViewGroup.TabView;


import java.util.HashMap;
import java.util.Map;

public class ContestExpenseActivity extends BaseActivity {

    public static final String ARG_EXPENSE_ID = "expenseId";
    public static final String ARG_GROUP_ID ="expenseName";
    public static final String ARG_EXPENSE_COST ="expenseCost";
    public static final String ARG_CURRENCY_ISO ="currencyISO";

    private DatabaseReference mDatabaseRootReference;
    private DatabaseReference mDatabasePaymentReference;
    private Query mDatabaseQueryFilter;


    private String expenseId;
    private String groupId;
    private Currency.CurrencyISO currencyISO;
    private Double expenseCost;
    private MyApplication ma;
    private ValueEventListener valueEventListener;
    private String paymentId;
    private PaymentFirebase paymentFirebase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contest_expense);

        ma = MyApplication.getInstance();

        expenseId           = getIntent().getStringExtra(ARG_EXPENSE_ID);
        groupId             = getIntent().getStringExtra(ARG_GROUP_ID);
        expenseCost         = Double.valueOf(getIntent().getStringExtra(ARG_EXPENSE_COST));
        currencyISO         = Currency.CurrencyISO.valueOf(getIntent().getStringExtra(ARG_CURRENCY_ISO));


        mDatabaseRootReference = FirebaseDatabase.getInstance().getReference();
        mDatabasePaymentReference = mDatabaseRootReference.child("expenses/"+expenseId+"/payments");
        mDatabaseQueryFilter = mDatabasePaymentReference.equalTo(ma.getUserPhoneNumber(), "userFirebaseId");


        //toolbar settings
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.contention));
        setSupportActionBar(toolbar);

        // Show the Up button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot childDataSnapshot : dataSnapshot.getChildren()){
                        paymentId = childDataSnapshot.getKey();
                        paymentFirebase = childDataSnapshot.getValue(PaymentFirebase.class);
                        if(paymentFirebase.getUserFirebaseId().equals(ma.getFirebaseId())) {
                            ((TextView) findViewById(R.id.expense_cost)).setText(expenseCost.toString());
                            ((TextView) findViewById(R.id.old_topay)).setText(paymentFirebase.getToPay().toString());
                        }


                    }




                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };



    }


    @Override
    public void onStart(){
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }


    @Override   //questo serve per il confirm e fill all payments
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_payment, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
/*
        if (id == R.id.confirm_contention) {


        }else if(id == 16908332){
            Intent intent3 = new Intent(this, ExpenseDetailFragment.class);
            intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            navigateUpTo(intent3);
            return true;
        }
*/
        return super.onOptionsItemSelected(item);
    }



}