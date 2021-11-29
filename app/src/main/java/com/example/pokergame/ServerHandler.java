package com.example.pokergame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ServerHandler extends AppCompatActivity {

    ImageView playCard1, playCard2, dealCard1, dealCard2, dealCard3, dealCard4, dealCard5;
    ImageView play1Card1, play1Card2, play2Card1, play2Card2, play3Card1, play3Card2, play4Card1, play4Card2;
    int images[] = {R.drawable.club_1, R.drawable.club_2, R.drawable.club_3, R.drawable.club_4, R.drawable.club_5, R.drawable.club_6, R.drawable.club_7, R.drawable.club_8, R.drawable.club_9, R.drawable.club_10, R.drawable.club_11, R.drawable.club_12, R.drawable.club_13,
            R.drawable.diamond_1, R.drawable.diamond_2, R.drawable.diamond_3, R.drawable.diamond_4, R.drawable.diamond_5, R.drawable.diamond_6, R.drawable.diamond_7, R.drawable.diamond_8, R.drawable.diamond_9, R.drawable.diamond_10, R.drawable.diamond_11, R.drawable.diamond_12, R.drawable.diamond_13,
            R.drawable.spade_1, R.drawable.spade_2, R.drawable.spade_3, R.drawable.spade_4, R.drawable.spade_5, R.drawable.spade_6, R.drawable.spade_7, R.drawable.spade_8, R.drawable.spade_9, R.drawable.spade_10, R.drawable.spade_11, R.drawable.spade_12, R.drawable.spade_13,
            R.drawable.heart_1, R.drawable.heart_2, R.drawable.heart_3, R.drawable.heart_4, R.drawable.heart_5, R.drawable.heart_6, R.drawable.heart_7, R.drawable.heart_8, R.drawable.heart_9, R.drawable.heart_10, R.drawable.heart_11, R.drawable.heart_12, R.drawable.heart_13,
            R.drawable.card_back};
    private Socket mSocket;
    String uName;
    String rName;
    int nextPlayerSpace = 1;
    String lobbyIntent;
    Button betBtnHolder, foldBtnHolder, callBtnHolder, startBtnHolder, leaveBtnHolder;
    EditText playerBetHolder;
    TextView play1UserText, play2UserText, play3UserText, play4UserText;
    TextView play1ChipText, play2ChipText, play3ChipText, play4ChipText;
    TextView play1StatusText, play2StatusText, play3StatusText, play4StatusText;
    TextView potText, turnTextHolder, playerChipText, currentPotText;


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
        setContentView(R.layout.activity_poker);
        Intent myIntent = getIntent(); // gets the previously created intent
        String UserName = myIntent.getStringExtra("userName"); // will return "FirstKeyValue"
        String RoomName = myIntent.getStringExtra("roomName"); // will return "SecondKeyValue"
        String LIntent = myIntent.getStringExtra("lobbyIntent"); // will return "SecondKeyValue"

        uName = UserName;
        rName = RoomName;
        lobbyIntent = LIntent;
        betBtnHolder = findViewById(R.id.betButton);
        foldBtnHolder = findViewById(R.id.foldButton);
        callBtnHolder = findViewById(R.id.callButton);
        startBtnHolder = findViewById(R.id.StartButton);
        leaveBtnHolder = findViewById(R.id.LeaveButton);
        playerBetHolder = findViewById(R.id.playerBetNumber);
        play1UserText = (TextView) findViewById(R.id.user1View1);
        play1ChipText = (TextView) findViewById(R.id.user1View2);
        play1StatusText = (TextView) findViewById(R.id.user1View3);
        play2UserText = (TextView) findViewById(R.id.user2View1);
        play2ChipText = (TextView) findViewById(R.id.user2View2);
        play2StatusText = (TextView) findViewById(R.id.user2View3);
        play3UserText = (TextView) findViewById(R.id.user3View1);
        play3ChipText = (TextView) findViewById(R.id.user3View2);
        play3StatusText = (TextView) findViewById(R.id.user3View3);
        play4UserText = (TextView) findViewById(R.id.user4View1);
        play4ChipText = (TextView) findViewById(R.id.user4View2);
        play4StatusText = (TextView) findViewById(R.id.user4View3);
        potText = (TextView) findViewById(R.id.currentPotValue);
        turnTextHolder = (TextView) findViewById(R.id.turnInfoText);
        playerChipText = (TextView) findViewById(R.id.playerMoney);
        currentPotText = (TextView) findViewById(R.id.currentPotValue);

        play1UserText.setText("none");
        play1ChipText.setText("0");
        play1StatusText.setText("empty");
        play2UserText.setText("none");
        play2ChipText.setText("0");
        play2StatusText.setText("empty");
        play3UserText.setText("none");
        play3ChipText.setText("0");
        play3StatusText.setText("empty");
        play4UserText.setText("none");
        play4ChipText.setText("0");
        play4StatusText.setText("empty");


        playCard1 = (ImageView) findViewById(R.id.playerCard1);
        playCard2 = (ImageView) findViewById(R.id.playerCard2);
        dealCard1 = (ImageView) findViewById(R.id.dealerCard1);
        dealCard2 = (ImageView) findViewById(R.id.dealerCard2);
        dealCard3 = (ImageView) findViewById(R.id.dealerCard3);
        dealCard4 = (ImageView) findViewById(R.id.dealerCard4);
        dealCard5 = (ImageView) findViewById(R.id.dealerCard5);
        play1Card1 = (ImageView) findViewById(R.id.imageUser1_view1);
        play1Card2 = (ImageView) findViewById(R.id.imageUser1_view2);
        play2Card1 = (ImageView) findViewById(R.id.imageUser2_view1);
        play2Card2 = (ImageView) findViewById(R.id.imageUser2_view2);
        play3Card1 = (ImageView) findViewById(R.id.imageUser3_view1);
        play3Card2 = (ImageView) findViewById(R.id.imageUser3_view2);
        play4Card1 = (ImageView) findViewById(R.id.imageUser4_view1);
        play4Card2 = (ImageView) findViewById(R.id.imageUser4_view2);

        playCard1.setImageResource(images[52]);
        playCard2.setImageResource(images[52]);
        dealCard1.setImageResource(images[52]);
        dealCard2.setImageResource(images[52]);
        dealCard3.setImageResource(images[52]);
        dealCard4.setImageResource(images[52]);
        dealCard5.setImageResource(images[52]);
        play1Card1.setImageResource(images[52]);
        play1Card2.setImageResource(images[52]);
        play2Card1.setImageResource(images[52]);
        play2Card2.setImageResource(images[52]);
        play3Card1.setImageResource(images[52]);
        play3Card2.setImageResource(images[52]);
        play4Card1.setImageResource(images[52]);
        play4Card2.setImageResource(images[52]);

        mSocket.connect();
        //Register all the listener and callbacks here.
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on("your turn", onYourTurn);
        mSocket.on("not your turn", notYourTurn);
        mSocket.on("hostDisconnected", hostDisconnect);
        mSocket.on("room already exists", roomExists);
        mSocket.on("room doesnt exist", roomDoesntExist);
        mSocket.on("check your turn", checkTurn);
        mSocket.on("new player joined", newPlayer);
        mSocket.on("user disconnected", reupdatePlayers);
        mSocket.on("money update", moneyUpdate);
        mSocket.on("reset players", resetPlayers);
        mSocket.on("update player1", updatePlayer1);
        mSocket.on("update player2", updatePlayer2);
        mSocket.on("update player3", updatePlayer3);
        mSocket.on("update player4", updatePlayer4);
        mSocket.on("pot update", potUpdate);
        mSocket.on("status update",statusUpdate);
        mSocket.on("display flop", displayFlop);
        mSocket.on("display turn", displayTurn);
        mSocket.on("display river", displayRiver);
        mSocket.on("get your hand", getHand);
        mSocket.on("display hand", displayHand);


        betBtnHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String betString = playerBetHolder.getText().toString().trim();

                if (TextUtils.isEmpty(betString)) {
                    playerBetHolder.setError("Number is required to bet");
                    return;
                }


                JSONObject data = new JSONObject();
                try {
                    data.put("betVal", betString);
                    data.put("roomName", rName);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                mSocket.emit("bet", data.toString());
            }
        });

        foldBtnHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocket.emit("fold", rName);
            }

        });

        callBtnHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocket.emit("call", rName);
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

                JSONObject data = new JSONObject();
                try {
                    data.put("userName", uName);
                    data.put("roomName", rName);


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                mSocket.emit("leave room", data.toString());
                mSocket.disconnect();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }

        });

    }


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


    public Emitter.Listener onYourTurn = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    turnTextHolder.setText("It is your turn");
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
                    turnTextHolder.setText("It is not your turn");
                }
            });
        }
    };

    public Emitter.Listener checkTurn = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSocket.emit("check turn", rName);
                }
            });
        }
    };

    public Emitter.Listener moneyUpdate = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String callStr = "call";
                    String raiseStr = "raise";
                    callBtnHolder.setText(callStr);
                    betBtnHolder.setText(raiseStr);
                    currentPotText.setText(args[0].toString());
                    mSocket.emit("get money values", rName);
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
                        data.put("intent", lobbyIntent);


                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    mSocket.emit("joinRoom", data.toString());
                }
            });
        }
    };

    public Emitter.Listener roomExists = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSocket.disconnect();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            });
        }
    };

    public Emitter.Listener roomDoesntExist = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSocket.disconnect();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            });
        }
    };

    public Emitter.Listener newPlayer = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String playerJoinMsg = "Player Joined";
                    String money = "5000";

                    switch (nextPlayerSpace) {
                        case 1:
                            play1UserText.setText(args[0].toString());
                            play1StatusText.setText(playerJoinMsg);
                            play1ChipText.setText(money);
                            nextPlayerSpace++;
                            break;
                        case 2:
                            play2UserText.setText(args[0].toString());
                            play2StatusText.setText(playerJoinMsg);
                            play2ChipText.setText(money);
                            nextPlayerSpace++;
                            break;
                        case 3:
                            play3UserText.setText(args[0].toString());
                            play3StatusText.setText(playerJoinMsg);
                            play3ChipText.setText(money);
                            nextPlayerSpace++;
                            break;
                        case 4:
                            play4UserText.setText(args[0].toString());
                            play4StatusText.setText(playerJoinMsg);
                            play4ChipText.setText(money);
                            nextPlayerSpace++;
                            break;

                    }

                }
            });
        }
    };


    public Emitter.Listener resetPlayers = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String none = "none";
                    String empty = "empty";
                    nextPlayerSpace = 1;
                    play1ChipText.setText("0");
                    play2UserText.setText(none);
                    play2ChipText.setText("0");
                    play3UserText.setText(none);
                    play3ChipText.setText("0");
                    play4UserText.setText(none);
                    play4ChipText.setText("0");
                }
            });
        }
    };

    public Emitter.Listener updatePlayer1 = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String playerJoinMsg = "Player Joined";
                    play1UserText.setText(args[0].toString());
                    play1ChipText.setText(args[1].toString());
                    nextPlayerSpace = 2;
                }
            });
        }
    };
    public Emitter.Listener updatePlayer2 = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String playerJoinMsg = "Player Joined";
                    play2UserText.setText(args[0].toString());
                    play2ChipText.setText(args[1].toString());
                    nextPlayerSpace = 3;
                }
            });
        }
    };
    public Emitter.Listener updatePlayer3 = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String playerJoinMsg = "Player Joined";
                    play3UserText.setText(args[0].toString());
                    play3ChipText.setText(args[1].toString());
                    nextPlayerSpace = 4;
                }
            });
        }
    };
    public Emitter.Listener updatePlayer4 = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String playerJoinMsg = "Player Joined";
                    play4UserText.setText(args[0].toString());
                    play4ChipText.setText(args[1].toString());
                    nextPlayerSpace = 5;
                }
            });
        }
    };

    public Emitter.Listener reupdatePlayers = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSocket.emit("update players", rName);
                }
            });
        }
    };

    public Emitter.Listener potUpdate = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    currentPotText.setText(args[0].toString());
                    playerChipText.setText(args[1].toString());

                }
            });
        }
    };

    public Emitter.Listener statusUpdate = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(play1UserText.getText().toString().compareTo(args[0].toString()) == 0)
                        play1StatusText.setText(args[1].toString());
                    else if(play2UserText.getText().toString().compareTo(args[0].toString()) == 0)
                        play2StatusText.setText(args[1].toString());
                    else if(play3UserText.getText().toString().compareTo(args[0].toString()) == 0)
                        play3StatusText.setText(args[1].toString());
                    else if(play4UserText.getText().toString().compareTo(args[0].toString()) == 0)
                        play4StatusText.setText(args[1].toString());



                }
            });
        }
    };

    public Emitter.Listener displayFlop = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int display1 = Integer.parseInt(args[0].toString());
                    int display2 = Integer.parseInt(args[1].toString());
                    int display3 = Integer.parseInt(args[2].toString());
                    dealCard1.setVisibility(View.VISIBLE);
                    dealCard2.setVisibility(View.VISIBLE);
                    dealCard3.setVisibility(View.VISIBLE);
                    dealCard1.setImageResource(images[display1]);
                    dealCard2.setImageResource(images[display2]);
                    dealCard3.setImageResource(images[display3]);
                }
            });
        }
    };

    public Emitter.Listener displayTurn = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int display1 = Integer.parseInt(args[0].toString());
                    dealCard4.setVisibility(View.VISIBLE);
                    dealCard4.setImageResource(images[display1]);
                }
            });
        }
    };

    public Emitter.Listener displayRiver = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int display1 = Integer.parseInt(args[0].toString());
                    dealCard5.setVisibility(View.VISIBLE);
                    dealCard5.setImageResource(images[display1]);
                }
            });
        }
    };

    public Emitter.Listener getHand = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSocket.emit("draw cards", rName);
                }
            });
        }
    };

    public Emitter.Listener displayHand = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int display1 = Integer.parseInt(args[0].toString());
                    int display2 = Integer.parseInt(args[1].toString());
                    playCard1.setVisibility(View.VISIBLE);
                    playCard2.setVisibility(View.VISIBLE);
                    playCard1.setImageResource(images[display1]);
                    playCard2.setImageResource(images[display2]);
                }
            });
        }
    };





}