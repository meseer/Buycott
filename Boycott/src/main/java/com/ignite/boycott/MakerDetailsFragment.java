package com.ignite.boycott;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ignite.boycott.R;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.ignite.boycott.MakerDetailsFragment.MakerDetailsCallback} interface
 * to handle interaction events.
 * Use the {@link MakerDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
//TODO: Ask confirmation on back/up navigation
public class MakerDetailsFragment extends Fragment {
    public static final String MAKER_NAME = "MakerName";
    public static final String OWNER_NAME = "OwnerName";
    public static final String TYPE_NAME = "TypeName";
    public static final String AFFILIATION_NAME = "AffiliationName";
    public static final String ALTERNATIVE_NAME = "AlternativeName";

    private BlacklistedMaker maker;

    private MakerDetailsCallback mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param maker Blacklisted maker
     * @return A new instance of fragment MakerDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MakerDetailsFragment newInstance(BlacklistedMaker maker) {
        MakerDetailsFragment fragment = new MakerDetailsFragment();
        Bundle args = new Bundle();
        args.putString(MAKER_NAME, maker.maker);
        args.putString(OWNER_NAME, maker.owner);
        args.putString(TYPE_NAME, maker.type);
        args.putString(AFFILIATION_NAME, maker.affiliation);
        args.putString(ALTERNATIVE_NAME, maker.alternative);
        fragment.setArguments(args);
        return fragment;
    }

    public MakerDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            maker = new BlacklistedMaker(
                    getArguments().getString(MAKER_NAME),
                    getArguments().getString(OWNER_NAME),
                    getArguments().getString(TYPE_NAME),
                    getArguments().getString(AFFILIATION_NAME),
                    getArguments().getString(ALTERNATIVE_NAME)
            );
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_maker_details, container, false);
        ((TextView) view.findViewById(R.id.makerName)).setText(maker.maker);
        ((TextView) view.findViewById(R.id.ownerName)).setText(maker.owner);
        ((TextView) view.findViewById(R.id.typeName)).setText(maker.type);
        ((TextView) view.findViewById(R.id.affiliationName)).setText(maker.affiliation);
        if (TextUtils.isEmpty(maker.alternative)) {
            //TODO: hide label or whole row
//            ((TextView)view.findViewById(R.id.alternativeLabel)).setTex
        } else {
            ((TextView) view.findViewById(R.id.alternativeName)).setText(maker.alternative);
        }
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (MakerDetailsCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement MakerDetailsCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface MakerDetailsCallback {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
