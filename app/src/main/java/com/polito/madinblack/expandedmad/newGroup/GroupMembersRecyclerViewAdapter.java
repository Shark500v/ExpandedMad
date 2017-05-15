package com.polito.madinblack.expandedmad.newGroup;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.polito.madinblack.expandedmad.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupMembersRecyclerViewAdapter extends RecyclerView.Adapter<GroupMembersRecyclerViewAdapter.ViewHolder>{
    public List<SelectUser> _data;
    Context _c;

    public GroupMembersRecyclerViewAdapter(List<SelectUser> selectUsers, Context context) {
        _data = selectUsers;
        _c = context;
    }

    @Override
    public GroupMembersRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_group_items, parent, false);
        return new GroupMembersRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final GroupMembersRecyclerViewAdapter.ViewHolder holder, final int position) {

        final SelectUser data = _data.get(position);
        holder.title.setText(data.getName());
        holder.phone.setText(data.getPhone());

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
    }

    @Override
    public int getItemCount() {
        return _data.size();
    }   //ritorna il numero di elementi nella lista

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        CircleImageView imageView;
        TextView title, phone;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            title = (TextView) view.findViewById(R.id.name);
            phone = (TextView) view.findViewById(R.id.no);
            imageView = (CircleImageView) view.findViewById(R.id.pic);
        }
    }
}
