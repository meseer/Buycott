package com.ignite.boycott.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.ignite.boycott.R;
import com.ignite.boycott.adapter.CategoryAdapter;
import com.ignite.boycott.io.model.Category;
import com.ignite.boycott.io.model.Maker;

/**
 * Created by mdelegan on 08.01.14.
 */
public class MakerListFragment extends ListFragment implements SearchView.OnQueryTextListener {
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    private static final String FILTER = "filterMakers";
    public static final String CATEGORY = "CATEGORY";
    private CategoryAdapter mAdapter;
    private MakerListCallbacks mCallbacks;
    private int mActivatedPosition = ListView.INVALID_POSITION;
    private String mFilter;
    private SearchView mSearchView;
    private Category mCategory;

    public static MakerListFragment newInstance(Category category, String filter) {
        MakerListFragment fragment = new MakerListFragment();
        Bundle args = new Bundle();
        args.putParcelable(CATEGORY, category);
        args.putString(FILTER, filter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mFilter = savedInstanceState.getString(FILTER);
            mCategory = savedInstanceState.getParcelable(CATEGORY);
        } else {
            if (getArguments() != null) {
                mCategory = getArguments().getParcelable(CATEGORY);
                mFilter = getArguments().getString(FILTER);
            }
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
        if (!(activity instanceof MakerListCallbacks)) {
            throw new ClassCastException(activity.toString()
                    + " must implement MakerListCallbacks");
        }
        mCallbacks = (MakerListCallbacks) activity;
        setHasOptionsMenu(true);

        //dirty hack
        //get parent won't work on tablet devices where all 3 fragment will be in single activity
        //FIXME propagate message to root activity
//        ((BoycottActivity) activity.getParent()).onSectionAttached(BoycottActivity.Fragments.CATEGORY);
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

        mAdapter = new CategoryAdapter(getActivity().getApplicationContext(), mCategory);
        mAdapter.setFilter(mFilter);

        setListAdapter(mAdapter);
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
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        mCallbacks.onMakerSelected(mCategory.getMaker(position));
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

    public interface MakerListCallbacks {
        public void onMakerSelected(Maker maker);
    }
}
