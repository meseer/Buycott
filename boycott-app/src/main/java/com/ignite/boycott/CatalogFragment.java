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
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    private SimpleCursorAdapter mAdapter;
    private BlacklistDao blacklistDao;
    private CatalogCallbacks mCallbacks;
    private int mActivatedPosition = ListView.INVALID_POSITION;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof CatalogCallbacks)) {
            throw new ClassCastException(activity.toString()
                    + " must implement MakerDetailsCallback");
        }
        mCallbacks = (CatalogCallbacks) activity;

        ((MainActivity) activity).onSectionAttached(MainActivity.Fragments.CATALOG);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        blacklistDao = new BlacklistDao(this.getActivity());

        this.setRetainInstance(true);

        mAdapter = new SimpleCursorAdapter(this.getActivity(), android.R.layout.simple_list_item_2, null,
                new String[] { "Maker", "Owner"}, new int[] { android.R.id.text1, android.R.id.text2 } , 0);

        setListAdapter(mAdapter);
        setListShown(false);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return BlacklistDao.newHistoryLoader(this.getActivity());
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
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        mCallbacks.onMakerSelected(id);
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }
    
    public interface CatalogCallbacks {
        public void onMakerSelected(long blacklistId);
    }
}
