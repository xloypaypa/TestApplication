package com.example.xsu.testapplication;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class NetService extends Service {

    private Handler handler;
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
                            Bundle bundle = new Bundle();
                            bundle.putString("message", new String(result));
                            Message message = new Message();
                            message.setData(bundle);
                            handler.sendMessage(message);
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

        public void setHandler(Handler handler) {
            NetService.this.handler = handler;
        }
    }
}
