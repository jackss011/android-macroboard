package com.jackss.ag.macroboard.network;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 */
public class Beacon
{
    private static final String TAG = "Beacon";

    /** Task which multicast a DatagramPacket over the network. Can be cancelled */
    private static class BeaconTask implements Runnable
    {
        @Override
        public void run()
        {
            final int port = 4545;
            MulticastSocket multicastSocket = null;

            Log.v(TAG, "Beacon start...");

            try
            {
                InetAddress group = InetAddress.getByName("230.185.192.108");
                multicastSocket = new MulticastSocket(port);    //TODO: needs to call leaveGroup?

                // {/*
                //     {Enumeration<NetworkInterface> l = NetworkInterface.getNetworkInterfaces();
                //     while(l.hasMoreElements())
                //     {
                //         NetworkInterface i = l.nextElement();
                //
                //         if(i.isUp() && !i.isLoopback())
                //         {
                //             TAG.v(TAG, i.getName());
                //         }
                //
                //     }
                //     multicastSocket.setNetworkInterface(NetworkInterface.getByName("rndis0"));}
                //     TAG.v(TAG, "run: Beacon starting on: " + multicastSocket.getNetworkInterface().getName());
                // */}
                /**/

                // create the packet
                byte[] message = "Hey dude".getBytes(StandardCharsets.UTF_8);
                DatagramPacket packet = new DatagramPacket(message, message.length, group, port);

                // send the packet until is cancelled
                for(;;)
                {
                    multicastSocket.send(packet);

                    // sleep to prevent too many unnecessary messages
                    try{ Thread.sleep(1000); } catch (InterruptedException e) { break; }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                if(multicastSocket != null) multicastSocket.close();
            }
        }
    }



    /** Executor used to run a BeaconTask */
    private ExecutorService beaconExecutor = Executors.newSingleThreadExecutor();

    /** Future of the running beacon. If null no BeaconTask is running */
    private Future beaconFuture = null;



    /** Is currently sending packets? */
    public boolean isRunning()
    {
        return beaconExecutor != null && !beaconExecutor.isShutdown() && !beaconExecutor.isTerminated()     //TODO: too many checks?
                && beaconFuture != null && !beaconFuture.isCancelled() && !beaconFuture.isDone();
    }

    /** Start sending packet */
    public void startBroadcast()
    {
        if(!isRunning())
        {
            try
            {
                beaconFuture = beaconExecutor.submit(new BeaconTask());
            }
            catch (Exception e){ e.printStackTrace(); }
        }
        else
        {
            Log.e(TAG, "Beacon is already running");
        }
    }

    /** Stop sending packets */
    public void stopBroadcast()
    {
        if(beaconFuture != null)
        {
            beaconFuture.cancel(true);
            beaconFuture = null;
        }

        Log.i(TAG, "Beacon future has been shutdown");
    }
}
