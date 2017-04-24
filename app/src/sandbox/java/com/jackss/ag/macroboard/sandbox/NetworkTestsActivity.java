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
import com.jackss.ag.macroboard.ui.fragments.ConnectDialogFragment;

import java.net.InetAddress;

public class NetworkTestsActivity extends AppCompatActivity implements NetBridge.OnConnectionStateListener
{
    private static final String TAG = "NetworkTestsActivity";

    private Button start;
    private Button stop;
    private Button send;
    private TextView result;

    ConnectDialogFragment connectDialogFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_tests);

        start = (Button) findViewById(R.id.sandbox_start);
        stop = (Button) findViewById(R.id.sandbox_stop);
        send = (Button) findViewById(R.id.sandbox_send);
        result = (TextView) findViewById(R.id.sandbox_result);


    }

    public void onClick(View view)
    {
        if(view.equals(start))
        {
            showDialog();
        }
        else if(view.equals(stop))
        {

        }
        else if(view.equals(send))
        {

        }
    }

    @Override
    public void onConnectionStateChanged(NetBridge.ConnectionState newState)
    {
        result.setText(newState.name());
    }

    private void showDialog()
    {
        if(connectDialogFragment == null)
        {
            connectDialogFragment = new ConnectDialogFragment();
        }

        connectDialogFragment.show(getFragmentManager(), null);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        showDialog();
    }
}
