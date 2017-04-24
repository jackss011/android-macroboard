package com.jackss.ag.macroboard.ui.fragments;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 *
 */
public class SelectArrayAdapter<T> extends ArrayAdapter<T>
{
    public SelectArrayAdapter(@NonNull Context context, @LayoutRes int resource)
    {
        super(context, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        View view = super.getView(position, convertView, parent);

        return view;
    }
}
