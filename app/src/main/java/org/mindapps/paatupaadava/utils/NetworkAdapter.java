package org.mindapps.paatupaadava.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import org.mindapps.paatupaadava.async.AsyncSendFileTask;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class NetworkAdapter implements WifiP2pManager.PeerListListener {

    public static final Integer PORT = 7778;
    private final String TAG = this.getClass().getName();
    private final List<WifiP2pDevice> peers = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public void sendFileToPeers(Context context, Uri song) {
        Log.i(TAG, "Sending files to peers");

        String path = getPath(context, song);
        File file = new File(path);
        Log.i(TAG, "File is from path " + path);
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));

            Log.i(TAG, "Reading from file with length " + file.length());
            byte[] songBytes = new byte[(int) file.length()];
            bufferedInputStream.read(songBytes);
            bufferedInputStream.close();

            new AsyncSendFileTask(songBytes, context).execute( peers);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getPath(Context context, Uri uri) {
        String[]  data = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(context, uri, data, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        peers.clear();
        peers.addAll(peerList.getDeviceList());

        Log.i(TAG, "Peers has changed. New size is " + peers.size());
    }


}
