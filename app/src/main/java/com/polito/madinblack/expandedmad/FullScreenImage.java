package com.polito.madinblack.expandedmad;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.polito.madinblack.expandedmad.expenseDetail.ExpenseDetailActivity;

public class FullScreenImage extends AppCompatActivity{
    private ImageView imageView;
    private String imageFromUrl;
    private String expenseName;
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
        imageView = (ImageView)findViewById(R.id.full_screen);

        Glide.with(this).load(imageFromUrl).override(2048,2048).centerCrop().fitCenter().diskCacheStrategy(DiskCacheStrategy.RESULT).error(R.drawable.bill).into(imageView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fullscreen, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, ExpenseDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            navigateUpTo(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
