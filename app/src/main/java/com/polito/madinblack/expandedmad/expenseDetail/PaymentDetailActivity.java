package com.polito.madinblack.expandedmad.expenseDetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.login.BaseActivity;
import com.polito.madinblack.expandedmad.model.Balance;
import com.polito.madinblack.expandedmad.model.HistoryInfo;
import com.polito.madinblack.expandedmad.model.MyApplication;
import com.polito.madinblack.expandedmad.model.PaymentFirebase;
import com.polito.madinblack.expandedmad.model.PaymentInfo;


import java.util.HashMap;
import java.util.Map;

public class PaymentDetailActivity extends BaseActivity {

    public static final String ARG_EXPENSE_ID = "expenseId";
    public static final String ARG_GROUP_ID ="expenseName";
    public static final String ARG_EXPENSE_COST ="expenseCost";
    public static final String ARG_CURRENCY_SYMBOL ="currencySymbol";
    public static final String ARG_USER_NAME ="userName" ;
    public static final String ARG_USER_SURNAME = "userName";

    private DatabaseReference mDatabaseRootReference;
    private DatabaseReference mDatabasePaymentsReference;
    private RecyclerView recyclerView;

    private String expenseId;
    private String groupId;
    private String currencySymbol;
    private String expenseUserName;
    private String expenseUserSurname;
    private Double expenseCost;
    private MyApplication ma;
    private PaymentRecyclerAdapter mAdapter;
    Map<String, PaymentInfo> changedPayments;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_detail);

        ma = MyApplication.getInstance();

        expenseId           = getIntent().getStringExtra(ARG_EXPENSE_ID);
        groupId             = getIntent().getStringExtra(ARG_GROUP_ID);
        expenseCost         = Double.valueOf(getIntent().getStringExtra(ARG_EXPENSE_COST));
        currencySymbol      = getIntent().getStringExtra(ARG_CURRENCY_SYMBOL);
        expenseUserName     = getIntent().getStringExtra(ARG_USER_NAME);
        expenseUserSurname  = getIntent().getStringExtra(ARG_USER_SURNAME);

        mDatabaseRootReference = FirebaseDatabase.getInstance().getReference();
        mDatabasePaymentsReference = mDatabaseRootReference.child("expenses/"+expenseId+"/payments");



        //toolbar settings
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = (RecyclerView) findViewById(R.id.payment_list);


        changedPayments = new HashMap<>();
        mAdapter = new PaymentRecyclerAdapter(
                PaymentFirebase.class,
                R.layout.list_item,
                RecyclerView.ViewHolder.class,
                getApplicationContext(),
                mDatabasePaymentsReference,
                changedPayments
        );
        recyclerView.setAdapter(mAdapter);
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

        if (id == R.id.confirm_payment && changedPayments.size()>1) {

            showProgressDialog();

            Double totPaid = 0D;
            PaymentInfo paymentInfoUserPaid = changedPayments.remove(ma.getUserPhoneNumber());
            final Map<String, PaymentInfo> paymentToUpdate = new HashMap<>();
            paymentToUpdate.putAll(changedPayments);
            changedPayments.clear();



            for(String paymentKey : paymentToUpdate.keySet()){

                final PaymentInfo paymentInfo = paymentToUpdate.get(paymentKey);
                totPaid += paymentInfo.getPaidNow();

                mDatabaseRootReference
                        .child("groups/"+groupId+"/users/"+paymentInfo.getUserFirebaseId()+"/balances/"+paymentInfoUserPaid.getUserFirebaseId())
                        .runTransaction(new Transaction.Handler() {

                            @Override
                            public Transaction.Result doTransaction(MutableData currentData) {
                                if (currentData.getValue() != null) {
                                    Balance balance = currentData.getValue(Balance.class);
                                    for(MutableData currentDataChild : currentData.getChildren()){
                                        if(currentDataChild.getKey().equals("balance"))
                                            currentDataChild.setValue(balance.getBalance() + paymentToUpdate.get(balance.getUserPhoneNumber()).getPaidNow());
                                    }
                                }
                                return Transaction.success(currentData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError,
                                                   boolean committed, DataSnapshot currentData) {
                                //This method will be called once with the results of the transaction.
                                //Update remove the user from the group

                            
                            }
                        });



                mDatabaseRootReference
                        .child("groups/"+groupId+"/users/"+paymentInfoUserPaid.getUserFirebaseId()+"/balances/"+paymentInfo.getUserFirebaseId())
                        .runTransaction(new Transaction.Handler() {

                            @Override
                            public Transaction.Result doTransaction(MutableData currentData) {
                                if (currentData.getValue() != null) {
                                    Balance balance = currentData.getValue(Balance.class);
                                    for(MutableData currentDataChild : currentData.getChildren()){
                                        if(currentDataChild.getKey().equals("balance"))
                                            currentDataChild.setValue(balance.getBalance() - paymentToUpdate.get(balance.getUserPhoneNumber()).getPaidNow());
                                    }

                                }
                                return Transaction.success(currentData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError,
                                                   boolean committed, DataSnapshot currentData) {
                                //This method will be called once with the results of the transaction.
                                //Update remove the user from the group
                                //if secondo thread ferma showDialog

                            }
                        });


                mDatabaseRootReference
                        .child("users/"+paymentInfo.getUserPhoneNumber()
                                +"/"+paymentInfo.getUserFirebaseId()+"/groups/"+groupId
                                +"/expenses/"+expenseId+"/myBalance")
                        .setValue((paymentInfo.getPaidBefore()+paymentInfo.getPaidNow())-paymentInfo.getToPaid());


                mDatabaseRootReference
                        .child("expenses/"+expenseId+"/payments/"+paymentInfo.getId()+"/paid")
                        .setValue(paymentInfo.getPaidBefore()+paymentInfo.getPaidNow());


                /*update the history*/
                HistoryInfo historyInfo = new HistoryInfo(paymentInfo.getUserNameDisplayed(), 1L, expenseCost, currencySymbol, expenseUserName);
                mDatabaseRootReference.child("history/"+groupId).push().setValue(historyInfo);


            }

            mDatabaseRootReference
                    .child("users/"+paymentInfoUserPaid.getUserPhoneNumber()
                            +"/"+paymentInfoUserPaid.getUserFirebaseId()+"/groups/"+groupId
                            +"/expenses/"+expenseId+"/myBalance")
                    .setValue(paymentInfoUserPaid.getBalance()-totPaid);


            mDatabaseRootReference
                    .child("expenses/"+expenseId+"/payments/"+paymentInfoUserPaid.getId()+"/paid")
                    .setValue(paymentInfoUserPaid.getPaidBefore()-totPaid);

            hideProgressDialog();

            Toast.makeText(getApplicationContext(), getString(R.string.payment_update), Toast.LENGTH_SHORT);


        }else if(id == R.id.fill_all_paid){





        }else if(id == 16908332){
            Intent intent3 = new Intent(this, ExpenseDetailFragment.class);
            intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            navigateUpTo(intent3);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}