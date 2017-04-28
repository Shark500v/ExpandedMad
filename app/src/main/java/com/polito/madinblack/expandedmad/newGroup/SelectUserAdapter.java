package com.polito.madinblack.expandedmad.newGroup;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.polito.madinblack.expandedmad.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SelectUserAdapter extends BaseAdapter {

    public List<SelectUser> _data, groupMem;
    private ArrayList<SelectUser> arraylist;
    Context _c;
    ViewHolder v;
    //RoundImage roundedImage;

    public SelectUserAdapter(List<SelectUser> selectUsers, Context context, List<SelectUser> GroupM) {
        _data = selectUsers;
        _c = context;
        this.arraylist = new ArrayList<SelectUser>();
        this.arraylist.addAll(_data);
        groupMem = GroupM;      //questa è la lista che devo aggiornre di volta in volta un utente viene aggiunto al gruppo dall'user
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
            view = li.inflate(R.layout.contacts_list_item, null);
            Log.e("Inside", "here--------------------------- In view1");
        } else {
            view = convertView;
            Log.e("Inside", "here--------------------------- In view2");
        }

        v = new ViewHolder();

        v.title = (TextView) view.findViewById(R.id.name);
        v.check = (CheckBox) view.findViewById(R.id.check);
        v.phone = (TextView) view.findViewById(R.id.no);
        v.imageView = (ImageView) view.findViewById(R.id.pic);
        v.email = (TextView) view.findViewById(R.id.email);

        final SelectUser data = (SelectUser) _data.get(i);
        v.title.setText(data.getName());
        v.check.setChecked(data.getCheckedBox());
        v.phone.setText(data.getPhone());
        //v.email.setText(data.getEmail());

        /*
        // Set image if exists
        try {

            if (data.getThumb() != null) {
                v.imageView.setImageBitmap(data.getThumb());
            } else {
                v.imageView.setImageResource(R.drawable.icon_utente);
            }
            // Setting round image
            /*
            Bitmap bm = BitmapFactory.decodeResource(view.getResources(), R.drawable.icon_utente); // Load default image
            roundedImage = new RoundImage(bm);
            v.imageView.setImageDrawable(roundedImage);

        } catch (OutOfMemoryError e) {
            // Add default picture
            v.imageView.setImageDrawable(this._c.getDrawable(R.drawable.icon_utente));
            e.printStackTrace();
        }
        */
        v.imageView.setImageDrawable(this._c.getDrawable(R.drawable.icon_utente));

        Log.e("Image Thumb", "--------------" + data.getThumb());

        // Set check box listener android
        v.check.setOnClickListener(new View.OnClickListener() {
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
        /*
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                CheckBox checkBox = (CheckBox) v.findViewById(R.id.check);
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
        */
        view.setTag(data);
        return view;
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
    static class ViewHolder {
        ImageView imageView;
        TextView title, phone, email;
        CheckBox check;
    }
}