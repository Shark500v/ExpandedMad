package com.polito.madinblack.expandedmad.groupManaging;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.stream.StreamFileLoader;
import com.bumptech.glide.signature.StringSignature;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.polito.madinblack.expandedmad.FirebaseImageLoader;
import com.polito.madinblack.expandedmad.Settings.SettingsActivity;
import com.polito.madinblack.expandedmad.UserPage;
import com.polito.madinblack.expandedmad.login.Logout;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.StatisticsGraphs;
import com.polito.madinblack.expandedmad.tabViewGroup.TabView;
import com.polito.madinblack.expandedmad.model.MyApplication;
import com.polito.madinblack.expandedmad.model.*;
import com.polito.madinblack.expandedmad.newGroup.SelectContact;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private MyApplication ma;

    private static final String TAG = "GroupListActivity";

    private TextView tv1, tv2;

    private CircleImageView userImage;

    private DatabaseReference mUserGroupsReference;
    private Query mQueryUserGroupsReference;
    private DatabaseReference mDatabaseRootReference;
    private StorageReference mStorage;
    private StorageReference mUserStorage;
    private SimpleItemRecyclerViewAdapter mAdapter;
    private NavigationView navigationView;
    private static Map<String,String> groupImages = new HashMap<String,String>();
    private File userPicture;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.starting_layoute);

        ma = MyApplication.getInstance();   //retrive del DB

        mDatabaseRootReference = FirebaseDatabase.getInstance().getReference();

        mUserGroupsReference   = mDatabaseRootReference.child("users/"+ma.getUserPhoneNumber()+"/"+ma.getFirebaseId()+"/groups");
        mQueryUserGroupsReference = mUserGroupsReference.orderByChild("timestamp");

        mStorage = FirebaseStorage.getInstance().getReference();
        mUserStorage = FirebaseStorage.getInstance().getReference().child("users").child(ma.getFirebaseId()).child("userProfilePicture.jpg");

        //toolbar settings
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //le righe di codice di sotto servono al drower laterale che compare
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);  //setDrawerListener(toggle) --> addDrawerListener(toggle)
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //fine codice per Drawer

        //setto nome e cognome nella nav bar
        View header = navigationView.getHeaderView(0);
        userImage = (CircleImageView) header.findViewById(R.id.imageView);
        tv1 = (TextView) header.findViewById(R.id.textView2);
        tv2 = (TextView) header.findViewById(R.id.textView3);

        mUserStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).diskCacheStrategy(DiskCacheStrategy.SOURCE).error(R.drawable.teamwork).into(userImage);
            }
        });

        tv1.setText(ma.getUserName() + " " + ma.getUserSurname());
        tv2.setText(ma.getUserPhoneNumber());
    }

    @Override
    public void onStart(){
        super.onStart();
        //in questo punto il codice prende la lista principale e la mostra come recyclerview
        View recyclerView = findViewById(R.id.group_list);
        assert recyclerView != null;

        if(mQueryUserGroupsReference!=null) {
            mAdapter = new SimpleItemRecyclerViewAdapter(this, mQueryUserGroupsReference);
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

    @Override
    protected void onResume() {
        super.onResume();
        int size = navigationView.getMenu().size();
        for (int i = 0; i < size; i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_addgroup) {
            //handle add group activity
            Intent intent=new Intent(GroupListActivity.this, SelectContact.class);
            //intent.putExtra("phoneId",phoneId);
            startActivity(intent);
            // Handle the camera action
        } else if (id == R.id.nav_expenses) {
            Intent intent = new Intent(GroupListActivity.this, StatisticsGraphs.class);
            startActivity(intent);

        } else if (id == R.id.nav_settings){
            Intent intent = new Intent(GroupListActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            Logout fragment = new Logout();
            fragment.show(getSupportFragmentManager(), "LogoutFragment");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void userInfo(View view){
        Intent intent = new Intent(GroupListActivity.this, UserPage.class);
        startActivity(intent);
    }

    @Override   //questo serve per il search button
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_onlysearch, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        // Configure the search info and add any event listeners...

        return super.onCreateOptionsMenu(menu);
    }

    //questa classe la usa per fare il managing della lista che deve mostrare
    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private Context mContext;
        private Query mQueryReference;
        private ValueEventListener mEventListener;

        private List<GroupForUser> mValues = new ArrayList<>();
        private List<GroupForUser> duplicato = new ArrayList<>();


        public SimpleItemRecyclerViewAdapter(final Context context, Query ref) {
            mContext = context;
            mQueryReference = ref;

            // Create child event listener
            // [START child_event_listener_recycler]
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mValues.clear();
                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                        mValues.add(postSnapshot.getValue(GroupForUser.class));

                    }
                    notifyDataSetChanged();


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user!=null) {
                        Log.w(TAG, "Groups:onCancelled", databaseError.toException());
                        Toast.makeText(mContext, getString(R.string.fail_load_group),
                                Toast.LENGTH_SHORT).show();
                    }
                    }
            };
            if(ref!=null)
                ref.addValueEventListener(eventListener);
            // [END child_event_listener_recycler]

            // Store reference to listener so it can be removed on app stop
            mEventListener = eventListener;
        }

        @Override
        public GroupListActivity.SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SimpleItemRecyclerViewAdapter.ViewHolder holder, final int position) {
            holder.mItem = mValues.get(position);   //mValues.get(position) rappresenta un singolo elemento della nostra lista di gruppi
            if (mValues.get(position).getSize() > 1) {
                holder.mNumView.setText(Long.toString(mValues.get(position).getSize()) + " " + getString(R.string.members));
            } else {
                holder.mNumView.setText(Long.toString(mValues.get(position).getSize()) + " " + getString(R.string.member));
            }
            //holder.mImage.setImageBitmap(downlaoadGroupImage(mValues.get(position).getId()));
            holder.mContentView.setText(mValues.get(position).getName());
            if (mValues.get(position).getNewExpenses() != 0) {
                holder.mNotification.setText(mValues.get(position).getNewExpenses().toString());
                holder.mNotification.setVisibility(View.VISIBLE);
            }
            //sopra vengono settati i tre campi che costituisco le informazioni di ogni singolo gruppo, tutti pronti per essere mostriti nella gui

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Context context = v.getContext();

                    final DatabaseReference mDatabaseGroupForUserReference = mDatabaseRootReference.child("users/" + ma.getFirebaseId() + "/" + ma.getUserPhoneNumber() + "groups/" + holder.mItem.getId());

                    mDatabaseGroupForUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Intent intent = new Intent(context, TabView.class); //qui setto la nuova attività da mostrare a schermo dopo che clicco
                            intent.putExtra("groupIndex", holder.mItem.getId());    //passo alla nuova activity l'ide del gruppo chè l'utente ha selezionto
                            intent.putExtra("groupName", holder.mItem.getName());
                            context.startActivity(intent);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });

            final StorageReference groupRef = mStorage.child("groups").child(mValues.get(position).getId()).child("groupPicture").child("groupPicture.jpg");
            groupRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(getApplicationContext()).load(uri).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.mImage);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }   //ritorna il numero di elementi nella lista


        public void cleanupListener() {
            if (mEventListener != null)
                mQueryReference.removeEventListener(mEventListener);
        }

        //questa è una classe di supporto che viene usata per creare la vista a schermo, non ho ben capito come funziona
        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mNumView;
            public final TextView mContentView;
            public final TextView mNotification;
            public final CircleImageView mImage;

            public GroupForUser mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mNumView = (TextView) view.findViewById(R.id.num_members);
                mContentView = (TextView) view.findViewById(R.id.content);
                mNotification = (TextView) view.findViewById(R.id.notification);
                mImage = (CircleImageView) view.findViewById(R.id.group_image_storage);
            }

            @Override
            public String toString() {
                return super.toString();
            }
        }
    }

}
