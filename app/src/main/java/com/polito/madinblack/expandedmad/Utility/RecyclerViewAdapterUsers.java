package com.polito.madinblack.expandedmad.Utility;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.model.Group;
import com.polito.madinblack.expandedmad.model.User;

import java.util.List;

//used to fill the my balance tab
public class RecyclerViewAdapterUsers extends RecyclerView.Adapter<RecyclerViewAdapterUsers.ViewHolder>{

    private final List<User> mValues;
    private Group groupSelected;

    public RecyclerViewAdapterUsers(List<User> items, Group g) {
        mValues = items;
        groupSelected = g;
    }

    @Override
    public RecyclerViewAdapterUsers.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_list_content, parent, false);
        return new RecyclerViewAdapterUsers.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewAdapterUsers.ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);                           //singolo utente
        holder.mIdView.setText(mValues.get(position).getName() + " " +mValues.get(position).getSurname());               //qui visualizzo nome e cognome
        //qui invece quanto deve o meno
        if (groupSelected.getMyCreditsDebits().get(mValues.get(position).getId())>0){
            holder.mContentView.setText(String.format("+%.2f", groupSelected.getMyCreditsDebits().get(mValues.get(position).getId())));
            holder.mContentView.setTextColor(Color.parseColor("#00c200"));
        }else if(groupSelected.getMyCreditsDebits().get(mValues.get(position).getId())<0){
            holder.mContentView.setText(String.format("%.2f", groupSelected.getMyCreditsDebits().get(mValues.get(position).getId())));
            holder.mContentView.setTextColor(Color.parseColor("#ff0000"));
        }else{
            holder.mContentView.setText(String.format("%.2f", groupSelected.getMyCreditsDebits().get(mValues.get(position).getId())));
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    /*Context context = v.getContext();
                    Intent intent = new Intent(context, ItemDetailActivity.class);
                    intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);

                    context.startActivity(intent);*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public User mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

}
