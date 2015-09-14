package com.holandago.urbbox.impactoocr.picture.manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ProgressBar;

import com.google.gson.JsonObject;
import com.holandago.urbbox.impactoocr.model.Image;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;

/**
 * Created by razu on 13/08/15.
 */
public class PictureCommunicator {

    public static final String BASE_URL = "http://impacto-ocr.herokuapp.com";
    public static final String RECOGNIZE_URL = BASE_URL+"/api/recognize";
    Context mContext;
    PictureCommunicatorDelegate mDelegate;
    ProgressDialog mProgressDialog;

    public PictureCommunicator(PictureCommunicatorDelegate delegate, Context context){
        mDelegate = delegate;
        mContext = context;
    }

    public void sendPicture(String encodedImage, String id){
        JsonObject parameters = Image.buildRecognizeParameters(encodedImage,id);
        if(parameters!=null) {
            launchRingDialogWithMessage("Favor aguardar");
            Ion.with(mContext)
                    .load(RECOGNIZE_URL)
                    .setJsonObjectBody(parameters)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (e != null) {
                                e.printStackTrace();
                                sendingFailedWithError(e);
                                dismissRingDialog();
                            } else {
                                 sentPicture(result);
                                dismissRingDialog();
                            }

                        }
                    });
        }
    }

    public void sendPictureWithString(String encodedImage, String id){
        JsonObject parameters = Image.buildRecognizeParameters(encodedImage,id);
        if(parameters!=null) {
            launchRingDialogWithMessage("Favor aguardar");
            Ion.with(mContext)
                    .load(RECOGNIZE_URL)
                    .setJsonObjectBody(parameters)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            if (e != null) {
                                e.printStackTrace();
                                sendingFailedWithError(e);
                                dismissRingDialog();
                            } else {
//                                sentPicture(result);
                                dismissRingDialog();
                            }

                        }
                    });
        }
    }

    public void launchRingDialogWithMessage(String message) {
        mProgressDialog = ProgressDialog.show(mContext, "Enviando imagem", message, true);
        mProgressDialog.setCancelable(true);
    }

    public void dismissRingDialog(){
        mProgressDialog.dismiss();
    }

    public void sendPicture(File image){
        launchRingDialogWithMessage("Favor aguardar");
        Ion.with(mContext)
                .load(RECOGNIZE_URL)
                .setTimeout(60 * 60 * 1000)
                .setMultipartFile("image", "image/jpeg", image)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            e.printStackTrace();
                            sendingFailedWithError(e);
                            dismissRingDialog();
                        } else {
                            dismissRingDialog();
//                            sentPicture(result);
                        }
                    }
                });
    }

    public void sentPicture(JsonObject result){
        mDelegate.sentPicture(result);
    }

    public void sendingFailedWithError(Exception e){
        mDelegate.sendingFailedWithError(e);
    }
}
