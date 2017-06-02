package com.polito.madinblack.expandedmad;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.polito.madinblack.expandedmad.groupManaging.GroupListActivity;
import com.polito.madinblack.expandedmad.model.MyApplication;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


import de.hdodenhof.circleimageview.CircleImageView;

public class UserPage extends AppCompatActivity{
    private StorageReference mUserStorage;
    private DatabaseReference mDatabaseForUrl;
    private CircleImageView userImage;
    private CircleImageView imageClick;
    private ImageView fullscreen;
    private TextView name;
    private TextView surname;
    private TextView phoneNumber;
    private String url;
    private Uri uri;
    private Bitmap bitmap;
    private byte[] imageData;
    private boolean visible = false;
    private ValueEventListener valueEventListener;
    private static int THUMBNAIL_SIZE = 256;

    //a constant to track the file chooser intent
    private static int RESULT_LOAD_IMAGE = 1;
    private static int RESULT_REQUEST_CAMERA = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        mUserStorage = FirebaseStorage.getInstance().getReference().child("users").child(MyApplication.getFirebaseId()).child("userProfilePicture.jpg");
        mDatabaseForUrl = FirebaseDatabase.getInstance().getReference().child("users").child(MyApplication.getUserPhoneNumber()).child(MyApplication.getFirebaseId()).child("urlImage");
        userImage = (CircleImageView)findViewById(R.id.user_picture);
        imageClick = (CircleImageView)findViewById(R.id.set_user_image);
        fullscreen = (ImageView)findViewById(R.id.user_image_fullscreen);

        name = (TextView)findViewById(R.id.name);
        surname = (TextView)findViewById(R.id.surname);
        phoneNumber = (TextView)findViewById(R.id.phone_number);
        name.setText(MyApplication.getUserName());
        surname.setText(MyApplication.getUserSurname());
        phoneNumber.setText(MyApplication.getUserPhoneNumber());

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                url = dataSnapshot.getValue(String.class);
                Glide.with(getApplicationContext()).load(url).override(128,128).centerCrop().fitCenter().diskCacheStrategy(DiskCacheStrategy.RESULT).error(R.drawable.businessman).into(userImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        /*mUserStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).diskCacheStrategy(DiskCacheStrategy.SOURCE).error(R.drawable.businessman).into(userImage);
            }
        });*/

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!visible) {
                    Glide.with(getApplicationContext()).load(url).override(2048,2048).centerCrop().fitCenter().diskCacheStrategy(DiskCacheStrategy.RESULT).error(R.drawable.businessman).into(fullscreen);
                    fullscreen.setVisibility(View.VISIBLE);
                    visible = true;
                }
            }
        });

        fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(visible){
                    fullscreen.setVisibility(View.GONE);
                    visible = false;
                }
            }
        });

        imageClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });


    }


    @Override
    public void onStart(){
        super.onStart();
        if(mDatabaseForUrl!=null && valueEventListener!=null)
            mDatabaseForUrl.addValueEventListener(valueEventListener);


    }

    @Override
    public void onStop(){
        super.onStop();
        if(mDatabaseForUrl!=null && valueEventListener!=null)
            mDatabaseForUrl.removeEventListener(valueEventListener);


    }


    private void selectImage() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(UserPage.this);
        dialog.setTitle(getString(R.string.add_photo));

        final CharSequence[] items = { getString(R.string.photo), getString(R.string.gallery), getString(R.string.cancel)};

        dialog.setItems(items, new android.content.DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                if(items[which].equals(getString(R.string.photo))) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, RESULT_REQUEST_CAMERA);
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
        if(resultCode == RESULT_OK && data != null) {
            if (requestCode == RESULT_LOAD_IMAGE) {
                uri = data.getData();
                try {
                    bitmap = getThumbnail(uri);
                    //bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                    imageData = bytes.toByteArray();
                    uploadProfilePicture(); //da decidere se caricare sullo storage qua o dando conferma
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (requestCode == RESULT_REQUEST_CAMERA) {
                bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                imageData = bytes.toByteArray();
                uploadProfilePicture();  //da decidere se caricare sullo storage qua o dando conferma
            }
        }
    }

    @SuppressWarnings("VisibleForTests")
    public void uploadProfilePicture() {
        if(imageData != null) {
            //final ProgressDialog progressDialog = new ProgressDialog(this);
            //progressDialog.setTitle(getString(R.string.uploading));
            //progressDialog.show();

            StorageReference filePathUsers = FirebaseStorage.getInstance().getReference().child("users").child(MyApplication.getFirebaseId()).child("userProfilePicture.jpg");

            filePathUsers.putBytes(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //if the upload is successfull
                    //hiding the progress dialog
                    //progressDialog.dismiss();

                    url = taskSnapshot.getDownloadUrl().toString();
                    mDatabaseForUrl.setValue(url);

                    Glide.with(getApplicationContext()).load(url).override(128,128).centerCrop().fitCenter().diskCacheStrategy(DiskCacheStrategy.RESULT).into(userImage);
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
    }

    @Override
    public void onBackPressed() {
        if(!visible){
            navigateUpTo(new Intent(UserPage.this, GroupListActivity.class));
        }else{
            fullscreen.setVisibility(View.GONE);
            visible = false;
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
}
