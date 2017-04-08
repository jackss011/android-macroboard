package com.jackss.ag.macroboard.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.TextView;
import com.jackss.ag.macroboard.R;
import com.jackss.ag.macroboard.ui.views.BottomNavigation;
import com.jackss.ag.macroboard.ui.views.BottomNavigationItem;

/**
 *  Fragment containing keyboard commands.
 *
 *  Commands are divided in sections which can be navigated through BottomNavigation.
 *  Implemented sections:  media controls,  text editing,  custom key macros.
 */

public class ButtonsFragment extends Fragment
{
    private ViewGroup sectionsBox;
    private BottomNavigation bottomNavigation;

    private ViewGroup textSection;
    private ViewGroup mediaSection;
    private ViewGroup customSection;

    private BottomNavigation.OnSelectionListener mNavigationListener = new BottomNavigation.OnSelectionListener()
    {
        @Override
        public void onSelection(int pos, BottomNavigationItem item)
        {
            View selectedSection = null;

            switch (pos)
            {
                case 0:
                    selectedSection = mediaSection;
                    break;

                case 1:
                    selectedSection = textSection;
                    break;

                case 2:
                    selectedSection = customSection;
                    break;
            }

            if(selectedSection != null)
            {
                mediaSection.setVisibility(View.GONE);
                textSection.setVisibility(View.GONE);
                customSection.setVisibility(View.GONE);

                selectedSection.setVisibility(View.VISIBLE);
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
        // inflate fragment layout
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_buttons, container, true);

        // create bottom navigation categories
        bottomNavigation = (BottomNavigation) view.findViewById(R.id.buttons_bottom_nav);
        bottomNavigation.addNavigationItem("Media", null);
        bottomNavigation.addNavigationItem("Text", null);
        bottomNavigation.addNavigationItem("Custom", null);
        bottomNavigation.setOnSelectionListener(mNavigationListener);

        mediaSection = (ViewGroup) inflater.inflate(R.layout.fragment_buttons_media, sectionsBox, true);
        textSection = (ViewGroup) inflater.inflate(R.layout.fragment_buttons_text, sectionsBox, true);

        // temporary custom macros
        customSection = buildCustomSection();

        // add categories to categories container
        sectionsBox = (ViewGroup) view.findViewById(R.id.buttons_container);
        sectionsBox.addView(mediaSection);
        sectionsBox.addView(textSection);
        sectionsBox.addView(customSection);

        return view;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    private ViewGroup buildCustomSection()
    {
        LinearLayout base = new LinearLayout(getContext());
        base.setOrientation(LinearLayout.VERTICAL);

        TextView placeholder = new TextView(getContext());
        placeholder.setText(getString(R.string.custom_category_empty));
        placeholder.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        base.setPadding(0, 40, 0, 0);
        base.addView(placeholder, lp);

        return base;
    }
}
