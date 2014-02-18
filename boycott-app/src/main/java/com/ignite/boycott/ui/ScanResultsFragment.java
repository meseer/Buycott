package com.ignite.boycott.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.internal.util.Predicate;
import com.ignite.boycott.R;
import com.ignite.boycott.dao.model.Product;
import com.ignite.boycott.dao.model.MakerFrequency;
import com.ignite.boycott.io.model.BoycottList;
import com.ignite.boycott.io.model.Maker;
import com.ignite.boycott.loader.BlacklistLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by meseer on 01.01.14.
 */
public class ScanResultsFragment extends ListFragment {
    public static final String ARG_BARCODE = "BARCODE";
    public static final String ARG_PRODUCT = "PRODUCT";
    public static final String ARG_MAKERS = "MAKERS";
    private static final String ARG_BLACKLIST = "BLACKLIST";
    public static final String MAKER = "Maker";
    public static final String TITLE = "Title";
    public static final String OWNER = "Owner";
    public static final String ID = "_id";
    private ScanResultCallbacks mListener;
    private String barcode;
    private SimpleAdapter mAdapter;
    private List<Map<String, String>> data;
    private BoycottList mBlacklist;

    public ScanResultsFragment() {
        // Required empty public constructor
    }

    private Map<String, String> row(MakerFrequency mf) {
        return row(barcode, null, mf.maker);
    }

    private Map<String, String> row(Product parcelable) {
        return row(parcelable.mBarcode, parcelable.mTitle, parcelable.mMaker);
    }

    private Map<String, String> row(String barcode, String title, String maker) {
        Map<String, String> result = new HashMap<>();
        result.put(ID, barcode);
        result.put(TITLE, title);
        result.put(OWNER, findOwner(maker));
        result.put(MAKER, maker);
        return result;
    }

    private String findOwner(String brand) {
        Maker maker = mBlacklist.findMaker(brand);
        if (maker == null) return null;
        else return maker.getOwner();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (ScanResultCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ScanResultCallbacks");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setRetainInstance(true);
        setEmptyText(getString(R.string.press_scan));

        data = extractData(getArguments());
        if (getArguments() != null) {
            barcode = getArguments().getString(ARG_BARCODE);
            mBlacklist = getArguments().getParcelable(ARG_BLACKLIST);
        }

        mAdapter = new SimpleAdapter(getActivity(), data, R.layout.scan_results_item,
                new String[] {ID, OWNER, MAKER, TITLE},
                new int[] { R.id.barcode, R.id.owner, R.id.maker, R.id.title });

        setListAdapter(mAdapter);
        updateBackground();

    }

    private ArrayList<Map<String, String>> extractData(Bundle args) {
        ArrayList<Map<String, String>> result = new ArrayList<>();
        if (args != null) {
            if (args.containsKey(ARG_PRODUCT)) {
                result.add(row((Product)args.getParcelable(ARG_PRODUCT)));
            }
            if (args.containsKey(ARG_MAKERS)) {
                List<MakerFrequency> mfList = args.getParcelableArrayList(ARG_MAKERS);
                if (mfList != null)
                    for (MakerFrequency mf : mfList) result.add(row(mf));
            }
        }
        return result;
    }

    private boolean isBlacklisted() {
        for (Map<String, String> row : data) {
            if (row.get(OWNER) != null) return true;
        }

        return false;
    }

    public void updateBackground() {
        if (mAdapter != null) {
            if (isBlacklisted()) {
                getListView().setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
            } else {
                getListView().setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public static Fragment newInstance(Product product, BoycottList boycottList) {
        ScanResultsFragment f = new ScanResultsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ScanResultsFragment.ARG_PRODUCT, product);
        args.putString(ScanResultsFragment.ARG_BARCODE, product.mBarcode);
        args.putParcelable(ScanResultsFragment.ARG_BLACKLIST, boycottList);
        f.setArguments(args);
        return f;
    }

    public static Fragment newInstance(ArrayList<MakerFrequency> makers, String mBarcode, BoycottList boycottList) {
        ScanResultsFragment f = new ScanResultsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ScanResultsFragment.ARG_MAKERS, makers);
        args.putString(ScanResultsFragment.ARG_BARCODE, mBarcode);
        args.putParcelable(ScanResultsFragment.ARG_BLACKLIST, boycottList);
        f.setArguments(args);
        return f;
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
    public interface ScanResultCallbacks {
        void reportMistake(String barcode);
    }
}
