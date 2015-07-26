package org.mindapps.paatupaadava.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
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
        Log.i(TAG, "Starting async task");
        this.dialog.show();
    }

    @Override
    protected Boolean doInBackground(List<WifiP2pDevice>... peers) {
        Log.i(TAG, "Doing in background for peers");

        if (peers.length == 0) {
            Log.e(TAG, "Something is definitely wrong");
            return false;
        }

        if (peers[0].size() == 0) {
            Log.e(TAG, "No peers found");
            return false;
        }



        Log.i(TAG, "What else is cooking " + peers.length);


        for (WifiP2pDevice peer : peers[0]) {
            String ipFromMac = getIPFromMac(peer.deviceAddress);
            Log.i(TAG, "Looping Getting IP from ARP" + ipFromMac);
        }

        for (WifiP2pDevice peer : peers[0]) {
            Log.i(TAG, "Selected peer " + peer + " InetAddress " + peer.deviceAddress);
            String ipFromMac = getIPFromMac(peer.deviceAddress);
            Log.i(TAG, "Getting IP from ARP" + ipFromMac);
            Socket socket = null;
            try {
                socket = new Socket(ipFromMac, PORT);
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataOutputStream.writeInt(data.length);
                dataOutputStream.write(data);
                dataOutputStream.flush();

                Log.i(TAG, "Write complete for peer " + ipFromMac);

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

    private static String getIPFromMac(String MAC) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {

                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    // Basic sanity check
                    String device = splitted[5];
                    if (device.matches(".*p2p-p2p0.*")){
                        String mac = splitted[3];
                        if (mac.matches(MAC)) {
                            return splitted[0];
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}