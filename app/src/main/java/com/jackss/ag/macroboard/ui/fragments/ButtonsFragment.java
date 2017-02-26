package com.jackss.ag.macroboard.ui.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jackss.ag.macroboard.R;

/**
 *
 */

public class ButtonsFragment extends Fragment
{
    public ButtonsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_buttons, container, true);
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }
}
