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
import com.jackss.ag.macroboard.network.Beacon;
import com.jackss.ag.macroboard.network.TcpConnection;
import com.jackss.ag.macroboard.network.UdpSender;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity
{
    Toolbar toolbar;
    InetAddress address;

    UdpSender sender;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Dumb Title");


        sender = new UdpSender();
        try
        {
            address = InetAddress.getByName("192.168.1.5");
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }

        final TcpConnection connection = new TcpConnection(4545);
        connection.accept();

        Button start = (Button) findViewById(R.id.btn_media_prev);
        start.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });

        Button stop = (Button) findViewById(R.id.btn_media_next);
        stop.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });

        final Button send = (Button) findViewById(R.id.btn_media_play);
        send.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sender.sendData(address, 4545,"Hey dummy");
                connection.sendData("Hello");
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
