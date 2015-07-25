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

    private static final Integer PORT = 7778;
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

    private class AsyncSendFileTask extends AsyncTask<List<WifiP2pDevice>, Integer, Boolean> {

        private final String TAG = this.getClass().getName();

        private final byte[] data;
        private ProgressDialog dialog;

        public AsyncSendFileTask(byte[] data, Context context) {
            this.data = data;
            this.dialog = new ProgressDialog(context);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Sending files to peers");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(List<WifiP2pDevice>... peers) {
            Log.i(TAG, "Doing in background for peers");

            if(peers.length == 0) {
                Log.i(TAG, "Something is definitely wrong");
                return false;
            }

            if(peers[0].size() == 0) {
                Log.i(TAG, "No peers found");
                return false;
            }

            for (WifiP2pDevice peer : peers[0]) {
                Log.i(TAG, "Selected peer " + peer + " InetAddress " + peer.deviceAddress);
                Socket socket = null;
                try {
                    socket = new Socket(peer.deviceAddress, PORT);
                    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    dataOutputStream.writeInt(data.length);
                    dataOutputStream.write(data);
                    dataOutputStream.flush();

                    Log.i(TAG, "Write complete for peer " + peer.deviceAddress);

                    dataOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    try {
                        if (socket != null) socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

        }
    }
}
