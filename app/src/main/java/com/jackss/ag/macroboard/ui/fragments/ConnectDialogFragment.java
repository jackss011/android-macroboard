package com.jackss.ag.macroboard.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import com.jackss.ag.macroboard.R;
import com.jackss.ag.macroboard.network.Beacon;
import com.jackss.ag.macroboard.network.SocketInfo;

import java.net.InetAddress;

/**
 *
 */
public class ConnectDialogFragment extends DialogFragment implements Beacon.OnBeaconEventListener
{
    private static final String TAG = "ConnectDialogFragment";

    private Beacon beacon;

    private ListView deviceList;
    private SocketAddressAdapter adapter;

    private Handler mHandler;
    private Runnable updateDevicesTask;

    private OnConnectDialogEventListener dialogEventListener;


// |==============================
// |==>  CLASSES
// |===============================

    public interface OnConnectDialogEventListener
    {
        void onDialogConnectRequest(String address);
    }

    private class UpdateDevicesTask implements Runnable
    {
        @Override
        public void run()
        {
            updateDevices();
            postDeviceUpdate();
        }
    }


// |==============================
// |==>  METHODS
// |===============================

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        beacon = new Beacon();
        beacon.setBeaconListener(this);

        adapter = new SocketAddressAdapter(getActivity());

        mHandler = new Handler();
        updateDevicesTask = new UpdateDevicesTask();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        ViewGroup view = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_connect, null);

        deviceList = (ListView) view.findViewById(R.id.device_list);
        deviceList.setAdapter(adapter);
        deviceList.setChoiceMode(AbsListView.CHOICE_MODE_NONE);
        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                deviceClicked(adapter.getItem(position));
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder .setView(view)
                .setNegativeButton(R.string.cancel, null)
                .setTitle(R.string.connect_to_device);

        return builder.create();
    }

    @Override
    public void onStart()
    {
        super.onStart();

        beacon.startBroadcast();
        postDeviceUpdate();
        updateUI();
    }

    @Override
    public void onStop()
    {
        super.onStop();

        beacon.stopBroadcast();
        stopDeviceUpdate();
    }

    /** Set an event used to list for connect requests */
    public void setDialogEventListener(OnConnectDialogEventListener dialogEventListener)
    {
        this.dialogEventListener = dialogEventListener;
    }

    // Schedule an update in the handler and remove any previous one.
    private void postDeviceUpdate()
    {
        stopDeviceUpdate();
        mHandler.postDelayed(updateDevicesTask, 3000); //TODO: hardcoded
    }

    // Remove any update callback from the handler
    private void stopDeviceUpdate()
    {
        mHandler.removeCallbacks(updateDevicesTask);
    }

    // Called when a device int the list is clicked
    private void deviceClicked(String address)
    {
        if(address == null) throw new AssertionError("Selected null address");

        if(dialogEventListener != null) dialogEventListener.onDialogConnectRequest(address);
        dismiss();
    }

    // Fresh UI update
    private void updateUI()
    {
        adapter.notifyDataSetChanged();
    }

    // Fetch devices from the beacon and add them to the adapter
    private void updateDevices()
    {
        adapter.clear();
        adapter.addAll(beacon.getDevicesAsStrings());
        updateUI();
    }

    @Override
    public void onDeviceFound(SocketInfo socketInfo)
    {
        updateDevices();
    }

    @Override
    public void onFailure()
    {
        throw new AssertionError("Beacon failed");
    }
}
