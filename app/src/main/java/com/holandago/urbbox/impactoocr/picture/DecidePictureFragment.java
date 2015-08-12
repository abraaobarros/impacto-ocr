package com.holandago.urbbox.impactoocr.picture;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.holandago.urbbox.impactoocr.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnPictureFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DecidePictureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DecidePictureFragment extends Fragment {

    // the fragment initialization parameters
    private static final String IMAGE_REPRESENTATION = "image_representation";

    private String mImageRepresantation;

    private OnPictureFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param imageRepresentation is the mImageRepresentation.
     * @return A new instance of fragment DecidePictureFragment.
     */
    public static DecidePictureFragment newInstance(String imageRepresentation) {
        DecidePictureFragment fragment = new DecidePictureFragment();
        Bundle args = new Bundle();
        args.putString(IMAGE_REPRESENTATION, imageRepresentation);
        fragment.setArguments(args);
        return fragment;
    }

    public DecidePictureFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImageRepresantation = getArguments().getString(IMAGE_REPRESENTATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_decide_picture, container, false);
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
