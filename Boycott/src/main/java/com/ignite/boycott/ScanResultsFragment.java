package com.ignite.boycott;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;
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

        getLoaderManager().initLoader(0, null, this);
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
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String sql = "select Barcode as _id, m.Maker as Maker, '" + getString(R.string.n_a) + "' as Title, " +
                "NULL as Type, NULL as Owner, NULL as Affiliation \n" +
                "from makers m where 1 = 2 and MakerCode = ? and CountryCode = ?";

        Integer countryCode = 0;
        Integer makerCode = 0;

        if (barcode != null) {
            countryCode = Integer.valueOf(barcode.substring(0,3));
            makerCode = Integer.valueOf(barcode.substring(3, 8));

            sql = "select Barcode as _id, m.Maker as Maker, '" + getString(R.string.n_a) + "' as Title, Type, Owner, Affiliation \n" +
                    "from makers m left outer join blacklist b on m.Maker = b.Maker\n" +
                    "where MakerCode = ? and m.Maker <> '' and CountryCode = ?\n" +
                    "group by m.Maker\n" +
                    "having length(Barcode) = 13\n" +
                    "order by Owner desc";
        }

        return new SQLiteCursorLoader(this.getActivity(), mDb, sql,
                new String[] { Integer.toString(makerCode), Integer.toString(countryCode) });
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
        //TODO: Process case when no data found in the database (cursor is empty)
        if (cursor.getCount() > 0) {
            if (isBlacklisted(cursor)) {
                toast(R.string.blacklisted);
                getListView().setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
            } else {
                toast(R.string.clean);
                getListView().setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
            }
        } else {
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
