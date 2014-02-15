package com.ignite.boycott.ui;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ignite.boycott.R;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 *
 */
public class SlidingPaneFragment extends Fragment {
    private SlidingPaneLayout pane;

    public SlidingPaneFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sliding_pane, container, false);

        pane = (SlidingPaneLayout)view.findViewById(R.id.maker_list_sliding);
        pane.setPanelSlideListener(new PaneListener());

        return view;
    }

    private class PaneListener implements SlidingPaneLayout.PanelSlideListener {
        @Override
        public void onPanelSlide(View view, float v) {
            Toast.makeText(getActivity().getApplicationContext(), "Panel slide", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPanelOpened(View view) {
            Toast.makeText(getActivity().getApplicationContext(), "Panel open", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPanelClosed(View view) {
            Toast.makeText(getActivity().getApplicationContext(), "Panel close", Toast.LENGTH_SHORT).show();
        }
    }

}
