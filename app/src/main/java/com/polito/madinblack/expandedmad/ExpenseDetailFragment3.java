package com.polito.madinblack.expandedmad;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.polito.madinblack.expandedmad.groupManaging.GroupListActivity;
import com.polito.madinblack.expandedmad.login.TelephoneInsertion;
import com.polito.madinblack.expandedmad.model.Expense;
import com.polito.madinblack.expandedmad.model.MyApplication;
import com.polito.madinblack.expandedmad.model.PaymentFirebase;
import com.polito.madinblack.expandedmad.utility.RecyclerViewAdapter;
import com.polito.madinblack.expandedmad.utility.TabView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ale on 08/05/2017.
 */

public class ExpenseDetailFragment3 extends Fragment {
    public static final String ARG_EXPENSE_NAME = "expenseName";
    public static final String ARG_EXPENSE_ID = "expenseId";

    RecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    private String expenseId;
    private String expenseName;
    private DatabaseReference mDatabaseExpenseReference;
    private ValueEventListener mValueEventListener;
    private MyApplication ma;
    private View rootView;


    public ExpenseDetailFragment3() {
        // Required empty public constructor
    }
/*
    //usato per instanziare un ogetto fragment
    public static ExpenseDetailFragment3 newInstance() {
        ExpenseDetailFragment3 fragment = new ExpenseDetailFragment3();
        Bundle args = new Bundle();
        //args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        //fragment.setArguments(args);
        return fragment;
    }
*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_EXPENSE_ID) && getArguments().containsKey(ARG_EXPENSE_ID)) {  //a quanto ho capito questa verifica mi dice se l'utente ha selezionato qualcosa di valido
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.

            expenseId = getArguments().getString(ARG_EXPENSE_ID);
            expenseName = getArguments().getString(ARG_EXPENSE_NAME);
            ma = MyApplication.getInstance();

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(expenseName);
            }

            mDatabaseExpenseReference = FirebaseDatabase.getInstance().getReference().child("expenses/"+expenseId);

            mValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Expense expense = dataSnapshot.getValue(Expense.class);

                    if(expense.getPaidByFirebaseId().equals(ma.getFirebaseId())) {
                        ImageButton imageButtonGo;
                        (rootView.findViewById(R.id.head_title)).setVisibility(View.VISIBLE);
                        (imageButtonGo = (ImageButton)rootView.findViewById(R.id.go_button)).setVisibility(View.VISIBLE);
                        imageButtonGo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), PaymentDetailActivity.class);
                                intent.putExtra(PaymentDetailActivity.ARG_EXPENSE_ID, expense.getId());
                                intent.putExtra(PaymentDetailActivity.ARG_EXPENSE_NAME, expense.getName());
                                startActivity(intent);
                            }
                        });


                    }else {
                        (rootView.findViewById(R.id.head_title)).setVisibility(View.GONE);
                        (rootView.findViewById(R.id.go_button)).setVisibility(View.GONE);
                    }

                    if(expense.getDescription()!=null && !(expense.getDescription().isEmpty()))
                        ((TextView) rootView.findViewById(R.id.description_field)).setText(expense.getDescription());
                    else {
                        ((TextView) rootView.findViewById(R.id.description_field)).setText(getString(R.string.no_detail));
                        ((TextView) rootView.findViewById(R.id.description_field)).setTextColor(Color.parseColor("#C1C5C0"));
                    }

                    PaymentFirebase paymentFirebase = expense.paymentFromUser(ma.getFirebaseId());
                    if(paymentFirebase!=null)
                        ((TextView) rootView.findViewById(R.id.paid_container)).setText(paymentFirebase.toString());
                    ((TextView) rootView.findViewById(R.id.tag_container)).setText(expense.getTag());
                    ((TextView) rootView.findViewById(R.id.cost_container)).setText(String.format("%.2f",(expense.getCost())));
                    ((TextView) rootView.findViewById(R.id.currency_container)).setText(expense.getCurrencySymbol());

                    if( expense.getPaidByPhoneNumber().equals(ma.getUserPhoneNumber()) )
                        ((TextView) rootView.findViewById(R.id.buyer_container)).setText(getString(R.string.you));
                    else
                        ((TextView) rootView.findViewById(R.id.buyer_container)).setText(expense.getPaidByName()+" "+expense.getPaidBySurname());
                    ((TextView) rootView.findViewById(R.id.date_container)).setText(Long.toString(expense.getDay()) + "/" + Long.toString(expense.getMonth()) + "/" + Long.toString(expense.getYear()));

                    Double balance = expense.paymentFromUser(ma.getFirebaseId()).getBalance();

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

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };


        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.expense_detail, container, false);

        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        //adapter = new RecyclerViewAdapterExpense(getContext(), mDatabaseExpenseListReference);
        if(mValueEventListener!=null)
            mDatabaseExpenseReference.addValueEventListener(mValueEventListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        if(mValueEventListener!=null)
            mDatabaseExpenseReference.removeEventListener(mValueEventListener);
    }
}
