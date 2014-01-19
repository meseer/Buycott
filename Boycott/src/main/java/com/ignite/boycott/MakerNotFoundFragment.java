package com.ignite.boycott;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ignite.buycott.R;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.ignite.boycott.MakerNotFoundFragment.MakerNotFoundCallbacks} interface
 * to handle interaction events.
 * Use the {@link MakerNotFoundFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class MakerNotFoundFragment extends Fragment implements View.OnClickListener {
    private static final String BARCODE = "barcode";


    private MakerNotFoundCallbacks mListener;
    private String barcode;
    private Button mNotifyButton;
    private EditText mBarcodeEditBox;
    private EditText mMakerEditBox;
    private EditText mProductEditBox;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_maker_not_found, container, false);
        mNotifyButton = (Button)view.findViewById(R.id.maker_notify);
        mNotifyButton.setOnClickListener(this);

        mBarcodeEditBox = (EditText)view.findViewById(R.id.not_found_barcode);
        mBarcodeEditBox.setText(barcode);

        mMakerEditBox = (EditText)view.findViewById(R.id.not_found_maker);
        mProductEditBox = (EditText)view.findViewById(R.id.not_found_product);

        return view;
    }

    @Override
    public void onClick(View v) {
        mListener.reportMakerNotFound(barcode,
                mMakerEditBox.getText().toString(),
                mProductEditBox.getText().toString());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (MakerNotFoundCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement MakerNotFoundCallbacks");
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
    public interface MakerNotFoundCallbacks {
        void reportMakerNotFound(String barcode, String maker, String productName);
    }
}
