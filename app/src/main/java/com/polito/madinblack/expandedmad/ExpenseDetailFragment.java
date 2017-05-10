package com.polito.madinblack.expandedmad;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.polito.madinblack.expandedmad.model.Expense;
import com.polito.madinblack.expandedmad.model.Group;
import com.polito.madinblack.expandedmad.model.MyApplication;

//ho fatto dei cambiamenti anche in questa classe, non ho ben capito come funziona e come si incastra con le altre
public class ExpenseDetailFragment extends Fragment {

    public static final String ARG_ITEM_ID = "itemId";
    private MyApplication ma;
    private Expense mItem;

    public ExpenseDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ma = MyApplication.getInstance();   //retrive del DB

        mItem = ma.getSelectedExpense(); //I select the group that I need

        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(mItem.getName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.expense_detail, container, false);

        // Show the content.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.description_field)).setText(mItem.getDescription());
            ((TextView) rootView.findViewById(R.id.paid_container)).setText(mItem.toString());
            ((TextView) rootView.findViewById(R.id.tag_container)).setText(mItem.getTag());
            ((TextView) rootView.findViewById(R.id.cost_container)).setText(String.format("%.2f",(mItem.getCost())));
            ((TextView) rootView.findViewById(R.id.currency_container)).setText(mItem.getCurrencySymbol());
            if( mItem.getPaidByPhoneNumber().equals(ma.getUserPhoneNumber()) )
                ((TextView) rootView.findViewById(R.id.buyer_container)).setText(getString(R.string.you));
            else
                ((TextView) rootView.findViewById(R.id.buyer_container)).setText(mItem.getPaidByName()+" "+mItem.getPaidBySurname());
            ((TextView) rootView.findViewById(R.id.date_container)).setText(Long.toString(mItem.getDay()) + "/" + Long.toString(mItem.getMonth()) + "/" + Long.toString(mItem.getYear()));

            Double balance = mItem.givePaymentForUser(ma.getUserPhoneNumber()).getBalance();

            if(balance > 0){
                ((TextView) rootView.findViewById(R.id.balance_container)).setText(String.format("+%.2f",(balance)));
                ((TextView) rootView.findViewById(R.id.balance_container)).setTextColor(Color.parseColor("#00c200"));
            }else if(balance < 0){
                ((TextView) rootView.findViewById(R.id.balance_container)).setText(String.format("%.2f",(balance)));
                ((TextView) rootView.findViewById(R.id.balance_container)).setTextColor(Color.parseColor("#ff0000"));
            }
            else{
                ((TextView) rootView.findViewById(R.id.balance_container)).setText(String.format("%.2f",(balance)));
            }
        }

        return rootView;
    }
}