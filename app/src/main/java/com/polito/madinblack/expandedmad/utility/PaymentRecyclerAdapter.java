package com.polito.madinblack.expandedmad.utility;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.chat.ChatRecyclerViewAdapter;
import com.polito.madinblack.expandedmad.model.Balance;
import com.polito.madinblack.expandedmad.model.CostUtil;
import com.polito.madinblack.expandedmad.model.Message;
import com.polito.madinblack.expandedmad.model.MyApplication;
import com.polito.madinblack.expandedmad.model.Payment;
import com.polito.madinblack.expandedmad.model.PaymentFirebase;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

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
    private Context contex;
    public PaymentRecyclerAdapter(Class<PaymentFirebase> modelClass, int modelLayout, Class<RecyclerView.ViewHolder> viewHolderClass, Context contex, Query ref,
                                  Map<String, PaymentInfo> changedPayment) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        ma =  MyApplication.getInstance();
        this.changedPayment = changedPayment;
        this.contex = contex;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            return new PaymentRecyclerAdapter.ViewHolder(view);

    }

    @Override
    protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, PaymentFirebase model, int position) {
        final PaymentFirebase paymentFirebase = model;
        final ViewHolder holder = (PaymentRecyclerAdapter.ViewHolder) viewHolder;
        if(!model.getUserFirebaseId().equals(ma.getFirebaseId())){
            holder.mUserView.setText(model.getUserNameDisplayed());
            holder.mPaid.setText("");
            holder.mToPaid.setText(String.format("%.2f",model.getDebit()));
            if(model.getDebit()==0){
                holder.mPaid.setEnabled(false);
            }

            holder.mFillPaid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /*PaymentInfo paymentInfo
                            = new PaymentInfo(paymentFirebase, paymentFirebase.getDebit());

                    changedPayment.put(paymentFirebase.getId(), paymentInfo);*/
                    holder.mPaid.setText(String.format("%.2f",paymentFirebase.getDebit()));
                }
            });

            holder.mPaid.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {

                    String editString = CostUtil.replaceDecimalComma(s.toString());
                    if(s.length() != 0 && CostUtil.isParsableAsDouble(editString) && Double.valueOf(editString)!=0){
                        if(Double.valueOf(editString)>Double.valueOf(CostUtil.replaceDecimalComma(holder.mToPaid.getText().toString()))){
                            /*
                            PaymentInfo paymentInfo
                                    = new PaymentInfo(paymentFirebase, paymentFirebase.getDebit());

                            changedPayment.put(paymentFirebase.getId(), paymentInfo);*/
                            holder.mPaid.setText(String.format("%.2f",paymentFirebase.getDebit()));

                        }else{
                            PaymentInfo paymentInfo
                                    = new PaymentInfo(paymentFirebase, Double.valueOf(CostUtil.replaceDecimalComma(holder.mPaid.getText().toString())));
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
            PaymentInfo paymentInfo  = new PaymentInfo(paymentFirebase, 0D);
            paymentInfo.setId(paymentFirebase.getId());
            changedPayment.put(paymentFirebase.getUserFirebaseId(), paymentInfo);
            holder.mUserView.setText(contex.getString(R.string.you));
            holder.mToPaid.setText(String.format("%.2f",model.getToPaid()));
            holder.mPaid.setText(String.format("%.2f",model.getPaid()));
            if(model.getPaid()!=0){
                holder.mPaid.setTextColor(Color.parseColor("#00c200"));
            }
            holder.mPaid.setEnabled(false);
            holder.mFillPaid.setVisibility(View.INVISIBLE);

        }
    }


    //questa è una classe di supporto che viene usata per creare la vista a schermo
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mUserView;
        public final EditText mPaid;
        public final TextView mToPaid;
        public final CircleImageView mFillPaid;
        public final View mView;

        public ViewHolder(View view) {
            super(view);
            mUserView = (TextView) view.findViewById(R.id.user_name);
            mPaid = (EditText) view.findViewById(R.id.paid);
            mToPaid = (TextView) view.findViewById(R.id.to_paid);
            mFillPaid = (CircleImageView) view.findViewById(R.id.fill_paid);
            mView = view;
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
        private String id;
        private String userNameDisplayed;



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
            this.userNameDisplayed = paymentFirebase.getUserNameDisplayed();

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

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserNameDisplayed() {
            return userNameDisplayed;
        }

        public void setUserNameDisplayed(String userNameDisplayed) {
            this.userNameDisplayed = userNameDisplayed;
        }
    }


}