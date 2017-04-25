package com.jackss.ag.macroboard.ui.fragments;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.jackss.ag.macroboard.R;


/**
 *
 */
class SocketAddressAdapter extends ArrayAdapter<String>
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
        label.setText(getItem(position));

        return view;
    }
}