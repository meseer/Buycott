package com.ignite.boycott;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.ignite.boycott.adapter.BoycottListAdapter;
import com.ignite.boycott.io.model.BoycottList;
import com.ignite.boycott.io.model.Maker;
import com.ignite.boycott.loader.BlacklistLoader;

/**
 * Created by mdelegan on 08.01.14.
 */
public class CatalogFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<BoycottList>, SearchView.OnQueryTextListener {
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    private static final String FILTER = "filter";
    private BoycottListAdapter mAdapter;
    private CatalogCallbacks mCallbacks;
    private int mActivatedPosition = ListView.INVALID_POSITION;
    private String mFilter;
    private SearchView mSearchView;
    private BoycottList mBoycottList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mFilter = savedInstanceState.getString(FILTER);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
        outState.putString(FILTER, mFilter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof CatalogCallbacks)) {
            throw new ClassCastException(activity.toString()
                    + " must implement MakerDetailsCallback");
        }
        mCallbacks = (CatalogCallbacks) activity;
        setHasOptionsMenu(true);

        ((MainActivity) activity).onSectionAttached(MainActivity.Fragments.CATALOG);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.catalog_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        if (!TextUtils.isEmpty(mFilter)) {
            //TODO: Show overlayed hint that filtering is enabled with option to disable it
            mSearchView.setQuery(mFilter, false);
            Toast.makeText(this.getActivity(), getString(R.string.filtering_by, mFilter), Toast.LENGTH_SHORT).show();
        }

        mSearchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        mFilter = s;
        mAdapter.setFilter(s);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                getActivity().onSearchRequested();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.setRetainInstance(true);

        mAdapter = new BoycottListAdapter(getActivity().getApplicationContext(), null);
        mAdapter.setFilter(mFilter);

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
    public Loader<BoycottList> onCreateLoader(int i, Bundle bundle) {
        //singleton - doesn't work
//        return BlacklistLoader.instance(getActivity().getApplicationContext());
        return new BlacklistLoader(getActivity().getApplicationContext());
    }

    @Override
    public void onLoadFinished(Loader<BoycottList> cursorLoader, BoycottList boycottList) {
        mBoycottList = boycottList;
        mAdapter.swapBoycotList(boycottList);

        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<BoycottList> objectLoader) {
        mAdapter.swapBoycotList(null);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        mCallbacks.onMakerSelected(mBoycottList.getItem(position));
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
        public void onMakerSelected(Maker maker);
    }
}
