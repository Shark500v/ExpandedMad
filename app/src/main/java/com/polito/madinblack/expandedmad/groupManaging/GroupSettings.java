package com.polito.madinblack.expandedmad.groupManaging;

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
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
import com.polito.madinblack.expandedmad.BuildConfig;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.UserPage;
import com.polito.madinblack.expandedmad.addUserToGroup.SelectContactToAdd;
import com.polito.madinblack.expandedmad.model.MyApplication;
import com.polito.madinblack.expandedmad.tabViewGroup.TabView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupSettings extends AppCompatActivity {
    private CircleImageView groupImage;
    private CircleImageView editGroupImage;
    private ImageView fullscreen;
    private StorageReference mStorageGroup;
    private DatabaseReference mDatabaseForUrl;
    private TextView editGroupName;
    private String groupName;
    private String groupId;
    private String url;
    private Uri uri;
    private Bitmap bitmap;
    private byte[] imageData;
    private boolean visible = false;
    private ValueEventListener valueEventListener;
    private ValueEventListener valueEventListenerName;
    private TextView createdBy;
    private DatabaseReference mCreatedByName;
    private DatabaseReference mCreatedByPhoneNumber;
    private String pictureImagePath;

    private static int THUMBNAIL_SIZE = 512;

    //a constant to track the file chooser intent
    private static int RESULT_LOAD_IMAGE = 1;
    private static int RESULT_REQUEST_CAMERA = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.group_info));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            groupName = extras.getString("groupName");
            groupId = extras.getString("groupIndex");
        }



        mDatabaseForUrl = FirebaseDatabase.getInstance().getReference().child("groups").child(groupId).child("urlImage");
        mStorageGroup = FirebaseStorage.getInstance().getReference().child("groups").child(groupId).child("groupPicture").child("groupPicture.jpg");
        mCreatedByPhoneNumber = FirebaseDatabase.getInstance().getReference().child("groups").child(groupId).child("createdByPhoneNumber");
        mCreatedByName = FirebaseDatabase.getInstance().getReference().child("groups").child(groupId).child("createdByFullName");

        groupImage = (CircleImageView) findViewById(R.id.group_picture);
        editGroupImage = (CircleImageView) findViewById(R.id.set_group_image);
        fullscreen = (ImageView)findViewById(R.id.group_image_fullscreen);
        editGroupName = (TextView) findViewById(R.id.edit_group_name);
        editGroupName.setText(String.format("%s: %s", getString(R.string.group_name), groupName));
        createdBy = (TextView) findViewById(R.id.created_by);


        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                url = dataSnapshot.getValue(String.class);
                Glide.with(getApplicationContext()).load(url).override(128,128).centerCrop().fitCenter().diskCacheStrategy(DiskCacheStrategy.RESULT).error(R.drawable.teamwork).into(groupImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        valueEventListenerName = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String userPhoneNumber = dataSnapshot.getValue(String.class);
                    if(userPhoneNumber.equals(MyApplication.getUserPhoneNumber()))
                        createdBy.setText(String.format("%s: %s", getString(R.string.created_by), getString(R.string.you)));
                    else{
                        mCreatedByName.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists())
                                    createdBy.setText(String.format("%s:\t%s", getString(R.string.created_by), dataSnapshot.getValue(String.class)));

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };



        /*mStorageGroup.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(groupImage);
            }
        });*/

        editGroupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupSettings.this, EditGroupName.class);
                intent.putExtra("groupName", groupName);
                intent.putExtra("groupIndex", groupId);
                startActivity(intent);
            }
        });

        editGroupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        groupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!visible) {
                    Glide.with(getApplicationContext()).load(url).override(2048,2048).centerCrop().fitCenter().diskCacheStrategy(DiskCacheStrategy.RESULT).error(R.drawable.teamwork).into(fullscreen);
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
    }

    @Override
    public void onStart(){
        super.onStart();
        if(mDatabaseForUrl!=null && valueEventListener!=null)
            mDatabaseForUrl.addValueEventListener(valueEventListener);

        if(mCreatedByPhoneNumber!=null && valueEventListenerName!=null)
            mCreatedByPhoneNumber.addListenerForSingleValueEvent(valueEventListenerName);


    }

    @Override
    public void onStop(){
        super.onStop();
        if(mDatabaseForUrl!=null && valueEventListener!=null)
            mDatabaseForUrl.removeEventListener(valueEventListener);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_member, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, TabView.class);
            intent.putExtra("groupName", groupName);
            intent.putExtra("groupIndex", groupId);
            navigateUpTo(intent);
            return true;
        }else if(id == R.id.add_members){
            Intent intent4 = new Intent(this, SelectContactToAdd.class);
            intent4.putExtra("groupIndex", groupId);
            intent4.putExtra("groupName", groupName);
            startActivity(intent4);
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectImage() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(GroupSettings.this);
        dialog.setTitle(getString(R.string.add_photo));

        final CharSequence[] items = { getString(R.string.photo), getString(R.string.gallery), getString(R.string.cancel)};

        dialog.setItems(items, new android.content.DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                if(items[which].equals(getString(R.string.photo))) {
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
                        uri = FileProvider.getUriForFile(GroupSettings.this, BuildConfig.APPLICATION_ID + ".provider", file);
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
                    uploadGroupPicture(); //da decidere se caricare sullo storage qua o dando conferma
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (requestCode == RESULT_REQUEST_CAMERA) {
                /*bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                imageData = bytes.toByteArray();
                uploadGroupPicture();  //da decidere se caricare sullo storage qua o dando conferma*/
                File imgFile = new File(pictureImagePath);
                if (imgFile.exists()) {
                    try {
                        bitmap = getThumbnail(FileProvider.getUriForFile(GroupSettings.this, BuildConfig.APPLICATION_ID + ".provider", imgFile));
                        //bitmap = (Bitmap) data.getExtras().get("data");
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                        imageData = bytes.toByteArray();
                        uploadGroupPicture();  //da decidere se caricare sullo storage qua o dando conferma
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @SuppressWarnings("VisibleForTests")
    public void uploadGroupPicture() {
        if(imageData != null) {
            //final ProgressDialog progressDialog = new ProgressDialog(this);
            //progressDialog.setTitle(getString(R.string.uploading));
            //progressDialog.show();

            final StorageReference filePathGroups = FirebaseStorage.getInstance().getReference().child("groups").child(groupId).child("groupPicture").child("groupPicture.jpg");

            filePathGroups.putBytes(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //if the upload is successfull
                    //hiding the progress dialog
                    //progressDialog.dismiss();

                    url = taskSnapshot.getDownloadUrl().toString();
                    mDatabaseForUrl = FirebaseDatabase.getInstance().getReference().child("groups").child(groupId).child("urlImage");
                    mDatabaseForUrl.setValue(url);

                    Glide.with(getApplicationContext()).load(url).override(128,128).centerCrop().fitCenter().diskCacheStrategy(DiskCacheStrategy.RESULT).into(groupImage);


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

    @Override
    public void onBackPressed() {
        if(!visible){
            super.onBackPressed();
        }else{
            fullscreen.setVisibility(View.GONE);
            visible = false;
        }
    }
}

