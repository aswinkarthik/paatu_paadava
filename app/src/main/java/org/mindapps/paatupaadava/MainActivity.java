package org.mindapps.paatupaadava;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends Activity  implements ChannelListener{

    private final IntentFilter intentFilter =  new IntentFilter();
    private WifiBroadcastReceiver receiver;
    private WifiP2pManager manager;
    private Channel channel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
//        discoverPeers(manager, channel);
    }

    private void discoverPeers(WifiP2pManager mManager, Channel mChannel) {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.i("App-Log", "Discover peer was successful");
            }

            @Override
            public void onFailure(int reason) {
                Log.d("App-Log", "I failed :( ");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new WifiBroadcastReceiver(manager, channel);
        registerReceiver(receiver,intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void broadcastMessageToClients(View view) {
        Log.i("Button-click", "I clicked broadcast");
        if (receiver.getNetworkInfo().isConnected()) {
            ConnectionInfoListener fileTransferAdapter = new FileTransferAdapter();
            manager.requestConnectionInfo(channel, fileTransferAdapter);
        } else {
            Log.i("Button-click","Connection not established");
        }
    }

    @Override
    public void onChannelDisconnected() {

    }
}
