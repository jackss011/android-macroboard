package com.jackss.ag.macroboard.network.wifi;

import android.os.*;
import android.support.annotation.Nullable;
import android.util.Log;
import com.jackss.ag.macroboard.settings.StaticSettings;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


/**
 * Manages a TCP connection.
 *
 */
public class TcpConnection
{
    private static final String TAG = "TcpConnection";


    // Used in handler messages what field
    private static final int MSG_WHAT_DATA = 200;
    private static final int MSG_WHAT_ERROR = 400;


    private Socket clientSocket;

    private ClientConnectionTask connectionTask;      // AsyncTask used to produce a connected socket

    private Thread inputThread;                 // Thread listening for input_stream data

    private PrintWriter outputPrinter;          // Printer used to send data to the output_stream


    private TcpState tcpState = TcpState.IDLE;

    private int port;

    private OnTcpListener tcpListener;



// |==============================
// |==>  CONSTRUCTORS
// |===============================

    public TcpConnection(int port)
    {
        this.port = port;
    }


// |==============================
// |==>  CLASSES
// |===============================

    enum  TcpState
    {
        IDLE,
        CONNECTING,
        CONNECTED,
        ERROR
    }


    interface OnTcpListener
    {
        void onData(String data);

        void onConnectionStateChanged(TcpState newState);
    }


    /** Struct containing address and port */
    private static class ConnectionInfo
    {
        InetAddress address;
        int port;

        ConnectionInfo(InetAddress address, int port)
        {
            this.address = address;
            this.port = port;
        }
    }


    /**
     * AsyncTask used to produce a connected TCP socket.
     */
    private class ConnectionTask extends AsyncTask<Integer, Void, Socket>
    {
        @Override
        protected Socket doInBackground(Integer... portArgs)
        {
            int port = portArgs[0];

            try(ServerSocket serverSocket = new ServerSocket(port))
            {
                return serverSocket.accept();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Socket socket)
        {
            // only if the task is not cancelled
            if(!isCancelled()) onConnectionResult(socket);
        }
    }


    private class ClientConnectionTask extends AsyncTask<ConnectionInfo, Void, Socket>
    {
        @Override
        protected Socket doInBackground(ConnectionInfo... args)
        {
            ConnectionInfo info = args[0];

            try
            {
                return new Socket(info.address, info.port);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Socket socket)
        {
            // only if the task is not cancelled
            if(!isCancelled()) onConnectionResult(socket);
        }
    }


    /**
     * Runnable running on a separate thread listening for TCP input stream data.
     * Data is sent to main_thread via Handler(main_looper).
     */
    private class InputHandler  implements Runnable
    {
        private static final String TAG = "InputHandler";

        private Handler mainHandler;
        private final Socket clientSocket;

        InputHandler(Socket socket)
        {
            this.clientSocket = socket;
        }
        
        private void createMainHandler()
        {
            mainHandler = new Handler(Looper.getMainLooper())   //TODO: this may cause leaks?
            {
                @Override
                public void handleMessage(Message msg)
                {
                    // RUNNING ON MAIN THREAD
                    switch(msg.what)
                    {
                        case MSG_WHAT_DATA:
                            onDataReceived((String) msg.obj);
                            break;

                        case MSG_WHAT_ERROR:
                            onInputThreadError();
                            break;
                    }
                }
            };
        }

        private void sendErrorMessage()
        {
            mainHandler.sendEmptyMessage(MSG_WHAT_ERROR);
        }

        @Override
        public void run()
        {
            Log.i(TAG, "Started input_thread");

            createMainHandler();

            try
            {
                BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                if(Thread.interrupted()) return;

                while(true)
                {
                    String readData = br.readLine();
                    if(Thread.interrupted()) break;

                    if(readData != null)
                        mainHandler.obtainMessage(MSG_WHAT_DATA, readData).sendToTarget();
                    else
                        sendErrorMessage();
                }
            }
            catch (Exception e)
            {
                if(!clientSocket.isClosed()) e.printStackTrace();
                sendErrorMessage();
            }
        }
    }



// |==============================
// |==>  METHODS
// |===============================

    // Called from ConnectionTask.onPostExecute(socket) when connection is finished
    private void onConnectionResult(Socket socket)
    {
        clientSocket = socket;
        connectionTask = null;

        if(isSocketConnected())
        {
            onConnected();
        }
        else
        {
            Log.e(TAG, "Connection result failed");
            onError();
        }

    }

    // Called if a connected socket is found
    private void onConnected()
    {
        Log.i(TAG, "Connected to: " + clientSocket.getInetAddress().getHostAddress());

        try
        {
            clientSocket.setTcpNoDelay(true);

            outputPrinter =
                    new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8)), true);

            if(inputThread != null) inputThread.interrupt();
            inputThread = new Thread(new InputHandler(clientSocket));
            inputThread.start();

            setTcpState(TcpState.CONNECTED);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            onError();
        }
    }

    // Called from input_thread if an error occurs using the main_handler (i.e. running on main_thread)
    private void onInputThreadError()
    {
        // "throw" an error only if the socket wasn't intentionally closed using Socket.close()
        //  since it means it is not an unexpected event
        if(isSocketConnected()) onError();
    }

    // Called internally when an error occurs
    private void onError()      //TODO: maybe use error types
    {
        setTcpState(TcpState.ERROR);
    }

    // Running on main thread. Called by input thread when data is read from the input buffer
    private void onDataReceived(String data)
    {
        if(tcpListener != null) tcpListener.onData(data);
    }

    // Internal set tcp state. Call the listener if a change occurred
    private void setTcpState(TcpState newState)
    {
        if(getTcpState() != newState)
        {
            this.tcpState = newState;
            if(tcpListener != null) tcpListener.onConnectionStateChanged(getTcpState());

            Log.v(TAG, "Moving to state: " + newState.name());
        }
    }

    /** Get the state of this TCP connection. */
    public TcpState getTcpState()
    {
        return tcpState;
    }

    /** Set the listener notified of data receiving and connection state change. */
    public void setTcpListener(@Nullable OnTcpListener tcpListener)
    {
        this.tcpListener = tcpListener;

        if(tcpListener != null) tcpListener.onConnectionStateChanged(getTcpState());
    }

    /** If isSocketConnected() equals true return the socket address, return null otherwise. */
    public InetAddress getConnectedAddress()
    {
        return isSocketConnected() ? clientSocket.getInetAddress() : null;
    }

    /** Get the port used by this connection. */
    public int getPort()
    {
        return port;
    }

    /** Return true if the connection is valid */
    public boolean isConnected()
    {
        return getTcpState() == TcpState.CONNECTED;
    }

    /** Return true if is currently try to connect, false otherwise. */
    private boolean isConnecting()
    {
        return  connectionTask != null                                          // valid ref
                && connectionTask.getStatus() == AsyncTask.Status.RUNNING       // task running
                && !connectionTask.isCancelled();                               // task not cancelled
    }

    /**
     * Return true if is the socket is connected, false otherwise.
     *
     * NOTE: The socket is considered connected even if is actually disconnected from the network.
     *       Only writing or reading from its streams determine if a socket is actually connected or not.
     *       TcpListener.onConnectionStateChange(TcpState.ERROR) is called (on the main thread) when such operations fail.
     */
    public boolean isSocketConnected()
    {
        return clientSocket != null && clientSocket.isConnected();
    }

    /** Check whenever calling startConnection will actually start a connection */
    public boolean canStartConnection()
    {
        return getTcpState() == TcpState.IDLE;
    }

    /**
     * Try to produce a connected Socket. State is moved to TcpState.CONNECTING.
     *
     * @return  return true if the connection is successfully started
     */
    public boolean startConnection(InetAddress address)
    {
        if(canStartConnection())
        {
            Log.i(TAG, "Connection in progress");

            connectionTask = new ClientConnectionTask();
            setTcpState(TcpState.CONNECTING);
            connectionTask.execute(new ConnectionInfo(address, StaticSettings.NET_PORT));

            return true;
        }

        return false;
    }

    /** Free every resource allowing to start a new connection. */
    public void reset()
    {
        Log.i(TAG, "Connection reset");

        // connection task
        if(connectionTask != null)
        {
            connectionTask.cancel(true);
            connectionTask = null;
        }

        // input thread
        if(inputThread != null)
        {
            inputThread.interrupt();
            inputThread = null;
        }

        // output printer
        if(outputPrinter != null)
        {
            outputPrinter.close();
            outputPrinter = null;
        }

        // client socket
        if(clientSocket != null) try
        {
            clientSocket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            clientSocket = null;
        }

        setTcpState(TcpState.IDLE);
    }

    /**
     * If the connection is open send a data string, do nothing otherwise.
     *
     * @param data Data string to send
     */
    public void sendData(String data)
    {
        if(isSocketConnected())   //TODO: should use getState()?
        {
            if(outputPrinter != null)
                outputPrinter.println(data);
            else
                throw new AssertionError("outputPrinter is null while the socket is connected!");
        }
        else
        {
            Log.e(TAG, "SendData() called when Socket is not connected");
            onError();
        }
    }
}
