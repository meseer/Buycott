package com.ignite.boycott;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;

import com.ignite.boycott.dao.HistoryDao;

/**
 * TODO: Add scan date, show only last several days, ability to load more when scrolled to the bottom
 * TODO: History management: delete entry, clear all, etc.
 * Created by mdelegan on 08.01.14.
 */
public class HistoryFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private HistoryCallbacks mListener;
    private SimpleCursorAdapter mAdapter;

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new SimpleCursorAdapter(this.getActivity(), R.layout.productrow, null,
                new String[] { "Maker", "Owner", "Barcode", "ProductName"},
                new int[] { R.id.maker, R.id.owner, R.id.barcode, R.id.title }, 0);
        setEmptyText(getString(R.string.history_hint));

        setListAdapter(mAdapter);
        setListShown(false);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return HistoryDao.createLoader(this.getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }

        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (mAdapter != null) mAdapter.swapCursor(null);
    }

    public interface HistoryCallbacks {

    }
}
