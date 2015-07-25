package org.mindapps.paatupaadava.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import static org.mindapps.paatupaadava.utils.NetworkAdapter.PORT;

public class AsyncSendFileTask extends AsyncTask<List<WifiP2pDevice>, Integer, Boolean> {

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