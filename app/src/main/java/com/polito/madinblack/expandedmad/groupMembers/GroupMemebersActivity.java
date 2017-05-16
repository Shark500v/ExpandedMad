package com.polito.madinblack.expandedmad.groupMembers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.polito.madinblack.expandedmad.R;
import java.util.ArrayList;
import java.util.List;
import com.polito.madinblack.expandedmad.model.MyApplication;
import com.polito.madinblack.expandedmad.model.UserForGroup;
import com.polito.madinblack.expandedmad.tabViewGroup.TabView;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupMemebersActivity extends AppCompatActivity {


    public String groupID = "init";
    private String name = "hello";
    private MyApplication ma;

    private static final String TAG = "GroupMemebersActivity";

    private DatabaseReference mUserGroupsReference;
    private DatabaseReference mDatabase;
    private StorageReference mStorageReference;
    private SimpleItemRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users_of_group);

        groupID = getIntent().getStringExtra("GROUP_ID");
        name = getIntent().getStringExtra("GROUP_NAME");

        ma = MyApplication.getInstance();

        mUserGroupsReference = FirebaseDatabase.getInstance().getReference()
                .child("groups").child(groupID).child("users");
        mDatabase = FirebaseDatabase.getInstance().getReference();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(name);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public void onStart(){
        super.onStart();
        //in questo punto il codice prende la lista principale e la mostra come recyclerview
        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;

        if(mUserGroupsReference!=null) {
            mAdapter = new SimpleItemRecyclerViewAdapter(this, mUserGroupsReference);
            ((RecyclerView) recyclerView).setAdapter(mAdapter);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // Clean up comments listener
        if(mAdapter!=null)
            mAdapter.cleanupListener();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, TabView.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            navigateUpTo(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private Context mContext;
        private DatabaseReference mDatabaseReference;
        private ChildEventListener mChildEventListener;

        private final List<UserForGroup> mValues = new ArrayList<>();
        private List<String> mValuesIds = new ArrayList<>();

        public SimpleItemRecyclerViewAdapter(final Context context, DatabaseReference ref) {
            mContext = context;
            mDatabaseReference = ref;

            // Create child event listener
            // [START child_event_listener_recycler]
            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                    // A new user has been added, add it to the displayed list
                    UserForGroup userForGroup = dataSnapshot.getValue(UserForGroup.class);

                    // [START_EXCLUDE]
                    // Update RecyclerView
                    if(!userForGroup.getPhoneNumber().equals(ma.getUserPhoneNumber())){
                        mValuesIds.add(dataSnapshot.getKey());
                        mValues.add(userForGroup);
                        notifyItemInserted(mValues.size() - 1);
                    }
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so displayed the changed comment.
                    UserForGroup userForGroup = dataSnapshot.getValue(UserForGroup.class);
                    String userKey = dataSnapshot.getKey();

                    // [START_EXCLUDE]
                    int userIndex = mValuesIds.indexOf(userKey);
                    if (userIndex > -1) {
                        // Replace with the new data
                        mValues.set(userIndex, userForGroup);

                        // Update the RecyclerView
                        notifyItemChanged(userIndex);
                    } else {
                        Log.w(TAG, "onChildChanged:unknown_child:" + userKey);
                    }
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so remove it.
                    String userKey = dataSnapshot.getKey();

                    // [START_EXCLUDE]
                    int userIndex = mValuesIds.indexOf(userKey);
                    if (userIndex > -1) {
                        // Remove data from the list
                        mValuesIds.remove(userIndex);
                        mValues.remove(userIndex);

                        // Update the RecyclerView
                        notifyItemRemoved(userIndex);
                    } else {
                        Log.w(TAG, "onChildRemoved:unknown_child:" + userKey);
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
                    Toast.makeText(mContext, getString(R.string.fail_load_group),
                            Toast.LENGTH_SHORT).show();
                }
            };
            ref.addChildEventListener(childEventListener);
            // [END child_event_listener_recycler]

            // Store reference to listener so it can be removed on app stop
            mChildEventListener = childEventListener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            final UserForGroup us = holder.mItem;
            mStorageReference = FirebaseStorage.getInstance().getReference().child("users").child(mValues.get(position).getFirebaseId()).child("userProfilePicture.jpg");
            mStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(mContext).load(uri).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.mImage);
                }
            });
            holder.mIdView.setText(mValues.get(position).getName() + " " + mValues.get(position).getSurname());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, UserDebts.class);
                    Bundle extras = new Bundle();
                    extras.putString("GROUP_ID",groupID);
                    extras.putString("FIREBASE_ID",us.getFirebaseId());
                    extras.putString("NAME",us.getName());
                    extras.putString("SURNAME", us.getSurname());
                    intent.putExtras(extras);

                    context.startActivity(intent);

                }
            });
            holder.mContentView.setText("");
        }

        public void cleanupListener() {
            if (mChildEventListener != null) {
                mDatabaseReference.removeEventListener(mChildEventListener);
            }
        }


        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final CircleImageView mImage;
            public final TextView mIdView;
            public final TextView mContentView;
            public UserForGroup mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImage = (CircleImageView) view.findViewById(R.id.user_icon);
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
