package com.polito.madinblack.expandedmad.tabViewGroup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.balanceDetail.BalanceDetailActivity;
import com.polito.madinblack.expandedmad.expenseDetail.ExpenseDetailActivity;
import com.polito.madinblack.expandedmad.expenseDetail.ExpenseDetailFragment;
import com.polito.madinblack.expandedmad.model.Balance;
import com.polito.madinblack.expandedmad.model.Currency;
import com.polito.madinblack.expandedmad.model.MyApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

//used to fill the my balance tab
public class RecyclerViewAdapterUsers extends RecyclerView.Adapter<RecyclerViewAdapterUsers.ViewHolder>{

    private List<Balance> mValues = new ArrayList<>();
    private List<String> mValuesIds = new ArrayList<>();
    private List<String> mUsersIds = new ArrayList<>();
    private Query dataref;
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseForUserUrl;
    private Context mContext;
    private ValueEventListener mEventListener;
    private String mGroupId;

    private static final String TAG = "MyBalanceActivity";

    public RecyclerViewAdapterUsers(Context ct, Query dr, String groupId) {

        dataref = dr;
        mContext = ct;
        mGroupId = groupId;

        // Create child event listener
        // [START child_event_listener_recycler]
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mValues.clear();
                mValuesIds.clear();
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    mValues.add(postSnapshot.getValue(Balance.class));
                    mValuesIds.add(dataSnapshot.getKey());
                    mUsersIds.add(postSnapshot.getKey());
                }
                notifyDataSetChanged();

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Groups:onCancelled", databaseError.toException());
                Toast.makeText(mContext, "Failed to load groups.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        dr.addValueEventListener(eventListener);
        // [END child_event_listener_recycler]

        // Store reference to listener so it can be removed on app stop
        mEventListener = eventListener;
    }

    @Override
    public RecyclerViewAdapterUsers.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_list_content, parent, false);
        return new RecyclerViewAdapterUsers.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewAdapterUsers.ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);                           //singolo utente
        holder.mId  = mUsersIds.get(position);

        if(holder.mItem.getUserPhoneNumber().equals(MyApplication.getUserPhoneNumber()))
            holder.mIdView.setText(mContext.getString(R.string.you));
        else
            holder.mIdView.setText(mValues.get(position).getFullName());               //qui visualizzo nome e cognome
        //qui invece quanto deve o meno
        if (mValues.get(position).getBalance()>0){
            holder.mContentView.setText(String.format(Locale.getDefault(), "+%.2f", Currency.convertCurrency(mValues.get(position).getBalance(), mValues.get(position).getCurrencyISO(), MyApplication.getCurrencyISOFavorite())) + " " + Currency.getSymbol(MyApplication.getCurrencyISOFavorite()));
            holder.mContentView.setTextColor(Color.parseColor("#00c200"));
        }else if(mValues.get(position).getBalance()<0){
            holder.mContentView.setText(String.format(Locale.getDefault(), "%.2f", Currency.convertCurrency(mValues.get(position).getBalance(), mValues.get(position).getCurrencyISO(), MyApplication.getCurrencyISOFavorite())) + " " + Currency.getSymbol(MyApplication.getCurrencyISOFavorite()));
            holder.mContentView.setTextColor(Color.parseColor("#ff0000"));
        }else{
            holder.mContentView.setText(String.format(Locale.getDefault(), "%.2f", Currency.convertCurrency(mValues.get(position).getBalance(), mValues.get(position).getCurrencyISO(), MyApplication.getCurrencyISOFavorite())) + " " + Currency.getSymbol(MyApplication.getCurrencyISOFavorite()));
        }

        mDatabaseForUserUrl = FirebaseDatabase.getInstance().getReference().child("users").child(holder.mItem.getUserPhoneNumber()).child(mUsersIds.get(position)).child("urlImage");
        mDatabaseForUserUrl.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String url = dataSnapshot.getValue(String.class);
                Glide.with(mContext).load(url).override(128,128).centerCrop().fitCenter().diskCacheStrategy(DiskCacheStrategy.RESULT).error(R.drawable.icon_utente).into(holder.mImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, BalanceDetailActivity.class);

                String [] nameSurname = holder.mItem.getFullName().split(" ");
                String name;
                if(nameSurname[0]!=null && !nameSurname[0].isEmpty())
                    name = nameSurname[0];
                else
                    name = nameSurname[1];
                intent.putExtra(BalanceDetailActivity.ARG_USER_BALANCE_NAME, name);
                intent.putExtra(BalanceDetailActivity.ARG_GROUP_ID, mGroupId);
                intent.putExtra(BalanceDetailActivity.ARG_BALANCE_ID, holder.mId);
                intent.putExtra(BalanceDetailActivity.ARG_BALANCE_CURRENCY, holder.mItem.getCurrencyISO().name());
                context.startActivity(intent);


            }
        });

        /*mStorageReference = FirebaseStorage.getInstance().getReference().child("users").child(mUsersIds.get(position)).child("userProfilePicture.jpg");
        mStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(mContext).load(uri).into(holder.mImage);
            }
        });*/

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
        if (mEventListener != null) {
            dataref.removeEventListener(mEventListener);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final CircleImageView mImage;
        public final TextView mIdView;
        public final TextView mContentView;
        public String mId;
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