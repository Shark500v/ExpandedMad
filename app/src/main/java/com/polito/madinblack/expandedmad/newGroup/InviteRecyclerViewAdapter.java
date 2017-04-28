package com.polito.madinblack.expandedmad.newGroup;


import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.polito.madinblack.expandedmad.R;

import java.util.List;

public class InviteRecyclerViewAdapter extends RecyclerView.Adapter<InviteRecyclerViewAdapter.ViewHolder>{

    private final List<SelectUser> mValues;

    public InviteRecyclerViewAdapter(List<SelectUser> list) {
        mValues = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invite_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(holder.mItem.getName());
        mValues.get(position).setEmail("");       //prima di tutto setto le email a null, in modo da poter fare un check dopo

        holder.mContentView.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String email = s.toString();
                mValues.get(position).setEmail(email);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }   //ritorna il numero di elementi nella lista

    //questa è una classe di supporto che viene usata per creare la vista a schermo, non ho ben capito come funziona
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final EditText mContentView;
        public SelectUser mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.name_surname);
            mContentView = (EditText) view.findViewById(R.id.input_email);
        }
    }
}
