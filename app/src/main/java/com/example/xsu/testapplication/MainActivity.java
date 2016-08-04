package com.example.xsu.testapplication;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private NetService.NetBinder netBinder;
    private EditText ip;
    private EditText port;
    private Button connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ip = (EditText) this.findViewById(R.id.ipInputEditText);
        port = (EditText) this.findViewById(R.id.portEditText);
        connect = (Button) this.findViewById(R.id.connectButton);

        Intent intent = new Intent(this, NetService.class);
        this.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                netBinder = (NetService.NetBinder) iBinder;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        }, BIND_AUTO_CREATE);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = MainActivity.this.ip.getText().toString();
                int port = Integer.parseInt(MainActivity.this.port.getText().toString());
                netBinder.connect(ip, port);
            }
        });
    }
}
