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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.polito.madinblack.expandedmad.expenseDetail.ExpenseDetailActivity;
import com.polito.madinblack.expandedmad.expenseDetail.ExpenseDetailFragment;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.model.Currency;
import com.polito.madinblack.expandedmad.model.Expense;
import com.polito.madinblack.expandedmad.model.ExpenseForUser;
import com.polito.madinblack.expandedmad.model.HistoryInfo;
import com.polito.madinblack.expandedmad.model.MyApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<ExpenseForUser> mValues = new ArrayList<>();
    private List<String> mValuesIds = new ArrayList<>();
    private Query dataref;
    private Context mContext;
    private int numContest = 0;
    private ChildEventListener mEventListener;

    private static final String TAG = "MyBalanceActivity";

    public RecyclerViewAdapter(Context ct, Query dr) {
        dataref = dr;
        mContext = ct;


        // Create child event listener
        // [START child_event_listener_recycler]
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                // A new info has been added, add it to the displayed list
                ExpenseForUser expenseForUser = dataSnapshot.getValue(ExpenseForUser.class);


                // [START_EXCLUDE]
                // Update RecyclerView
                /*if(getItemCount() == 0){
                    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.cardList);
                    TextView tx = (TextView) findViewById(R.id.textView);
                    tx.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }*/
                if (mValuesIds.indexOf(dataSnapshot.getKey()) > -1) {
                    Log.e(TAG, "Spesa duplicata: " + expenseForUser.getName());
                    return;
                }
                if(expenseForUser.getState() == Expense.State.CONTESTED){
                    mValuesIds.add(0, dataSnapshot.getKey());
                    mValues.add(0, expenseForUser);
                    notifyItemInserted(numContest);
                    numContest++;
                    Log.e(TAG, "Spesa contesa: " + expenseForUser.getName());
                }else{
                    mValuesIds.add(numContest, dataSnapshot.getKey());
                    mValues.add(numContest, expenseForUser);
                    notifyItemInserted(mValues.size()-1);
                    Log.e(TAG, "Spesa aggiunta in posizione : " + expenseForUser.getName() + " "+numContest);
                }
                // [END_EXCLUDE]

            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // An info has changed, use the key to determine if we are displaying this
                // info and if so displayed the changed info.
                ExpenseForUser expenseForUser = dataSnapshot.getValue(ExpenseForUser.class);
                String infoKey = dataSnapshot.getKey();

                // [START_EXCLUDE]
                int infoIndex = mValuesIds.indexOf(infoKey);
                if (infoIndex > -1) {
                    // Replace with the new data
                    mValues.set(infoIndex, expenseForUser);

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
        mEventListener = childEventListener;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_list_content, parent, false);
        return new RecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewAdapter.ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);   //mValues.get(position) rappresenta un singolo elemento della nostra lista di spese
        if(holder.mItem.getState()== Expense.State.TRANSFER)
            holder.mIdView.setText(mValues.get(position).getName()+" "+holder.mView.getContext().getString(R.string.transfer));
        else
            holder.mIdView.setText(mValues.get(position).getName());

        if(holder.mItem.getState()== Expense.State.CONTESTED)
            holder.mIdView.setTextColor(Color.parseColor("#FF9800"));
        else if(holder.mItem.getState()== Expense.State.REJECTED)
            holder.mIdView.setTextColor(Color.parseColor("#ff0000"));
        else
            holder.mIdView.setTextColor(Color.parseColor("#009688"));

        if(mValues.get(position).getMyBalance()>0) {
            holder.mContentView.setText(String.format(Locale.getDefault(), "+%.2f", Currency.convertCurrency(mValues.get(position).getMyBalance(), mValues.get(position).getCurrencyISO(), MyApplication.getCurrencyISOFavorite())) + " " + Currency.getSymbol(MyApplication.getCurrencyISOFavorite()));
            holder.mContentView.setTextColor(Color.parseColor("#00c200"));
        }else if(mValues.get(position).getMyBalance()<0) {
            holder.mContentView.setText(String.format(Locale.getDefault(), "%.2f", Currency.convertCurrency(mValues.get(position).getMyBalance(),  mValues.get(position).getCurrencyISO(), MyApplication.getCurrencyISOFavorite())) + " " + Currency.getSymbol(MyApplication.getCurrencyISOFavorite()));
            holder.mContentView.setTextColor(Color.parseColor("#ff0000"));
        }
        else{
            holder.mContentView.setText(String.format(Locale.getDefault(), "%.2f", Currency.convertCurrency(mValues.get(position).getMyBalance(),  mValues.get(position).getCurrencyISO(), MyApplication.getCurrencyISOFavorite())) + " " + Currency.getSymbol(MyApplication.getCurrencyISOFavorite()));
            holder.mContentView.setTextColor(Color.parseColor("#000000"));
        }

        SpannableStringBuilder str;
        if(mValues.get(position).getPaidByPhoneNumber().equals(MyApplication.getUserPhoneNumber())) {
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
