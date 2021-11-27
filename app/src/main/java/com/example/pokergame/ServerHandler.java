package com.example.pokergame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ImageView;
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
    Button shuffleButtonHolder, refreshBtnHolder, startBtnHolder, leaveBtnHolder;
    EditText storeStringHolder;
    TextView textHolder, turnTextHolder;


    {
        try {
            mSocket = IO.socket("http://poker-server-thing.glitch.me");
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
        shuffleButtonHolder = findViewById(R.id.shufflebutton);
        refreshBtnHolder = findViewById(R.id.refreshButton);
        startBtnHolder = findViewById(R.id.startButton);
        storeStringHolder = findViewById(R.id.storeString);
        textHolder = (TextView) findViewById(R.id.whatisit);
        turnTextHolder = (TextView) findViewById(R.id.turnText);
        leaveBtnHolder = findViewById(R.id.leaveButton);



        iv=(ImageView) findViewById(R.id.guy);

        iv.setImageResource(images[0]);
        textHolder.setText("nothing here");
        mSocket.connect();
        //Register all the listener and callbacks here.
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on("update card", onUpdateCard);
        mSocket.on("refresh string", refreshString);
        mSocket.on("your turn", onYourTurn);
        mSocket.on("not your turn", notYourTurn);
        mSocket.on("hostCheck", returnHostCheck);
        mSocket.on("setNotHost", returnNotHost);
        mSocket.on("hostDisconnected", hostDisconnect);

        shuffleButtonHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = storeStringHolder.getText().toString().trim();


                if(TextUtils.isEmpty(email)) {
                    storeStringHolder.setError("Email is required");
                    return;
                }

                JSONObject data = new JSONObject();
                try {
                    data.put("storeString", email);
                    data.put("roomName", rName);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                mSocket.emit("store this", data.toString());
            }
        });

        refreshBtnHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocket.emit("grab string", rName);
            }

        });

        startBtnHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocket.emit("start game", rName);
            }

        });
        leaveBtnHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveLobby(v);
            }

        });

    }

    public Emitter.Listener returnHostCheck = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSocket.emit("hostCheck", rName);
                }
            });
        }
    };

        public Emitter.Listener returnNotHost = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSocket.emit("setNotHost");
                    }
                });
            }
    };

    public Emitter.Listener hostDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    leaveBtnHolder.callOnClick();
                }
            });
        }
    };
    public Emitter.Listener refreshString = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String newDisplayString = args[0].toString();
                    textHolder.setText(newDisplayString);
                }
            });
        }
    };

    public Emitter.Listener onYourTurn = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    turnTextHolder.setText("your turn");
                }
            });
        }
    };

    public Emitter.Listener notYourTurn = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    turnTextHolder.setText("not your turn");
                }
            });
        }
    };


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
        mSocket.disconnect();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}
