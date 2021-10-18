package com.example.moretext;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.zr.MoreTextLayout;

public class MainActivity extends AppCompatActivity {
    MoreTextLayout mtl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mtl = findViewById(R.id.mtl);


    }
}
