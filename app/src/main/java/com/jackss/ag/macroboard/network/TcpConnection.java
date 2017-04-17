package com.jackss.ag.macroboard.network;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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

    private static final int WHAT_DATA = 200;
    private static final int WHAT_ERROR = 400;

    private Socket clientSocket;

    private OnTcpListener listener;

    private ConnectionTask connectionTask;

    private Thread inputThread;

    private PrintWriter outputPrinter;


    public interface OnTcpListener
    {
        void onData(String data);

        void onConnectionState(int state);
    }

    class ConnectionTask extends AsyncTask<Integer, Void, Socket>
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
                    // MAIN THREAD HERE
                    switch(msg.what)
                    {
                        case WHAT_DATA:
                            String data = (String) msg.obj;
                            Log.v(TAG, "On main thread data: " + data);
                            break;

                        case WHAT_ERROR:
                            Log.e(TAG, "Tcp connection error");
                            Log.v(TAG, String.valueOf(clientSocket.isConnected()));
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
                Log.v(TAG, "Started input thread");

                while(true)
                {
                    if(Thread.interrupted()) break;

                    String readData = br.readLine();
                    if(readData != null)
                    {
                        Log.v(TAG, "Read line: " + readData);
                        mainHandler.obtainMessage(0, readData).sendToTarget();
                    }
                    else 
                    {
                        mainHandler.sendEmptyMessage(WHAT_ERROR);
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                Log.v(TAG, String.valueOf(clientSocket.isClosed()));
                mainHandler.sendEmptyMessage(WHAT_ERROR);
            }
        }
    }


    private void onConnectionResult(Socket socket)
    {
        clientSocket = socket;
        connectionTask = null;

        if(isConnected())
        {
            onConnected();
        }
        else Log.e(TAG, "Connection failed");
    }

    private void onConnected()
    {
        Log.v(TAG, "Connected to: " + clientSocket.getInetAddress().getHostAddress());

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

    private boolean isConnected()
    {
        return clientSocket != null && clientSocket.isConnected();
    }

    private boolean isConnecting()
    {
        return  connectionTask != null                                          // valid ref
                && connectionTask.getStatus() == AsyncTask.Status.RUNNING       // task running
                && !connectionTask.isCancelled();                               // task not cancelled
    }

    public void setTcpListener(OnTcpListener listener)
    {
        this.listener = listener;
    }

    public void accept(int port)
    {
        if(!isConnecting())
        {
            connectionTask = new ConnectionTask();
            connectionTask.execute(port);
            Log.v(TAG, "Task started");
        }
    }

    public void disconnect()
    {

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
}










// class OutputHandler implements Runnable
// {
//     final Socket socket;
//
//     Handler handler;
//
//     OutputHandler(Socket socket)
//     {
//         this.socket = socket;
//     }
//
//     @Override
//     public void run()
//     {
//         Looper.prepare();
//
//         handler = new Handler()
//         {
//             @Override
//             public void handleMessage(Message msg)
//             {
//
//             }
//         };
//
//
//
//
//         Looper.loop();
//     }
// }
