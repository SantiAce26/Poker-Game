package com.example.pokergame;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ServerHandler extends AppCompatActivity{

    ImageView iv;
    int images[] = {R.drawable.chain_33_000, R.drawable.chain_73_001};
    private Socket mSocket;
    String uName;
    String rName;


    {
        try {
            mSocket = IO.socket("http://10.0.2.2:3000");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servertest);
        Intent myIntent = getIntent(); // gets the previously created intent
        String UserName = myIntent.getStringExtra("userName"); // will return "FirstKeyValue"
        String RoomName= myIntent.getStringExtra("roomName"); // will return "SecondKeyValue"
        uName = UserName;
        rName = RoomName;



        iv=(ImageView) findViewById(R.id.guy);

        iv.setImageResource(images[0]);
        mSocket.connect();
        //Register all the listener and callbacks here.
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on("update card", onUpdateCard);
    }

    public Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = new JSONObject();
                    try {
                        data.put("userName", uName);
                        data.put("roomName", rName);


                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    mSocket.emit("joinRoom", data.toString());
                }
            });
        }
    };

    public void doLeftImage(View view)
    {
        JSONObject data = new JSONObject();
        try {
            data.put("cardSelect", "Left");
            data.put("roomName", rName);


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mSocket.emit("updateCard", data.toString());

    }

    public void doRightImage(View view)
    {
        JSONObject data = new JSONObject();
        try {
            data.put("cardSelect", "Right");
            data.put("roomName", rName);


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mSocket.emit("updateCard", data.toString());
    }

    public void updateLeftImage()
    {

        iv.setImageResource(images[0]);

    }

    public void updateRightImage()
    {
        iv.setImageResource(images[1]);
    }

    public Emitter.Listener onUpdateCard = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

                    String cardSelect = args[0].toString();
                    mSocket.emit("testcard", args[0]);
                    if(cardSelect.compareTo("Left") == 0)
                    {
                        updateLeftImage();
                    }
                    else
                    {
                        updateRightImage();
                    }
        }
    };

    public void leaveLobby(View view)
    {
        JSONObject data = new JSONObject();
        try {
            data.put("userName", uName);
            data.put("roomName", rName);


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mSocket.emit("leaveRoom", data.toString());
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}
