package com.polito.madinblack.expandedmad.balanceDetail;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.login.BaseActivity;
import com.polito.madinblack.expandedmad.model.BalanceHistory;
import com.polito.madinblack.expandedmad.model.Currency;
import com.polito.madinblack.expandedmad.model.MyApplication;
import com.polito.madinblack.expandedmad.model.Type;
import com.polito.madinblack.expandedmad.tabViewGroup.TabView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Ale on 02/06/2017.
 */

public class BalanceDetailActivity extends BaseActivity {

    public static final String ARG_GROUP_ID = "groupID";
    public static final String ARG_BALANCE_ID = "balanceID";
    public static final String ARG_USER_BALANCE_NAME = "userBalanceName";
    public static final String ARG_BALANCE_CURRENCY = "balanceCurrency";

    private String groupId;
    private String balanceId;
    private String userBalanceName;
    private Currency.CurrencyISO balanceCurrencyISO;
    private RecyclerView recyclerView;
    private BalanceAdapter mAdapter;
    private DatabaseReference mDatabaseBalanceHistoryReference;
    private TextView mTotValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balance_detail);



        groupId             = getIntent().getStringExtra(ARG_GROUP_ID);
        balanceId           = getIntent().getStringExtra(ARG_BALANCE_ID);
        userBalanceName     = getIntent().getStringExtra(ARG_USER_BALANCE_NAME);
        balanceCurrencyISO  = Currency.CurrencyISO.valueOf(getIntent().getStringExtra(ARG_BALANCE_CURRENCY));

        mDatabaseBalanceHistoryReference = FirebaseDatabase.getInstance().getReference()
                .child("groups/"+groupId+"/users/"+ MyApplication.getFirebaseId()+"/balances/"+balanceId+"/balancesHistory");

        ((TextView) findViewById(R.id.balance_toward)).setText(getString(R.string.balance_towards)+" "+userBalanceName);
        mTotValue = (TextView) findViewById(R.id.tot_value);


        //toolbar settings
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }




    }

    @Override
    public void onStart(){
        super.onStart();


        recyclerView = (RecyclerView) findViewById(R.id.balance_list);
        mAdapter = new BalanceAdapter(
                getApplicationContext(),
                mDatabaseBalanceHistoryReference
        );
        if(recyclerView!=null && mAdapter!=null)
            recyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onStop(){
        super.onStop();
        if(mAdapter!=null)
            mAdapter.cleanupListener();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, TabView.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            navigateUpTo(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public class BalanceAdapter extends RecyclerView.Adapter<BalanceAdapter.BalanceViewHolder> {

        private List<BalanceHistory> mValues = new ArrayList<>();
        private DatabaseReference dataref;
        private List<String> mValuesIds = new ArrayList<>();
        private Context mContext;
        private ChildEventListener mChildEventListener;
        private Double totValue;

        private static final String TAG = "BalanceDetailActivity";

        public BalanceAdapter(Context ct, DatabaseReference dr) {
            dataref = dr;
            mContext = ct;
            totValue = 0D;


            // Create child event listener
            // [START child_event_listener_recycler]
            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                    // A new info has been added, add it to the displayed list
                    BalanceHistory balanceHistory = dataSnapshot.getValue(BalanceHistory.class);

                    /*
                    // [START_EXCLUDE]
                    // Update RecyclerView
                    if(getItemCount() == 0){
                        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.cardList);
                        TextView tx = (TextView) findViewById(R.id.textView);
                        tx.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                    */
                    mValuesIds.add(0, dataSnapshot.getKey());
                    mValues.add(0, balanceHistory);
                    notifyItemInserted(0);
                    // [END_EXCLUDE]

                }


                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                    // An info has changed, use the key to determine if we are displaying this
                    // info and if so displayed the changed info.
                    BalanceHistory balanceHistory = dataSnapshot.getValue(BalanceHistory.class);
                    String infoKey = dataSnapshot.getKey();

                    // [START_EXCLUDE]
                    int infoIndex = mValuesIds.indexOf(infoKey);
                    if (infoIndex > -1) {
                        // Replace with the new data
                        mValues.set(infoIndex, balanceHistory);

                        // Update the RecyclerView
                        notifyItemChanged(infoIndex);

                    } else {
                        Log.w(TAG, "onChildChanged:unknown_child:" + infoKey);
                    }
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                    // An info has changed, use the key to determine if we are displaying this
                    // info and if so remove it.
                    String infoKey = dataSnapshot.getKey();

                    // [START_EXCLUDE]
                    int infoIndex = mValuesIds.indexOf(infoKey);
                    if (infoIndex > -1) {
                        // Remove data from the list
                        mValuesIds.remove(infoKey);
                        mValues.remove(infoIndex);

                        // Update the RecyclerView
                        notifyItemRemoved(infoIndex);
                    } else {
                        Log.w(TAG, "onChildRemoved:unknown_child:" + infoKey);
                    }
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                    // A comment has changed position, use the key to determine if we are
                    // displaying this comment and if so move it.
                    //Group movedGroup = dataSnapshot.getValue(Group.class);
                    //String groupKey = dataSnapshot.getKey();

                    // ...
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "Groups:onCancelled", databaseError.toException());
                    Toast.makeText(mContext, "Failed to load expenses.",
                            Toast.LENGTH_SHORT).show();
                }
            };
            dataref.addChildEventListener(childEventListener);
            // [END child_event_listener_recycler]

            // Store reference to listener so it can be removed on app stop
            mChildEventListener = childEventListener;
        }


        @Override
        public int getItemCount() {
            return mValues.size();
        }

        @Override
        public void onBindViewHolder(BalanceAdapter.BalanceViewHolder balanceViewHolder, int i) {
            BalanceHistory balanceHistory = mValues.get(i);
            totValue += balanceHistory.getValue();

            if(totValue>0) {
                mTotValue.setText(String.format(Locale.getDefault(), "+%.2f", Currency.convertCurrency(totValue, balanceCurrencyISO, MyApplication.getCurrencyISOFavorite()))+ " " + Currency.getSymbol(MyApplication.getCurrencyISOFavorite()));
                mTotValue.setTextColor(Color.parseColor("#00c200"));
            }
            else if(totValue<0){
                mTotValue.setText(String.format(Locale.getDefault(), "%.2f", Currency.convertCurrency(totValue, balanceCurrencyISO, MyApplication.getCurrencyISOFavorite()))+ " " + Currency.getSymbol(MyApplication.getCurrencyISOFavorite()));
                mTotValue.setTextColor(Color.parseColor("#ff0000"));
            }else{
                mTotValue.setText(String.format(Locale.getDefault(), "%.2f", Currency.convertCurrency(totValue, balanceCurrencyISO, MyApplication.getCurrencyISOFavorite()))+ " " + Currency.getSymbol(MyApplication.getCurrencyISOFavorite()));
            }


            String textType;
            balanceViewHolder.mExpenseName.setText(balanceHistory.getExpenseName());
            if(balanceHistory.getType()== Type.NEW_EXPENSE){
                textType = mContext.getResources().getString(R.string.new_expense);
                balanceViewHolder.mExpenseName.setTextColor(Color.parseColor("#009688"));
            }else if(balanceHistory.getType()== Type.SETTLE_UP) {
                textType = mContext.getResources().getString(R.string.settle_up_of);
                balanceViewHolder.mExpenseName.setTextColor(Color.parseColor("#009688"));
            }else if(balanceHistory.getType()== Type.STORNED) {
                textType = mContext.getResources().getString(R.string.storned);
                balanceViewHolder.mExpenseName.setTextColor(Color.parseColor("#ff0000"));
            }else {
                textType = "";
            }

            balanceViewHolder.mType.setText(textType);
            if(balanceHistory.getValue()>0) {
                balanceViewHolder.mValue.setText(String.format(Locale.getDefault(), "+%.2f", Currency.convertCurrency(balanceHistory.getValue(), balanceCurrencyISO, MyApplication.getCurrencyISOFavorite()))
                        + " " + Currency.getSymbol(MyApplication.getCurrencyISOFavorite()));
                balanceViewHolder.mValue.setTextColor(Color.parseColor("#00c200"));
            }else if(balanceHistory.getValue()<0){
                balanceViewHolder.mValue.setText(String.format(Locale.getDefault(), "%.2f", Currency.convertCurrency(balanceHistory.getValue(), balanceCurrencyISO, MyApplication.getCurrencyISOFavorite()))
                        + " " +  Currency.getSymbol(MyApplication.getCurrencyISOFavorite()));
                balanceViewHolder.mValue.setTextColor(Color.parseColor("#ff0000"));
            }else{
                balanceViewHolder.mValue.setText(String.format(Locale.getDefault(), "%.2f", Currency.convertCurrency(balanceHistory.getValue(), balanceCurrencyISO, MyApplication.getCurrencyISOFavorite()))
                        + " " + Currency.getSymbol(MyApplication.getCurrencyISOFavorite()));
            }

            balanceViewHolder.mDate.setText(balanceHistory.convertDateToString());




        }


        @Override
        public BalanceViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.balance_list_item, viewGroup, false);
            View textView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.balance_list, viewGroup, false);
            return new BalanceViewHolder(itemView, textView);
        }

        public void cleanupListener() {
            if (mChildEventListener != null) {
                dataref.removeEventListener(mChildEventListener);
            }
        }

        public class BalanceViewHolder extends RecyclerView.ViewHolder {

            protected TextView mType;
            protected TextView mExpenseName;
            protected TextView mValue;
            protected TextView mDate;

            public BalanceViewHolder(View v, View textView) {
                super(v);
                mType = (TextView)  v.findViewById(R.id.type);
                mExpenseName = (TextView) v.findViewById(R.id.expense_name);
                mValue = (TextView) v.findViewById(R.id.value);
                mDate = (TextView) v.findViewById(R.id.date);
            }
        }
    }

}


