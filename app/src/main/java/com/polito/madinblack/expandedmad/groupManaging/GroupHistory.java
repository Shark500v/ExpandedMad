package com.polito.madinblack.expandedmad.groupManaging;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.StyleSpan;
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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.groupMembers.GroupMemebersActivity;
import com.polito.madinblack.expandedmad.model.ExpenseForUser;
import com.polito.madinblack.expandedmad.model.HistoryInfo;
import com.polito.madinblack.expandedmad.model.MyApplication;
import com.polito.madinblack.expandedmad.utility.RecyclerViewAdapterUsers;
import com.polito.madinblack.expandedmad.utility.TabView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GroupHistory extends AppCompatActivity {

    private HistoryAdapter adapter;
    private RecyclerView recyclerView;
    private DatabaseReference mDatabaseReference;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_my);

        setContentView(R.layout.activity_group_history);

        //toolbar settings
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (savedInstanceState == null) {
            groupId = getIntent().getStringExtra("GROUP_ID");

            mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("history/"+groupId);

        }
        actionBar.setTitle(getString(R.string.group_history));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == 16908332){
            Intent intent = new Intent(this, TabView.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            navigateUpTo(intent);
            return true;
        }else
            return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStart(){
        super.onStart();


        adapter = new HistoryAdapter( this, mDatabaseReference);

        recyclerView = (RecyclerView) findViewById(R.id.cardList);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Clean up comments listener
        if(adapter!=null)
            adapter.cleanupListener();
    }


    private List<HistoryInfo> createList(int size) {

        List<HistoryInfo> result = new ArrayList<HistoryInfo>();
        for (int i=1; i <= size; i++) {
            HistoryInfo ci = new HistoryInfo();
            ci.setName("Name " + i);
            ci.setContent("Surname " + i + " Ciao ho aggiunto la spesa");
            ci.setDate(new Date());
            result.add(ci);

        }

        return result;
    }

    public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ContactViewHolder> {

        private List<HistoryInfo> mValues = new ArrayList<>();
        private DatabaseReference dataref;
        private List<String> mValuesIds = new ArrayList<>();
        private Context mContext;
        private ChildEventListener mChildEventListener;

        private static final String TAG = "MyBalanceActivity";

        public HistoryAdapter(Context ct, DatabaseReference dr) {
            dataref = dr;
            mContext = ct;


            // Create child event listener
            // [START child_event_listener_recycler]
            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                    // A new info has been added, add it to the displayed list
                    HistoryInfo historyInfo = dataSnapshot.getValue(HistoryInfo.class);


                    // [START_EXCLUDE]
                    // Update RecyclerView
                    if(getItemCount() == 0){
                        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.cardList);
                        TextView tx = (TextView) findViewById(R.id.textView);
                        tx.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    mValuesIds.add(dataSnapshot.getKey());
                    mValues.add(historyInfo);
                    notifyItemInserted(mValues.size() - 1);
                    // [END_EXCLUDE]

                }


                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                    // An info has changed, use the key to determine if we are displaying this
                    // info and if so displayed the changed info.
                    HistoryInfo historyInfo = dataSnapshot.getValue(HistoryInfo.class);
                    String infoKey = dataSnapshot.getKey();

                    // [START_EXCLUDE]
                    int infoIndex = mValuesIds.indexOf(infoKey);
                    if (infoIndex > -1) {
                        // Replace with the new data
                        mValues.set(infoIndex, historyInfo);

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
        public void onBindViewHolder(ContactViewHolder contactViewHolder, int i) {
            HistoryInfo ci = mValues.get(i);

            SpannableString spanString = new SpannableString(ci.getName() + " " + ci.getContent());
            spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, ci.getName().length(), 0);
            contactViewHolder.vContent.setText( spanString );
            contactViewHolder.vTitle.setText(ci.convertDateToString());
        }

        @Override
        public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.card_layout_history, viewGroup, false);

            return new ContactViewHolder(itemView);
        }

        public void cleanupListener() {
            if (mChildEventListener != null) {
                dataref.removeEventListener(mChildEventListener);
            }
        }

        public class ContactViewHolder extends RecyclerView.ViewHolder {

            protected TextView vContent;
            protected TextView vTitle;

            public ContactViewHolder(View v) {
                super(v);
                vContent = (TextView)  v.findViewById(R.id.txtContent);
                vTitle = (TextView) v.findViewById(R.id.title);
            }
        }
    }

}
