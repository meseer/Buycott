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
public class ScanResultsFragment extends ListFragment {
    private SimpleCursorAdapter mAdapter;
    private OnScanResultsInteractionListener mListener;

    public static ScanResultsFragment newInstance() {
        ScanResultsFragment fragment = new ScanResultsFragment();
        return fragment;
    }

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
                    + " must implement OnFragmentInteractionListener");
        }

        ((MainActivity) activity).onSectionAttached(1);
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        if (barcode != null) {
//            outState.putString(ARG_BARCODE, barcode);
//        }
//    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setRetainInstance(true);

        setEmptyText(getString(R.string.press_scan));

        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.productrow, null,
                new String[] {"_id", "Owner", "Maker", "Title"},
                new int[] { R.id.barcode, R.id.owner, R.id.maker, R.id.title }, 0);
        setListAdapter(mAdapter);
    }

//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        if (savedInstanceState != null && barcode == null) {
//            barcode = savedInstanceState.getString(ARG_BARCODE);
//            getLoaderManager().restartLoader(0, null, this);
//        }
//    }

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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(""+id);
        }
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
        public void onFragmentInteraction(String id);

        void makerNotFound(String barcode);
    }
}
