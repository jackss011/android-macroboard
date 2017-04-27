package com.jackss.ag.macroboard.sandbox;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.jackss.ag.macroboard.R;
import com.jackss.ag.macroboard.network.*;
import com.jackss.ag.macroboard.ui.fragments.ConnectDialogFragment;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkTestsActivity extends AppCompatActivity implements NetAdapter.OnNetworkEventListener
{
    private static final String TAG = "NetworkTestsActivity";

    private Button start;
    private Button stop;
    private Button send;
    private TextView result;

    NetAdapter netAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_tests);

        start = (Button) findViewById(R.id.sandbox_start);
        stop = (Button) findViewById(R.id.sandbox_stop);
        send = (Button) findViewById(R.id.sandbox_send);
        result = (TextView) findViewById(R.id.sandbox_result);

        netAdapter = NetAdapter.getInstance();
    }

    public void onClick(View view)
    {
        if(view.equals(start))
        {
            netAdapter.connectDialog(this);
        }
        else if(view.equals(stop))
        {
            netAdapter.disconnect();
        }
        else if(view.equals(send))
        {

        }
    }


    @Override
    protected void onStart()
    {
        super.onStart();

        netAdapter.registerListener(this);
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        netAdapter.unregisterListener();
    }

    @Override
    public void onNetworkStateChanged(NetAdapter.State newState)
    {
        result.setText(newState.name());
    }

    @Override
    public void onNetworkFailure()
    {
        Log.e(TAG, "Net failure");
        result.setText("Failure");
    }
}