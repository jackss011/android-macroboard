package com.jackss.ag.macroboard.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import com.jackss.ag.macroboard.R;
import com.jackss.ag.macroboard.network.Beacon;

public class MainActivity extends AppCompatActivity
{
    Toolbar toolbar;
    Beacon mBeacon;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Dumb Title");

        mBeacon = new Beacon();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mBeacon.startBroadcast();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        mBeacon.stopBroadcast();
    }
}
