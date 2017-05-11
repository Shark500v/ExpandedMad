package com.polito.madinblack.expandedmad.utility;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.database.Transaction;
import com.polito.madinblack.expandedmad.ExpenseDetailActivity;
import com.polito.madinblack.expandedmad.ExpenseDetailFragment;
import com.polito.madinblack.expandedmad.ExpenseDetailFragment3;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.model.ExpenseForUser;
import com.polito.madinblack.expandedmad.model.MyApplication;

import java.util.ArrayList;
import java.util.List;

//questa classe la usa per fare il managing della lista che deve mostrare
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private String index = "hello";
    private List<ExpenseForUser> mValues = new ArrayList<>();
    private DatabaseReference dataref;
    private List<String> mValuesIds = new ArrayList<>();
    private Context mContext;
    private ChildEventListener mChildEventListener;
    private MyApplication ma;
    private DatabaseReference mDatabaseNewExpenseReference;
    private Context context;
    private String strin;

    private static final String TAG = "MyBalanceActivity";

    public RecyclerViewAdapter(Context ct, DatabaseReference dr, String index) {
        this.index = index;
        dataref = dr;
        mContext = ct;
        ma = MyApplication.getInstance();

        // Create child event listener
        // [START child_event_listener_recycler]
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                // A new expense has been added, add it to the displayed list
                ExpenseForUser expense = dataSnapshot.getValue(ExpenseForUser.class);

                //upload newExpense
                mDatabaseNewExpenseReference = FirebaseDatabase.getInstance().getReference().child("users/"+ma.getUserPhoneNumber()+"/"+ma.getFirebaseId()+"/groups/"+expense.getGroupId()+"/newExpenses");

                mDatabaseNewExpenseReference.runTransaction(new Transaction.Handler() {

                    @Override
                    public Transaction.Result doTransaction(MutableData currentData) {
                        if((Long) currentData.getValue()!=0)
                            currentData.setValue(0L);

                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError,
                                           boolean committed, DataSnapshot currentData) {
                        //This method will be called once with the results of the transaction.
                        //Update remove the user from the group
                    }
                });

                // [START_EXCLUDE]
                // Update RecyclerView
                mValuesIds.add(dataSnapshot.getKey());
                mValues.add(expense);
                notifyItemInserted(mValues.size() - 1);
                // [END_EXCLUDE]

            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // An expense has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                ExpenseForUser expense = dataSnapshot.getValue(ExpenseForUser.class);
                String expenseKey = dataSnapshot.getKey();

                // [START_EXCLUDE]
                int expenseIndex = mValuesIds.indexOf(expenseKey);
                if (expenseIndex > -1) {
                    // Replace with the new data
                    mValues.set(expenseIndex, expense);

                    // Update the RecyclerView
                    notifyItemChanged(expenseIndex);

                } else {
                    Log.w(TAG, "onChildChanged:unknown_child:" + expenseKey);
                }
                // [END_EXCLUDE]
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                String expenseKey = dataSnapshot.getKey();

                // [START_EXCLUDE]
                int expenseIndex = mValuesIds.indexOf(expenseKey);
                if (expenseIndex > -1) {
                    // Remove data from the list
                    mValuesIds.remove(expenseKey);
                    mValues.remove(expenseIndex);

                    // Update the RecyclerView
                    notifyItemRemoved(expenseIndex);
                } else {
                    Log.w(TAG, "onChildRemoved:unknown_child:" + expenseKey);
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
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_list_content, parent, false);
        return new RecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewAdapter.ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);   //mValues.get(position) rappresenta un singolo elemento della nostra lista di spese
        holder.mIdView.setText(mValues.get(position).getName());
        if(mValues.get(position).getMyBalance()>0) {
            holder.mContentView.setText(String.format("+%.2f", mValues.get(position).getMyBalance()) + " " + mValues.get(position).getCurrencySymbol());
            holder.mContentView.setTextColor(Color.parseColor("#00c200"));
        }else if(mValues.get(position).getMyBalance()<0) {
            holder.mContentView.setText(String.format("%.2f", mValues.get(position).getMyBalance()) + " " + mValues.get(position).getCurrencySymbol());
            holder.mContentView.setTextColor(Color.parseColor("#ff0000"));
        }
        else{
            holder.mContentView.setText(String.format("%.2f", mValues.get(position).getMyBalance()) + " " + mValues.get(position).getCurrencySymbol());
        }

        final SpannableStringBuilder str;
        if(mValues.get(position).getPaidByPhoneNumber().equals(ma.getUserPhoneNumber())) {
            str = new SpannableStringBuilder(mContext.getString(R.string.paid_by)+" "+ mContext.getString(R.string.you));
            str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), mContext.getString(R.string.paid_by).length()+1, (mContext.getString(R.string.paid_by).length()+1 + mContext.getString(R.string.you).length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        else {
            str = new SpannableStringBuilder(mContext.getString(R.string.paid_by)+" "+ mValues.get(position).getPaidByName());
            str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), mContext.getString(R.string.paid_by).length()+1, (mContext.getString(R.string.paid_by).length()+1 + mValues.get(position).getPaidByName().length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }
        holder.mPaydBy.setText(str);
        //sopra vengono settati i tre campi che costituisco le informazioni di ogni singolo gruppo, tutti pronti per essere mostriti nella gui



        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                Context context = v.getContext();
                Intent intent = new Intent(context, ExpenseDetailActivity.class);
                intent.putExtra(ExpenseDetailFragment3.ARG_EXPENSE_ID, holder.mItem.getId());
                intent.putExtra(ExpenseDetailFragment3.ARG_EXPENSE_NAME, holder.mItem.getName());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }   //ritorna il numero di elementi nella lista

    public void cleanupListener() {
        if (mChildEventListener != null) {
            dataref.removeEventListener(mChildEventListener);
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
