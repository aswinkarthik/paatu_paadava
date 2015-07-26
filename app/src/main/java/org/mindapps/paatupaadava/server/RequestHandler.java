package org.mindapps.paatupaadava.server;

import android.util.Log;

import org.mindapps.paatupaadava.MainActivity;
import org.mindapps.paatupaadava.utils.MP3Player;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class RequestHandler extends Thread {
    private final Socket socket;
    private final MainActivity activity;
    private final String TAG = "Request-Handler";

    public RequestHandler(Socket socket, MainActivity activity) {
        this.socket = socket;
        this.activity = activity;
    }

    @Override
    public void run() {
        BufferedWriter writer = null;
        Log.i(TAG, "New request");
        Log.i(TAG, "Socket " + socket.getInetAddress() + " " + socket.isConnected() + " " + socket.getPort());
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            int length = in.readInt();

            Log.i(TAG, "Handling request");
            if (length > 0) {
                byte[] data = new byte[length];
                Log.i(TAG, "Incoming " + length);
                in.readFully(data);
                in.close();
                Log.i(TAG, "Download complete");

                File outputDir = activity.getCacheDir();
                File outputFile = File.createTempFile("song", "mp3", outputDir);
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile));
                out.write(data);
                out.flush();
                out.close();
                Log.i(TAG, "File created with path " + outputFile.getAbsolutePath());


                activity.setDownloadedSong(outputFile);
                MP3Player player = new MP3Player();
                Log.i(TAG, "Playing downloaded song");
                player.playSong(outputFile);

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
