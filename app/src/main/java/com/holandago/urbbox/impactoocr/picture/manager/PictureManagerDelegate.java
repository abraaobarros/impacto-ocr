package com.holandago.urbbox.impactoocr.picture.manager;

/**
 * Created by razu on 12/08/15.
 */
public interface PictureManagerDelegate {

    void dispatchTakePictureIntent();

    void launchDecidePictureFragment(String encodedImage);
}
