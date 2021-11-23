package com.example.pokergame;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class ServerHandler {
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://10.0.2.2:3000");
        }catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }

}
