package com.holandago.urbbox.impactoocr.picture.manager;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by razu on 13/08/15.
 */
public class PictureCommunicator {

    Context mContext;
    PictureCommunicatorDelegate mDelegate;

    public PictureCommunicator(PictureCommunicatorDelegate delegate){
        mDelegate = delegate;
    }

    public void sendPicture(Bitmap picture){

    }

    public void sentPicture(){
        
    }

    public void sendingFailedWithError(){

    }
}
