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
import android.support.v7.app.ActionBar;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.polito.madinblack.expandedmad.GroupManaging.GroupListActivity;
import com.polito.madinblack.expandedmad.model.Expense;
import com.polito.madinblack.expandedmad.model.Group;
import com.polito.madinblack.expandedmad.model.MyApplication;
import com.polito.madinblack.expandedmad.model.User;
import com.polito.madinblack.expandedmad.model.Expense.Tag;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class ExpenseFillData extends AppCompatActivity {

    private int myinteger = 0;
    private int numMembers = 0;
    private int itemSelected;
    private String groupID = "index";
    private Group groupSelected;
    private MyApplication ma;
    private Map<Long, Float> userCost;
    private List<User> users;
    public RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_fill);
        //toolbar settings
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ma = MyApplication.getInstance();   //retrive del DB

        // Show the Up button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        Intent beginner = getIntent();
        groupSelected = ma.getSingleGroup(Long.valueOf(beginner.getStringExtra("index"))); //recupero l'id del gruppo selezionato, e quindi il gruppo stesso
        groupID = beginner.getStringExtra("index");   //id del gruppo, che devo considerare

        users = new ArrayList<>(groupSelected.getUsers());
        userCost = new HashMap<>();

        //show current date
        showDate(new Date());

        populateSpinner();

        EditText inputAmount = (EditText)findViewById(R.id.input_amount);
        inputAmount.addTextChangedListener(new MyTextWatcher(inputAmount));

        //in questo punto il codice prende la lista principale e la mostra come recyclerview
        recyclerView = (RecyclerView) findViewById(R.id.users_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    private void populateSpinner() {
        // you need to have a list of data that you want the spinner to display
        List<String> spinnerArray =  new ArrayList<String>();
        Iterator<User> us = groupSelected.getUsers().iterator();
        while(us.hasNext()) {
            User u = us.next();
            spinnerArray.add(u.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) findViewById(R.id.paidBy_spinner);
        sItems.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.confirm_expense) {


            EditText inputTitle = (EditText)findViewById(R.id.input_title);
            String title = inputTitle.getText().toString();

            EditText inputAmout = (EditText)findViewById(R.id.input_amount);
            String amountS = inputAmout.getText().toString();
            Float amount = Float.valueOf(amountS);

            Spinner inputPaidBy = (Spinner) findViewById(R.id.paidBy_spinner);
            int index = inputPaidBy.getSelectedItemPosition();
            User userSelect = groupSelected.getUsers().get(index);

            Spinner tag_spinner = (Spinner) findViewById(R.id.tag_spinner);
            String tagS = tag_spinner.getSelectedItem().toString();
            Tag tag = Tag.valueOf(tagS);

            TextView data = (TextView) findViewById(R.id.input_date);
            String dataS = data.getText().toString();
            String [] dayS = dataS.split("/");
            int day = Integer.parseInt(dayS[0]);
            int month = Integer.parseInt(dayS[1]);
            int year = Integer.parseInt(dayS[2]);











                /*
            for(int i=0; i<....; i++){
                TextView user = (TextView) findViewById(R.id.username);
                TextView personal = (TextView) findViewById(R.id.personal_amount);

            }
            */

            TextView description = (TextView) findViewById(R.id.input_description);
            String descriptionS = description.getText().toString();


            Expense newExpense = new Expense(title, tag, amount, descriptionS, Expense.Currency.EURO, groupSelected, userSelect, year, month, day);
            Iterator<Long> userId = userCost.keySet().iterator();
            while(userId.hasNext()){
                Long idUser = userId.next();
                newExpense.addPayment(groupSelected.getUser(idUser), 0f, userCost.get(userId));

            }
            groupSelected.addExpense(newExpense);



            Intent intent = new Intent(this, ExpenseListActivity.class);


            //intent.putExtra(EXTRA_MESSAGE, userID);

            startActivity(intent);
            return true;
        }else if(id == 16908332){   //questo è l'id del back button, non capisco perchè con R.id.home non ha lo stesso id
            Intent intent3 = new Intent(this, ExpenseListActivity.class);
            intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            navigateUpTo(intent3);
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
                    //modifyProportion(editable.toString());
                    break;
            }
        }
    }


    private void modifyProportion(String value) {
        float amount = value.equals("")?0:Float.parseFloat(value);
        float price = amount/userCost.size();
        for(int i=0;i<users.size();i++){
            Long key = users.get(i).getId();
            if(userCost.containsKey(key)){
                userCost.put(key, price);
            }

            recyclerView.getAdapter().notifyItemChanged(i, userCost.get(i));
        }
    }

    public void increaseInteger(View view) {
        /*RecyclerView rv = (RecyclerView)view;
        myinteger++;
        display(myinteger);*/

    }

    public void decreaseInteger(View view) {
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
        TextView dateText = (TextView)findViewById(R.id.input_date);
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
        TextView dateText = (TextView)findViewById(R.id.input_date);
        dateText.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }


    private void display(int number) {
        TextView displayInteger = (TextView) findViewById(R.id.integer_number);
        displayInteger.setText("" + number);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new ExpenseFillData.SimpleItemRecyclerViewAdapter(users));
    }

    //questa classe la usa per fare il managing della lista che deve mostrare
    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<ExpenseFillData.SimpleItemRecyclerViewAdapter.ViewHolder> {


        public SimpleItemRecyclerViewAdapter(List<User> userssGroup) {

            users = userssGroup;
        }

        @Override
        public ExpenseFillData.SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_for_new_expense, parent, false);
            return new ExpenseFillData.SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SimpleItemRecyclerViewAdapter.ViewHolder holder, final int position) {
            holder.mItem = users.get(position);   //mValues.get(position) rappresenta un singolo elemento della nostra lista di gruppi
            holder.mIdView.setText(holder.mItem.getName());
            holder.partition.setText(userCost.get(holder.mItem.getId()).toString());
            holder.minus.setOnClickListener(new View.OnClickListener() {

                //Minus button
                @Override
                public void onClick(View v) {
                    float part = Float.parseFloat(holder.mNumber.getText().toString());
                    part--;
                    holder.mNumber.setText(Float.toString(part));
                    if(part == 1){
                        holder.mNumber.setEnabled(false);
                        userCost.remove(users.get(position).getId());
                    }


                    recyclerView.getAdapter().notifyItemChanged(position, users.get(position));
                }
            });
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
            return userCost.size();
        }   //ritorna il numero di elementi nella lista

        //questa è una classe di supporto che viene usata per creare la vista a schermo, non ho ben capito come funziona
        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mNumber;
            public final TextView partition;
            public User mItem;
            public final Button minus;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.username);
                mNumber = (TextView) view.findViewById(R.id.integer_number);
                partition = (TextView) view.findViewById(R.id.personal_amount);
                minus = (Button) view.findViewById(R.id.decrease);
            }

            @Override
            public String toString() {
                return super.toString() + " '"  + "'";
            }
        }
    }

}
