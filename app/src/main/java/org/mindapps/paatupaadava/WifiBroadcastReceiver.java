package org.mindapps.paatupaadava;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;

public class WifiBroadcastReceiver extends BroadcastReceiver {

    private final String TAG = this.getClass().getName();

    private WifiP2pManager manager;
    private final WifiP2pManager.Channel channel;
    private PeerListListener peerListListener = new PeerList();
    private NetworkInfo networkInfo;

    public boolean isConnected() {
        return networkInfo.isConnected();
    }

    public WifiBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel) {
        this.manager = manager;
        this.channel = channel;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        switch (action) {
            case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:

                break;
            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                if (manager != null) {
                    Log.i(TAG, "P2P Change Happened");
                    manager.requestPeers(channel, peerListListener);
                }
                break;
            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                Log.i(TAG, "Wifi P2P Connection changed action. Network info");

                if (manager != null) {
                    this.networkInfo = intent
                            .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                }

                break;
            case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                Log.i(TAG, "Wifi P2P this device changed");
                break;
        }
    }
}
