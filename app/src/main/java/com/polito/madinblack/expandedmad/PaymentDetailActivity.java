package com.polito.madinblack.expandedmad;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.polito.madinblack.expandedmad.chat.ChatRecyclerViewAdapter;
import com.polito.madinblack.expandedmad.login.Logout;
import com.polito.madinblack.expandedmad.model.Expense;
import com.polito.madinblack.expandedmad.model.GroupForUser;
import com.polito.madinblack.expandedmad.model.Message;
import com.polito.madinblack.expandedmad.model.MyApplication;
import com.polito.madinblack.expandedmad.model.Payment;
import com.polito.madinblack.expandedmad.model.PaymentFirebase;
import com.polito.madinblack.expandedmad.newGroup.SelectContact;
import com.polito.madinblack.expandedmad.utility.PaymentRecyclerAdapter;
import com.polito.madinblack.expandedmad.utility.TabView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Ale on 11/05/2017.
 */


public class PaymentDetailActivity extends AppCompatActivity {

    public static final String ARG_EXPENSE_ID = "expenseId";
    public static final String ARG_EXPENSE_NAME ="expenseName" ;
    private MyApplication ma;

    private static final String TAG = "PaymentDetailActivity";

    private TextView tv1, tv2;

    private DatabaseReference mUserGroupsReference;
    private DatabaseReference mDatabaseRootReference;
    private StorageReference mStorage;
    private com.polito.madinblack.expandedmad.groupManaging.GroupListActivity.SimpleItemRecyclerViewAdapter mAdapter;
    private static Map<String,String> groupImages = new HashMap<String,String>();
    private RecyclerView recyclerView;
    private DatabaseReference ref;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_detail);

        ma = MyApplication.getInstance();   //retrive del DB

        mDatabaseRootReference = FirebaseDatabase.getInstance().getReference();

        mUserGroupsReference   = mDatabaseRootReference.child("users/"+ma.getUserPhoneNumber()+"/"+ma.getFirebaseId()+"/groups");
        mStorage = FirebaseStorage.getInstance().getReference();


        //toolbar settings
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = (RecyclerView) findViewById(R.id.payment_list);

        ref = FirebaseDatabase.getInstance().getReference().child("expenses");

        PaymentRecyclerAdapter mAdapter = new PaymentRecyclerAdapter(
                PaymentFirebase.class,
                R.layout.list_item,
                PaymentRecyclerAdapter.ViewHolder.class,
                ref
        );
        recyclerView.setAdapter(mAdapter);
    }

    //le due funzioni sottostanti servono al menù laterale che esce
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override   //questo serve per il search button
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_payment, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.confirm_payment) {


        }else if(id == R.id.fill_all_paid){




        }
        return super.onOptionsItemSelected(item);
    }

}