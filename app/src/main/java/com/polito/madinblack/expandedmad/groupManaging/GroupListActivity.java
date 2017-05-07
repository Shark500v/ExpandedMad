package com.polito.madinblack.expandedmad.groupManaging;

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
import com.polito.madinblack.expandedmad.login.Logout;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.StatisticsGraphs;
import com.polito.madinblack.expandedmad.utility.TabView;
import com.polito.madinblack.expandedmad.model.MyApplication;
import com.polito.madinblack.expandedmad.model.*;
import com.polito.madinblack.expandedmad.newGroup.SelectContact;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private MyApplication ma;

    private static final String TAG = "GroupListActivity";

    private TextView tv1, tv2;

    private DatabaseReference mUserGroupsReference;
    private DatabaseReference mDatabaseRootReference;
    private StorageReference mStorage;
    private SimpleItemRecyclerViewAdapter mAdapter;
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.starting_layoute);

        ma = MyApplication.getInstance();   //retrive del DB

       // FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mDatabaseRootReference = FirebaseDatabase.getInstance().getReference();

        mUserGroupsReference   = mDatabaseRootReference.child("users/"+ma.getUserPhoneNumber()+"/"+ma.getFirebaseId()+"/groups");
        mStorage = FirebaseStorage.getInstance().getReference();


        //toolbar settings
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //le righe di codice di sotto servono al drower laterale che compare
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);  //setDrawerListener(toggle) --> addDrawerListener(toggle)
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //setto nome e cognome nella nav bar
        View header = navigationView.getHeaderView(0);
        tv1 = (TextView) header.findViewById(R.id.textView2);
        tv2 = (TextView) header.findViewById(R.id.textView3);
        tv1.setText(ma.getUserName() + " " + ma.getUserSurname());
        tv2.setText(ma.getUserPhoneNumber());
    }

    @Override
    public void onStart(){
        super.onStart();
        //in questo punto il codice prende la lista principale e la mostra come recyclerview
        View recyclerView = findViewById(R.id.group_list);
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

        } else if (id == R.id.nav_logout) {
            Logout fragment = new Logout();
            fragment.show(getSupportFragmentManager(), "LogoutFragment");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override   //questo serve per il search button
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_onlysearch, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        // Configure the search info and add any event listeners...

        return super.onCreateOptionsMenu(menu);
    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

    }


    //questa classe la usa per fare il managing della lista che deve mostrare
    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private Context mContext;
        private DatabaseReference mDatabaseReference;
        private ChildEventListener mChildEventListener;

        private List<String> mValuesIds = new ArrayList<>();
        private List<GroupForUser> mValues = new ArrayList<>();


        public SimpleItemRecyclerViewAdapter(final Context context, DatabaseReference ref) {
            mContext = context;
            mDatabaseReference = ref;

            // Create child event listener
            // [START child_event_listener_recycler]
            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                    // A new comment has been added, add it to the displayed list
                    GroupForUser groupForUser = dataSnapshot.getValue(GroupForUser.class);

                    // [START_EXCLUDE]
                    // Update RecyclerView
                    mValuesIds.add(dataSnapshot.getKey());
                    mValues.add(groupForUser);
                    notifyItemInserted(mValues.size() - 1);
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so displayed the changed comment.
                    GroupForUser newGroupForUser = dataSnapshot.getValue(GroupForUser.class);
                    String groupKey = dataSnapshot.getKey();

                    // [START_EXCLUDE]
                    int groupIndex = mValuesIds.indexOf(groupKey);
                    if (groupIndex > -1) {
                        // Replace with the new data
                        mValues.set(groupIndex, newGroupForUser);

                        // Update the RecyclerView
                        notifyItemChanged(groupIndex);
                    } else {
                        Log.w(TAG, "onChildChanged:unknown_child:" + groupKey);
                    }
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so remove it.
                    String groupKey = dataSnapshot.getKey();

                    // [START_EXCLUDE]
                    int groupIndex = mValuesIds.indexOf(groupKey);
                    if (groupIndex > -1) {
                        // Remove data from the list
                        mValuesIds.remove(groupIndex);
                        mValues.remove(groupIndex);

                        // Update the RecyclerView
                        notifyItemRemoved(groupIndex);
                    } else {
                        Log.w(TAG, "onChildRemoved:unknown_child:" + groupKey);
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

        //metodo per fare il download dallo storage delle immagini dei gruppi
        //nella NewGroup non serve, da togliere
        public void downlaoadGroupImage(String groupCode){
            StorageReference groupRef = mStorage.child("Groups").child(groupCode).child("GroupPicture").child("groupPicture.jpg");
            try {
                final File localFile = File.createTempFile("image", "tmp");
                groupRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        bitmap = BitmapFactory.decodeFile(localFile.getPath());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public GroupListActivity.SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SimpleItemRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);   //mValues.get(position) rappresenta un singolo elemento della nostra lista di gruppi
            //holder.mImageView.setImageBitmap(bitmap);
            holder.mNumView.setText(Long.toString(mValues.get(position).getSize()) + " " + getString(R.string.members));
            holder.mContentView.setText(mValues.get(position).getName());
            //sopra vengono settati i tre campi che costituisco le informazioni di ogni singolo gruppo, tutti pronti per essere mostriti nella gui

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Context context = v.getContext();


                    DatabaseReference mDatabaseGroupForUserReference = mDatabaseRootReference.child("users/"+ma.getFirebaseId()+"/"+ma.getUserPhoneNumber()+"groups/"+holder.mItem.getId());

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
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }   //ritorna il numero di elementi nella lista


        public void cleanupListener() {
            if (mChildEventListener != null) {
                mDatabaseReference.removeEventListener(mChildEventListener);
            }
        }



        //questa è una classe di supporto che viene usata per creare la vista a schermo, non ho ben capito come funziona
        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            //public final CircleImageView mImageView;
            public final TextView mNumView;
            public final TextView mContentView;
            public GroupForUser mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                //mImageView = (CircleImageView) findViewById(R.id.group_image_storage);
                mNumView = (TextView) view.findViewById(R.id.num_members);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

}
