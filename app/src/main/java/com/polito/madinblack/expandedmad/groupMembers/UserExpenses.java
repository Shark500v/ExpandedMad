package com.polito.madinblack.expandedmad.groupMembers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.polito.madinblack.expandedmad.ExpenseDetailActivity;
import com.polito.madinblack.expandedmad.ExpenseDetailFragment;
import com.polito.madinblack.expandedmad.R;

import java.util.List;

import com.polito.madinblack.expandedmad.dummy.Expense;
import com.polito.madinblack.expandedmad.dummy.Group;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class UserExpenses extends AppCompatActivity {

    private String groupID = "";
    private String userID = "";
    private Expense eItem;  //quello che vado a mostrare in questa activity è una lista di spese
    private Group.GroupElements groupSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_expenses);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_detail_user);


        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }



        if (savedInstanceState == null) {

            Bundle extras = getIntent().getExtras();
            groupID = extras.getString("GROUP_ID");
            userID = extras.getString("USER_ID");
            actionBar.setTitle(userID);

            groupSelected = Group.Group_MAP.get(groupID);  //recupero l'id del gruppo selezionato, e quindi il gruppo stesso
            eItem = (Expense) groupSelected.getList();

            View recyclerView = findViewById(R.id.expense_list);
            assert recyclerView != null;
            setupRecyclerView((RecyclerView) recyclerView);
        }

    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new UserExpenses.SimpleItemRecyclerViewAdapter(eItem.list));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_details:
                Intent intent = new Intent(this, UserDebts.class);
                Bundle extras = new Bundle();
                extras.putString("GROUP_ID",groupID);
                extras.putString("USER_ID",userID);
                intent.putExtras(extras);
                startActivity(intent);
                return true;

            case R.id.home:
                //navigateUpTo(new Intent(this, GroupMemebersActivity.class));    //definisco il parente verso cui devo tornare indietro
                Intent intent2 = new Intent(this, GroupMemebersActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                navigateUpTo(intent2);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail_user, menu);
        return true;
    }

    public void userMoreInfo(View view) {
        Intent intent = new Intent(this, UserDebts.class);
        intent.putExtra(EXTRA_MESSAGE, userID);

        startActivity(intent);
    }


        //questa classe la usa per fare il managing della lista che deve mostrare
        public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<UserExpenses.SimpleItemRecyclerViewAdapter.ViewHolder> {

            private final List<Expense.ExpenseElement> mValues;

            public SimpleItemRecyclerViewAdapter(List<Expense.ExpenseElement> expenses) {
                mValues = expenses;
            }

            @Override
            public UserExpenses.SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_list_content, parent, false);
                return new UserExpenses.SimpleItemRecyclerViewAdapter.ViewHolder(view);
            }

            @Override
            public void onBindViewHolder(final UserExpenses.SimpleItemRecyclerViewAdapter.ViewHolder holder, int position) {
                holder.mItem = mValues.get(position);   //mValues.get(position) rappresenta un singolo elemento della nostra lista di spese
                holder.mIdView.setText(mValues.get(position).id);
                holder.mContentView.setText(mValues.get(position).content);
                //sopra vengono settati i tre campi che costituisco le informazioni di ogni singolo gruppo, tutti pronti per essere mostriti nella gui

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Context context = v.getContext();
                        Intent intent = new Intent(context, ExpenseDetailActivity.class);   //qui setto la nuova attività da mostrare a schermo dopo che clicco
                        intent.putExtra(ExpenseDetailFragment.ARG_ITEM_ID, holder.mItem.id);

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
                public final TextView mContentView;
                public Expense.ExpenseElement mItem;

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
