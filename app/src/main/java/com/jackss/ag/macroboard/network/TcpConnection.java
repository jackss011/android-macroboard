package com.jackss.ag.macroboard.network;

import android.os.*;
import android.util.Log;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Manages a TCP connection
 *
 */
public class TcpConnection
{
    private static final String TAG = "TcpConnection";


    // Used in handler messages what field
    private static final int MSG_WHAT_DATA = 200;
    private static final int MSG_WHAT_ERROR = 400;


    // TCP connection socket
    private Socket clientSocket;

    // AsyncTask used to produce a connected socket
    private ConnectionTask connectionTask;

    // Thread listening for input_stream data
    private Thread inputThread;

    // Printer used to send data to the output_stream
    private PrintWriter outputPrinter;


    // Port used for the tcp connection
    private int port;

    // Listener used to listen for connection changes
    private OnTcpListener listener;



    //
    // ========== CONSTRUCTOR ===========
    //

    public TcpConnection(int port)
    {
        this.port = port;
    }


    //
    // ========== INNER CLASSES ===========
    //

    public interface OnTcpListener
    {
        void onData(String data);

        void onConnectionState(int state);
    }

    /**
     * AsyncTask used to produce a connected TCP socket
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

    /**
     * Runnable running on a separate thread listening for TCP input stream data.
     * Data is sent to main_thread via Handler(main_looper)
     */
    private class InputHandler  implements Runnable
    {
        private Handler mainHandler;
        private final Socket clientSocket;

        InputHandler(Socket socket)
        {
            this.clientSocket = socket;
        }
        
        private void createMainHandler()
        {
            mainHandler = new Handler(Looper.getMainLooper())
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
                            onError();
                            break;
                    }
                }
            };
        }

        @Override
        public void run()
        {
            createMainHandler();

            try
            {
                BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                Log.i(TAG, "Started input thread");

                while(true)
                {
                    if(Thread.interrupted()) break;
                    String readData = br.readLine();
                    if(Thread.interrupted()) break;

                    if(readData != null)
                    {
                        mainHandler.obtainMessage(MSG_WHAT_DATA, readData).sendToTarget();
                    }
                    else 
                    {
                        mainHandler.sendEmptyMessage(MSG_WHAT_ERROR);
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if(!Thread.interrupted())
                    mainHandler.sendEmptyMessage(MSG_WHAT_ERROR);
                else
                    Log.i(TAG, "Skipping MGS_WHAT_ERROR since input_thread has been interrupted");
            }
        }
    }



    //
    // ========== METHODS ===========
    //

    private void onConnectionResult(Socket socket)
    {
        clientSocket = socket;
        connectionTask = null;

        if(isConnected())
        {
            onConnected();
        }
        else Log.e(TAG, "Connection result: failed");
    }

    private void onConnected()
    {
        Log.i(TAG, "Connected to: " + clientSocket.getInetAddress().getHostAddress());

        try
        {
            clientSocket.setKeepAlive(true);

            outputPrinter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())), true);
            
            if(inputThread != null) inputThread.interrupt();
            inputThread = new Thread(new InputHandler(clientSocket));
            inputThread.start();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void onError()
    {
        Log.e(TAG, "Connection error");
    }

    private void onDataReceived(String data)
    {
        Log.v(TAG, "Tcp data: " + data);
    }

    private boolean isConnecting()
    {
        return  connectionTask != null                                          // valid ref
                && connectionTask.getStatus() == AsyncTask.Status.RUNNING       // task running
                && !connectionTask.isCancelled();                               // task not cancelled
    }

    private boolean isConnected()
    {
        return clientSocket != null && clientSocket.isConnected();
    }

    public void accept()
    {
        if(!isConnecting())
        {
            connectionTask = new ConnectionTask();
            connectionTask.execute(port);
            Log.i(TAG, "Connecting...");
        }
    }

    public void reset()
    {
        Log.v(TAG, "Connection reset");

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
        outputPrinter = null;

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
    }

    public void sendData(String data)
    {
        if(isConnected())
        {
            if(outputPrinter != null)
                outputPrinter.println(data);
            else
                throw new AssertionError("outputPrinter is null while the socket is connected!");
        }
        else Log.e(TAG, "socket is not connected");
    }

    public void setTcpListener(OnTcpListener listener)
    {
        this.listener = listener;
    }
}

