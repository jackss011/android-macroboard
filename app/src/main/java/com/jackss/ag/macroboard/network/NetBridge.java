package com.jackss.ag.macroboard.network;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 *
 */
abstract public class NetBridge<T>
{
    public enum DataReliability
    {
        RELIABLE,
        UNRELIABLE
    }

    public enum BridgeState
    {
        IDLE,
        CONNECTING,
        CONNECTED,
        ERROR
    }

    public interface OnConnectionStateListener
    {
        void onConnectionStateChanged(BridgeState newState);
    }

    private Context context;

    private OnConnectionStateListener connectionStateListener;

    private BridgeState connectionState;


    public NetBridge(Context context)
    {
        this.context = context;
    }


    // interface
    abstract public boolean canStartConnection();

    abstract public void startConnection(T address);

    abstract public void stopConnection();

    abstract public boolean isConnected();

    abstract public boolean sendData(String data, DataReliability reliability);

    public boolean sendData(String data)
    {
        return sendData(data, DataReliability.RELIABLE);
    }

    // final
    public void setConnectionStateListener(@Nullable OnConnectionStateListener connectionStateListener)
    {
        this.connectionStateListener = connectionStateListener;

        if(connectionStateListener != null) connectionStateListener.onConnectionStateChanged(getConnectionState());
    }

    protected void setConnectionState(BridgeState state)
    {
        if(this.connectionState != state)
        {
            this.connectionState = state;
            if(connectionStateListener != null) connectionStateListener.onConnectionStateChanged(this.connectionState);
        }

        Log.v("NetBridge", "Moving to state: " + state.name());
    }

    public BridgeState getConnectionState()
    {
        return connectionState;
    }

    public Context getContext()
    {
        return context;
    }
}
