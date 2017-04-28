package com.polito.madinblack.expandedmad;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.polito.madinblack.expandedmad.groupManaging.GroupListActivity;

public class SignupActivity extends AppCompatActivity {
    private EditText inputPhoneNumber;
    private EditText inputPassword;
    private EditText inputPasswordConfirm;
    private Button buttonSignUp;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();

        buttonSignUp = (Button)findViewById(R.id.signup_button);
        inputPhoneNumber = (EditText)findViewById(R.id.phonenumber);
        inputPassword = (EditText) findViewById(R.id.password);
        inputPasswordConfirm =(EditText)findViewById(R.id.passwordConfirm);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);

        buttonSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                final String phoneNumber = inputPhoneNumber.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String passwordConfirm= inputPasswordConfirm.getText().toString().trim();
                //controllo di aver inserito qualcosa
                if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(getApplicationContext(), "Enter phone number", Toast.LENGTH_SHORT).show();
                    return;
                }
                //controllo di aver inserito un numero di telefono (solo numeri)
                if(!TextUtils.isDigitsOnly(phoneNumber)) {
                    Toast.makeText(getApplicationContext(), "Invalid phone number format", Toast.LENGTH_SHORT).show();
                    return;
                }
                //controllo che ci sia la password
                if(TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //controllo che la password sia lunga almeno 8
                if(password.length()<8) {
                    Toast.makeText(getApplicationContext(), "Password must be at least 8 characters long!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //controllo che le password inserite coincidano
                if(!TextUtils.equals(password,passwordConfirm)) {
                    Toast.makeText(getApplicationContext(), "Passwords don't match!", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                //per ora mi registro con una mail fittizia nel formato <numeroTelefono>@phonenumber.com, poi magari cambiamo
                //anche sulla password stesso discorso, per ora fissa, poi la setterei con il codice di conferma
                auth.createUserWithEmailAndPassword(phoneNumber.concat("@phonenumber.com"), password).addOnCompleteListener(SignupActivity.this,new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent=new Intent(SignupActivity.this, GroupListActivity.class);
                            intent.putExtra("phoneN", phoneNumber);
                            startActivity(intent);
                            finish();

                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
