package com.polito.madinblack.expandedmad;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.polito.madinblack.expandedmad.dummy.DummyContent;
import com.polito.madinblack.expandedmad.dummy.Group;

//ho fatto dei cambiamenti anche in questa classe, non ho ben capito come funziona e come si incastra con le altre
public class ExpenseDetailFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_GROUP_ID = "group_id";

    private Group.GroupElements mItem;

    public ExpenseDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {  //a quanto ho capito questa verifica mi dice se l'utente ha selezionat qualcosa di valido
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = Group.Group_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.content);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.expense_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.description_field)).setText(mItem.details);
        }

        return rootView;
    }
}