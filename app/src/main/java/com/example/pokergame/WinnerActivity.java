package com.example.pokergame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class WinnerActivity extends AppCompatActivity {

    TextView userNameHolder, mainMenuHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winner);

        Intent myIntent = getIntent();
        String userName = myIntent.getStringExtra("userName");

        userNameHolder = findViewById(R.id.userNameText);
        mainMenuHolder = findViewById(R.id.goBackMainMenu);
        userNameHolder.setText(userName);

        mainMenuHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });


    }
}