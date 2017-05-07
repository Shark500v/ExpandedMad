package com.polito.madinblack.expandedmad.notUsed;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.polito.madinblack.expandedmad.R;

public class UserInfoRegistration extends AppCompatActivity{

    String phoneId;
    EditText inputName, inputSurname;
    CollapsingToolbarLayout barLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inputName=(EditText)findViewById(R.id.name_field);
        inputSurname=(EditText)findViewById(R.id.surname_field);
        barLayout=(CollapsingToolbarLayout)findViewById(R.id.toolbar_layout);

        Bundle extras=getIntent().getExtras();
        if(extras!=null)
            phoneId=extras.getString("phoneN");

        barLayout.setTitle(phoneId);

    }
}
