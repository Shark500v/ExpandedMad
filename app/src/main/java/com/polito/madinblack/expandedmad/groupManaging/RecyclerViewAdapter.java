package com.polito.madinblack.expandedmad.groupManaging;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.Utility.TabView;
import com.polito.madinblack.expandedmad.model.Group;

import java.util.List;

//questa classe la usa per fare il managing della lista che deve mostrare
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private final List<Group> mValues;

    public RecyclerViewAdapter(List<Group> groups) {
        mValues = groups;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewAdapter.ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);   //mValues.get(position) rappresenta un singolo elemento della nostra lista di gruppi
        holder.mNumView.setText(Integer.toString(mValues.get(position).getUsers().size()) + " members");
        holder.mContentView.setText(mValues.get(position).getName());
        //sopra vengono settati i tre campi che costituisco le informazioni di ogni singolo gruppo, tutti pronti per essere mostriti nella gui

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, TabView.class); //qui setto la nuova attività da mostrare a schermo dopo che clicco
                intent.putExtra("index", holder.mItem.getId().toString());    //passo alla nuova activity l'ide del gruppo chè l'utente ha selezionto

                context.startActivity(intent);
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
        public final TextView mNumView;
        public final TextView mContentView;
        public Group mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNumView = (TextView) view.findViewById(R.id.num_members);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}