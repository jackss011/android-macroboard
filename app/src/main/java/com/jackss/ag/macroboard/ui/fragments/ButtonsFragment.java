package com.jackss.ag.macroboard.ui.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import com.jackss.ag.macroboard.R;
import com.jackss.ag.macroboard.ui.views.BottomNavigation;
import com.jackss.ag.macroboard.ui.views.BottomNavigationItem;

/**
 *  Fragment containing buttons and sections available to the user.
 *
 *  Buttons can be configured in the settings.
 */

public class ButtonsFragment extends Fragment
{
    private ViewGroup buttonsContainer;
    private BottomNavigation bottomNavigation;

    private ViewGroup textButtons;
    private ViewGroup mediaButtons;
    private TextView customButtons;

    private BottomNavigation.OnSelectionListener mNavigationListener = new BottomNavigation.OnSelectionListener()
    {
        @Override
        public void onSelection(int pos, BottomNavigationItem item)
        {
            switch (pos)
            {
                case 0:
                    mediaButtons.setVisibility(View.VISIBLE);
                    textButtons.setVisibility(View.GONE);
                    customButtons.setVisibility(View.GONE);
                    break;

                case 1:
                    mediaButtons.setVisibility(View.GONE);
                    textButtons.setVisibility(View.VISIBLE);
                    customButtons.setVisibility(View.GONE);
                    break;
                case 2:
                    mediaButtons.setVisibility(View.GONE);
                    textButtons.setVisibility(View.GONE);
                    customButtons.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_buttons, container, true);

        bottomNavigation = (BottomNavigation) view.findViewById(R.id.buttons_bottom_nav);
        bottomNavigation.addNavigationItem("Media", null);
        bottomNavigation.addNavigationItem("Text", null);
        bottomNavigation.addNavigationItem("Custom", null);
        bottomNavigation.setOnSelectionListener(mNavigationListener);

        mediaButtons = (ViewGroup) inflater.inflate(R.layout.media_buttons, buttonsContainer, false);
        textButtons = (ViewGroup) inflater.inflate(R.layout.text_buttons, buttonsContainer, false);

        customButtons = new TextView(getContext());
        customButtons.setText("No custom buttons");
        customButtons.setPadding(20, 20, 20, 20);

        buttonsContainer = (ViewGroup) view.findViewById(R.id.buttons_container);
        buttonsContainer.addView(mediaButtons);
        buttonsContainer.addView(textButtons);
        buttonsContainer.addView(customButtons);

        return view;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }
}
