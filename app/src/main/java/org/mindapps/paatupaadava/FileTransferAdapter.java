package org.mindapps.paatupaadava;

import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class FileTransferAdapter implements WifiP2pManager.ConnectionInfoListener {
    private int PORT = 7711;
    private Socket socket;

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        String hostAddress = info.groupOwnerAddress.getHostAddress();
        Log.i("Group-owner","Grp formed "+info.groupFormed+" Grp owner "+info.isGroupOwner+" Address "+hostAddress);
        if( info.isGroupOwner && info.groupFormed ) { //server
            new BroadcastServer().execute();
        } else if ( info.groupFormed) { //client
            try {
                Log.i("Client","Client socket created");
                Log.i("Client","Host address "+hostAddress+" port "+PORT);

                socket = new Socket(hostAddress, PORT);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String line = bufferedReader.readLine();

                Log.i("Broadcast-Msg", line);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(socket != null && socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
}
