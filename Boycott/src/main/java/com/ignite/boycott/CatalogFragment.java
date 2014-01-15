package com.ignite.boycott;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ListView;

import com.commonsware.cwac.loaderex.acl.SQLiteCursorLoader;

/**
 * Created by mdelegan on 08.01.14.
 */
public class CatalogFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String SELECTED_ITEM_ID = "SelectedItemId";
    private SimpleCursorAdapter mAdapter;
    private Makers mDb;
    private CatalogInteractionListener mListener;
    private long mSelectedItemId;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(SELECTED_ITEM_ID, mSelectedItemId);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (CatalogInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        ((MainActivity) activity).onSectionAttached(2);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mDb = Makers.instance(this.getActivity());

        this.setRetainInstance(true);

        mAdapter = new SimpleCursorAdapter(this.getActivity(), android.R.layout.simple_list_item_2, null,
                new String[] { "Maker", "Owner"}, new int[] { android.R.id.text1, android.R.id.text2 } , 0);

        setListAdapter(mAdapter);
        setListShown(false);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_ITEM_ID)) {
            mSelectedItemId = savedInstanceState.getLong(SELECTED_ITEM_ID);
            mListener.onMakerSelected(mSelectedItemId);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String sql = "select _id, Maker, Owner from blacklist";

        return new SQLiteCursorLoader(this.getActivity(), mDb, sql, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }

        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> objectLoader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        mListener.onMakerSelected(id);
        mSelectedItemId = id;
    }

    public interface CatalogInteractionListener {
        public void onMakerSelected(long blacklistId);
    }
}
