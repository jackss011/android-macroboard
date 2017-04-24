package com.jackss.ag.macroboard.network;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.ArraySet;
import android.util.Log;
import com.jackss.ag.macroboard.utils.ExpiringList;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 */
public class Beacon
{
    private static final String TAG = "Beacon";

    private static final int MSG_WHAT_ADDRESS = 1200;
    private static final int MSG_WHAT_ERROR = 1400;

    private ExecutorService multicastExecutor = Executors.newSingleThreadExecutor();
    private Future multicastFuture;

    private Thread receiverThread;
    private ReceiverTask receiverTask;

    private OnBeaconEventListener eventListener;

    private ExpiringList<InetAddress> deviceList;


    
// |==============================
// |==>  CLASSES
// |===============================

    /** Beacon callbacks */
    public interface OnBeaconEventListener  //TODO: rename this class
    {
        /** Called when a new device response */
        void onDeviceFound(InetAddress address);

        /** Called if an error occurs */
        void onFailure();
    }


    /** Multicast UDP packets over the network */
    private static class MulticastTask implements Runnable
    {
        @Override
        public void run()
        {
            Log.i(TAG, "Starting broadcasting thread");

            try(MulticastSocket multicastSocket = new MulticastSocket())
            {
                InetAddress group = InetAddress.getByName("228.5.6.7"); //TODO: hardcoded
                multicastSocket.joinGroup(group);

                while(true)
                {
                    String test = "test";
                    byte sending[] = test.getBytes(StandardCharsets.UTF_8);
                    DatagramPacket packet = new DatagramPacket(sending, sending.length, group, 4545); //TODO: hardcoded

                    multicastSocket.send(packet);

                    try{ Thread.sleep(1000); } catch (InterruptedException e) { break; }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


    /** Receive packets sent as response from listening devices */
    private class ReceiverTask implements Runnable
    {
        private Handler mainHandler;

        private DatagramSocket receiverSocket;


        private void createMainHandler() //TODO: use parent class maybe
        {
            mainHandler = new Handler(Looper.getMainLooper())
            {
                @Override
                public void handleMessage(Message msg)
                {
                    switch (msg.what)
                    {
                        case MSG_WHAT_ADDRESS:
                            onAddress((InetAddress) msg.obj);
                            break;

                        case MSG_WHAT_ERROR:
                            onError();
                            break;

                        default:
                            throw new AssertionError("Unknown msg.what");
                    }
                }
            };
        }

        // Close receiver socket. Needed to interrupt DatagramSocket.receive(packet)
        void shutdown()
        {
            if(receiverSocket != null) receiverSocket.close();
        }

        @Override
        public void run()
        {
            Log.i(TAG, "Starting receiverSocket thread");

            createMainHandler();

            try
            {
                receiverSocket = new DatagramSocket(4545);

                while(true)
                {
                    byte buff[] = new byte[256];
                    DatagramPacket request = new DatagramPacket(buff, buff.length);
                    receiverSocket.receive(request);

                    if(request.getAddress() != null) //TODO: check for response
                    {
                        mainHandler.obtainMessage(MSG_WHAT_ADDRESS, request.getAddress()).sendToTarget();
                    }
                    else throw new AssertionError("Unexpected error");

                    if(Thread.interrupted()) break;
                }

                Log.i(TAG, "Quitting receiverSocket thread");
            }
            catch (Exception e)
            {
                if(receiverSocket != null && !receiverSocket.isClosed())
                    mainHandler.sendEmptyMessage(MSG_WHAT_ERROR);

                e.printStackTrace();
            }
            finally
            {
                shutdown();
            }
        }
    }


// |==============================
// |==>  CONSTRUCTOR
// |===============================

    public Beacon()
    {
        deviceList = new ExpiringList<>(10);
    }



// |==============================
// |==>  METHODS
// |==============================

    private void onAddress(InetAddress address)
    {
        if(deviceList.add(address))
        {
            Log.i(TAG, "Found new address");

            if(eventListener != null) eventListener.onDeviceFound(address);
        }
    }

    private void onError()
    {
        Log.i(TAG, "Unknown error occurred");

        stopBroadcast();
        if(eventListener != null) eventListener.onFailure();
    }

    public Set<String> getDevicesAsStrings()
    {
        Set<String> devices = new HashSet<>();

        for(InetAddress address : deviceList.getList())
            devices.add(address.getHostAddress());

        return devices;
    }

    /** Set listener for beacon events */
    public void setBeaconListener(OnBeaconEventListener listener)
    {
        this.eventListener = listener;
    }

    /** Is currently sending packets? */
    public boolean isRunning()      //TODO: too many checks?
    {
        return multicastExecutor != null && !multicastExecutor.isShutdown() && !multicastExecutor.isTerminated()
                && multicastFuture != null && !multicastFuture.isCancelled() && !multicastFuture.isDone();
    }

    /** Start sending packet */
    public void startBroadcast()
    {
        if(!isRunning())
        {
            deviceList.clear();

            receiverTask = new ReceiverTask();
            receiverThread = new Thread(receiverTask);
            receiverThread.setDaemon(true);
            receiverThread.start();

            multicastFuture = multicastExecutor.submit(new MulticastTask());
        }
        else Log.e(TAG, "Beacon is already running");
    }

    /** Stop sending packets */
    public void stopBroadcast()
    {
        deviceList.clear();

        if(multicastFuture != null)
        {
            multicastFuture.cancel(true);
            multicastFuture = null;
        }

        if(receiverThread != null)
        {
            receiverThread.interrupt(); //TODO: maybe not necessary
            receiverThread = null;
        }

        if(receiverTask != null)
        {
            receiverTask.shutdown();
            receiverTask = null;
        }

        Log.i(TAG, "Beacon future has been shutdown");
    }
}