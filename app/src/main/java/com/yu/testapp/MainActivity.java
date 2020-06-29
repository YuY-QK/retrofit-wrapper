package com.yu.testapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by yu on 2020/6/25.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Retrofit
     *
     * @param view
     */
    public void retrofitClick(View view) {
        startActivity(new Intent(this, RetrofitActivity.class));
    }
}
