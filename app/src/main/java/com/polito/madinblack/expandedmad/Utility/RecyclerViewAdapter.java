package com.polito.madinblack.expandedmad.Utility;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.polito.madinblack.expandedmad.ExpenseDetailActivity;
import com.polito.madinblack.expandedmad.ExpenseDetailFragment;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.model.Expense;
import com.polito.madinblack.expandedmad.model.MyApplication;

import java.util.List;



//questa classe la usa per fare il managing della lista che deve mostrare
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Expense> mValues;
    private String index = "hello";

    public RecyclerViewAdapter(List<Expense> expenses, String index) {
        mValues = expenses;
        this.index = index;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_list_content, parent, false);
        return new RecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewAdapter.ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);   //mValues.get(position) rappresenta un singolo elemento della nostra lista di spese
        holder.mIdView.setText(mValues.get(position).getName());
        if(mValues.get(position).getMyBalance()>0) {
            holder.mContentView.setText(String.format("+%.2f",mValues.get(position).getMyBalance()) + " " + mValues.get(position).getCurrency());
            holder.mContentView.setTextColor(Color.parseColor("#00c200"));
        }else if(mValues.get(position).getMyBalance()<0) {
            holder.mContentView.setText(String.format("%.2f",mValues.get(position).getMyBalance()) + " " + mValues.get(position).getCurrency());
            holder.mContentView.setTextColor(Color.parseColor("#ff0000"));
        }
        else{
            holder.mContentView.setText(String.format("%.2f",mValues.get(position).getMyBalance()) + " " + mValues.get(position).getCurrency());
        }

        if(mValues.get(position).getPaying().getId()== MyApplication.myself.getId())
            holder.mPaydBy.setText("Paid by: You");
        else
            holder.mPaydBy.setText("Paid by: " + mValues.get(position).getPaying().getName());

        //sopra vengono settati i tre campi che costituisco le informazioni di ogni singolo gruppo, tutti pronti per essere mostriti nella gui

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context context = v.getContext();
                Intent intent = new Intent(context, ExpenseDetailActivity.class);   //qui setto la nuova attività da mostrare a schermo dopo che clicco
                intent.putExtra(ExpenseDetailFragment.ARG_ITEM_ID, holder.mItem.getId().toString());
                intent.putExtra(ExpenseDetailFragment.ARG_GROUP_ID, index);         //così gli passo l'indice del gruppo di interesse

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
        public final TextView mIdView;
        public final TextView mPaydBy;
        public final TextView mContentView;
        public Expense mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mPaydBy = (TextView) view.findViewById(R.id.paidBy);
            mContentView = (TextView) view.findViewById(R.id.content);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
