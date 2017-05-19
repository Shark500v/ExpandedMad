package com.polito.madinblack.expandedmad.notUsed;

/**
 * Created by Ale on 10/05/2017.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.model.CostUtil;
import com.polito.madinblack.expandedmad.model.PaymentFirebase;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    //private String _listDataHeader; // header title
    // child data in format child payment
    private List<PaymentFirebase> _listDataChild;
    private List<Double> _listPaid;
    private String groupId;
    private PaymentFirebase paymentPaidFirebase;
    private Double expenseCost;
    private Map<String, PaymentFirebase> payments;

    public ExpandableListAdapter(Context context, List<PaymentFirebase> listChildData, String groupId, PaymentFirebase paymentPaidFirebase, Double expenseCost) {
        this._context = context;
        this.groupId = groupId;
        //this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this.expenseCost = expenseCost;
        this.paymentPaidFirebase = paymentPaidFirebase;
        this._listPaid = new ArrayList<Double>();
        for(int i=0; i<listChildData.size(); i++){
            _listPaid.add(0d);
        }
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final PaymentFirebase childPayment = (PaymentFirebase) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView userChild = (TextView) convertView
                .findViewById(R.id.user_name);
        userChild.setText(childPayment.getUserFullName());

        final EditText userPaid = (EditText) convertView
                .findViewById(R.id.paid);
        userPaid.setHint(0);



        final TextView userToPaid = (TextView) convertView
                .findViewById(R.id.to_paid);
        userToPaid.setText(childPayment.getDebit().toString());

        if(childPayment.getDebit()==0){
            userPaid.setEnabled(false);
        }

        ImageButton immageButton = (ImageButton) convertView
                .findViewById(R.id.fill_paid);

        immageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                //childPayment.setPaid(childPayment.getDebit());
                _listPaid.set(childPosition, childPayment.getDebit());
                userPaid.setText(childPayment.getDebit().toString());

            }
        });

        userPaid.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if(s.length() != 0 && CostUtil.isParsableAsDouble(s.toString())){
                    if(Double.valueOf(s.toString())>Double.valueOf(userToPaid.getText().toString())){
                        //childPayment.setPaid(childPayment.getDebit());
                        _listPaid.set(childPosition, childPayment.getDebit());
                        userPaid.setText(childPayment.getDebit().toString());

                    }else{
                        //childPayment.setPaid(Double.valueOf(userToPaid.getText().toString()));
                        _listPaid.set(childPosition, Double.valueOf(userToPaid.getText().toString()));
                        userPaid.setText(childPayment.getDebit().toString());
                    }


                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public int getGroupCount() {
        return 1;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.head_title);
        lblListHeader.setTypeface(null, Typeface.BOLD);

        ImageButton immageButtonAll = (ImageButton) convertView
                .findViewById(R.id.fill_all_paid);

        ImageButton confirmImmageButton = (ImageButton) convertView
                .findViewById(R.id.confirm_button);

        immageButtonAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                /*for(PaymentFirebase paymentFirebase : _listDataChild){
                    paymentFirebase.setPaid(paymentFirebase.getDebit());

                }
                notifyDataSetChanged();
                */
            }
        });

        confirmImmageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
/*
                DatabaseReference mDatabaseRootReference = FirebaseDatabase.getInstance().getReference();
                Double totPaid = 0D;
                for(int i=0; i<_listDataChild.size(); i++){
                    if(!_listPaid.get(i).equals(0D))
                        continue;
                    final PaymentFirebase paymentFirebase = (PaymentFirebase) getChild(0, i);
                    final int pos = i;
                    totPaid += _listPaid.get(i);

                    mDatabaseRootReference
                            .child("groups/"+groupId+"/users/"+paymentFirebase.getUserFirebaseId()+"/balances/"+paymentPaidFirebase.getUserFirebaseId()+"/balance")
                            .runTransaction(new Transaction.Handler() {

                                @Override
                                public Transaction.Result doTransaction(MutableData currentData) {
                                    if (currentData.getValue() == null) {
                                        //no default value for data, set one
                                        currentData.setValue(paymentFirebase.getBalance() + _listPaid.get(pos));
                                    } else {
                                        // perform the update operations on data
                                        currentData.setValue(currentData.getValue(Double.class) + _listPaid.get(pos));
                                    }
                                    return Transaction.success(currentData);
                                }

                                @Override
                                public void onComplete(DatabaseError databaseError,
                                                       boolean committed, DataSnapshot currentData) {
                                    //This method will be called once with the results of the transaction.
                                    //Update remove the user from the group
                                }
                            });



                    mDatabaseRootReference
                            .child("groups/"+groupId+"/users/"+paymentPaidFirebase.getUserFirebaseId()+"/balances/"+paymentFirebase.getUserFirebaseId()+"/balance")
                            .runTransaction(new Transaction.Handler() {

                        @Override
                        public Transaction.Result doTransaction(MutableData currentData) {
                            if (currentData.getValue() == null) {
                                //no default value for data, set one
                                currentData.setValue(expenseCost - _listPaid.get(pos));
                            } else {
                                // perform the update operations on data
                                currentData.setValue(currentData.getValue(Double.class) - _listPaid.get(pos));
                            }
                            return Transaction.success(currentData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError,
                                               boolean committed, DataSnapshot currentData) {
                            //This method will be called once with the results of the transaction.
                            //Update remove the user from the group
                        }
                    });


                    mDatabaseRootReference
                            .child("users/"+paymentFirebase.getUserPhoneNumber()
                                    +"/"+paymentFirebase.getUserFirebaseId()+"/groups/"+groupId
                                    +"/expenses/"+paymentFirebase.getExpenseId()+"myBalance")
                            .runTransaction(new Transaction.Handler() {

                                @Override
                                public Transaction.Result doTransaction(MutableData currentData) {
                                    if (currentData.getValue() == null) {
                                        //no default value for data, set one
                                        currentData.setValue(paymentFirebase.getBalance() - _listPaid.get(pos));
                                    } else {
                                        // perform the update operations on data
                                        currentData.setValue(currentData.getValue(Double.class) - _listPaid.get(pos));
                                    }
                                    return Transaction.success(currentData);
                                }

                                @Override
                                public void onComplete(DatabaseError databaseError,
                                                       boolean committed, DataSnapshot currentData) {
                                    //This method will be called once with the results of the transaction.
                                    //Update remove the user from the group
                                }
                            });



                    paymentFirebase.setPaid(paymentFirebase.getPaid()+_listPaid.get(i));
                    payments.put(paymentFirebase.getId(), paymentFirebase);


                }

                paymentPaidFirebase.setPaid(paymentPaidFirebase.getPaid()-totPaid);
                payments.put(paymentPaidFirebase.getId(), paymentPaidFirebase);
                mDatabaseRootReference
                        .child("expenses/"+paymentPaidFirebase.getExpenseId()+"/payments")
                        .setValue(payments);

*/
            }

        });






        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
