package com.jackss.ag.macroboard.network;

import android.content.Context;
import android.util.Log;

/**
 *
 */
abstract public class NetBridge
{
    public enum DataReliability
    {
        RELIABLE,
        UNRELIABLE
    }

    public enum ConnectionState
    {
        IDLE,
        CONNECTING,
        CONNECTED,
        ERROR
    }

    interface OnConnectionStateListener
    {
        void onConnectionStateChanged(ConnectionState newState);
    }

    private Context context;

    private OnConnectionStateListener connectionStateListener;

    private ConnectionState connectionState;


    public NetBridge(Context context)
    {
        this.context = context;
    }


    // interface
    abstract public boolean canStartConnection();

    abstract public void startConnection();

    abstract public void stopConnection();

    abstract public boolean isConnected();

    abstract public boolean sendData(String data, DataReliability reliability);

    public boolean sendData(String data)
    {
        return sendData(data, DataReliability.RELIABLE);
    }

    // final
    public void setConnectionStateListener(OnConnectionStateListener connectionStateListener)
    {
        this.connectionStateListener = connectionStateListener;
    }

    protected void setConnectionState(ConnectionState state)
    {
        if(this.connectionState != state)
        {
            this.connectionState = state;
            if(connectionStateListener != null) connectionStateListener.onConnectionStateChanged(this.connectionState);
        }

        Log.v("NetBridge", "Moving to state: " + state.name());
    }

    public ConnectionState getConnectionState()
    {
        return connectionState;
    }

    public Context getContext()
    {
        return context;
    }
}