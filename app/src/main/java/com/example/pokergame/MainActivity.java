package com.example.pokergame;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class MainActivity extends AppCompatActivity {

    TextView userNameHolder;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userNameHolder = findViewById(R.id.welcomeMessage);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = fAuth.getCurrentUser().getUid();

        //Welcome message with user's name
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                userNameHolder.setText("Welcome " + value.getString("username"));
            }
        });

    }

    public void createLobby(View view) {
        //Logs user out if they click on the logout button
        startActivity(new Intent(getApplicationContext(), CreateLobbyActivity.class));
        finish();
    }

    public void joinLobby(View view) {
        //Logs user out if they click on the logout button
        startActivity(new Intent(getApplicationContext(), LobbyListActivity.class));
        finish();
    }


    public void logout(View view) {
        //Logs user out if they click on the logout button
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    public void leaveApp(View view) {
        //Exits application
        finish();
        Log.d("Exit", "User exiting application");
        finishAffinity();
        System.exit(0);
    }

}