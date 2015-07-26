package org.mindapps.paatupaadava.server;

import android.os.AsyncTask;
import android.util.Log;

import org.mindapps.paatupaadava.MainActivity;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends AsyncTask<Void, Void, Void> {
    public static int PORT = 7779;
    private final MainActivity activity;
    private ServerSocket serverSocket;
    private String TAG = this.getClass().getName();

    public Server(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(Void[] params) {
        try {
            Log.i(TAG, "Creating ServerSocket");
            if(serverSocket == null) {
                serverSocket = new ServerSocket(PORT);
            }
            Log.i(TAG, "Waiting for requests");
            do {
                Socket newClient = serverSocket.accept();
                new RequestHandler(newClient, activity).run();
            } while (true);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null)
                    this.serverSocket.close();
                Log.i(TAG, "Server closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
