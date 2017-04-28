package com.polito.madinblack.expandedmad.group_members;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.polito.madinblack.expandedmad.ExpenseDetailActivity;
import com.polito.madinblack.expandedmad.ExpenseDetailFragment;
import com.polito.madinblack.expandedmad.ExpenseListActivity;
import com.polito.madinblack.expandedmad.R;

import java.util.List;

import com.polito.madinblack.expandedmad.model.Group;
import com.polito.madinblack.expandedmad.model.MyApplication;
import com.polito.madinblack.expandedmad.model.User;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class GroupMemebersActivity extends AppCompatActivity {

    public String groupID = "init";
    private String name = "hello";
    private MyApplication ma;
    private Group groupSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users_of_group);

        groupID = getIntent().getStringExtra("GROUP_ID");


        ma = MyApplication.getInstance();
        groupSelected = ma.getSingleGroup(Long.valueOf(getIntent().getStringExtra("GROUP_ID")));
        name = groupSelected.getName();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(name);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, ExpenseListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            navigateUpTo(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        //recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(groupSelected.getUsers2()));
    }

    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<User> mValues;

        public SimpleItemRecyclerViewAdapter(List<User> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            if(holder.mItem.getId().equals(ma.getUserPhoneNumber()))
                holder.mIdView.setText("You");
            else
                holder.mIdView.setText(mValues.get(position).getName() + " " + mValues.get(position).getSurname());
            holder.mContentView.setText("");

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*
                    Context context = v.getContext();
                    Intent intent = new Intent(context, UserExpenses.class);
                    Bundle extras = new Bundle();
                    extras.putString("GROUP_ID",groupID);
                    extras.putString("USER_ID",holder.mItem.id);
                    intent.putExtras(extras);

                    context.startActivity(intent);
                    */
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
}
