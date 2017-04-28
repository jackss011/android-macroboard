package com.jackss.ag.macroboard.network;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import com.jackss.ag.macroboard.network.wifi.WifiBridge;
import com.jackss.ag.macroboard.ui.fragments.ConnectDialogFragment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 */
public class NetAdapter implements Sender.OnSendListener
{
    private static final String TAG = "NetAdapter";

// |===============================
// |==>  STATIC MEMBERS
// |===============================

    private static NetAdapter instance;

    public static NetAdapter getInstance()
    {
        if(instance == null) instance = new NetAdapter();
        return instance;
    }



// |===============================
// |==>  CLASSES
// |===============================

    public enum State
    {
        IDLE,
        CONNECTING,
        CONNECTED
    }


    public interface OnNetworkEventListener
    {
        void onNetworkStateChanged(State newState);

        void onNetworkFailure();
    }


    NetBridge.OnConnectionStateListener networkListener = new NetBridge.OnConnectionStateListener()
    {
        @Override
        public void onConnectionStateChanged(NetBridge.BridgeState newState)
        {
            switch (newState)
            {
                case IDLE:
                    if (listener != null) listener.onNetworkStateChanged(State.IDLE);
                    break;
                case CONNECTING:
                    if (listener != null) listener.onNetworkStateChanged(State.CONNECTING);
                    break;
                case CONNECTED:
                    if (listener != null) listener.onNetworkStateChanged(State.CONNECTED);
                    break;
                case ERROR:
                    failure();
                    break;
            }
        }
    };


    private final ConnectDialogFragment.OnConnectDialogEventListener connectionDialogListener =
            new ConnectDialogFragment.OnConnectDialogEventListener()
    {
        @Override
        public void onDialogConnectRequest(String address)
        {
            try
            {
                netBridge.startConnection(InetAddress.getByName(address));
            }
            catch (UnknownHostException e)
            {
                e.printStackTrace();
                throw new AssertionError("Dialog returned invalid address");
            }
        }
    };


// |===============================
// |==>  FIELDS
// |===============================

    private WifiBridge netBridge;

    private ConnectDialogFragment connectDialogFragment;

    private OnNetworkEventListener listener;

    private Sender sender;


// |===============================
// |==>  CONSTRUCTORS
// |===============================

    private NetAdapter()
    {
        netBridge = new WifiBridge(null);
        netBridge.setConnectionStateListener(networkListener);

        connectDialogFragment = new ConnectDialogFragment();
        connectDialogFragment.setDialogEventListener(connectionDialogListener);

        sender = new Sender(this);
    }


// |===============================
// |==>  METHODS
// |===============================

    private void failure()
    {
        if(listener != null) listener.onNetworkFailure();
    }

    @Override
    public void sendData(String data, NetBridge.DataReliability reliability)    //TODO: move this inside NetBridge?
    {
        netBridge.sendData(data, reliability);
    }

    public void connectDialog(Activity activity)
    {
        if(!isConnected())
            connectDialogFragment.show(activity.getFragmentManager(), null);
        else
            Log.e(TAG, "Socket is already connected");
    }

    public void disconnect()
    {
        connectDialogFragment.dismiss();

        netBridge.stopConnection();
    }

    public boolean isConnected()
    {
        return netBridge.getConnectionState() == NetBridge.BridgeState.CONNECTED;
    }

    public void registerListener(@NonNull OnNetworkEventListener listener)
    {
        this.listener = listener;
    }

    public void unregisterListener()
    {
        this.listener = null;
    }

    public Sender getSender()
    {
        return sender;
    }
}





// public static boolean isWifiConnected(@NonNull Context context)
// {
//     ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//     Network networks[] = manager.getAllNetworks();
//
//     for(Network net : networks)
//     {
//         NetworkInfo info = manager.getNetworkInfo(net);
//         if(info.getType() == ConnectivityManager.TYPE_WIFI) return info.isConnected();
//     }
//
//     return false;
// }