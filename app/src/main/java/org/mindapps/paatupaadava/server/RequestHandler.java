package org.mindapps.paatupaadava.server;

import android.util.Log;

import org.mindapps.paatupaadava.MainActivity;
import org.mindapps.paatupaadava.utils.MP3Player;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class RequestHandler extends Thread {

    public static int IP_DISCOVERY = 1;
    public static int INCOMING_FILE = 2;
    public static String ENCODING = "UTF-8";


    private final Socket socket;
    private final MainActivity activity;
    private final String TAG = "Request-Handler";

    public RequestHandler(Socket socket, MainActivity activity) {
        this.socket = socket;
        this.activity = activity;
    }

    @Override
    public void run() {
        Log.i(TAG, "New request");
        Log.i(TAG, "Socket " + socket.getInetAddress() + " " + socket.isConnected() + " " + socket.getPort());
        DataInputStream in = null;
        try {
            in = new DataInputStream(socket.getInputStream());

            int command = in.readInt();
            Log.i(TAG, "Request type " + command);
            byte[] dataFromStream = getDataFromStream(in);

            switch (command) {
                case 1:
                    //Handshake to report IP Address
                    String ipAddress = new String(dataFromStream, ENCODING);
                    Log.i(TAG, "IP Discovery " + ipAddress);
                    activity.addToClientIpPool(ipAddress);
                    break;
                case 2:
                    //File Transfer
                    Log.i(TAG, "File transfer initiated ");
                    storeToTempFile(dataFromStream);
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void storeToTempFile(byte[] data) {
        try {
            File outputDir = activity.getCacheDir();
            File outputFile = File.createTempFile("song", "mp3", outputDir);
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile));
            out.write(data);
            out.flush();
            out.close();
            Log.i(TAG, "File created with path " + outputFile.getAbsolutePath());


            activity.setDownloadedSong(outputFile);

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private byte[] getDataFromStream(DataInputStream in) throws IOException {
        int lengthOfData = in.readInt();
        byte[] data = new byte[lengthOfData];
        in.readFully(data);
        return data;
    }
}
