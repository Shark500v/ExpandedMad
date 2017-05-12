package com.polito.madinblack.expandedmad;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.polito.madinblack.expandedmad.model.CostUtil;
import com.polito.madinblack.expandedmad.model.DecimalDigitsInputFilter;
import com.polito.madinblack.expandedmad.model.Expense;

import com.polito.madinblack.expandedmad.model.MyApplication;
import com.polito.madinblack.expandedmad.model.Payment;
import com.polito.madinblack.expandedmad.model.UserForGroup;
import com.polito.madinblack.expandedmad.utility.TabView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ExpenseFillData extends AppCompatActivity {

    private RecyclerView recyclerView;
    private String groupID = "index";
    private MyApplication ma;
    private List<UserForGroup> users;
    private List<Payment> mValues;
    private DatabaseReference databaseReference;
    private StorageReference mStorage;
    private EditText inputName, inputAmount;
    private TextInputLayout inputLayoutName, inputLayoutAmount;
    private Double amount;
    private Double roundedAmount = 0d;
    private String expenseName;
    private boolean onBind;
    Uri selectedImage;
    byte[] bytesArr;
    Bitmap bitmap;
    String expenseId;

    private static int RESULT_LOAD_IMAGE = 1;
    private static int RESULT_REQUEST_CAMERA = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_fill);
        //toolbar settings
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //prepare instance variable
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_title);
        inputLayoutAmount = (TextInputLayout) findViewById(R.id.input_layout_amount);
        inputName = (EditText) findViewById(R.id.input_title);
        inputAmount = (EditText) findViewById(R.id.input_amount);

       // inputAmount.setFilters(new InputFilter[] { new DecimalDigitsInputFilter(2)});

        ma = MyApplication.getInstance();   //retrive del DB

        users = new ArrayList<>();

        mStorage = FirebaseStorage.getInstance().getReference();

        Intent beginner = getIntent();
        groupID = beginner.getStringExtra("groupIndex");   //id del gruppo, che devo considerare


        //this remove focus from edit text when activity starts
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //show current date
        showDate(new Date());

        EditText inputAmount = (EditText)findViewById(R.id.input_amount);
        inputAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                modifyProportion(CostUtil.replaceDecimalComma(s.toString()));
            }
        });

        onBind = false;

        //in questo punto il codice prende la lista principale e la mostra come recyclerview
        recyclerView = (RecyclerView) findViewById(R.id.users_list);
        assert recyclerView != null;
        setupRecyclerView(recyclerView);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.confirm_expense) {

            Intent intent;

            if (!validateName()) {
                return true;
            }
            if (!validateAmount()) {
                return true;
            }


            Spinner tag_spinner = (Spinner) findViewById(R.id.tag_spinner);
            String tag = tag_spinner.getSelectedItem().toString();

            EditText data = (EditText) findViewById(R.id.input_date);
            String dataS = data.getText().toString();

            String [] dayS = dataS.split("/");
            Long day = Long.parseLong(dayS[0]);
            Long month = Long.parseLong(dayS[1]);
            Long year = Long.parseLong(dayS[2]);


            TextView description = (TextView) findViewById(R.id.input_description);
            String descriptionS = description.getText().toString();

            for(Payment payment : mValues){
                roundedAmount += payment.getToPaid();
            }

            /*added to set Paid value*/
            for(Payment payment : mValues){
                if(payment.getUserPhoneNumber().equals(ma.getUserPhoneNumber())){
                    payment.setPaid(roundedAmount);

                }
            }


            expenseId = Expense.writeNewExpense(FirebaseDatabase.getInstance().getReference(),
                    expenseName,
                    tag,
                    ma.getFirebaseId(),
                    ma.getUserPhoneNumber(),
                    ma.getUserName(),
                    ma.getUserSurname(),
                    amount,
                    roundedAmount,
                    "Euro",
                    "€",
                    groupID,
                    year,
                    month,
                    day,
                    descriptionS,
                    mValues
                    );
            /*
            Sarà da chiamare write new payment
            Expense newExpense = new Expense(expenseName, tag, amount, descriptionS, Expense.Currency.EURO, groupSelected, ma.myself, year, month, day);

            for(int i=0;i<mValues.size();i++){
                mValues.get(i).setExpense(newExpense);
            }

            for(int i=0;i<mValues.size();i++){
                //set Total Expense to people who pay
                if(mValues.get(i).getUser().getId() == ma.myself.getId()){
                    mValues.get(i).setPaid(amount);
                }
                newExpense.addPayment(mValues.get(i));
            }


            groupSelected.addExpense(newExpense);*/

            if(expenseId != null){
                uploadFile();
            }

            intent = new Intent(this, TabView.class);
            intent.putExtra("index", groupID);
            //startActivity(intent);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }else if(id == 16908332){
            Intent intent3 = new Intent(this, TabView.class);
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
        final CharSequence[] items = { getString(R.string.photo),
                getString(R.string.gallery), getString(R.string.cancel) };

        AlertDialog.Builder builder = new AlertDialog.Builder(ExpenseFillData.this);
        builder.setTitle(getString(R.string.add_proof));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                //boolean result=Utility.checkPermission(ExpenseFillData.this);

                if (items[item].equals(getString(R.string.photo))) {
                    //userChoosenTask ="Take Photo";
                    //if(result)
                    photoFromCamera();

                } else if (items[item].equals(getString(R.string.gallery))) {
                    //userChoosenTask ="Choose from Library";
                    //if(result)
                    photoFromGallery();

                } else if (items[item].equals(getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void photoFromGallery(){
        Intent i = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    private void photoFromCamera()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, RESULT_REQUEST_CAMERA);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            CircleImageView imageView = (CircleImageView) findViewById(R.id.expense_proof);
            imageView.setPadding(4,4,4,4);

            if (requestCode == RESULT_LOAD_IMAGE && null != data){
                selectedImage = data.getData();
                //String[] filePathColumn = {MediaStore.Images.Media.DATA};

            /*Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            // String picturePath contains the path of selected Image
            //CircularImageView imageView = (CircularImageView) findViewById(R.id.expense_proof);
            //imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));*/

                //imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                    bytesArr = bytes.toByteArray();
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {

                    //cambio qui come gestire errore
                    e.printStackTrace();
                }
            }
            else if (requestCode == RESULT_REQUEST_CAMERA){ //null!=data?????????
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                bytesArr = bytes.toByteArray();

                /*File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");

                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

                imageView.setImageBitmap(thumbnail);
            }
        }
    }

    @SuppressWarnings("VisibleForTests")
    private void uploadFile() {
        if(bytesArr != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            StorageReference filePath = mStorage.child("groups").child(groupID).child("expenses").child(expenseId);
            filePath.putBytes(bytesArr).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //if the upload is successfull
                    //hiding the progress dialog
                    progressDialog.dismiss();
                    //and displaying a success toast
                    Toast.makeText(getApplicationContext(), getString((R.string.file_uploaded)), Toast.LENGTH_LONG).show();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();
                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            progressDialog.setMessage(getString(R.string.uploading) + ": "+ ((int)progress) + "%");
                        }
                    })
                    .addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                            System.out.println(getString(R.string.upload_pause));
                        }
                    });
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
        String amountS = CostUtil.replaceDecimalComma(inputAmount.getText().toString().trim());
        if (amountS.isEmpty()) {
            inputLayoutAmount.setError(getString(R.string.err_msg_amount));
            requestFocus(inputAmount);
            return false;
        } else {
            try {
                amount = Double.valueOf(amountS);
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

    public void resetPayment(View view) {
        boolean enableWeight;

        if(inputAmount.getText().toString().equals("")){
            amount = 0d;
            enableWeight = false;
        }else{
            amount = Double.parseDouble(CostUtil.replaceDecimalComma(inputAmount.getText().toString()));
            enableWeight = true;
        }

        for(int i=0;i<mValues.size();i++){
            Payment currentPayment = mValues.get(i);
            if(currentPayment.isModified())
                currentPayment.setModified(false);
            currentPayment.setWeight(1);
            currentPayment.setToPaid(amount / users.size());
            currentPayment.setWeightEnabled(enableWeight);
            recyclerView.getAdapter().notifyItemChanged(i, currentPayment);
        }

    }

    private void modifyPayment() {
        int totalWeigth = 0;
        double netAmount = amount;

        for(Payment pay:mValues){
            if(pay.isModified()){
                netAmount -= pay.getToPaid();
            }
            else
                totalWeigth += pay.getWeight();
        }

        for(int i=0;i<mValues.size();i++){
            Payment currentPayment = mValues.get(i);
            if(netAmount>0 && !currentPayment.isModified())
                currentPayment.setToPaid( (netAmount * currentPayment.getWeight())/totalWeigth);
            else if(netAmount <=0 && !currentPayment.isModified())
                currentPayment.setToPaid(0f);
            else //currentPayment.isModified()
                continue;
            recyclerView.getAdapter().notifyItemChanged(i, currentPayment);
        }
    }

    private void modifyProportion(String value) {

        boolean enableWeight;
        int totalWeigth = 0;

        if(value.equals("")){
            amount = 0d;
            enableWeight = false;
        }else{
            amount = Double.parseDouble(value);
            enableWeight = true;
        }

        double netAmount = amount;

        for(Payment pay:mValues){
            if(pay.isModified()){
                netAmount -= pay.getToPaid();
            }
            else
                totalWeigth += pay.getWeight();
        }

        for(int i=0;i<mValues.size();i++){
            Payment currentPayment = mValues.get(i);
            if(netAmount>0 && !currentPayment.isModified())
                currentPayment.setToPaid( (netAmount * currentPayment.getWeight())/totalWeigth);
            else if(netAmount <=0 && !currentPayment.isModified())
                currentPayment.setToPaid(0f);
            else //currentPayment.isModified()
                continue;
            currentPayment.setWeightEnabled(enableWeight);
            recyclerView.getAdapter().notifyItemChanged(i, currentPayment);
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

    private void setupRecyclerView(@NonNull final RecyclerView recyclerView) {

        FirebaseDatabase.getInstance().getReference().child("groups").child(groupID)
                .child("users").addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        users.clear();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            UserForGroup user = postSnapshot.getValue(UserForGroup.class);
                            users.add(user);
                        }

                        List<Payment> payment = new ArrayList<>();
                        for(int i=0;i<users.size();i++){
                            payment.add(new Payment(users.get(i).getFirebaseId(), users.get(i).getPhoneNumber(), users.get(i).getName(), null, 0D, 0D));
                        }
                        recyclerView.setAdapter(new ExpenseFillData.SimpleItemRecyclerViewAdapter(payment));
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        System.out.println(getString(R.string.read_failed) + firebaseError.getMessage());
                    }
                });
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
            holder.mItem = mValues.get(position);
            if(holder.mItem.getUserPhoneNumber().equals(ma.getUserPhoneNumber()))
                holder.mIdView.setText(getString(R.string.you));
            else
                holder.mIdView.setText( holder.mItem.getUserName());
            onBind = true;
            holder.partition.setText( new DecimalFormat("#0.00").format( holder.mItem.getToPaid()));
            onBind = false;
            holder.mNumber.setText( String.valueOf(holder.mItem.getWeight()) );
            holder.minus.setEnabled( holder.mItem.isWeightEnabled());
            holder.plus.setEnabled( holder.mItem.isWeightEnabled());
            holder.partition.setEnabled( holder.mItem.isWeightEnabled() );

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

                /*to Modify*/
                //partition.setFilters(new InputFilter[] { new DecimalDigitsInputFilter(2)});
                partition.addTextChangedListener( new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        if(!onBind){
                            Double value = CostUtil.replaceDecimalComma(s.toString()).equals("")?0:Double.parseDouble(CostUtil.replaceDecimalComma(s.toString()));
                            mItem.setToPaid(value);
                            mItem.setModified(true);
                            mNumber.setText("-");
                            minus.setEnabled(false);
                            plus.setEnabled(false);
                            modifyPayment();
                        }
                    }
                });
                plus.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int currentWeight = Integer.parseInt(mNumber.getText().toString());
                        mItem.setWeight(++currentWeight);
                        mNumber.setText( String.valueOf(mItem.getWeight()) );
                        modifyPayment();

                    }
                });
                minus.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(Integer.parseInt(mNumber.getText().toString()) > 0){
                            int currentWeight = Integer.parseInt(mNumber.getText().toString());
                            mItem.setWeight(--currentWeight);
                            mNumber.setText( String.valueOf(mItem.getWeight()) );
                            modifyPayment();
                        }
                    }
                });
            }

            @Override
            public String toString() {
                return super.toString() + " '"  + "'";
            }
        }
    }
}


