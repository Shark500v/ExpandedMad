package com.polito.madinblack.expandedmad.expenseDetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.polito.madinblack.expandedmad.FullScreenImage;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.tabViewGroup.TabView;

import de.hdodenhof.circleimageview.CircleImageView;

//questa classe viene richiamata dopo che clicco su di un gruppo
public class ExpenseDetailActivity extends AppCompatActivity {
    private CircleImageView expenseImage;
    private DatabaseReference mDatabaseForExpenseUrl;
    private String expenseId;
    private String expenseName;
    private String url;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_detail);
        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ExpenseDetailFragment.ARG_EXPENSE_ID, getIntent().getStringExtra(ExpenseDetailFragment.ARG_EXPENSE_ID));
            arguments.putString(ExpenseDetailFragment.ARG_EXPENSE_NAME, getIntent().getStringExtra(ExpenseDetailFragment.ARG_EXPENSE_NAME));
            ExpenseDetailFragment fragmentExpense;
            fragmentExpense = new ExpenseDetailFragment();
            fragmentExpense.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().add(R.id.expense_detail_container, fragmentExpense).commit();
        }
        expenseId = getIntent().getStringExtra(ExpenseDetailFragment.ARG_EXPENSE_ID);
        expenseName = getIntent().getStringExtra(ExpenseDetailFragment.ARG_EXPENSE_NAME);

        mDatabaseForExpenseUrl = FirebaseDatabase.getInstance().getReference().child("expenses").child(expenseId).child("urlImage");

        expenseImage = (CircleImageView)findViewById(R.id.expense_image);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                url = dataSnapshot.getValue(String.class);
                Glide.with(getApplicationContext()).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).error(R.drawable.bill).into(expenseImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };






        expenseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExpenseDetailActivity.this, FullScreenImage.class);
                intent.putExtra("imageUrl", url);
                intent.putExtra("expenseName", expenseName);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onStart(){
        super.onStart();

        if(mDatabaseForExpenseUrl!=null && valueEventListener!=null)
            mDatabaseForExpenseUrl.addValueEventListener(valueEventListener);


    }

    @Override
    public void onStop(){
        super.onStop();
        if(mDatabaseForExpenseUrl!=null && valueEventListener!=null)
            mDatabaseForExpenseUrl.removeEventListener(valueEventListener);


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
}
