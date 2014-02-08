package com.ignite.boycott;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.widget.SimpleAdapter;

import com.ignite.boycott.dao.BlacklistDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by meseer on 01.01.14.
 */
public class ScanResultsFragment extends ListFragment {
    public static final String ARG_BARCODE = "BARCODE";
    public static final String ARG_PRODUCT = "PRODUCT";
    public static final String ARG_MAKERS = "MAKERS";
    public static final String MAKER = "Maker";
    public static final String TITLE = "Title";
    public static final String OWNER = "Owner";
    public static final String ID = "_id";
    private ScanResultCallbacks mListener;
    private String barcode;
    private SimpleAdapter mAdapter;
    private List<Map<String, String>> data;
    private BlacklistDao blacklist;

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

    private String findOwner(String maker) {
        //TODO: Do this in background (e.g. use Bolts)
        //TODO: get Owner list by Maker code, not by name, to include sub-contractors for known products
        return blacklist.getOwner(maker);
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (barcode != null) {
            outState.putString(ARG_BARCODE, barcode);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setRetainInstance(true);
        setEmptyText(getString(R.string.press_scan));

        blacklist = new BlacklistDao(getActivity());
        data = extractData(getArguments());
        if (getArguments() != null) {
            barcode = getArguments().getString(ARG_BARCODE);
        }

        mAdapter = new SimpleAdapter(getActivity(), data, R.layout.productrow,
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

    public static Fragment newInstance(Product product) {
        ScanResultsFragment f = new ScanResultsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ScanResultsFragment.ARG_PRODUCT, product);
        args.putString(ScanResultsFragment.ARG_BARCODE, product.mBarcode);
        f.setArguments(args);
        return f;
    }

    public static Fragment newInstance(ArrayList<MakerFrequency> makers, String mBarcode) {
        ScanResultsFragment f = new ScanResultsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ScanResultsFragment.ARG_MAKERS, makers);
        args.putString(ScanResultsFragment.ARG_BARCODE, mBarcode);
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
