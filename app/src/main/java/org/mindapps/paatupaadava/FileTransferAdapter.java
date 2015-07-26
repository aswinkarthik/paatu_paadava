package org.mindapps.paatupaadava;

import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import org.mindapps.paatupaadava.server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class FileTransferAdapter implements WifiP2pManager.ConnectionInfoListener {
    private Socket socket;

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        String hostAddress = info.groupOwnerAddress.getHostAddress();
        Log.i("Group-owner","Grp formed "+info.groupFormed+" Grp owner "+info.isGroupOwner+" Address "+hostAddress);
        if( info.isGroupOwner && info.groupFormed ) { //server

        } else if ( info.groupFormed) { //client
            try {
                Log.i("Client","Client socket created");
                Log.i("Client","Host address "+hostAddress+" port "+ Server.PORT);

                socket = new Socket(hostAddress, Server.PORT);
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
