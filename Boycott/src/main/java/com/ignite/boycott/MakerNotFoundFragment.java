package com.ignite.boycott;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ignite.buycott.R;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MakerNotFoundFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MakerNotFoundFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class MakerNotFoundFragment extends Fragment implements View.OnClickListener {
    private static final String BARCODE = "barcode";


    private OnFragmentInteractionListener mListener;
    private String barcode;
    private Button mNotifyButton;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @param barcode Parameter 1.
     * @return A new instance of fragment MakerNotFoundFragment.
     */
    public static MakerNotFoundFragment newInstance(String barcode) {
        MakerNotFoundFragment fragment = new MakerNotFoundFragment();
        Bundle args = new Bundle();
        args.putString(BARCODE, barcode);
        fragment.setArguments(args);
        return fragment;
    }
    public MakerNotFoundFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            barcode = getArguments().getString(BARCODE);
        }

        mNotifyButton = (Button)getView().findViewById(R.id.maker_notify);
        mNotifyButton.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_maker_not_found, container, false);
    }

    @Override
    public void onClick(View v) {
        mListener.reportMakerNotFound(barcode);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
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
    public interface OnFragmentInteractionListener {
        void reportMakerNotFound(String barcode);
    }

}
