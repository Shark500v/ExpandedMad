package com.polito.madinblack.expandedmad.groupManaging;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.model.*;

public class GroupDetailFragment extends Fragment {


    public static final String ARG_G_ID = "item_id";

    private GroupForUser mItem;



    public GroupDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        //mItem = ma.getGroupForUser();

        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(mItem.getName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.group_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.group_detail)).setText("Coap"/*mItem.getDetails()*/);
        }

        return rootView;
    }
}