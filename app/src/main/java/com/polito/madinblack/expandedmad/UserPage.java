package com.polito.madinblack.expandedmad;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
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
import com.polito.madinblack.expandedmad.model.MyApplication;
import com.polito.madinblack.expandedmad.newGroup.NewGroup;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserPage extends AppCompatActivity{
    private StorageReference mUserStorage;
    private DatabaseReference mDatabaseForUrl;
    private CircleImageView userImage;
    private CircleImageView imageClick;
    private MyApplication ma;
    private TextView name;
    private TextView surname;
    private TextView phoneNumber;
    private String url;
    private Uri uri;
    private Bitmap bitmap;
    private byte[] imageData;

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

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            url = extras.getString("userImage");
        }

        ma = MyApplication.getInstance();
        mUserStorage = FirebaseStorage.getInstance().getReference().child("users").child(ma.getFirebaseId()).child("userProfilePicture.jpg");
        mDatabaseForUrl = FirebaseDatabase.getInstance().getReference().child("users").child(ma.getUserPhoneNumber()).child(ma.getFirebaseId()).child("urlImage");
        userImage = (CircleImageView)findViewById(R.id.user_picture);
        imageClick = (CircleImageView)findViewById(R.id.set_user_image);

        name = (TextView)findViewById(R.id.name);
        surname = (TextView)findViewById(R.id.surname);
        phoneNumber = (TextView)findViewById(R.id.phone_number);
        name.setText(ma.getUserName());
        surname.setText(ma.getUserSurname());
        phoneNumber.setText(ma.getUserPhoneNumber());

        mDatabaseForUrl.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                url = dataSnapshot.getValue(String.class);
                if(url != null) {
                    Glide.with(getApplicationContext()).load(url).into(userImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Glide.with(getApplicationContext()).load(url).into(userImage);
            }
        });

        /*mUserStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).diskCacheStrategy(DiskCacheStrategy.SOURCE).error(R.drawable.businessman).into(userImage);
            }
        });*/

        imageClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
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
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                    imageData = bytes.toByteArray();
                    userImage.setImageBitmap(bitmap);
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
                userImage.setImageBitmap(bitmap);
                uploadProfilePicture();  //da decidere se caricare sullo storage qua o dando conferma
            }
        }
    }

    @SuppressWarnings("VisibleForTests")
    public void uploadProfilePicture() {
        if(imageData != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(getString(R.string.uploading));
            progressDialog.show();

            final StorageReference filePathUsers = FirebaseStorage.getInstance().getReference().child("users").child(ma.getFirebaseId()).child("userProfilePicture.jpg");

            filePathUsers.putBytes(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //if the upload is successfull
                    //hiding the progress dialog
                    progressDialog.dismiss();

                    mDatabaseForUrl.setValue(taskSnapshot.getDownloadUrl().toString());

                    //StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("Group", groupCode).build(); //da cambiare, solo per prova
                    //filePathGroups.updateMetadata(metadata);
                    //and displaying a success toast
                    Toast.makeText(getApplicationContext(), getString(R.string.file_uploaded), Toast.LENGTH_LONG).show();
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

                            progressDialog.setMessage(getString(R.string.uploading) + ": " + ((int)progress) + "%");
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

}
