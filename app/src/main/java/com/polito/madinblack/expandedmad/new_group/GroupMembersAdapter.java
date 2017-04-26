package com.polito.madinblack.expandedmad.new_group;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.polito.madinblack.expandedmad.R;

import java.util.List;

public class GroupMembersAdapter extends BaseAdapter {

    public List<SelectUser> _data;
    Context _c;
    ViewHolder v;

    public GroupMembersAdapter(List<SelectUser> GroupM, Context context) {
        _data = GroupM;
        _c = context;
    }

    @Override
    public int getCount() {
        return _data.size();
    }

    @Override
    public Object getItem(int i) {
        return _data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        if (view == null) {
            LayoutInflater li = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.new_group_items, null);
        } else {
            view = convertView;
        }

        v = new ViewHolder();

        v.title = (TextView) view.findViewById(R.id.name);
        v.phone = (TextView) view.findViewById(R.id.no);
        v.imageView = (ImageView) view.findViewById(R.id.pic);

        final SelectUser data = _data.get(i);
        v.title.setText(data.getName());
        v.phone.setText(data.getPhone());

        // Set image if exists
        try {

            if (data.getThumb() != null) {
                v.imageView.setImageBitmap(data.getThumb());
            } else {
                v.imageView.setImageResource(R.drawable.icon_utente);
            }
        } catch (OutOfMemoryError e) {
            // Add default picture
            v.imageView.setImageDrawable(this._c.getDrawable(R.drawable.icon_utente));
            e.printStackTrace();
        }

        view.setTag(data);
        return view;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView title, phone;
    }

}
