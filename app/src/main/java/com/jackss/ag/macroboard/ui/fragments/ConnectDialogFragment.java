package com.jackss.ag.macroboard.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.jackss.ag.macroboard.R;
import com.jackss.ag.macroboard.network.Beacon;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 *
 */
public class ConnectDialogFragment extends DialogFragment implements Beacon.OnBeaconEventListener
{
    private static final String TAG = "ConnectDialogFragment";

    private Beacon beacon;

    private ListView deviceList;
    private SocketAddressAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        beacon = new Beacon();
        beacon.setBeaconListener(this);

        adapter = new SocketAddressAdapter(getActivity());
        adapter.add("Test row");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        ViewGroup view = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_connect, null);

        deviceList = (ListView) view.findViewById(R.id.device_list);
        setupDeviceList();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder .setView(view)
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Log.d(TAG, "Dialog cancel");
                    }
                })
                .setTitle("Connect to device");

        return builder.create();
    }

    private void setupDeviceList()
    {
        deviceList.setAdapter(adapter);
        deviceList.setChoiceMode(AbsListView.CHOICE_MODE_NONE);

        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

            }
        });

    }

    @Override
    public void onStart()
    {
        super.onStart();

        beacon.startBroadcast();
        updateUI();
    }

    @Override
    public void onStop()
    {
        super.onStop();

        beacon.stopBroadcast();
    }

    private void setConnectEnabled(boolean enabled)
    {
        AlertDialog dialog = (AlertDialog) getDialog();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(enabled);
    }

    @Override
    public void onDeviceFound(InetAddress address)
    {
        adapter.clear();
        adapter.addAll(beacon.getDevicesAsStrings());
        updateUI();
    }

    private void updateUI()
    {
        adapter.notifyDataSetChanged();
        setConnectEnabled(adapter.getCount() > 0);
    }

    @Override
    public void onFailure()
    {
        Log.e(TAG, "Failed beacon");
        dismiss();
    }
}
