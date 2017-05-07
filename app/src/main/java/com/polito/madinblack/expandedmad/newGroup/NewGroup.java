package com.polito.madinblack.expandedmad.newGroup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.polito.madinblack.expandedmad.groupManaging.GroupListActivity;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.model.Group;
import com.polito.madinblack.expandedmad.model.MyApplication;
import com.polito.madinblack.expandedmad.model.UserForGroup;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewGroup extends AppCompatActivity {


    private DatabaseReference mDatabaseReferenceRoot;
    private StorageReference mStorage;
    private MyApplication ma;
    String phoneId;
    List<SelectUser> groupM;
    List<SelectUser> invite;
    ListView lv;
    GroupMembersAdapter adapter;
    String groupCode;
    CircleImageView groupImage;
    CircleImageView btn_group_image;
    ImageView fullScreen;
    Bitmap bitmap = null;
    Uri uri;
    byte[] imageData;
    boolean visible = false; //boolean per visualizzazione a schermo intero

    //a constant to track the file chooser intent
    private static int RESULT_LOAD_IMAGE = 1;
    private static int RESULT_REQUEST_CAMERA = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mStorage = FirebaseStorage.getInstance().getReference();

        groupM = (List<SelectUser>) getIntent().getSerializableExtra("Group Members");
        invite = (List<SelectUser>) getIntent().getSerializableExtra("invite");
        mDatabaseReferenceRoot = FirebaseDatabase.getInstance().getReference();

        groupImage = (CircleImageView)findViewById(R.id.group_icon);
        btn_group_image = (CircleImageView)findViewById(R.id.btn_group_image);
        fullScreen = (ImageView)findViewById(R.id.full_screen);

        //controllo per rotazione dello schermo che richiama la onCreate
        //serve per non far sparire l'immagine caricata nella ImageView quando ruoto il cell
        if(savedInstanceState == null){
            groupImage.setImageResource(R.drawable.teamwork);
        }else {
            bitmap = savedInstanceState.getParcelable("bitmap");
            groupImage.setImageBitmap(bitmap);
            if (savedInstanceState.getBoolean("visible")) {
                fullScreen.setImageBitmap(bitmap);
                fullScreen.setVisibility(View.VISIBLE);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                visible = true;
            }
        }

        //visualizzo la foto a schermo intero
        groupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bitmap != null) {
                    fullScreen.setImageBitmap(bitmap);
                    fullScreen.setVisibility(View.VISIBLE);
                    visible = true;
                }
            }
        });

        //esco dalla visualizzazione a schermo intero
        fullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(visible == true){
                    fullScreen.setVisibility(View.GONE);
                    visible = false;
                }
            }
        });

        btn_group_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        lv = (ListView) findViewById(R.id.list1);
        adapter = new GroupMembersAdapter(groupM, this);

        // Show the Up button in the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        lv.setAdapter(adapter);
    }

    //creo dialog per decidere da dove prendere la foto da caricare
    private void selectImage() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(NewGroup.this);
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
                    groupImage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (requestCode == RESULT_REQUEST_CAMERA) {
                bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                imageData = bytes.toByteArray();
                groupImage.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.confirm_group) {

            Intent intent;

            EditText inputGroupName = (EditText) findViewById(R.id.group_name);
            String groupName = inputGroupName.getText().toString();
            if (groupName.isEmpty()) {
                /*
                intent = new Intent(this, NewGroup.class);
                startActivity(intent);
                */
                View mv = findViewById(R.id.main_content);
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                v.vibrate(250);
                Snackbar.make(mv, R.string.invalid_group_name, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                return true;
            }



            List<UserForGroup> userForGroupList = new ArrayList<>();

            for(SelectUser selectUser : groupM){

                String [] items = selectUser.getName().split(" ");
                if(items[0]==null)
                    items[0]=" ";
                if(items[1]==null)
                    items[1]=" ";


                UserForGroup userForGroup = new UserForGroup(selectUser.getPhone(), selectUser.getFirebaseId(), items[0], items[1]);
                for(int i=0; i<userForGroupList.size(); i++){
                    userForGroupList.get(i).connect(userForGroup);
                    userForGroup.connect(userForGroupList.get(i));

                }
                userForGroupList.add(userForGroup);

            }

            UserForGroup userForGroup = new UserForGroup(ma.getUserPhoneNumber(), ma.getFirebaseId(), ma.getUserName(), ma.getUserSurname());
            for(int i=0; i<userForGroupList.size(); i++){
                userForGroupList.get(i).connect(userForGroup);
                userForGroup.connect(userForGroupList.get(i));

            }
            userForGroupList.add(userForGroup);


            groupCode = Group.writeNewGroup(mDatabaseReferenceRoot, groupName, userForGroupList);

            if(groupCode != null) {
                uploadGroupPhoto();
            }

            if(invite==null || invite.isEmpty()){
                Intent intent1=new Intent(NewGroup.this, GroupListActivity.class); //da cambiare (dovra' andare alla pagina del gruppo creato)
                startActivity(intent1);
            }else{
                if(invite.isEmpty()){
                    Intent intent1=new Intent(NewGroup.this, GroupListActivity.class); //da cambiare (dovra' andare alla pagina del gruppo creato)
                    startActivity(intent1);
                }else {
                    //devo invitare i nuovi membri del gruppo
                    Intent intent2 = new Intent(NewGroup.this, InviteActivity.class);
                    intent2.putExtra("InviteList", (Serializable) invite);
                    intent2.putExtra("Code", groupCode);
                    startActivity(intent2);
                }
            }

            return true;

        }else if (id == android.R.id.home){
            Intent intent = new Intent(this, SelectContact.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            navigateUpTo(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_new_group, menu);
        return true;
    }

    @SuppressWarnings("VisibleForTests")
    public void uploadGroupPhoto() {
        if(imageData != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(getString(R.string.uploading));
            progressDialog.show();

            final StorageReference filePathGroups = mStorage.child("Groups").child(groupCode).child("GroupPicture").child("groupPicture.jpg");

            filePathGroups.putBytes(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //if the upload is successfull
                    //hiding the progress dialog
                    progressDialog.dismiss();

                    StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("Group", groupCode).build(); //da cambiare, solo per prova
                    filePathGroups.updateMetadata(metadata);
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


    //salvo l'immagine per poterla visualizzare dopo la rotazione del cell
    @Override
    public void onSaveInstanceState(Bundle toSave) {
        super.onSaveInstanceState(toSave);
        toSave.putParcelable("bitmap", bitmap);
        toSave.putBoolean("visible", visible);
    }

    //metodo per fare il download dallo storage delle immagini dei gruppi
    //nella NewGroup non serve, da togliere
    public void downlaoadGroupImage(){
        StorageReference groupRef = mStorage.child("Groups").child(groupCode).child("GroupPicture").child("groupPicture.jpg");
        try {
            final File localFile = File.createTempFile("image", "tmp");
            groupRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    bitmap = BitmapFactory.decodeFile(localFile.getPath());
                    groupImage.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}