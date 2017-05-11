package com.polito.madinblack.expandedmad.utility;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.model.Balance;
import com.polito.madinblack.expandedmad.model.CostUtil;
import com.polito.madinblack.expandedmad.model.MyApplication;
import com.polito.madinblack.expandedmad.model.PaymentFirebase;
import java.util.Map;

//questa classe la usa per fare il managing della lista che deve mostrare
public class PaymentRecyclerAdapter extends FirebaseRecyclerAdapter<PaymentFirebase,RecyclerView.ViewHolder> {
    /**
     * @param modelClass      Firebase will marshall the data at a location into
     *                        an instance of a class that you provide
     * @param modelLayout     This is the layout used to represent a single item in the list.
     *                        You will be responsible for populating an instance of the corresponding
     *                        view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param ref             The Firebase location to watch for data changes. Can also be a slice of a location,
     *                        using some combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     */
    private MyApplication ma;
    private Map<String, PaymentInfo> changedPayment;
    private String paymentUserPaidExpenseId;
    private PaymentInfo paymentUserPaidExpense;

    public PaymentRecyclerAdapter(Class<PaymentFirebase> modelClass, int modelLayout, Class<RecyclerView.ViewHolder> viewHolderClass, Query ref,
                                  Map<String, PaymentInfo> changedPayment, PaymentInfo paymentUserPaidExpense, String paymentUserPaidExpenseId) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        ma =  MyApplication.getInstance();
        this.changedPayment = changedPayment;
        this.paymentUserPaidExpense = paymentUserPaidExpense;
        this.paymentUserPaidExpenseId = paymentUserPaidExpenseId;

    }




    @Override
    protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, PaymentFirebase model, int position) {
        final PaymentFirebase paymentFirebase = model;
        //da cambiare il controllo, è solo per ricordarmi che se mio mess va a destra
        if(!model.getUserFirebaseId().equals(ma.getFirebaseId())){
            final ViewHolder holder = (ViewHolder) viewHolder;
            holder.mUserView.setText(model.getUserNameDisplayed());
            holder.mToPaid.setText(model.getDebit().toString());
            if(model.getDebit()==0){
                holder.mPaid.setEnabled(false);
            }

            holder.mFillPaid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PaymentInfo paymentInfo
                            = new PaymentInfo(paymentFirebase, paymentFirebase.getDebit());

                    changedPayment.put(paymentFirebase.getId(), paymentInfo);
                    holder.mPaid.setText(paymentFirebase.getDebit().toString());
                }
            });

            holder.mPaid.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {
                    if(s.length() != 0 && CostUtil.isParsableAsDouble(s.toString()) && Double.valueOf(s.toString())!=0){
                        if(Double.valueOf(s.toString())>Double.valueOf(holder.mToPaid.getText().toString())){
                            /*
                            PaymentInfo paymentInfo
                                    = new PaymentInfo(paymentFirebase, paymentFirebase.getDebit());

                            changedPayment.put(paymentFirebase.getId(), paymentInfo);*/
                            holder.mPaid.setText(paymentFirebase.getDebit().toString());

                        }else{
                            PaymentInfo paymentInfo
                                    = new PaymentInfo(paymentFirebase, Double.valueOf(holder.mPaid.getText().toString()));
                            changedPayment.put(paymentFirebase.getId(), paymentInfo);
                        }


                    }
                    else{
                        changedPayment.remove(paymentFirebase.getId());
                    }
                }

                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {

                }
            });
        } else{
            paymentUserPaidExpense = new PaymentInfo(model, 0D);
            paymentUserPaidExpenseId = model.getId();

        }
    }


    //questa è una classe di supporto che viene usata per creare la vista a schermo
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mUserView;
        public final EditText mPaid;
        public final TextView mToPaid;
        public final ImageButton mFillPaid;

        public ViewHolder(View view) {
            super(view);
            mUserView = (TextView) view.findViewById(R.id.user_name);
            mPaid = (EditText) view.findViewById(R.id.paid);
            mToPaid = (TextView) view.findViewById(R.id.to_paid);
            mFillPaid = (ImageButton) view.findViewById(R.id.fill_paid);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mUserView.getText() + "'";
        }
    }

    public class PaymentInfo{
        private String userFirebaseId;
        private String userPhoneNumber;
        private Double paidNow;
        private Double balance;
        private Double paidBefore;

        public PaymentInfo(String userFirebaseId, String userPhoneNumber, Double paidBefore, Double paidNow, Double balance) {
            this.userFirebaseId = userFirebaseId;
            this.userPhoneNumber = userPhoneNumber;
            this.paidNow = paidNow;
            this.balance = balance;
            this.paidBefore = paidBefore;
        }

        public PaymentInfo(PaymentFirebase paymentFirebase, Double paidNow){
            this.userFirebaseId = paymentFirebase.getUserFirebaseId();
            this.userPhoneNumber = paymentFirebase.getUserPhoneNumber();
            this.paidNow = paidNow;
            this.balance = paymentFirebase.getBalance();
            this.paidBefore = paymentFirebase.getPaid();

        }

        public String getUserFirebaseId() {
            return userFirebaseId;
        }

        public void setUserFirebaseId(String userFirebaseId) {
            this.userFirebaseId = userFirebaseId;
        }

        public String getUserPhoneNumber() {
            return userPhoneNumber;
        }

        public void setUserPhoneNumber(String userPhoneNumber) {
            this.userPhoneNumber = userPhoneNumber;
        }


        public Double getBalance() {
            return balance;
        }

        public void setBalance(Double balance) {
            this.balance = balance;
        }

        public Double getPaidNow() {
            return paidNow;
        }

        public void setPaidNow(Double paidNow) {
            this.paidNow = paidNow;
        }

        public Double getPaidBefore() {
            return paidBefore;
        }

        public void setPaidBefore(Double paidBefore) {
            this.paidBefore = paidBefore;
        }
    }


}