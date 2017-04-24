package com.jackss.ag.macroboard.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import java.net.InetAddress;

/**
 *
 */
public class NetAdapter
{
    private static NetAdapter instance;

    public static NetAdapter getInstance()
    {
        if(instance == null) instance = new NetAdapter();
        return instance;
    }

    private NetAdapter() {}

    public static boolean isWifiConnected(@NonNull Context context)
    {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network networks[] = manager.getAllNetworks();

        for(Network net : networks)
        {
            NetworkInfo info = manager.getNetworkInfo(net);
            if(info.getType() == ConnectivityManager.TYPE_WIFI) return info.isConnected();
        }

        return false;
    }

    NetBridge netBridge;

    public void startConnection(InetAddress address)
    {
        netBridge = new WifiBridge(null);
        netBridge.startConnection(address);
    }
}
