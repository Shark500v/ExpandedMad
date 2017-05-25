package com.polito.madinblack.expandedmad.tabViewGroup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.polito.madinblack.expandedmad.expenseDetail.ExpenseDetailActivity;
import com.polito.madinblack.expandedmad.expenseDetail.ExpenseDetailFragment;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.model.Currency;
import com.polito.madinblack.expandedmad.model.ExpenseForUser;
import com.polito.madinblack.expandedmad.model.MyApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//questa classe la usa per fare il managing della lista che deve mostrare
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<ExpenseForUser> mValues = new ArrayList<>();
    private Query dataref;
    private Context mContext;
    private ValueEventListener mEventListener;
    private MyApplication ma;




    private static final String TAG = "MyBalanceActivity";

    public RecyclerViewAdapter(Context ct, Query dr) {
        dataref = dr;
        mContext = ct;
        ma = MyApplication.getInstance();

        // Create child event listener
        // [START child_event_listener_recycler]
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)  {
                mValues.clear();
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    mValues.add(postSnapshot.getValue(ExpenseForUser.class));
                }
                notifyDataSetChanged();
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Groups:onCancelled", databaseError.toException());
                Toast.makeText(mContext, "Failed to load expenses.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        dataref.addValueEventListener(eventListener);
        // [END child_event_listener_recycler]

        // Store reference to listener so it can be removed on app stop
        mEventListener = eventListener;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_list_content, parent, false);
        return new RecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewAdapter.ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);   //mValues.get(position) rappresenta un singolo elemento della nostra lista di spese
        holder.mIdView.setText(mValues.get(position).getName());
        if(mValues.get(position).getMyBalance()>0) {
            holder.mContentView.setText(String.format(Locale.getDefault(), "+%.2f", Currency.convertCurrency(mValues.get(position).getMyBalance(), mValues.get(position).getCurrencyISO(), MyApplication.getCurrencyISOFavorite())) + " " + Currency.getSymbol(MyApplication.getCurrencyISOFavorite()));
            holder.mContentView.setTextColor(Color.parseColor("#00c200"));
        }else if(mValues.get(position).getMyBalance()<0) {
            holder.mContentView.setText(String.format(Locale.getDefault(), "%.2f", Currency.convertCurrency(mValues.get(position).getMyBalance(),  mValues.get(position).getCurrencyISO(), MyApplication.getCurrencyISOFavorite())) + " " + Currency.getSymbol(MyApplication.getCurrencyISOFavorite()));
            holder.mContentView.setTextColor(Color.parseColor("#ff0000"));
        }
        else{
            holder.mContentView.setText(String.format(Locale.getDefault(), "%.2f", Currency.convertCurrency(mValues.get(position).getMyBalance(),  mValues.get(position).getCurrencyISO(), MyApplication.getCurrencyISOFavorite())) + " " + Currency.getSymbol(MyApplication.getCurrencyISOFavorite()));
        }

        final SpannableStringBuilder str;
        if(mValues.get(position).getPaidByPhoneNumber().equals(ma.getUserPhoneNumber())) {
            str = new SpannableStringBuilder(mContext.getString(R.string.paid_by)+" "+ mContext.getString(R.string.you));
            str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), mContext.getString(R.string.paid_by).length()+1, (mContext.getString(R.string.paid_by).length()+1 + mContext.getString(R.string.you).length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        else {
            String fullName = mValues.get(position).getPaidByName()+" "+mValues.get(position).getPaidBySurname();
            str = new SpannableStringBuilder(mContext.getString(R.string.paid_by)+" "+ fullName);
            str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), mContext.getString(R.string.paid_by).length()+1, (mContext.getString(R.string.paid_by).length()+1 + fullName.length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }
        holder.mPaydBy.setText(str);
        //sopra vengono settati i tre campi che costituisco le informazioni di ogni singolo gruppo, tutti pronti per essere mostriti nella gui



        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                Context context = v.getContext();
                Intent intent = new Intent(context, ExpenseDetailActivity.class);
                intent.putExtra(ExpenseDetailFragment.ARG_EXPENSE_ID, holder.mItem.getId());
                intent.putExtra(ExpenseDetailFragment.ARG_EXPENSE_NAME, holder.mItem.getName());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }   //ritorna il numero di elementi nella lista

    public void cleanupListener() {
        if (mEventListener != null) {
            dataref.removeEventListener(mEventListener);
        }
    }

    //questa è una classe di supporto che viene usata per creare la vista a schermo, non ho ben capito come funziona
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mPaydBy;
        public final TextView mContentView;
        public ExpenseForUser mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mPaydBy = (TextView) view.findViewById(R.id.paidBy);
            mContentView = (TextView) view.findViewById(R.id.content);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
