package com.example.xsu.testapplication;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class NetService extends Service {

    private NetBinder netBinder;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        netBinder = new NetBinder();
        return netBinder;
    }

    private void connect(final String ip, final int port) {
        new Thread() {
            @Override
            public void run() {
                try {
                    socket = new Socket(ip, port);
                    inputStream = socket.getInputStream();
                    outputStream = socket.getOutputStream();

                    outputStream.write("16x/getSessionID#{}".getBytes());

                    //noinspection InfiniteLoopStatement
                    while (true) {
                        Thread.sleep(200);
                        byte[] temp = new byte[1024];
                        int len = inputStream.read(temp);
                        if (len != 0 ) {
                            byte[] result = new byte[len];
                            System.arraycopy(temp, 0, result, 0, len);
                            Log.e("message", new String(result));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    public class NetBinder extends Binder {

        public void connect(String ip, int port) {
            NetService.this.connect(ip, port);
        }
    }
}
