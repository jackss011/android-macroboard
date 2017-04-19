package com.jackss.ag.macroboard.ui.activities;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.jackss.ag.macroboard.R;
import com.jackss.ag.macroboard.network.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity
{
    Toolbar toolbar;
    InetAddress address;


    NetBridge netBridge;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Dumb Title");

        netBridge = new WifiBridge(getApplicationContext());


        Button start = (Button) findViewById(R.id.btn_media_prev);
        start.setText("start");
        start.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                netBridge.startConnection();
            }
        });

        Button stop = (Button) findViewById(R.id.btn_media_next);
        stop.setText("stop");
        stop.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                netBridge.stopConnection();
            }
        });

        final Button send = (Button) findViewById(R.id.btn_media_play);
        send.setText("send");
        send.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                netBridge.sendData("Hello");
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }
}
