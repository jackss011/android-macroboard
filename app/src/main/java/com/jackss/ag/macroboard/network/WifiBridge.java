package com.jackss.ag.macroboard.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.util.Log;


/**
 *
 */
public class WifiBridge extends NetBridge
{
    private static final String TAG = "WifiBridge";

    private TcpConnection tcpConnection;

    private UdpSender udpSender;

    private TcpConnection.OnTcpListener tcpListener= new TcpConnection.OnTcpListener()
    {
        @Override
        public void onData(String data)
        {
            Log.v(TAG, "Data received: " + data);
        }

        @Override
        public void onConnectionStateChanged(TcpConnection.TcpState newState)
        {
            setConnectionState(ConnectionState.values()[newState.ordinal()]);       //TODO: non-safe enum transposition
        }
    };


    public WifiBridge(Context context)
    {
        super(context);

        tcpConnection = new TcpConnection(4545);
        tcpConnection.setTcpListener(tcpListener);
        udpSender = new UdpSender();
    }

    @Override
    public boolean canStartConnection()
    {
        return isWifiConnected(getContext());
    }

    @Override
    public void startConnection()
    {
        if(canStartConnection())
            tcpConnection.startConnection();
    }

    @Override
    public void stopConnection()
    {
        tcpConnection.reset();
    }

    @Override
    public boolean isConnected()
    {
        return tcpConnection.getTcpState() == TcpConnection.TcpState.CONNECTED;
    }

    @Override
    public boolean sendData(String data, DataReliability reliability)
    {
        if(data == null) throw new AssertionError("sending null data string");

        if(!isConnected())
        {
            Log.v(TAG, "sending data from non-connected wifi_bridge");
            return false;
        }

        if(reliability == DataReliability.RELIABLE)
        {
            tcpConnection.sendData(data);
        }
        else
        {
            udpSender.sendData(tcpConnection.getConnectedAddress(), tcpConnection.getPort(), data);
        }

        return true;
    }

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
}
