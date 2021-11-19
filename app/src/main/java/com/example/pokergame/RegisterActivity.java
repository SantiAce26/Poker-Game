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

public class RegisterActivity extends AppCompatActivity {

    //initializing variables

    EditText userNameHolder, emailHolder, passwordHolder, passwordConfirmHolder;
    Button registerBtnHolder;
    TextView loginBtnHolder;
    FirebaseAuth fAuth;
    ProgressBar progressBarHolder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Sets each specified text box or button to a variable

        userNameHolder = findViewById(R.id.userName);
        emailHolder = findViewById(R.id.userEmail);
        passwordHolder = findViewById(R.id.registerPassword);
        passwordConfirmHolder = findViewById(R.id.registerPasswordConfirm);
        registerBtnHolder = findViewById(R.id.buttonRegister);
        loginBtnHolder = findViewById(R.id.alreadyRegistered);

        //Starts up firebase database authenticator

        fAuth = FirebaseAuth.getInstance();
        progressBarHolder = findViewById(R.id.progressBar);
        progressBarHolder.setVisibility(View.INVISIBLE);

        //If user already has registered, skips registration
        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        //When user clicks on register, checks for each text box to see if it's empty, if not
        //registers user to database

        registerBtnHolder.setOnClickListener(new View.OnClickListener() {

           @Override
           public void  onClick (View v) {
               String email = emailHolder.getText().toString().trim();
               String password = passwordHolder.getText().toString().trim();
               String confirmPass = passwordConfirmHolder.getText().toString().trim();
               String userName = userNameHolder.getText().toString().trim();

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

               if(TextUtils.isEmpty(userName)) {
                   userNameHolder.setError("User name is required");
                   return;
               }

               if(!TextUtils.isEmpty(confirmPass) && !TextUtils.isEmpty(password)) {
                   if(!confirmPass.equals(password)) {
                       passwordConfirmHolder.setError("Passwords must match");
                       return;
                   }
               }

               if(TextUtils.isEmpty(confirmPass)) {
                   passwordConfirmHolder.setError("Please confirm your password");
                   return;
               }

               progressBarHolder.setVisibility(View.VISIBLE);

               //Registering user to database

               fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "User Registered", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));

                        } else {
                            Toast.makeText(RegisterActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBarHolder.setVisibility(View.GONE);
                        }
                   }
               });
           }
        });


        //If user wants to log in instead of register

        loginBtnHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
    }


}