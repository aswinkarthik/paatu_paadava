package org.mindapps.paatupaadava;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.net.wifi.p2p.WifiP2pManager.PeerListListener;

public class PeerList implements PeerListListener {

    private final List<WifiP2pDevice> peers = new ArrayList<>();

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        peers.clear();
        peers.addAll(peerList.getDeviceList());

        Log.i("App-Log", "Peers has changed. New size is " + peers.size());
    }
}
