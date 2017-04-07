package com.polito.madinblack.expandedmad;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
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

import com.polito.madinblack.expandedmad.model.Expense;
import com.polito.madinblack.expandedmad.model.Expense.Tag;
import com.polito.madinblack.expandedmad.model.Group;
import com.polito.madinblack.expandedmad.model.MyApplication;
import com.polito.madinblack.expandedmad.model.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ExpenseFillData_2 extends AppCompatActivity {

    private int myinteger = 0;
    private int numMembers = 0;
    private int itemSelected;
    private RecyclerView recyclerView;
    private String groupID = "index";
    private Group groupSelected;
    private MyApplication ma;
    private Map<Long, Float> userCost;
    private List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_fill2);
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

        showDate(new Date());

        populateSpinner();





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
            Tag tag = Tag.valueOf(tagS.toUpperCase());

            TextView data = (TextView) findViewById(R.id.input_date);
            String dataS = data.getText().toString();
            String [] dayS = dataS.split("/");
            int day = Integer.parseInt(dayS[0]);
            int month = Integer.parseInt(dayS[1]);
            int year = Integer.parseInt(dayS[2]);


            TextView description = (TextView) findViewById(R.id.input_description);
            String descriptionS = description.getText().toString();


            Expense newExpense = new Expense(title, tag, amount, descriptionS, Expense.Currency.EURO, groupSelected, userSelect, year, month, day);
            Iterator<User> userId = users.iterator();
            while(userId.hasNext()){
                User userI = userId.next();
                newExpense.addPayment(userI, 0f, amount/users.size());

            }
            groupSelected.addExpense(newExpense);



            Intent intent = new Intent(this, ExpenseListActivity.class);


            intent.putExtra("index", groupID);

            startActivity(intent);
            return true;
        }else if(id == 16908332){
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





}
