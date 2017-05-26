package com.polito.madinblack.expandedmad.groupManaging;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
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
import com.google.firebase.database.ValueEventListener;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.model.Currency;
import com.polito.madinblack.expandedmad.model.HistoryInfo;
import com.polito.madinblack.expandedmad.tabViewGroup.TabView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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


        adapter = new HistoryAdapter(getApplicationContext(), mDatabaseReference);

        recyclerView = (RecyclerView) findViewById(R.id.cardList);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        /*recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);*/
    }

    @Override
    public void onStop() {
        super.onStop();

        // Clean up comments listener
        if(adapter!=null)
            adapter.cleanupListener();
    }


    public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

        private List<HistoryInfo> mValues = new ArrayList<>();
        private DatabaseReference dataref;
        private List<String> mValuesIds = new ArrayList<>();
        private Context mContext;
        private ValueEventListener mEventListener;

        private static final String TAG = "MyBalanceActivity";

        public HistoryAdapter(Context ct, DatabaseReference dr) {
            dataref = dr;
            mContext = ct;


            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mValues.clear();
                    mValuesIds.clear();
                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                            mValuesIds.add(dataSnapshot.getKey());
                            mValues.add(childDataSnapshot.getValue(HistoryInfo.class));

                        }/*
                        if(getItemCount() == 0){
                            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.cardList);
                            TextView tx = (TextView) findViewById(R.id.textView);
                            tx.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }*/
                        notifyDataSetChanged();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };


            dataref.addValueEventListener(valueEventListener);
            // [END child_event_listener_recycler]

            // Store reference to listener so it can be removed on app stop
            mEventListener = valueEventListener;
        }


        @Override
        public int getItemCount() {
            return mValues.size();
        }

        @Override
        public void onBindViewHolder(HistoryAdapter.ViewHolder holder, int i) {
            holder.mItem = mValues.get(i);

            String message = getMessageFromNumber(holder.mItem);
            SpannableString spanString = new SpannableString(holder.mItem.getName() + " " + message );
            spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, holder.mItem.getName().length(), 0);
            holder.vContent.setText( spanString );
            holder.vTitle.setText(holder.mItem.convertDateToString());
        }

        private String getMessageFromNumber(HistoryInfo hi){
            long l = hi.getContent();
            switch ((int)l){
                case 0:
                    return getString(R.string.history_expense)+ " "+String.format(Locale.getDefault(), "%.2f",hi.getCost()) +" "+ Currency.toString(hi.getCurrencyISO());
                case 1:
                    return getString(R.string.history_payment_part1)+ " "+ hi.getPaidTo()+ " "
                            +getString(R.string.history_payment_part2)+String.format(Locale.getDefault(), "%.2f",hi.getCost()) +" "+Currency.toString(hi.getCurrencyISO());
                case 2:
                    return getString(R.string.history_new_memeber);
                case 3:
                    return getString(R.string.history_new_group);
                case 4:
                    return getString(R.string.history_transfer_part1) + " "+ hi.getExpenseName() + " "
                            + getString(R.string.history_transfer_part2);
                default:
                    return "Error";
            }
        }

        @Override
        public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.card_layout_history, viewGroup, false);

            return new HistoryAdapter.ViewHolder(itemView);
        }

        public void cleanupListener() {
            if (mEventListener != null) {
                dataref.removeEventListener(mEventListener);
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView vContent;
            public TextView vTitle;
            public HistoryInfo mItem;

            public ViewHolder(View v) {
                super(v);
                vContent = (TextView)  v.findViewById(R.id.txtContent);
                vTitle = (TextView) v.findViewById(R.id.title);
            }
        }
    }

}
