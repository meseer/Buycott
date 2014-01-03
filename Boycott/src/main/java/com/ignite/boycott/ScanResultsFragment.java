package com.ignite.boycott;

import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.ignite.buycott.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by meseer on 01.01.14.
 */
public class ScanResultsFragment extends ListFragment {
    /**
     * The fragment argument representing the scanned barcode value.
     */
    private static final String ARG_BARCODE = "barcode";
    private String barcode;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        setRetainInstance(true);

        if (savedInstanceState != null && barcode == null) {
            barcode = savedInstanceState.getString(ARG_BARCODE);
        }

        if (getListAdapter() == null) {
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), R.layout.productrow, null,
                    new String[] {"_id", "MakerCode", "Maker", "Title"},
                    new int[] { R.id.barcode, R.id.makercode, R.id.maker, R.id.title });
            setListAdapter(adapter);
        }

        if (barcode != null) {
            //TODO: Load data in background
            //TODO: Cache results, don't hit database on rotate (use custom adapter?)
            processBarcode();
        }
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (barcode != null) {
            outState.putString(ARG_BARCODE, barcode);
        }
    }

    private void processBarcode() {
        if (barcode == null || this.getActivity() == null) return;

        try {
            Makers makersDb = new Makers(this.getActivity());
            Cursor product = makersDb.getProduct(barcode);
            if (product.getCount() == 0)
                product = makersDb.getMaker(barcode);

            if (product.getCount() > 0) {
                //TODO: this is quick and dirty, use LoaderManager with a CursorLoader for proper implementation

                ((SimpleCursorAdapter)getListAdapter()).changeCursor(product);

                if (isBlacklisted(makersDb, product)) {
                    toast(R.string.blacklisted);
                    //causes error: content view not yet created
//                    getListView().setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                } else {
                    toast(R.string.clean);
//                    getListView().setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                }
            } else {
                toast(R.string.maker_not_found);
            }
        } catch (Exception e) {
            Toast.makeText(this.getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            throw e;
        }
    }

    private boolean isBlacklisted(Makers makersDb, Cursor product) {
        List<String> makers = getMakerList(product);

        Cursor blacklisted = makersDb.getBlacklisted(makers);
        return blacklisted.getCount() > 0;
    }

    private List<String> getMakerList(Cursor product) {
        product.moveToFirst();

        int makerIndex = product.getColumnIndexOrThrow("Maker");
        List<String> makers = new ArrayList<>();
        do {
            makers.add(product.getString(makerIndex));
        } while (product.moveToNext());
        return makers;
    }

    private void toast(int stringId) {
        Toast.makeText(this.getActivity(), stringId, Toast.LENGTH_LONG).show();
    }

    public void onScanResult(String code) {
        this.barcode = code;
        processBarcode();
    }
}
