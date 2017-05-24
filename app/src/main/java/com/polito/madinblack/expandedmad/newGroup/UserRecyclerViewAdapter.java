package com.polito.madinblack.expandedmad.newGroup;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.model.MyApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.ViewHolder>{

    public List<SelectUser> _data, groupMem;
    private ArrayList<SelectUser> arraylist;
    Context _c;
    MyApplication ma;

    public UserRecyclerViewAdapter(List<SelectUser> selectUsers, Context context, List<SelectUser> GroupM) {
        _data = selectUsers;
        _c = context;
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(_data);
        groupMem = GroupM;      //questa è la lista che devo aggiornre di volta in volta un utente viene aggiunto al gruppo dall'user
        ma = MyApplication.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final UserRecyclerViewAdapter.ViewHolder holder, final int position) {
        final SelectUser data = _data.get(position);
        holder.title.setText(data.getName());
        holder.check.setChecked(data.getCheckedBox());
        holder.phone.setText(data.getPhone());
        if(data.getPhone().equals(ma.getUserPhoneNumber())){
            holder.check.setEnabled(false);
            holder.me.setVisibility(View.VISIBLE);
        }else{
            holder.check.setEnabled(true);
            holder.me.setVisibility(View.INVISIBLE);
        }

        // Set image if exists
        try {
            if (data.getThumb() != null) {
                holder.imageView.setImageBitmap(data.getThumb());
            } else {
                holder.imageView.setImageResource(R.drawable.contact_circle);
            }
        } catch (OutOfMemoryError e) {
            // Add default picture
            holder.imageView.setImageDrawable(this._c.getDrawable(R.drawable.contact_circle));
            e.printStackTrace();
        }
        Log.e("Image Thumb", "--------------" + data.getThumb());

        // Set check box listener android
        holder.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) view;
                if (checkBox.isChecked()) {
                    data.setCheckedBox(true);
                    //agiungo l'utente alla lista di utenti che devo contattare quando creo il gruppo
                    groupMem.add(data);
                } else {
                    data.setCheckedBox(false);
                    //elimino l'utente precedentemente aggiunto nel caso l'user cambiasse idea
                    groupMem.remove(data);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return _data.size();
    }   //ritorna il numero di elementi nella lista

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        CircleImageView imageView;
        TextView title, phone, me;
        CheckBox check;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            title = (TextView) view.findViewById(R.id.name);
            check = (CheckBox) view.findViewById(R.id.check);
            phone = (TextView) view.findViewById(R.id.no);
            me = (TextView) view.findViewById(R.id.me);
            imageView = (CircleImageView) view.findViewById(R.id.pic);
        }
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        _data.clear();
        if (charText.length() == 0) {
            _data.addAll(arraylist);
        } else {
            for (SelectUser wp : arraylist) {
                if (wp.getName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    _data.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
