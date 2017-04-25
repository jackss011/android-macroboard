package com.jackss.ag.macroboard.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.util.Log;
import com.jackss.ag.macroboard.settings.StaticSettings;

import java.net.InetAddress;


/**
 *
 */
public class WifiBridge extends NetBridge<InetAddress>
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

        tcpConnection = new TcpConnection(StaticSettings.NET_PORT);
        tcpConnection.setTcpListener(tcpListener);
        udpSender = new UdpSender();
    }

    @Override
    public boolean canStartConnection()  //TODO: useless function
    {
        return true;
    }

    @Override
    public void startConnection(InetAddress address)
    {
        if(canStartConnection())
            tcpConnection.startConnection(address);
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
}
