package com.jackss.ag.macroboard.network;

import android.support.annotation.NonNull;
import android.view.View;

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


    View.OnClickListener mCopyClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            sendListener.sendData(Packager.packActionCopy(), NetBridge.DataReliability.RELIABLE);
        }
    };

    public void sendTest(NetBridge.DataReliability reliability)
    {
        sendListener.sendData("Test from Sender", reliability);
    }
}
