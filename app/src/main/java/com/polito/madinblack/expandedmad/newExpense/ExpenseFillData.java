package com.polito.madinblack.expandedmad.newExpense;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
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
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;


import com.bumptech.glide.Glide;
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
import com.polito.madinblack.expandedmad.BuildConfig;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.UserPage;
import com.polito.madinblack.expandedmad.model.CostUtil;
import com.polito.madinblack.expandedmad.model.Currency;
import com.polito.madinblack.expandedmad.model.Expense;

import com.polito.madinblack.expandedmad.model.MyApplication;
import com.polito.madinblack.expandedmad.model.Payment;
import com.polito.madinblack.expandedmad.model.UserForGroup;
import com.polito.madinblack.expandedmad.tabViewGroup.TabView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ExpenseFillData extends AppCompatActivity {

    private RecyclerView recyclerView;
    private String groupID = "index";
    private List<UserForGroup> users;
    private List<Payment> mValues;
    private DatabaseReference databaseReference;
    private DatabaseReference mDatabaseForLoadUrl;
    private StorageReference mStorage;
    private EditText inputName, inputAmount, inputRoundedAmount, inputRoundedCurrency;
    private TextInputLayout inputLayoutName, inputLayoutAmount, inputLayoutTag;
    private ScrollView scrollView;
    private Spinner tag_spinner;
    private LinearLayout layoutRounded;
    private Double amount;
    private String currencySymbol;
    private String currencyISO;
    private Double roundedAmount = 0d;
    private String expenseName;
    private boolean onBind;
    private boolean isRounded;
    Uri selectedImage;
    byte[] bytesArr;
    Bitmap bitmap;
    String expenseId;
    private Spinner spinner;
    private String url;
    private String groupName;
    private String pictureImagePath;

    private static int THUMBNAIL_SIZE = 256;

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
        inputLayoutName         = (TextInputLayout) findViewById(R.id.input_layout_title);
        inputLayoutAmount       = (TextInputLayout) findViewById(R.id.input_layout_amount);
        inputLayoutTag          = (TextInputLayout) findViewById(R.id.input_layout_tag);
        layoutRounded           = (LinearLayout) findViewById(R.id.layout_rounded);
        inputName               = (EditText) findViewById(R.id.input_title);
        inputAmount             = (EditText) findViewById(R.id.input_amount);
        inputRoundedAmount      = (EditText) findViewById(R.id.input_rounded_cost);
        inputRoundedCurrency    = (EditText) findViewById(R.id.input_rounded_cost_currency);
        tag_spinner             = (Spinner) findViewById(R.id.tag_spinner);
        scrollView              = (ScrollView) findViewById(R.id.scrollView);


       //inputAmount.setFilters(new InputFilter[] { new DecimalDigitsInputFilter(2)});



        users = new ArrayList<>();

        mStorage = FirebaseStorage.getInstance().getReference();

        Intent beginner = getIntent();
        groupID = beginner.getStringExtra("groupIndex");   //id del gruppo, che devo considerare
        groupName = beginner.getStringExtra("groupName");

        //this remove focus from edit text when activity starts
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //show current date
        showDate(new Date());

        spinner = (Spinner) findViewById(R.id.currency);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<> (this,
                android.R.layout.simple_spinner_item, Currency.getCurrencyValues(MyApplication.getCurrencyISOFavorite()));
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        //add callback for tag_spinner
        tag_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                inputLayoutTag.setErrorEnabled(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // do nothing
            }

        });


        inputRoundedCurrency.setText(MyApplication.getCurrencyISOFavorite().toString());

        inputAmount = (EditText)findViewById(R.id.input_amount);
        inputAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = CostUtil.replaceDecimalComma(s.toString());
                if(str == null){
                    inputLayoutAmount.setError(getString(R.string.err_msg_correct_amount));
                    requestFocus(inputAmount);
                    modifyProportion("");
                }else{
                    inputLayoutAmount.setErrorEnabled(false);
                    modifyProportion(str);
                }
            }
        });

        onBind = false;

        //in questo punto il codice prende la lista principale e la mostra come recyclerview
        recyclerView = (RecyclerView) findViewById(R.id.users_list);
        assert recyclerView != null;
        setupRecyclerView(recyclerView);

        //add listener to spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currencySymbol= Currency.getSymbol(Currency.CurrencyISO.valueOf((String) parent.getItemAtPosition(position)));
                currencyISO = Currency.CurrencyISO.valueOf((String) parent.getItemAtPosition(position)).toString();
                if(isRounded){
                    inputRoundedCurrency.setText(currencyISO);
                    inputRoundedCurrency.startAnimation(AnimationUtils.loadAnimation(ExpenseFillData.this, android.R.anim.fade_in));
                }
                if(recyclerView.getAdapter() != null)
                    recyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


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
            if (!validateTag()) {
                return true;
            }

            //Wrote by Alessio
            /*
            Spinner currency_spinner = (Spinner) findViewById(R.id.currency);
            String currencySymbol = currency_spinner.getSelectedItem().toString();
            String[] names=getResources().getStringArray(R.array.currency);
            String currency = names[currency_spinner.getSelectedItemPosition()];
            */

            Spinner currency_spinner = (Spinner) findViewById(R.id.currency);
            String currencySymbol = Character.toString(currency_spinner.getSelectedItem().toString().charAt(0));
            Currency.CurrencyISO currencyISO = Currency.CurrencyISO.valueOf(currency_spinner.getSelectedItem().toString());


            String tag = tag_spinner.getSelectedItem().toString();

            EditText data = (EditText) findViewById(R.id.input_date);
            String dataS = data.getText().toString();

            String [] dayS = dataS.split("/");
            Long day = Long.parseLong(dayS[0]);
            Long month = Long.parseLong(dayS[1]);
            Long year = Long.parseLong(dayS[2]);


            TextView description = (TextView) findViewById(R.id.input_description);
            String descriptionS = description.getText().toString();

            if(roundedAmount == 0){
                roundedAmount = amount;
                /*for(Payment payment : mValues){
                    roundedAmount += payment.getToPaid();
                }*/
            }

            /*added to set Paid value*/
            for(Payment payment : mValues){
                if(payment.getUserPhoneNumber().equals(MyApplication.getUserPhoneNumber())){
                    payment.setPaid(roundedAmount);

                }
            }


            expenseId = Expense.writeNewExpense(FirebaseDatabase.getInstance().getReference(),
                    expenseName,
                    tag,
                    MyApplication.getFirebaseId(),
                    MyApplication.getUserPhoneNumber(),
                    MyApplication.getUserName(),
                    MyApplication.getUserSurname(),
                    amount,
                    roundedAmount,
                    currencyISO,
                    groupID,
                    year,
                    month,
                    day,
                    descriptionS,
                    Expense.State.ONGOING,
                    -1*System.currentTimeMillis(),
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
            intent.putExtra("groupIndex", groupID);
            intent.putExtra("groupName", groupName);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);


        /*Intent i = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);*/
    }

    private void photoFromCamera()
    {
        /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, RESULT_REQUEST_CAMERA);
        }*/
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + ".jpg";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(storageDir != null) {
            pictureImagePath = storageDir.getAbsolutePath() + "/" + imageFileName;
            File file = new File(pictureImagePath);
            selectedImage = FileProvider.getUriForFile(ExpenseFillData.this, BuildConfig.APPLICATION_ID + ".provider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImage);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, RESULT_REQUEST_CAMERA);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            CircleImageView imageView = (CircleImageView) findViewById(R.id.expense_proof);
            imageView.setPadding(4,4,4,4);

            if (requestCode == RESULT_LOAD_IMAGE && data != null){
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
                    bitmap = getThumbnail(selectedImage);
                    //bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                    bytesArr = bytes.toByteArray();
                    Glide.with(getApplicationContext()).load(bytesArr).override(128,128).centerCrop().fitCenter().into(imageView);
                } catch (IOException e) {

                    //cambio qui come gestire errore
                    e.printStackTrace();
                }
            }
            else if (requestCode == RESULT_REQUEST_CAMERA){ //null!=data?????????
                /*Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                bytesArr = bytes.toByteArray();*/

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
                File imgFile = new File(pictureImagePath);
                if (imgFile.exists()) {
                    try {
                        bitmap = getThumbnail(FileProvider.getUriForFile(ExpenseFillData.this, BuildConfig.APPLICATION_ID + ".provider", imgFile));
                        //bitmap = (Bitmap) data.getExtras().get("data");
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                        bytesArr = bytes.toByteArray();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Glide.with(getApplicationContext()).load(bytesArr).override(128,128).centerCrop().fitCenter().into(imageView);
            }
        }
    }

    @SuppressWarnings("VisibleForTests")
    private void uploadFile() {
        if(bytesArr != null) {
            //final ProgressDialog progressDialog = new ProgressDialog(this);
            //progressDialog.setTitle("Uploading");
            //progressDialog.show();

            StorageReference filePath = mStorage.child("groups").child(groupID).child("expenses").child(expenseId + ".jpg");
            filePath.putBytes(bytesArr).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //if the upload is successfull
                    //hiding the progress dialog
                    //progressDialog.dismiss();

                    url = taskSnapshot.getDownloadUrl().toString();
                    mDatabaseForLoadUrl = FirebaseDatabase.getInstance().getReference().child("expenses").child(expenseId).child("urlImage");
                    mDatabaseForLoadUrl.setValue(url);

                    //and displaying a success toast
                    //Toast.makeText(getApplicationContext(), getString((R.string.file_uploaded)), Toast.LENGTH_LONG).show();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            //progressDialog.dismiss();
                            //and displaying error message
                            //Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //progressDialog.setMessage(getString(R.string.uploading) + ": "+ ((int)progress) + "%");
                        }
                    })
                    .addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                            //System.out.println(getString(R.string.upload_pause));
                        }
                    });
        }
    }

    public Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException{
        InputStream input = this.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();

        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
            return null;
        }

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        input = this.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio){
        int k = Integer.highestOneBit((int)Math.floor(ratio));
        if(k==0) return 1;
        else return k;
    }


    private boolean validateName() {
        expenseName = inputName.getText().toString().trim();
        if (expenseName.isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_title));
            requestFocus(inputName);
            scrollView.scrollTo(0,0);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateAmount() {
        String amountS = inputAmount.getText().toString().trim();
        CostUtil.replaceDecimalComma(amountS);
        if (amountS.isEmpty()) {
            inputLayoutAmount.setError(getString(R.string.err_msg_amount));
            requestFocus(inputAmount);
            scrollView.scrollTo(0,0);
            return false;
        } else {
            try {
                amount = Double.valueOf(amountS);
            } catch (NumberFormatException ex) {
                inputLayoutAmount.setError(getString(R.string.err_msg_amount));
                requestFocus(inputAmount);
                scrollView.scrollTo(0,0);
                return false;
            }
            inputLayoutAmount.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateTag() {
        int index = tag_spinner.getSelectedItemPosition();
        if(index == 0){
            inputLayoutTag.setError(getString(R.string.err_msg_tag));
            requestFocus(tag_spinner);
            scrollView.scrollTo(0,250);
            return false;
        } else {
            inputLayoutTag.setErrorEnabled(false);
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
            amount = checkDivsionAndGetNet(mValues.size(), Double.parseDouble(CostUtil.replaceDecimalComma(inputAmount.getText().toString())));
            enableWeight = true;
        }

        for(int i=0;i<mValues.size();i++){
            Payment currentPayment = mValues.get(i);
            if(currentPayment.isModified())
                currentPayment.setModified(false);
            currentPayment.setWeight(1);
            currentPayment.setToPay(amount / users.size());
            currentPayment.setWeightEnabled(enableWeight);
            recyclerView.getAdapter().notifyItemChanged(i, currentPayment);
        }

    }

    public void showReoundedCost(){
        if(isRounded){
            inputRoundedAmount.setText(new DecimalFormat("#0.00").format( roundedAmount));
        }else{
            //show with animation
            inputRoundedCurrency.setText(currencyISO);
            inputRoundedAmount.setText(new DecimalFormat("#0.00").format( roundedAmount));
            layoutRounded.setVisibility(View.VISIBLE);
            layoutRounded.startAnimation(AnimationUtils.loadAnimation(ExpenseFillData.this, android.R.anim.fade_in));

            isRounded = true;
        }
    }

    public double checkDivsionAndGetNet(int totalWeight, double netAmount){
        int intAmount = (int)(netAmount*100);

        /*String str = amount.toString();
        if(str.indexOf('.') !=-1){
            int diff = str.length() - str.indexOf('.');
            if(diff>3){
                int difference = intAmount % totalWeight;
                if(difference != 0){
                    roundedAmount = (Double.parseDouble(str.substring(0, str.length()-1)) * 100 + difference)/100;
                    showReoundedCost();
                    str = ((Double)netAmount).toString();
                    if(str.indexOf('.') !=-1){
                        diff = str.length() - str.indexOf('.');
                        if(diff>3){
                            netAmount = Double.parseDouble(str.substring(0, str.length()-1));
                        }
                    }
                    return (netAmount * 100 + difference)/100;
                }
                //roundedAmount = Double.parseDouble(str.substring(0, str.length()-1));
                showReoundedCost();
                str = ((Double)netAmount).toString();
                if(str.indexOf('.') !=-1){
                    diff = str.length() - str.indexOf('.');
                    if(diff>3){
                        return Double.parseDouble(str.substring(0, str.length()-1));
                    }
                }
                return netAmount;
            }else{*/
                int difference = intAmount % totalWeight;
                if(difference != 0){
                    difference = totalWeight - difference;
                    roundedAmount = (amount * 100 + difference)/100;
                    showReoundedCost();
                    return (netAmount * 100 + difference)/100;
                }
           /* }
        }*/
        if(isRounded){
            //hide with animation
            layoutRounded.startAnimation(AnimationUtils.loadAnimation(ExpenseFillData.this, android.R.anim.fade_out));
            layoutRounded.setVisibility(View.GONE);
        }
        isRounded = false;
        return netAmount;

    }

    private void modifyPayment() {
        int totalWeigth = 0;
        double netAmount = amount;

        for(Payment pay:mValues){
            if(pay.isModified()){
                netAmount -= pay.getToPay();
            }
            else
                totalWeigth += pay.getWeight();
        }

        netAmount = checkDivsionAndGetNet(totalWeigth, netAmount);

        for(int i=0;i<mValues.size();i++){
            Payment currentPayment = mValues.get(i);
            if(netAmount>0 && !currentPayment.isModified())
                currentPayment.setToPay( (netAmount * currentPayment.getWeight())/totalWeigth);
            else if(netAmount <=0 && !currentPayment.isModified())
                currentPayment.setToPay(0f);
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
            if(value.charAt(0) == '.')
                value = "0"+value;
            amount = Double.parseDouble(value);
            enableWeight = true;
        }

        double netAmount = amount;

        for(Payment pay:mValues){
            if(pay.isModified()){
                netAmount -= pay.getToPay();
            }
            else
                totalWeigth += pay.getWeight();
        }

        netAmount = checkDivsionAndGetNet(totalWeigth, netAmount);

        for(int i=0;i<mValues.size();i++){
            Payment currentPayment = mValues.get(i);
            if(netAmount>0 && !currentPayment.isModified())
                currentPayment.setToPay( (netAmount * currentPayment.getWeight())/totalWeigth);
            else if(netAmount <=0 && !currentPayment.isModified())
                currentPayment.setToPay(0f);
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
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
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
                            if(!user.getFirebaseId().equals(MyApplication.getFirebaseId()))
                                users.add(user);
                            else
                                users.add(0, user);
                        }
                        Collections.sort(users.subList(1, users.size()), new Comparator<UserForGroup>() {
                            @Override
                            public int compare(UserForGroup u1, UserForGroup u2) {
                                return u1.getName().equals(u2.getName()) ? u1.getSurname().compareTo(u2.getSurname()) : u1.getName().compareTo(u2.getName());
                            }
                        });


                        List<Payment> payment = new ArrayList<>();
                        for(int i=0;i<users.size();i++){
                            payment.add(new Payment(users.get(i).getFirebaseId(), users.get(i).getPhoneNumber(), users.get(i).getName(), users.get(i).getSurname(), null, 0D, 0D));
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
            if(holder.mItem.getUserPhoneNumber().equals(MyApplication.getUserPhoneNumber()))
                holder.mIdView.setText(getString(R.string.you));
            else
                holder.mIdView.setText( holder.mItem.getUserFullName());
            onBind = true;
            holder.partition.setText( new DecimalFormat("#0.00").format( holder.mItem.getToPay()));
            onBind = false;
            holder.mNumber.setText( String.valueOf(holder.mItem.getWeight()) );
            holder.minus.setEnabled( holder.mItem.isWeightEnabled());
            holder.plus.setEnabled( holder.mItem.isWeightEnabled());
            holder.partition.setEnabled( holder.mItem.isWeightEnabled());
            holder.paymentSymbol.setText(currencySymbol);

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
            public final TextView paymentSymbol;

            public Payment mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.username);
                mNumber = (TextView) view.findViewById(R.id.integer_number);
                partition = (EditText) view.findViewById(R.id.personal_amount);
                plus = (Button) view.findViewById(R.id.increase);
                minus = (Button) view.findViewById(R.id.decrease);
                paymentSymbol = (TextView) view.findViewById(R.id.payment_currency_symbol);



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
                            String str = s.toString();
                            Double value;
                            if(str.equals(""))
                                value = 0d;
                            else{
                                str = CostUtil.replaceDecimalComma(s.toString());
                                if(str == null){
                                    partition.setError(getString(R.string.err_msg_correct_amount));
                                    requestFocus(partition);
                                    value = 0d;
                                }
                                else
                                    value = Double.parseDouble(str);
                            }
                            mItem.setToPay(value);
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


