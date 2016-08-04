package com.example.xsu.testapplication;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private NetService.NetBinder netBinder;
    private EditText ip;
    private EditText port;
    private Button connect;
    private TextView sessionId;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                sessionId.setText(message.getData().getString("message"));
                return true;
            }
        });

        ip = (EditText) this.findViewById(R.id.ipInputEditText);
        port = (EditText) this.findViewById(R.id.portEditText);
        connect = (Button) this.findViewById(R.id.connectButton);
        sessionId = (TextView) this.findViewById(R.id.sessionIdText);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPreferences.getString("ip", null) != null) {
            ip.setText(sharedPreferences.getString("ip", null));
        }
        if (sharedPreferences.getInt("port", -1) != -1) {
            port.setText(String.format(Locale.getDefault(), "%d", sharedPreferences.getInt("port", -1)));
        }

        Intent intent = new Intent(this, NetService.class);
        this.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                netBinder = (NetService.NetBinder) iBinder;
                netBinder.setHandler(handler);
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

                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString("ip", ip);
                edit.putInt("port", port);
                edit.apply();

                netBinder.connect(ip, port);
            }
        });
    }
}
