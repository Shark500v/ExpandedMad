package com.polito.madinblack.expandedmad.utility;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.model.Message;
import com.polito.madinblack.expandedmad.model.MyApplication;
import com.polito.madinblack.expandedmad.model.PaymentFirebase;

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

    public PaymentRecyclerAdapter(Class<PaymentFirebase> modelClass, int modelLayout, Class<PaymentRecyclerAdapter.ViewHolder> viewHolderClass, Query ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        ma =  MyApplication.getInstance();
    }




    @Override
    protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, PaymentFirebase model, int position) {
        //da cambiare il controllo, è solo per ricordarmi che se mio mess va a destra
        if(!model.getUserFirebaseId().equals(ma.getFirebaseId())){
            ViewHolder holder = (ViewHolder) viewHolder;
            holder.mUserView.setText(model.getUserNameDisplayed());
            holder.mToPaid.setText(model.getDebit().toString());
            holder.mFillPaid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
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
}