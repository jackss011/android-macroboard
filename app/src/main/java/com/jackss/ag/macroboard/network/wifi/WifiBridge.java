package com.jackss.ag.macroboard.network.wifi;

import android.content.Context;
import android.util.Log;
import com.jackss.ag.macroboard.network.NetBridge;
import com.jackss.ag.macroboard.network.Packager;
import com.jackss.ag.macroboard.settings.StaticSettings;

import java.io.PrintWriter;
import java.net.InetAddress;


/**
 *
 */
public class WifiBridge extends NetBridge<InetAddress>
{
    private static final String TAG = "WifiBridge";

    private TcpConnection tcpConnection;

    private UdpSender udpSender;

    private boolean handShakeComplete = false;


    private TcpConnection.OnTcpListener tcpListener= new TcpConnection.OnTcpListener()
    {
        @Override
        public void onData(String data)
        {
            Log.v(TAG, "Data received: " + data);

            if(isHandShakeComplete())
            {
                Log.v(TAG, "Valid data");   //TODO: manage data here
            }
            else
            {
                if(Packager.unpackHandShake(data))
                {
                    Log.i(TAG, "Valid handshake");
                    handShakeComplete = true;
                    setConnectionState(BridgeState.CONNECTED);
                }
                else
                    Log.i(TAG, "Invalid handshake");
            }
        }

        @Override
        public void onConnectionStateChanged(TcpConnection.TcpState newState)
        {
            switch (newState)
            {
                case IDLE:
                    setConnectionState(BridgeState.IDLE);
                    break;

                case CONNECTING:
                    setConnectionState(BridgeState.CONNECTING);
                    break;

                case CONNECTED:
                    sendHandShake();
                    break;

                case ERROR:
                    setConnectionState(BridgeState.ERROR);
                    break;
            }
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
    public boolean canStartConnection()
    {
        return tcpConnection.canStartConnection();
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
        invalidateHandShake();
    }

    @Override
    public boolean isConnected()
    {
        return getConnectionState() == BridgeState.CONNECTED;
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

    private void sendHandShake()
    {
        if(getConnectionState() == BridgeState.CONNECTING)
            tcpConnection.sendData(Packager.packHandShake());
    }

    private boolean isHandShakeComplete()
    {
        return handShakeComplete;
    }

    private void invalidateHandShake()
    {
        handShakeComplete = false;
    }
}
