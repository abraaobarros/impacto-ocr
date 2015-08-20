package com.holandago.urbbox.impactoocr.picture.manager;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import com.google.gson.JsonObject;
import com.holandago.urbbox.impactoocr.model.Image;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by razu on 12/08/15.
 */
public class PictureManager implements PictureCommunicatorDelegate {

    PictureManagerDelegate mDelegate;
    PictureCommunicator mPictureCommunicator;
    String mCurrentPhotoPath;
    Bitmap mCurrentImage = null;

    public PictureManager(PictureManagerDelegate delegate, Context context){
        mDelegate = delegate;
        mPictureCommunicator = new PictureCommunicator(this,context);
    }

    public void onCameraResultOk(Intent data){
        Bundle extras = data.getExtras();
        mCurrentImage = (Bitmap) extras.get("data");
        if(mCurrentImage!=null) {
            sendImage(mCurrentImage);
        }
    }

    public void onGalleryResultOk(Intent data){
        // Get the Image from data
        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        // Get the cursor
        Cursor cursor = mDelegate.getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        // Move to first row
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String imgDecodableString = cursor.getString(columnIndex);
        cursor.close();
        mCurrentImage = BitmapFactory.decodeFile(imgDecodableString);
        sendImage(mCurrentImage);
    }

    public void onCameraResultOk(){
        File f = new File(mCurrentPhotoPath);
        sendImage(f);
    }

    public void sendImage(Bitmap image){
        mPictureCommunicator.sendPicture(Image.convertToBase64(image));
    }

    public void sendImage(File f){
        mPictureCommunicator.sendPicture(f);
    }

    public void sendImage(String encodedImage){
        mPictureCommunicator.sendPicture(encodedImage);
    }

    public void discardImage(){
        mCurrentImage = null;
    }

    public void setDelegate(PictureManagerDelegate delegate){
        mDelegate = delegate;
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    public String getCurrentPhotoPath(){
        return mCurrentPhotoPath;
    }

    // ### PictureCommunicatorDelegate methods
    @Override
    public void sentPicture(JsonObject result){
        mDelegate.sentPicture(result);
    }

    @Override
    public void sendingFailedWithError(Exception e){
        mDelegate.sendingFailedWithError(e);
    }

    // ### PictureManagerDelegate methods
    public void launchDecidePictureFragment(Bitmap image){
        String encodedImage = Image.convertToBase64(image);
        mDelegate.launchDecidePictureFragment(encodedImage);
    }

    public void launchCameraIntent(){
        mDelegate.dispatchTakePictureIntent();
    }

    public void launchCameraIntentWithFile(){
        mDelegate.dispatchTakePictureIntentWithFile();
    }

    public void launchGalleryIntent(){
        mDelegate.dispatchGalleryIntent();
    }



}
