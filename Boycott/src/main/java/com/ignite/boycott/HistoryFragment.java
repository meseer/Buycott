package com.ignite.boycott;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;

import com.ignite.buycott.R;

/**
 * Created by mdelegan on 08.01.14.
 */
public class HistoryFragment extends ListFragment {
    private HistoryCallbacks mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (HistoryCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement HistoryCallbacks");
        }

        ((MainActivity) activity).onSectionAttached(MainActivity.Fragments.HISTORY);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_2, new String[0]));
        setEmptyText(getString(R.string.history_hint));
    }

    public interface HistoryCallbacks {

    }
}
