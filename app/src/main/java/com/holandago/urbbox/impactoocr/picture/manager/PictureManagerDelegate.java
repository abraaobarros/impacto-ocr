package com.holandago.urbbox.impactoocr.picture.manager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;

import com.google.gson.JsonObject;

import java.io.File;

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

    void performCrop(File f);

    void launchQrCodeIntent();

    void savePictureToGallery(Intent mediaScanIntent);

    Context getContext();

    ContentResolver getContentResolver();
}
