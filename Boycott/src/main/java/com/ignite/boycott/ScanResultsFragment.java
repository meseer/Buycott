package com.ignite.boycott;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ListView;

import com.ignite.buycott.R;

/**
 * Created by meseer on 01.01.14.
 */
//TODO: Rollback MakerNotFoundActivity to Fragment and use it here as one of the options (greed, red, not found)
//TODO: Use this ScanResultFragment as on of the options
public class ScanResultsFragment extends ListFragment {
    private static final String ARG_BARCODE = "BARCODE";
    private SimpleCursorAdapter mAdapter;
    private OnScanResultsInteractionListener mListener;
    private String barcode;
    private Cursor mCursor;

    public ScanResultsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnScanResultsInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnScanResultsInteractionListener");
        }

        if (activity instanceof MainActivity)
            ((MainActivity) activity).onSectionAttached(1);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (barcode != null) {
            outState.putString(ARG_BARCODE, barcode);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setRetainInstance(true);

        setEmptyText(getString(R.string.press_scan));

        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.productrow, null,
                new String[] {"_id", "Owner", "Maker", "Title"},
                new int[] { R.id.barcode, R.id.owner, R.id.maker, R.id.title }, 0);
        setListAdapter(mAdapter);
        if (mCursor != null) onScanResult(mCursor);
    }

    private boolean isBlacklisted(Cursor product) {
        if (product.getCount() == 0) return false;

        int makerIndex = product.getColumnIndexOrThrow("Owner");
        product.moveToFirst();
        do {
            if (product.getString(makerIndex) != null) return true;
        } while (product.moveToNext());

        return false;
    }

    public void onScanResult(Cursor cursor) {
        if (mAdapter != null) {
            mAdapter.swapCursor(cursor);
            if (isBlacklisted(cursor)) {
                getListView().setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
            } else {
                getListView().setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
            }
            if (isResumed()) {
                setListShown(true);
            } else {
                setListShownNoAnimation(true);
            }
        } else {
            mCursor = cursor;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnScanResultsInteractionListener {
        void reportMistake(String barcode);
    }
}
