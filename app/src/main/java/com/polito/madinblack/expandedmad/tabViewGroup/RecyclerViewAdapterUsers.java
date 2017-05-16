package com.polito.madinblack.expandedmad.tabViewGroup;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.model.Balance;
import com.polito.madinblack.expandedmad.model.Currency;
import com.polito.madinblack.expandedmad.model.MyApplication;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

//used to fill the my balance tab
public class RecyclerViewAdapterUsers extends RecyclerView.Adapter<RecyclerViewAdapterUsers.ViewHolder>{

    private List<Balance> mValues = new ArrayList<>();
    private DatabaseReference dataref;
    private StorageReference mStorageReference;
    private List<String> mValuesIds = new ArrayList<>();
    private Context mContext;
    private ChildEventListener mChildEventListener;

    private static final String TAG = "MyBalanceActivity";

    public RecyclerViewAdapterUsers(Context ct, DatabaseReference dr) {

        dataref = dr;
        mContext = ct;

        // Create child event listener
        // [START child_event_listener_recycler]
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                // A new comment has been added, add it to the displayed list
                Balance balance = dataSnapshot.getValue(Balance.class);

                // [START_EXCLUDE]
                // Update RecyclerView
                mValuesIds.add(dataSnapshot.getKey());
                mValues.add(balance);
                notifyItemInserted(mValues.size() - 1);
                // [END_EXCLUDE]
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                Balance balance = dataSnapshot.getValue(Balance.class);
                String balanceKey = dataSnapshot.getKey();

                // [START_EXCLUDE]
                int balanceIndex = mValuesIds.indexOf(balanceKey);
                if (balanceIndex > -1) {
                    // Replace with the new data
                    mValues.set(balanceIndex, balance);

                    // Update the RecyclerView
                    notifyItemChanged(balanceIndex);
                } else {
                    Log.w(TAG, "onChildChanged:unknown_child:" + balanceKey);
                }
                // [END_EXCLUDE]
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                String balanceKey = dataSnapshot.getKey();

                // [START_EXCLUDE]
                int balanceIndex = mValuesIds.indexOf(balanceKey);
                if (balanceIndex > -1) {
                    // Remove data from the list
                    mValuesIds.remove(balanceKey);
                    mValues.remove(balanceIndex);

                    // Update the RecyclerView
                    notifyItemRemoved(balanceIndex);
                } else {
                    Log.w(TAG, "onChildRemoved:unknown_child:" + balanceKey);
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
                Toast.makeText(mContext, "Failed to load groups.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        dr.addChildEventListener(childEventListener);
        // [END child_event_listener_recycler]

        // Store reference to listener so it can be removed on app stop
        mChildEventListener = childEventListener;
    }

    @Override
    public RecyclerViewAdapterUsers.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_list_content, parent, false);
        return new RecyclerViewAdapterUsers.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewAdapterUsers.ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);                           //singolo utente
        MyApplication ma = MyApplication.getInstance();
        if(holder.mItem.getUserPhoneNumber().equals(ma.getUserPhoneNumber()))
            holder.mIdView.setText(mContext.getString(R.string.you));
        else
            holder.mIdView.setText(mValues.get(position).getUserName() + " " +mValues.get(position).getUserSurname());               //qui visualizzo nome e cognome
        //qui invece quanto deve o meno
        if (mValues.get(position).getBalance()>0){
            holder.mContentView.setText(String.format("+%.2f", mValues.get(position).getBalance()) + " " + Currency.getSymbol(mValues.get(position).getCurrencyISO()));
            holder.mContentView.setTextColor(Color.parseColor("#00c200"));
        }else if(mValues.get(position).getBalance()<0){
            holder.mContentView.setText(String.format("%.2f", mValues.get(position).getBalance()) + " " + Currency.getSymbol(mValues.get(position).getCurrencyISO()));
            holder.mContentView.setTextColor(Color.parseColor("#ff0000"));
        }else{
            holder.mContentView.setText(String.format("%.2f", mValues.get(position).getBalance()) + " " + Currency.getSymbol(mValues.get(position).getCurrencyISO()));
        }
        mStorageReference = FirebaseStorage.getInstance().getReference().child("users").child(mValuesIds.get(position)).child("userProfilePicture.jpg");
        mStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(mContext).load(uri).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.mImage);
            }
        });

        /*
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ItemDetailActivity.class);
                    intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);

                    context.startActivity(intent);
            }
        });
        */
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void cleanupListener() {
        if (mChildEventListener != null) {
            dataref.removeEventListener(mChildEventListener);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final CircleImageView mImage;
        public final TextView mIdView;
        public final TextView mContentView;
        public Balance mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImage = (CircleImageView)view.findViewById(R.id.user_icon);
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }


}