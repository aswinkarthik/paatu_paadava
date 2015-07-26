package org.mindapps.paatupaadava.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.mindapps.paatupaadava.server.RequestHandler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;

import static org.mindapps.paatupaadava.server.Server.PORT;

public class AsyncSendTimeTask extends AsyncTask<Void, Integer, Boolean>{
    private final String TAG = this.getClass().getName();

    private Long scheduleTime;
    private Context context;
    private Iterator<String> clientIpPoolsIterator;

    public AsyncSendTimeTask(Long scheduleTime, Context context, Iterator<String> clientIpPoolsIterator) {
        this.scheduleTime = scheduleTime;
        this.context = context;
        this.clientIpPoolsIterator = clientIpPoolsIterator;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Log.i(TAG, "Lets download some song");
        while (clientIpPoolsIterator.hasNext()) {
            String peer = clientIpPoolsIterator.next();
            Log.i(TAG, "Scheduling for peer " + peer);
            Socket socket = null;
            DataOutputStream dataOutputStream = null;
            try {
                socket = new Socket(peer, PORT);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataOutputStream.writeInt(RequestHandler.SYNC_TIME);
                dataOutputStream.writeLong(this.scheduleTime);
                dataOutputStream.flush();

                Log.i(TAG, "Write complete for peer " + peer);

                dataOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                try {
                    if (socket != null) socket.close();
                    if (dataOutputStream != null) dataOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

}
