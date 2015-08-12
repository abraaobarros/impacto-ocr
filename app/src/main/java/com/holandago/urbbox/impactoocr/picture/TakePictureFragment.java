package com.holandago.urbbox.impactoocr.picture;

import android.app.Activity;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.holandago.urbbox.impactoocr.MainActivity;
import com.holandago.urbbox.impactoocr.R;


/**
 * A placeholder fragment containing a simple view.
 */
public class TakePictureFragment extends Fragment {

    private OnPictureFragmentInteractionListener mListener;

    private LinearLayout mCameraLayout;

    public TakePictureFragment() {
        // Required empty public constructor
    }

    public static TakePictureFragment newInstance(){
        TakePictureFragment fragment = new TakePictureFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_take_picture, container, false);
    }

    /**
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        mCameraLayout = (LinearLayout) view.findViewById(R.id.camera_layout);

        mCameraLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCameraButtonPressed(MainActivity.CAMERA_CLICK_URI);
            }
        });
    }

    public void onCameraButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnPictureFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnPictureFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
