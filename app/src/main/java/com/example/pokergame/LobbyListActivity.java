package com.example.pokergame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import io.grpc.Server;

public class LobbyListActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    EditText roomNameHolder;
    Button joinLobbyButton, mainMenuLobbyButton;
    String userName;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby_list);

        roomNameHolder = findViewById(R.id.lobbyName);
        joinLobbyButton = findViewById(R.id.joinLobby);
        mainMenuLobbyButton = findViewById(R.id.mainMenuJoinLobby);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = fAuth.getCurrentUser().getUid();
        //Welcome message with user's name
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                userName = value.getString("username");
            }
        });

        joinLobbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomName = roomNameHolder.getText().toString().trim();
                if(TextUtils.isEmpty(roomName)) {
                    roomNameHolder.setError("Room Name is Required");
                    return;
                }

                Intent myIntent = new Intent(getApplicationContext(), ServerHandler.class);
                myIntent.putExtra("userName", userName);
                myIntent.putExtra("roomName", roomName);
                myIntent.putExtra("lobbyIntent", "join");
                startActivity(myIntent);
                finish();


            }
        });

        mainMenuLobbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(myIntent);
                finish();
            }
        });

    }
}