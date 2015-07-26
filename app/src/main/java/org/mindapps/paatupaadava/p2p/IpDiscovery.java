package org.mindapps.paatupaadava.p2p;

import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import org.apache.http.conn.util.InetAddressUtils;
import org.mindapps.paatupaadava.MainActivity;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

import static org.mindapps.paatupaadava.server.RequestHandler.ENCODING;
import static org.mindapps.paatupaadava.server.RequestHandler.IP_DISCOVERY;
import static org.mindapps.paatupaadava.server.Server.PORT;

public class IpDiscovery implements WifiP2pManager.ConnectionInfoListener {

    private MainActivity activity;
    private String TAG = this.getClass().getName();

    public IpDiscovery(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        String owner = info.groupOwnerAddress.getHostAddress();
        Log.i(TAG, "Group formed:" + info.groupFormed + " isGroupOwner:" + info.isGroupOwner );
        if(info.groupFormed && !info.isGroupOwner) {
            //These are all clients reporting their IP to owner
            Log.i(TAG, "I am a peer. My group owner is " + owner );
            String ipAddress = this.getIPAddress(true);
            Log.i(TAG, "My IP is " + ipAddress );
            try {
                Socket socket = new Socket(owner, PORT);
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeInt(IP_DISCOVERY);

                byte[] ipAddressBytes = ipAddress.getBytes(Charset.forName(ENCODING));
                outputStream.writeInt(ipAddressBytes.length);
                outputStream.write(ipAddressBytes);
                outputStream.flush();
                outputStream.close();
                Log.i(TAG, "Sent him my Ip");
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (info.groupFormed && info.isGroupOwner) {
            activity.setTextView("Group owner");
        }
    }


    public  String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 port suffix
                                return delim<0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } // for now eat exceptions
        return "No-ip found";
    }
}
