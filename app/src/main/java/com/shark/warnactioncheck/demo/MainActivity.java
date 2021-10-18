package com.shark.warnactioncheck.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.shark.checkwarnaction.AppCheck;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCheck.start(getBaseContext());
        setContentView(R.layout.activity_main);
    }
}