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

    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_GROUP_ID = "group_id";

    private MyApplication ma;

    private Group group;
    private Expense mItem;

    public ExpenseDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ma = MyApplication.getInstance();   //retrive del DB

        if (getArguments().containsKey(ARG_ITEM_ID) && getArguments().containsKey(ARG_GROUP_ID)) {  //a quanto ho capito questa verifica mi dice se l'utente ha selezionat qualcosa di valido
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            group = ma.getSingleGroup(Long.valueOf(getArguments().getString(ARG_GROUP_ID))); //I select the group that I need
            mItem = group.getSingleExpense(Long.valueOf(getArguments().getString(ARG_ITEM_ID)));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getName());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.expense_detail, container, false);

        // Show the content.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.description_field)).setText(mItem.getDescription());
            ((TextView) rootView.findViewById(R.id.tag_container)).setText(mItem.getTag().toString());
            ((TextView) rootView.findViewById(R.id.cost_container)).setText(Float.toString(mItem.getCost()));
            ((TextView) rootView.findViewById(R.id.currency_container)).setText(mItem.getCurrency().toString());
            ((TextView) rootView.findViewById(R.id.buyer_container)).setText(mItem.getPaying().getName()+" "+mItem.getPaying().getSurname());
            ((TextView) rootView.findViewById(R.id.date_container)).setText(Integer.toString(mItem.getDay()) + "/" + Integer.toString(mItem.getMonth()) + "/" + Integer.toString(mItem.getYear()));
            if(mItem.getMyBalance() >= 0){
                ((TextView) rootView.findViewById(R.id.balance_container)).setText("+"+Float.toString(mItem.getMyBalance()));
                ((TextView) rootView.findViewById(R.id.balance_container)).setTextColor(Color.parseColor("#00c200"));
            }else{
                ((TextView) rootView.findViewById(R.id.balance_container)).setText(Float.toString(mItem.getMyBalance()));
                ((TextView) rootView.findViewById(R.id.balance_container)).setTextColor(Color.parseColor("#ff0000"));
            }
        }

        return rootView;
    }
}