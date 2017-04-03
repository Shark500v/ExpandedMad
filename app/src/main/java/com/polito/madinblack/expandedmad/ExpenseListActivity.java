package com.polito.madinblack.expandedmad;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.polito.madinblack.expandedmad.dummy.DummyContent;
import com.polito.madinblack.expandedmad.dummy.Group;

import java.util.List;

/**
 * An activity representing a list of Expenses. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ExpenseDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ExpenseListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_list);
        //toolbar settings
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //qui bisogna aggiungere un nuovo gruppo, in questo momento lo faccio nel modo semplice
                Group.AddNewGroup();
                //devo notificare la vista che qualcosa è cambiato
                RecyclerView recyclerView = (RecyclerView)findViewById(R.id.expense_list);
                recyclerView.getAdapter().notifyDataSetChanged();   //rendo visibili le modifiche apportate
                //questo stampa al fondo la scritta
                Snackbar.make(view, "New Group added!", Snackbar.LENGTH_LONG).setAction("Action", null).show();

            }
        });

        //in questo punto il codice prende la lista principale e la mostra come recyclerview
        View recyclerView = findViewById(R.id.expense_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        //questa riga di codice vale solo per gli schermi grandi, mi piacerebbe settare il fatto di poter vedere la doppia schermata quando
        //mettiamo lo schermo in landscape mode, secondo me i settaggi si mettono quì, ma ci sono cose più importanti da fare
        if (findViewById(R.id.expense_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }   //sugli smartphone dentro l'if non si entra mai !!!!
    }

    //tecnicamente si poteva anche gestire sopra questa funzione, direttamente nel main
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(Group.Groups));
    }

    //questa classe la usa per fare il managing della lista che deve mostrare
    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Group.GroupElements> mValues;

        public SimpleItemRecyclerViewAdapter(List<Group.GroupElements> groups) {
            mValues = groups;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);   //mValues.get(position) rappresenta un singolo elemento della nostra lista di gruppi
            holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).content);
            //sopra vengono settati i tre campi che costituisco le informazioni di ogni singolo gruppo, tutti pronti per essere mostriti nella gui

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) { //per i tablet
                        Bundle arguments = new Bundle();
                        arguments.putString(ExpenseDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        ExpenseDetailFragment fragment = new ExpenseDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction().replace(R.id.expense_detail_container, fragment).commit();
                    } else { //questo di sotto vale per gli smartphone (sia potrait che landscape mode)
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ExpenseDetailActivity.class);   //qui setto la nuova attività da mostrare a schermo dopo che clicco
                        intent.putExtra(ExpenseDetailFragment.ARG_ITEM_ID, holder.mItem.id);

                        context.startActivity(intent);
                    }
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
            public Group.GroupElements mItem;

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
