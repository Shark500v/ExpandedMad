package com.polito.madinblack.expandedmad.expenseDetail;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.model.CostUtil;
import com.polito.madinblack.expandedmad.model.Currency;
import com.polito.madinblack.expandedmad.model.MyApplication;
import com.polito.madinblack.expandedmad.model.PaymentFirebase;
import com.polito.madinblack.expandedmad.model.PaymentInfo;

import java.util.Locale;
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
    private Currency.CurrencyISO expenseCurrencyISO;


    public PaymentRecyclerAdapter(Class<PaymentFirebase> modelClass, int modelLayout, Class<RecyclerView.ViewHolder> viewHolderClass, Context contex, Query ref,
                                  Map<String, PaymentInfo> changedPayment, Currency.CurrencyISO expenseCurrencyISO) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        ma =  MyApplication.getInstance();
        this.changedPayment = changedPayment;
        this.contex = contex;
        this.expenseCurrencyISO = expenseCurrencyISO;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        View viewItem = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);
        return new PaymentRecyclerAdapter.ViewHolder(view, viewItem, expenseCurrencyISO);

    }

    @Override
    protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, PaymentFirebase model, int position) {
        final PaymentFirebase paymentFirebase = model;
        final ViewHolder holder = (PaymentRecyclerAdapter.ViewHolder) viewHolder;

        if(!model.getUserFirebaseId().equals(ma.getFirebaseId())){

            holder.mCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String item = (String) parent.getItemAtPosition(position);
                    holder.mToPaid.setText(String.format(Locale.getDefault(), "%.2f", Currency.convertCurrency(paymentFirebase.getDebit(), Currency.getISOCode(item), expenseCurrencyISO)));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            holder.mUserView.setText(model.getUserFullName());


            if(model.getDebit()==0){
                holder.mPaid.setEnabled(false);
            }

            holder.mFillPaid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.mPaid.setText( holder.mToPaid.getText().toString());
                }
            });

            holder.mPaid.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {

                    String editString = CostUtil.replaceDecimalComma(s.toString());
                    if(s.length() != 0 && CostUtil.isParsableAsDouble(editString) && Double.valueOf(editString)!=0){
                        if(Double.valueOf(editString)>Double.valueOf(CostUtil.replaceDecimalComma(holder.mToPaid.getText().toString()))){
                            holder.mPaid.setText(holder.mToPaid.getText().toString());
                        }else{
                            PaymentInfo paymentInfo
                                    = new PaymentInfo(paymentFirebase, Currency.convertCurrency(Double.valueOf(CostUtil.replaceDecimalComma(holder.mPaid.getText().toString())), expenseCurrencyISO, Currency.getISOCode(holder.mCurrency.getSelectedItem().toString())));
                            changedPayment.put(paymentFirebase.getUserPhoneNumber(), paymentInfo);
                        }


                    }
                    else{
                        changedPayment.remove(paymentFirebase.getUserPhoneNumber());
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
            changedPayment.put(paymentFirebase.getUserPhoneNumber(), paymentInfo);
            holder.mUserView.setText(contex.getString(R.string.you));
            holder.mToPaid.setText(String.format(Locale.getDefault(), "%.2f",model.getToPaid()));
            holder.mPaid.setText(String.format(Locale.getDefault(), "%.2f",model.getPaid()));
            if(model.getPaid()!=0){
                holder.mPaid.setTextColor(Color.parseColor("#00c200"));
            }
            holder.mPaid.setEnabled(false);
            holder.mFillPaid.setVisibility(View.INVISIBLE);
            holder.mCurrency.setEnabled(false);



        }
    }


    //questa è una classe di supporto che viene usata per creare la vista a schermo
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mUserView;
        public final EditText mPaid;
        public final TextView mToPaid;
        public final CircleImageView mFillPaid;
        public final Spinner mCurrency;
        public final View mView;


        public ViewHolder(View view, View viewParent, Currency.CurrencyISO expenseCurrencyISO) {
            super(view);
            mUserView = (TextView) view.findViewById(R.id.user_name);
            mPaid = (EditText) view.findViewById(R.id.paid);
            mToPaid = (TextView) view.findViewById(R.id.to_paid);
            mFillPaid = (CircleImageView) view.findViewById(R.id.fill_paid);
            mCurrency = (Spinner) view.findViewById(R.id.currency);
            mView = view;
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(viewParent.getContext(), android.R.layout.simple_spinner_item, Currency.getCurrencySymbols(expenseCurrencyISO));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mCurrency.setAdapter(adapter);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mUserView.getText() + "'";
        }
    }




}