package com.holandago.urbbox.impactoocr.picture.manager;

import android.content.ContentResolver;
import android.content.Intent;

import com.google.gson.JsonObject;

/**
 * Created by razu on 12/08/15.
 */
public interface PictureManagerDelegate {

    void dispatchTakePictureIntent();

    void launchDecidePictureFragment(String encodedImage);

    void sentPicture(JsonObject result);

    void dispatchTakePictureIntentWithFile();

    void sendingFailedWithError(Exception e);

    void dispatchGalleryIntent();

    void performCrop(Intent data);

    ContentResolver getContentResolver();
}
