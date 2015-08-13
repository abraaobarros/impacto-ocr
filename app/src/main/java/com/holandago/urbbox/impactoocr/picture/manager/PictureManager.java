package com.holandago.urbbox.impactoocr.picture.manager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

/**
 * Created by razu on 12/08/15.
 */
public class PictureManager {

    PictureManagerDelegate mDelegate;

    public PictureManager(PictureManagerDelegate delegate){
        mDelegate = delegate;
    }

    public void onCameraResultOk(Intent data){
        Bundle extras = data.getExtras();
        Bitmap imageBitmap = (Bitmap) extras.get("data");
    }

    // ### PictureManagerDelegate methods

    public void launchCameraIntent(){
        mDelegate.dispatchTakePictureIntent();
    }



}
