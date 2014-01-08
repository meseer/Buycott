package com.ignite.boycott;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;

import com.commonsware.cwac.loaderex.acl.SQLiteCursorLoader;

/**
 * Created by mdelegan on 08.01.14.
 */
public class CatalogFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private int firstViewablePosition;
    private final static String POSITION_TAG = "Catalog Position";
    private SimpleCursorAdapter mAdapter;
    private Makers mDb;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mDb = new Makers(this.getActivity());

        this.setRetainInstance(true);

        if (savedInstanceState != null && savedInstanceState.containsKey(POSITION_TAG)) {
            this.firstViewablePosition = savedInstanceState.getInt(POSITION_TAG);
        }

        getLoaderManager().initLoader(0, null, this);

        mAdapter = new SimpleCursorAdapter(this.getActivity(), android.R.layout.simple_list_item_2, null,
                new String[] {"_id", "Brand", "Owner"}, new int[] {  } , 0);

        setListAdapter(mAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION_TAG, getListView().getFirstVisiblePosition());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String sql = "select Maker, Owner from blacklist";

        return new SQLiteCursorLoader(this.getActivity(), mDb, sql, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.swapCursor(cursor);

        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> objectLoader) {
        mAdapter.swapCursor(null);
    }
}
