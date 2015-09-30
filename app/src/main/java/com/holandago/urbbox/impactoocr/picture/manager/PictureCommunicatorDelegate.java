package com.holandago.urbbox.impactoocr.picture.manager;

import android.content.Context;

import com.google.gson.JsonObject;

/**
 * Created by razu on 13/08/15.
 */
public interface PictureCommunicatorDelegate {

    void sentPicture(JsonObject result);

    void sendingFailedWithError(Exception e);

    Context getContext();
}
