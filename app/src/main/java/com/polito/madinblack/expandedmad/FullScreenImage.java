package com.polito.madinblack.expandedmad;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import com.polito.madinblack.expandedmad.expenseDetail.ExpenseDetailActivity;
import com.polito.madinblack.expandedmad.model.Expense;
import com.polito.madinblack.expandedmad.model.MyApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FullScreenImage extends AppCompatActivity{
    private ImageView imageView;
    private String imageFromUrl;
    private String expenseName;
    private String expenseId;
    private DatabaseReference mDatatabaseExpenseReference;
    private DatabaseReference mDatabaseForUrl;
    private String permission;
    private CoordinatorLayout coordinatorLayout;
    private static int THUMBNAIL_SIZE = 256;
    private String pictureImagePath;
    private Uri uri;
    private Bitmap bitmap;
    private byte[] imageData;
    private String groupID;
    private String url;

    //a constant to track the file chooser intent
    private static int RESULT_LOAD_IMAGE = 1;
    private static int RESULT_REQUEST_CAMERA = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_fullscreen);
        toolbar.setTitle(expenseName);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        imageFromUrl = getIntent().getStringExtra("imageUrl");
        expenseName = getIntent().getStringExtra("expenseName");
        expenseId = getIntent().getStringExtra("expenseId");
        imageView = (ImageView)findViewById(R.id.full_screen);

        //coordinatorLayout = (CoordinatorLayout) findViewById(R.id.snackbarPos);
        mDatabaseForUrl = FirebaseDatabase.getInstance().getReference().child("expenses").child(expenseId).child("urlImage");

        Glide.with(this).load(imageFromUrl).override(2048,2048).centerCrop().fitCenter().diskCacheStrategy(DiskCacheStrategy.RESULT).error(R.drawable.bill).into(imageView);

        mDatatabaseExpenseReference = FirebaseDatabase.getInstance().getReference().child("expenses").child(expenseId);
        mDatatabaseExpenseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Expense expense = dataSnapshot.getValue(Expense.class);
                groupID = expense.getGroupId();
                if (expense.getPaidByFirebaseId().equals(MyApplication.getFirebaseId())) {
                    if (!dataSnapshot.child("urlImage").exists()) {
                        permission = expenseId;
                    } else {
                        permission = getString(R.string.expense_image_already_set);
                    }
                } else {
                    permission = getString(R.string.expense_not_added_by_me);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fullscreen, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, ExpenseDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                navigateUpTo(intent);
                return true;

            case R.id.add_expense_image:
                if(permission.equals(expenseId)){
                    selectImage();
                }
                else if(permission.equals(getString(R.string.expense_image_already_set))){
                    Toast.makeText(this, getString(R.string.expense_image_already_set), Toast.LENGTH_LONG).show();
                }else if(permission.equals(getString(R.string.expense_not_added_by_me))){
                    Toast.makeText(this, getString(R.string.expense_not_added_by_me), Toast.LENGTH_LONG).show();
                }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void selectImage() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(FullScreenImage.this);
        dialog.setTitle(getString(R.string.add_proof));

        final CharSequence[] items = { getString(R.string.photo), getString(R.string.gallery), getString(R.string.cancel)};

        dialog.setItems(items, new android.content.DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                if(items[which].equals(getString(R.string.photo))) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String imageFileName = timeStamp + ".jpg";
                    File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    if(storageDir != null) {
                        pictureImagePath = storageDir.getAbsolutePath() + "/" + imageFileName;
                        File file = new File(pictureImagePath);
                        uri = FileProvider.getUriForFile(FullScreenImage.this, BuildConfig.APPLICATION_ID + ".provider", file);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(intent, RESULT_REQUEST_CAMERA);
                        }
                    }
                } else if(items[which].equals(getString(R.string.gallery))){
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);
                } else if(items[which].equals(getString(R.string.cancel))){
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if (requestCode == RESULT_LOAD_IMAGE && data != null) {
                uri = data.getData();
                try {
                    bitmap = getThumbnail(uri);
                    //bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                    imageData = bytes.toByteArray();
                    uploadExpensePicture();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (requestCode == RESULT_REQUEST_CAMERA) {
                File imgFile = new File(pictureImagePath);
                if (imgFile.exists()) {
                    try {
                        bitmap = getThumbnail(FileProvider.getUriForFile(FullScreenImage.this, BuildConfig.APPLICATION_ID + ".provider", imgFile));
                        //bitmap = (Bitmap) data.getExtras().get("data");
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                        imageData = bytes.toByteArray();
                        uploadExpensePicture();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                /*File imgFile = new  File(pictureImagePath);
                if(imgFile.exists()) {
                    try {
                        bitmap = (Bitmap) data.getExtras().get("data");
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                        imageData = bytes.toByteArray();
                        uploadProfilePicture();  //da decidere se caricare sullo storage qua o dando conferma
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }*/
            }
        }
    }

    @SuppressWarnings("VisibleForTests")
    public void uploadExpensePicture() {
        if(imageData != null) {
            //final ProgressDialog progressDialog = new ProgressDialog(this);
            //progressDialog.setTitle(getString(R.string.uploading));
            //progressDialog.show();

            StorageReference filePathUsers = FirebaseStorage.getInstance().getReference().child("groups").child(groupID).child("expenses").child(expenseId + ".jpg");

            filePathUsers.putBytes(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //if the upload is successfull
                    //hiding the progress dialog
                    //progressDialog.dismiss();

                    url = taskSnapshot.getDownloadUrl().toString();
                    mDatabaseForUrl.setValue(url);

                    //Glide.with(getApplicationContext()).load(url).override(128,128).centerCrop().fitCenter().diskCacheStrategy(DiskCacheStrategy.RESULT).into(imageView);
                    //ma.putImageurl(ma.getFirebaseId(), url);

                    //StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("Group", groupCode).build(); //da cambiare, solo per prova
                    //filePathGroups.updateMetadata(metadata);
                    //and displaying a success toast
                    //Toast.makeText(getApplicationContext(), getString(R.string.file_uploaded), Toast.LENGTH_LONG).show();
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

                            //progressDialog.setMessage(getString(R.string.uploading) + ": " + ((int)progress) + "%");
                        }
                    })
                    .addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                            //System.out.println(getString(R.string.upload_pause));
                        }
                    });
        }
        Intent intent1 = new Intent(FullScreenImage.this, ExpenseDetailActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        navigateUpTo(intent1);
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
}
