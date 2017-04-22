package com.jackss.ag.macroboard.sandbox;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.jackss.ag.macroboard.R;
import com.jackss.ag.macroboard.network.Beacon;
import com.jackss.ag.macroboard.network.NetBridge;
import com.jackss.ag.macroboard.network.WifiBridge;

import java.net.InetAddress;

public class NetworkTestsActivity extends AppCompatActivity implements NetBridge.OnConnectionStateListener
{
    private static final String TAG = "NetworkTestsActivity";
    private Button start;
    private Button stop;
    private Button send;
    private TextView result;

    private WifiBridge wifiBridge;
    private Beacon beacon;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_tests);

        start = (Button) findViewById(R.id.sandbox_start);
        stop = (Button) findViewById(R.id.sandbox_stop);
        send = (Button) findViewById(R.id.sandbox_send);
        result = (TextView) findViewById(R.id.sandbox_result);

        wifiBridge = new WifiBridge(this);
        wifiBridge.setConnectionStateListener(this);

        beacon = new Beacon();
        beacon.setBeaconListener(new Beacon.OnBeaconEventListener()
        {
            @Override
            public void onDeviceFound(InetAddress address)
            {
                result.setText(address.getHostAddress());
            }

            @Override
            public void onFailure()
            {
                result.setText("Failed");
            }
        });
    }

    public void onClick(View view)
    {
        if(view.equals(start))
        {
            Log.v(TAG, "Test ");
            //wifiBridge.startConnection();
            beacon.startBroadcast();
        }
        else if(view.equals(stop))
        {
            //wifiBridge.stopConnection();
            beacon.stopBroadcast();
        }
        else if(view.equals(send))
        {
            //wifiBridge.sendData("Hello");
        }
    }

    @Override
    public void onConnectionStateChanged(NetBridge.ConnectionState newState)
    {
        result.setText(newState.name());
    }
}
