package com.jackss.ag.macroboard.ui.fragments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.jackss.ag.macroboard.R;
import com.jackss.ag.macroboard.network.wifi.SocketInfo;
import com.jackss.ag.macroboard.utils.StaticLibrary;


/**
 *
 */
class SocketAddressAdapter extends ArrayAdapter<SocketInfo>
{
    SocketAddressAdapter(@NonNull Context context)//, @LayoutRes int resource)
    {
        super(context, 0);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        View view = convertView == null
                ? LayoutInflater.from(getContext()).inflate(R.layout.row_connect_device, parent, false)
                : convertView;

        TextView label = (TextView) view.findViewById(R.id.connect_device_text);
        SocketInfo item = getItem(position);
        label.setText(item != null ? StaticLibrary.sanitizeHostName(item.hostName) : "Error");

        return view;
    }
}
