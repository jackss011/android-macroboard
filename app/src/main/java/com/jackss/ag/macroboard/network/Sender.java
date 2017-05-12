package com.jackss.ag.macroboard.network;

import android.support.annotation.NonNull;

/**
 *
 */
public class Sender
{
    interface OnSendListener
    {
        void sendData(String data, NetBridge.DataReliability reliability);
    }


    private OnSendListener sendListener;


    Sender(@NonNull OnSendListener sendListener)
    {
        this.sendListener = sendListener;
    }


    public void sendTest(NetBridge.DataReliability reliability)
    {
        sendListener.sendData("Test from Sender", reliability);
    }

    public void sendActionCopy()
    {
        sendListener.sendData(Packager.packActionCopy(), NetBridge.DataReliability.RELIABLE);
    }
}
