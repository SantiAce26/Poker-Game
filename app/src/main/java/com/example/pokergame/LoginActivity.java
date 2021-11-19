package com.example.pokergame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText emailHolder, passwordHolder;
    Button loginBtnHolder;
    TextView createBtnHolder;
    ProgressBar progressBarHolder;
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailHolder = findViewById(R.id.userEmailLog);
        passwordHolder = findViewById(R.id.userPassword);
        progressBarHolder = findViewById(R.id.progressBar2);
        loginBtnHolder = findViewById(R.id.buttonLogin);
        createBtnHolder = findViewById(R.id.newRegister);

        fAuth = FirebaseAuth.getInstance();

        loginBtnHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailHolder.getText().toString().trim();
                String password = passwordHolder.getText().toString().trim();

                if(TextUtils.isEmpty(email)) {
                    emailHolder.setError("Email is required");
                    return;
                }

                if(TextUtils.isEmpty(password)) {
                    passwordHolder.setError("Password is required");
                    return;
                }

                if(password.length() < 6) {
                    passwordHolder.setError("Password must be greater than 6 characters");
                    return;
                }

                progressBarHolder.setVisibility(View.VISIBLE);

                //Authenticate the user
                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "User Logged in Sucessfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(LoginActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        createBtnHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });

    }
}