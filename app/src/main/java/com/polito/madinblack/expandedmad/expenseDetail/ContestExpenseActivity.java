package com.polito.madinblack.expandedmad.expenseDetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.polito.madinblack.expandedmad.model.CostUtil;
import com.polito.madinblack.expandedmad.model.Currency;
import com.polito.madinblack.expandedmad.model.Expense;
import com.polito.madinblack.expandedmad.model.HistoryInfo;
import com.polito.madinblack.expandedmad.model.MyApplication;
import com.polito.madinblack.expandedmad.model.Payment;
import com.polito.madinblack.expandedmad.model.PaymentFirebase;
import com.polito.madinblack.expandedmad.model.PaymentInfo;
import com.polito.madinblack.expandedmad.tabViewGroup.TabView;


import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ContestExpenseActivity extends BaseActivity {

    public static final String ARG_EXPENSE_ID = "expenseId";
    public static final String ARG_GROUP_ID ="expenseName";
    public static final String ARG_EXPENSE_COST ="expenseCost";
    public static final String ARG_CURRENCY_ISO ="currencyISO";
    public static final String ARG_EXPENSE_STATE = "expenseState";
    public static final String ARG_PAYMENT_CONTEST_ID = "paymentContestId";
    public static final String ARG_EXPENSE_USER_FIREBASEID = "expenseFirebaseId";
    public static final int RESULT_MODIFIED_YET = 2;
    public static final int RESULT_DELETED = 3;

    private DatabaseReference mDatabaseRootReference;
    private DatabaseReference mDatabasePaymentReference;
    private Query mDatabaseQueryFilter;


    private String expenseId;
    private String groupId;
    private Currency.CurrencyISO currencyISO;
    private Double expenseCost;
    private MyApplication ma;
    private ValueEventListener valueEventListener;
    private PaymentFirebase paymentFirebase;
    private Spinner mSpinnerCurrency;
    private TextView mExpenseCurrencySymbol;
    private TextView mOldToPayCurrencySymbol;
    private TextView mNewToPayCurrencySymbol;
    private TextView mExpenseCost;
    private TextView mOldToPay;
    private TextView mGeneratedByTextView;
    private EditText mNewToPay;
    private EditText mMotivationEditText;
    private TextInputLayout mMotivationLayout;
    private Expense.State expenseState;
    private String expenseUserFirebaseId;
    private List<PaymentFirebase> paymentFirebaseList;
    private List<Payment> paymentList;
    private String mPaymentContestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contest_layout);

        ma = MyApplication.getInstance();
        paymentFirebaseList = new ArrayList<>();
        paymentList = new ArrayList<>();

        expenseId             = getIntent().getStringExtra(ARG_EXPENSE_ID);
        groupId               = getIntent().getStringExtra(ARG_GROUP_ID);
        expenseCost           = Double.valueOf(getIntent().getStringExtra(ARG_EXPENSE_COST));
        currencyISO           = Currency.CurrencyISO.valueOf(getIntent().getStringExtra(ARG_CURRENCY_ISO));
        expenseState          = Expense.State.valueOf(getIntent().getStringExtra(ARG_EXPENSE_STATE));
        expenseUserFirebaseId = getIntent().getStringExtra(ARG_EXPENSE_USER_FIREBASEID);
        if(expenseState != Expense.State.ONGOING) {
            mPaymentContestId = getIntent().getStringExtra(ARG_PAYMENT_CONTEST_ID);
        }


        mDatabaseRootReference = FirebaseDatabase.getInstance().getReference();
        mDatabasePaymentReference = mDatabaseRootReference.child("expenses/" + expenseId + "/payments");
        //mDatabaseQueryFilter = mDatabasePaymentReference.orderByChild("userFirebaseId").equalTo(ma.getUserPhoneNumber(), "userFirebaseId");

        //toolbar settings
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(expenseState == Expense.State.ONGOING)
            toolbar.setTitle(getString(R.string.contention));
        else
            toolbar.setTitle(getString(R.string.contention_information));
        setSupportActionBar(toolbar);

        // Show the Up button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        mSpinnerCurrency = (Spinner) findViewById(R.id.currencyISO);
        mExpenseCurrencySymbol = (TextView) findViewById(R.id.expense_currency);
        mOldToPayCurrencySymbol = (TextView) findViewById(R.id.old_topay_currency);
        mNewToPayCurrencySymbol = (TextView) findViewById(R.id.new_topay_currency);
        mExpenseCost = (TextView) findViewById(R.id.expense_cost);
        mOldToPay = (TextView) findViewById(R.id.old_topay);
        mNewToPay = (EditText)  findViewById(R.id.new_topay);
        mMotivationEditText = (EditText)  findViewById(R.id.input_motivation);
        //mMotivationLayout = (TextInputLayout) findViewById(R.id.input_motivation_layout);
        mGeneratedByTextView = (TextView) findViewById(R.id.generated_by_name);


        mExpenseCurrencySymbol.setText(Currency.getSymbol(currencyISO));
        mOldToPayCurrencySymbol.setText(Currency.getSymbol(currencyISO));
        mNewToPayCurrencySymbol.setText(Currency.getSymbol(currencyISO));
        mExpenseCost.setText(expenseCost.toString());
        if(expenseState == Expense.State.ONGOING){
            mGeneratedByTextView.setText(getString(R.string.you));
        }



        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, Currency.getCurrencyValues(currencyISO));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCurrency.setAdapter(adapter);


        if(expenseState == Expense.State.ONGOING) {
            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        PaymentFirebase tmpPaymentFirebase;
                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {

                            tmpPaymentFirebase = childDataSnapshot.getValue(PaymentFirebase.class);
                            paymentFirebaseList.add(tmpPaymentFirebase);
                            if (tmpPaymentFirebase.getUserFirebaseId().equals(ma.getFirebaseId())) {
                                paymentFirebase = tmpPaymentFirebase;
                                mOldToPay.setText(String.format(Locale.getDefault(), "%.2f", paymentFirebase.getToPay()));
                            }


                        }

                        mSpinnerCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String item = (String) parent.getItemAtPosition(position);
                                mExpenseCurrencySymbol.setText(Currency.getSymbol(Currency.CurrencyISO.valueOf(item)));
                                mOldToPayCurrencySymbol.setText(Currency.getSymbol(Currency.CurrencyISO.valueOf(item)));
                                mNewToPayCurrencySymbol.setText(Currency.getSymbol(Currency.CurrencyISO.valueOf(item)));
                                mExpenseCost.setText(String.format(Locale.getDefault(), "%.2f", Currency.convertCurrency(expenseCost, currencyISO, Currency.CurrencyISO.valueOf(item))));
                                if (paymentFirebase != null && paymentFirebase.getUserFirebaseId().equals(ma.getFirebaseId()))
                                    mOldToPay.setText(String.format(Locale.getDefault(), "%.2f", Currency.convertCurrency(paymentFirebase.getToPay(), currencyISO, Currency.CurrencyISO.valueOf(item))));

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });



                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

        }else{

            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                            //memorizzare tutti gli user id e payment firebase solo se uguale a quello passato per parametro
                            PaymentFirebase tmpPaymentFirebase = childDataSnapshot.getValue(PaymentFirebase.class);
                            paymentFirebaseList.add(tmpPaymentFirebase);
                            if(childDataSnapshot.getValue(PaymentFirebase.class).getId().equals(mPaymentContestId))
                                paymentFirebase = tmpPaymentFirebase;

                        }


                            paymentFirebase = dataSnapshot.getValue(PaymentFirebase.class);
                            if (paymentFirebase.getUserFirebaseId().equals(ma.getFirebaseId())) {
                                mGeneratedByTextView.setText(getString(R.string.you));

                            }
                            else{
                                mGeneratedByTextView.setText(paymentFirebase.getUserFullName());
                            }
                        mOldToPay.setText(String.format(Locale.getDefault(), "%.2f", paymentFirebase.getToPay()));
                        mNewToPay.setText(String.format(Locale.getDefault(), "%.2f", paymentFirebase.getNewToPay()));
                        mNewToPay.setEnabled(false);
                        mMotivationEditText.setText(paymentFirebase.getMotivation());
                        mMotivationEditText.setEnabled(false);


                        mSpinnerCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String item = (String) parent.getItemAtPosition(position);
                                mExpenseCurrencySymbol.setText(Currency.getSymbol(Currency.CurrencyISO.valueOf(item)));
                                mOldToPayCurrencySymbol.setText(Currency.getSymbol(Currency.CurrencyISO.valueOf(item)));
                                mNewToPayCurrencySymbol.setText(Currency.getSymbol(Currency.CurrencyISO.valueOf(item)));
                                mExpenseCost.setText(String.format(Locale.getDefault(), "%.2f", Currency.convertCurrency(expenseCost, currencyISO, Currency.CurrencyISO.valueOf(item))));
                                if (paymentFirebase != null)
                                    mOldToPay.setText(String.format(Locale.getDefault(), "%.2f", Currency.convertCurrency(paymentFirebase.getToPay(), currencyISO, Currency.CurrencyISO.valueOf(item))));

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        if(expenseState== Expense.State.CONTESTED && paymentFirebase.getUserFirebaseId().equals(ma.getFirebaseId())){
                            (findViewById(R.id.confirm_delete_button_layout)).setVisibility(View.VISIBLE);
                            ((Button)findViewById(R.id.confirm_delete_button)).setText(getString(R.string.delete_contention));
                            (findViewById(R.id.confirm_delete_button)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DatabaseReference expenseStateReference = mDatabaseRootReference.child("expenses/"+expenseId+"/state");
                                    showProgressDialog();
                                    expenseStateReference.runTransaction(new Transaction.Handler() {
                                        @Override
                                        public Transaction.Result doTransaction(MutableData mutableData) {
                                            Expense.State expenseState = mutableData.getValue(Expense.State.class);
                                            if(expenseState == Expense.State.CONTESTED){
                                                mutableData.setValue(Expense.State.ONGOING);

                                            }else{
                                                return Transaction.abort();
                                            }


                                            return Transaction.success(mutableData);

                                        }

                                        @Override
                                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                            if(b){
                                                setResult(RESULT_DELETED);
                                                hideProgressDialog();
                                                finish();
                                            }
                                            else {
                                                setResult(RESULT_MODIFIED_YET);
                                                hideProgressDialog();
                                                finish();
                                            }
                                        }
                                    });
                                }
                            });

                        }else if(expenseState== Expense.State.CONTESTED && ma.getFirebaseId().equals(expenseUserFirebaseId)){
                            (findViewById(R.id.confirm_delete_button_layout)).setVisibility(View.VISIBLE);
                            ((Button)findViewById(R.id.confirm_delete_button)).setText(getString(R.string.confirm_contention));
                            (findViewById(R.id.confirm_delete_button)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showProgressDialog();
                                    DatabaseReference expenseStateReference = mDatabaseRootReference.child("expenses/"+expenseId+"/state");
                                    expenseStateReference.runTransaction(new Transaction.Handler() {
                                        @Override
                                        public Transaction.Result doTransaction(MutableData mutableData) {
                                            Expense.State expenseState = mutableData.getValue(Expense.State.class);
                                            if(expenseState == Expense.State.CONTESTED){
                                                final Long timestamp = -1*System.currentTimeMillis();
                                               //inside this part there will be the code to create the storno
                                                for(PaymentFirebase paymentFirebase : paymentFirebaseList){
                                                    mDatabaseRootReference.child("users").child(paymentFirebase.getUserPhoneNumber()).child(paymentFirebase.getUserFirebaseId())
                                                            .child("groups").child(groupId).child("timestamp").setValue(timestamp);
                                                    paymentList.add(new Payment(paymentFirebase, expenseId, true));

                                                }

                                                mDatabaseRootReference.child("expenses/"+expenseId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if(dataSnapshot.exists()){
                                                            Expense expense = dataSnapshot.getValue(Expense.class);
                                                            Expense.writeNewExpense(mDatabaseRootReference, expense.getName()+getString(R.string.transfer) , expense.getTag(), expense.getPaidByFirebaseId(), expense.getPaidByPhoneNumber(),
                                                                    expense.getPaidByName(), expense.getPaidBySurname(), expense.getCost(), expense.getRoundedCost(), expense.getCurrencyISO(),
                                                                    groupId, expense.getYear(), expense.getMonth(), expense.getDay(), expense.getDescription(), Expense.State.TRANSFER, timestamp, paymentList);


                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });


                                            }else{
                                                return Transaction.abort();
                                            }


                                            return Transaction.success(mutableData);

                                        }

                                        @Override
                                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                            if(b){
                                                setResult(RESULT_OK);
                                                hideProgressDialog();
                                                finish();
                                            }
                                            else {
                                                setResult(RESULT_MODIFIED_YET);
                                                hideProgressDialog();
                                                finish();
                                            }
                                        }
                                    });
                                }
                            });
                        }


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };




        }

        mNewToPay.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                String editString = CostUtil.replaceDecimalComma(s.toString());


                if(editString!=null && s.length() != 0 && CostUtil.isParsableAsDouble(editString) && Double.valueOf(editString)!=0){
                    if(Double.valueOf(editString)>Double.valueOf(CostUtil.replaceDecimalComma(mExpenseCost.getText().toString()))){
                        mNewToPay.setText(mExpenseCost.getText().toString());
                    }
                }
                else if(editString==null)
                {
                    mNewToPay.setError(getString(R.string.err_msg_correct_amount));
                    mNewToPay.setText("");
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });


    }


    @Override
    public void onStart(){
        super.onStart();
        if(mDatabasePaymentReference!=null && valueEventListener!=null)
            mDatabasePaymentReference.addListenerForSingleValueEvent(valueEventListener);

    }

    @Override
    public void onStop() {
        super.onStop();

    }


    @Override   //questo serve per il confirm e fill all payments
    public boolean onCreateOptionsMenu(Menu menu) {
        if(expenseState== Expense.State.ONGOING)
            getMenuInflater().inflate(R.menu.menu_contest, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.confirm_contention) {


            boolean focusRequest = false;
            final String newToPayString = CostUtil.replaceDecimalComma(mNewToPay.getText().toString());
            if(newToPayString==null || newToPayString.isEmpty() || !CostUtil.isParsableAsDouble(newToPayString)){
                mNewToPay.setError(getString(R.string.err_msg_correct_amount));
                requestFocus(mNewToPay);
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 250 milliseconds
                vibrator.vibrate(250);
                focusRequest = true;


            }

            if(mMotivationEditText.getText().toString().isEmpty()){
                mMotivationEditText.setError(getString(R.string.err_motivation));
                if(!focusRequest) {
                    requestFocus(mMotivationEditText);
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    // Vibrate for 250 milliseconds
                    vibrator.vibrate(250);

                }
                focusRequest = true;
            }

            //error case
            if(focusRequest)
                return true;

            showProgressDialog();

            //write inside expense the contest
            DatabaseReference expenseStateReference = mDatabaseRootReference.child("expenses/"+expenseId+"/state");
            expenseStateReference.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    Expense.State expenseState = mutableData.getValue(Expense.State.class);
                    if(expenseState == Expense.State.ONGOING){
                        mDatabaseRootReference.child("expenses/"+expenseId+"/paymentContestedId").setValue(paymentFirebase.getId());
                        mDatabaseRootReference.child("expenses/"+expenseId+"/payments/"+paymentFirebase.getId()+"/newToPay").setValue(Double.parseDouble(newToPayString));
                        mDatabaseRootReference.child("expenses/"+expenseId+"/payments/"+paymentFirebase.getId()+"/motivation").setValue(mMotivationEditText.getText().toString());
                        //mDatabaseRootReference.child("expenses/"+expenseId+"/timestamp/"+paymentFirebase.getId()+"/motivation");
                        mutableData.setValue(Expense.State.CONTESTED);

                        for(PaymentFirebase paymentFirebase : paymentFirebaseList){
                            mDatabaseRootReference.child("users").child(paymentFirebase.getUserPhoneNumber()).child(paymentFirebase.getUserFirebaseId())
                                                .child("groups").child(groupId).child("timestamp").setValue(null);

                        }



                    }else{
                        return Transaction.abort();
                    }


                    return Transaction.success(mutableData);

                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                    if(b){
                        setResult(RESULT_OK);
                        hideProgressDialog();
                        finish();
                    }
                    else {
                        setResult(RESULT_CANCELED);
                        hideProgressDialog();
                        finish();
                    }
                }
            });




        }else if(id == 16908332){
            Intent intent3 = new Intent(this, ExpenseDetailFragment.class);
            intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            navigateUpTo(intent3);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

}