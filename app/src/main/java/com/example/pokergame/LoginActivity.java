package com.example.pokergame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText emailHolder, passwordHolder;
    Button loginBtnHolder;
    TextView createBtnHolder, forgotPassHolder;
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
        forgotPassHolder = findViewById(R.id.forgotPassword);

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
                            progressBarHolder.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        //Sends user to registration activity
        createBtnHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });

        //Reset password when user forgets password with popup 
        forgotPassHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetEmail = new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password");
                passwordResetDialog.setMessage("Enter email to receive reset password link.");
                passwordResetDialog.setView(resetEmail);

                //Checks if email that user sent is valid
                passwordResetDialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String currentEmail = resetEmail.getText().toString();

                        fAuth.sendPasswordResetEmail(currentEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(LoginActivity.this, "Reset Link sent your email.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, "Error! Link not sent " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                passwordResetDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing but close dialog
                    }
                });
            }
        });

    }
}