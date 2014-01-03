package com.ignite.boycott;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ignite.buycott.R;

/**
 * Created by meseer on 01.01.14.
 */
public class ScanResultsFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * The fragment argument representing the scanned barcode value.
     */
    private static final String ARG_BARCODE = "barcode";
    private String barcode;

    private SimpleCursorAdapter mAdapter;
    private Makers mDb;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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

        mDb = new Makers(this.getActivity());

        setRetainInstance(true);

        setEmptyText(getString(R.string.press_scan));

        if (savedInstanceState != null && barcode == null) {
            barcode = savedInstanceState.getString(ARG_BARCODE);
        }

        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.productrow, null,
                new String[] {"_id", "Owner", "Maker", "Title"},
                new int[] { R.id.barcode, R.id.owner, R.id.maker, R.id.title }, 0);
        setListAdapter(mAdapter);

//        getLoaderManager().initLoader(0, null, this);

        if (barcode != null) {
            //TODO: Load data in background
            //TODO: Cache results, don't hit database on rotate (use custom adapter?)
            processBarcode();
        }

    }

    private void processBarcode() {
        if (barcode == null || this.getActivity() == null) return;

        try {
            Cursor cursor = mDb.getProductOrMakers(barcode);

            if (cursor.getCount() > 0) {
                //TODO: this is quick and dirty, use LoaderManager with a CursorLoader for proper implementation
                mAdapter.swapCursor(cursor);

                if (isBlacklisted(cursor)) {
                    toast(R.string.blacklisted);
                    //causes error: content view not yet created
                    getListView().setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                } else {
                    toast(R.string.clean);
                    getListView().setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                }
            } else {
                toast(R.string.maker_not_found);
            }
        } catch (Exception e) {
            Toast.makeText(this.getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("ScanResults", "Failed to process barcode " + barcode, e);
        }
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

    private void toast(int stringId) {
        Toast.makeText(this.getActivity(), stringId, Toast.LENGTH_LONG).show();
    }

    public void onScanResult(String code) {
        this.barcode = code;
//        getLoaderManager().restartLoader(0, null, this);
        processBarcode();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
        //TODO: Process case when no data found in the database (cursor is empty)
        if (cursor.getCount() == 0) {
            toast(R.string.maker_not_found);
        }

        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }
}
