package com.polito.madinblack.expandedmad;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.polito.madinblack.expandedmad.R;

public class ConfirmRegistration extends AppCompatActivity{
    Button buttonConfirmCode;
    EditText inputConfCode;
    ProgressBar progressBar;
    FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmation_code_layout);



    }
}
