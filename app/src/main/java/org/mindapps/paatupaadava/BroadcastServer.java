package org.mindapps.paatupaadava;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BroadcastServer extends AsyncTask<Void, Void, Void> {
    private int PORT = 7711;
    private ServerSocket serverSocket;
    private String TAG = this.getClass().getName();

    @Override
    protected Void doInBackground(Void[] params) {
        try {
            Log.i("Server", "Server socket created");
            serverSocket = new ServerSocket(PORT);
            do {
                Socket newClient = serverSocket.accept();
                new RequestHandler(newClient).run();
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
