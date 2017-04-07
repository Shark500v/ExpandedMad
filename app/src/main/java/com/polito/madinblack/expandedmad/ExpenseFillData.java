package com.polito.madinblack.expandedmad;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.polito.madinblack.expandedmad.GroupManaging.GroupListActivity;
import com.polito.madinblack.expandedmad.dummy.Group;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class ExpenseFillData extends AppCompatActivity {

    private int myinteger = 0;
    private int numMembers = 0;
    private int itemSelected;
    private List<Group.GroupElements> mValues;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_fill);
        //toolbar settings
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //show current date
        showDate(new Date());


        EditText inputAmount = (EditText)findViewById(R.id.input_amount);
        inputAmount.addTextChangedListener(new MyTextWatcher(inputAmount));

        //in questo punto il codice prende la lista principale e la mostra come recyclerview
        recyclerView = (RecyclerView) findViewById(R.id.users_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.confirm_expense) {

            Intent intent = new Intent(this, GroupListActivity.class);
            //intent.putExtra(EXTRA_MESSAGE, userID);

            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_new_expense, menu);
        return true;
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_amount:
                    modifyProportion(editable.toString());
                    break;
            }
        }
    }

    private void modifyProportion(String value) {
        float amount = value.equals("")?0:Float.parseFloat(value);
        float price = amount/mValues.size();
        for(int i=0;i<mValues.size();i++){
            mValues.get(i).details = Float.toString(price);
            recyclerView.getAdapter().notifyItemChanged(i, mValues.get(i));
        }
    }

    public void increaseInteger(View view) {
        /*RecyclerView rv = (RecyclerView)view;
        myinteger++;
        display(myinteger);*/

    }public void decreaseInteger(View view) {
        /*myinteger--;
        display(myinteger);*/
    }

    public void showDataPicker(View view) {
        Calendar myCalendar = Calendar.getInstance();
        new DatePickerDialog(this,
                myDateListener,  myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showDate(Date data) {
        EditText dateText = (EditText)findViewById(R.id.input_date);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateText.setText(dateFormat.format(data)); //16/11/2016
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,int arg1, int arg2, int arg3) {
                    showDate(arg1, arg2, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        EditText dateText = (EditText)findViewById(R.id.input_date);
        dateText.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }


    private void display(int number) {
        TextView displayInteger = (TextView) findViewById(R.id.integer_number);
        displayInteger.setText("" + number);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new ExpenseFillData.SimpleItemRecyclerViewAdapter(Group.Groups));
    }

    //questa classe la usa per fare il managing della lista che deve mostrare
    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<ExpenseFillData.SimpleItemRecyclerViewAdapter.ViewHolder> {


        public SimpleItemRecyclerViewAdapter(List<Group.GroupElements> groups) {

            mValues = groups;
        }

        @Override
        public ExpenseFillData.SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_for_new_expense, parent, false);
            return new ExpenseFillData.SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ExpenseFillData.SimpleItemRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);   //mValues.get(position) rappresenta un singolo elemento della nostra lista di gruppi
            holder.mIdView.setText(mValues.get(position).id);
            holder.partition.setText(mValues.get(position).details);
            //holder.mContentView.setText(mValues.get(position).content);
            //sopra vengono settati i tre campi che costituisco le informazioni di ogni singolo gruppo, tutti pronti per essere mostriti nella gui
            /*holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    holder.mIdView.setText("5");
                }
            });*/
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }   //ritorna il numero di elementi nella lista

        //questa è una classe di supporto che viene usata per creare la vista a schermo, non ho ben capito come funziona
        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mNumber;
            public final TextView partition;
            public Group.GroupElements mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.username);
                mNumber = (TextView) view.findViewById(R.id.integer_number);
                partition = (TextView) view.findViewById(R.id.personal_amount);
            }

            @Override
            public String toString() {
                return super.toString() + " '"  + "'";
            }
        }
    }

}
