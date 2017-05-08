package com.polito.madinblack.expandedmad.groupManaging;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.model.HistoryInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GroupHistory extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_my);

        setContentView(R.layout.activity_group_history);
        RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        HistoryAdapter ca = new HistoryAdapter(createList(30));
        if(ca.getItemCount()>0){
            TextView tx = (TextView) findViewById(R.id.textView);
            tx.setVisibility(View.GONE);
            recList.setVisibility(View.VISIBLE);
            recList.setAdapter(ca);
        }
    }





    private List<HistoryInfo> createList(int size) {

        List<HistoryInfo> result = new ArrayList<HistoryInfo>();
        for (int i=1; i <= size; i++) {
            HistoryInfo ci = new HistoryInfo();
            ci.setName("Name " + i);
            ci.setContent("Surname " + i + " Ciao ho aggiunto la spesa");
            ci.setDate(new Date());
            result.add(ci);

        }

        return result;
    }

    public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ContactViewHolder> {

        private List<HistoryInfo> contactList;

        public HistoryAdapter(List<HistoryInfo> contactList) {
            this.contactList = contactList;
        }


        @Override
        public int getItemCount() {
            return contactList.size();
        }

        @Override
        public void onBindViewHolder(ContactViewHolder contactViewHolder, int i) {
            HistoryInfo ci = contactList.get(i);
            contactViewHolder.vName.setText(ci.getName());
            contactViewHolder.vContent.setText(ci.getContent());
            contactViewHolder.vTitle.setText(ci.getDateAsString());
        }

        @Override
        public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.card_layout_history, viewGroup, false);

            return new ContactViewHolder(itemView);
        }

        public class ContactViewHolder extends RecyclerView.ViewHolder {

            protected TextView vName;
            protected TextView vContent;
            protected TextView vTitle;

            public ContactViewHolder(View v) {
                super(v);
                vName =  (TextView) v.findViewById(R.id.txtName);
                vContent = (TextView)  v.findViewById(R.id.txtContent);
                vTitle = (TextView) v.findViewById(R.id.title);
            }
        }
    }

}

