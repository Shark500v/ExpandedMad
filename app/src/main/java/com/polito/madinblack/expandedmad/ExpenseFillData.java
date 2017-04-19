package com.polito.madinblack.expandedmad;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
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
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.polito.madinblack.expandedmad.GroupManaging.GroupListActivity;
import com.polito.madinblack.expandedmad.model.Expense;
import com.polito.madinblack.expandedmad.model.Group;
import com.polito.madinblack.expandedmad.model.MyApplication;
import com.polito.madinblack.expandedmad.model.Payment;
import com.polito.madinblack.expandedmad.model.User;
import com.polito.madinblack.expandedmad.model.Expense.Tag;

import java.io.IOException;
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

    private RecyclerView recyclerView;
    private String groupID = "index";
    private Group groupSelected;
    private MyApplication ma;
    private List<User> users;
    private List<Integer> mWeights;
    private List<Payment> mValues;
    DatabaseReference databaseReference;
    private EditText inputName, inputAmount;
    private TextInputLayout inputLayoutName, inputLayoutAmount;
    private Float amount;
    private String expenseName;

    private static int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_fill);
        //toolbar settings
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //prepare instance variable
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_title);
        inputLayoutAmount = (TextInputLayout) findViewById(R.id.input_layout_amount);
        inputName = (EditText) findViewById(R.id.input_title);
        inputAmount = (EditText) findViewById(R.id.input_amount);

        ma = MyApplication.getInstance();   //retrive del DB

        Intent beginner = getIntent();
        groupSelected = ma.getSingleGroup(Long.valueOf(beginner.getStringExtra("index"))); //recupero l'id del gruppo selezionato, e quindi il gruppo stesso
        groupID = beginner.getStringExtra("index");   //id del gruppo, che devo considerare

        users = new ArrayList<>(groupSelected.getUsers());

        //this remove focus from edit text when activity starts
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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


            boolean isNull = false;
            Intent intent;

            if (!validateName()) {
                return true;
            }
            if (!validateAmount()) {
                return true;
            }

            Spinner tag_spinner = (Spinner) findViewById(R.id.tag_spinner);
            String tagS = tag_spinner.getSelectedItem().toString();
            Tag tag = Tag.valueOf(tagS.toUpperCase());

            EditText data = (EditText) findViewById(R.id.input_date);
            String dataS = data.getText().toString();
            if(dataS.isEmpty() || dataS==null) {
                intent = new Intent(this, ExpenseFillData.class);
                intent.putExtra("index", groupID);
                startActivity(intent);
                return true;
            }

            String [] dayS = dataS.split("/");
            int day = Integer.parseInt(dayS[0]);
            int month = Integer.parseInt(dayS[1]);
            int year = Integer.parseInt(dayS[2]);


            TextView description = (TextView) findViewById(R.id.input_description);
            String descriptionS = description.getText().toString();
            if(descriptionS.isEmpty() || descriptionS==null) {
                descriptionS = " ";

            }

            Expense newExpense = new Expense(expenseName, tag, amount, descriptionS, Expense.Currency.EURO, groupSelected, ma.myself, year, month, day);

            for(int i=0;i<mValues.size();i++){
                mValues.get(i).setExpense(newExpense);
            }

            for(int i=0;i<mValues.size();i++){
                newExpense.addPayment(mValues.get(i));
            }


            groupSelected.addExpense(newExpense);

            intent = new Intent(this, ExpenseListActivity.class);
            intent.putExtra("index", groupID);
            startActivity(intent);
            finish();
            return true;
        }else if(id == 16908332){
            Intent intent3 = new Intent(this, ExpenseListActivity.class);
            intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            navigateUpTo(intent3);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //aggiunge una spesa al gruppo nel database associandogli una chiave univoca
    public void writeNewExpense(Expense expense){
        databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        String expenseId = databaseReference.push().getKey();
        databaseReference.child(groupID).child(expenseId).setValue(expense);

        //bisogna aggiungere la spesa anche sotto users

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_new_expense, menu);
        return true;
    }

    public void uploadPhoto(View view){
        Intent i = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            // String picturePath contains the path of selected Image
            //CircularImageView imageView = (CircularImageView) findViewById(R.id.expense_proof);
            //imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            CircularImageView imageView = (CircularImageView) findViewById(R.id.expense_proof);
            //imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            try {
                imageView.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage));
            } catch (IOException e) {

                //cambio qui come gestire errore
                e.printStackTrace();
            }
        }
    }


    private boolean validateName() {
        expenseName = inputName.getText().toString().trim();
        if (expenseName.isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_title));
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateAmount() {
        String amountS = inputAmount.getText().toString().trim();
        if (amountS.isEmpty()) {
            inputLayoutAmount.setError(getString(R.string.err_msg_amount));
            requestFocus(inputAmount);
            return false;
        } else {
            try {
                amount = Float.valueOf(amountS);
            } catch (NumberFormatException ex) {
                inputLayoutAmount.setError(getString(R.string.err_msg_amount));
                requestFocus(inputAmount);
                return false;
            }
            inputLayoutAmount.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
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
            mValues.get(i).setToPaid(price);
            recyclerView.getAdapter().notifyItemChanged(i, mValues.get(i));
        }
    }

    private void adjustPartition(){
        for(int i=0;i<mValues.size();i++){
            mValues.get(i).getToPaid();
            recyclerView.getAdapter().notifyItemChanged(i, mValues.get(i));
        }
    }

    private void fixPayment(){
        int totalWeigth = 0;

        for(Payment pay:mValues){
            totalWeigth += pay.getWeight();
        }

        for(Payment pay:mValues){
            pay.setToPaid( (amount * pay.getWeight())/totalWeigth);
        }

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
                    showDate(arg1, arg2+1, arg3);
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
        List<Payment> payment = new ArrayList<>();
        for(int i=0;i<users.size();i++){
            payment.add(new Payment(users.get(i), null, (float)0.00, (float)0.00));
        }
        //initialize list
        List<Integer> weights = new ArrayList<>();
        for(User u:users){
            weights.add(2);
        }
        recyclerView.setAdapter(new ExpenseFillData.SimpleItemRecyclerViewAdapter(payment));
    }

    //questa classe la usa per fare il managing della lista che deve mostrare
    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {


        public SimpleItemRecyclerViewAdapter(List<Payment> payments) {

            mValues = payments;
        }

        @Override
        public ExpenseFillData.SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_for_new_expense, parent, false);
            return new ExpenseFillData.SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ExpenseFillData.SimpleItemRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);   //mValues.get(position) rappresenta un singolo elemento della nostra lista di gruppi
            holder.mIdView.setText( holder.mItem.getUser().getName());
            holder.partition.setText( holder.mItem.getToPaid().toString());
            //holder.mNumber.setText( mWeights.get(position));
            //sopra vengono settati i tre campi che costituisco le informazioni di ogni singolo gruppo, tutti pronti per essere mostriti nella gui
            holder.minus.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(Integer.parseInt(holder.mNumber.getText().toString()) > 1){
                        holder.mNumber.setText("0");
                        fixPayment();
                    }
                }
            });
            holder.plus.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    holder.mNumber.setText("5");
                    fixPayment();

                }
            });
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
        }

        //questa è una classe di supporto che viene usata per creare la vista a schermo, non ho ben capito come funziona
        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mNumber;
            public final EditText partition;
            public final Button plus;
            public final Button minus;
            public Payment mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.username);
                mNumber = (TextView) view.findViewById(R.id.integer_number);
                partition = (EditText) view.findViewById(R.id.personal_amount);
                plus = (Button) view.findViewById(R.id.increase);
                minus = (Button) view.findViewById(R.id.decrease);
            }

            @Override
            public String toString() {
                return super.toString() + " '"  + "'";
            }
        }
    }
}


