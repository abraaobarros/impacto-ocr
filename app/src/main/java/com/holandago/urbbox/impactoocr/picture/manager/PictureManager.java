package com.holandago.urbbox.impactoocr.picture.manager;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.holandago.urbbox.impactoocr.model.Image;

import java.io.File;
import java.io.FileOutputStream;
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
    String page_id;

    public PictureManager(PictureManagerDelegate delegate, Context context){
        mDelegate = delegate;
        mPictureCommunicator = new PictureCommunicator(this,context);
    }

    public void onCameraResultOk(){
        performCrop(new File(mCurrentPhotoPath));
    }

    public void onCameraResultOk(Intent data){
        performCrop(data);
    }

    public void performCrop(File f){
        mDelegate.performCrop(f);
    }

    public Bitmap setBitmap(int targetW, int targetH){

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
    }

    public void onCropResultOk(){
        mCurrentImage = setBitmap(1024,1443);
        if(mCurrentImage!=null) {
            Bitmap scaledImage = Bitmap.createScaledBitmap(mCurrentImage, 1024, 1443, true);
            File imageFile = null;
            try {
                imageFile = createImageFile();
            }catch (IOException e){
                e.printStackTrace();
            }
            if(imageFile != null) {
                saveToFile(imageFile, scaledImage);
            }
            sendImage(scaledImage);
//            addPicToGallery(imageFile);
        }

    }

//    private void addPicToGallery(File imageFile) {
//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        Uri contentUri = Uri.fromFile(imageFile);
//        mediaScanIntent.setData(contentUri);
//        mDelegate.savePictureToGallery(mediaScanIntent);
//    }

    public void onCropResultOk(Intent data){
        //get the returned data
        Bundle extras = data.getExtras();
        //get the cropped bitmap
        mCurrentImage = extras.getParcelable("data");
        if(mCurrentImage!=null) {
            Bitmap scaledImage = Bitmap.createScaledBitmap(mCurrentImage, 1024, 1443, true);
            File imageFile = null;
            try {
                imageFile = createImageFile();
            }catch (IOException e){
                e.printStackTrace();
            }
            if(imageFile != null) {
                saveToFile(imageFile, scaledImage);
            }
            sendImage(scaledImage);
        }
    }

    private void performCrop(Intent data){
        mDelegate.performCrop(data);
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

    public void sendImage(byte[] byteArray){

        mPictureCommunicator.sendPicture(Image.convertToBase64(byteArray), page_id);
    }

    public void sendImage(Bitmap image){
        mPictureCommunicator.sendPicture(Image.convertToBase64(image), page_id);
    }

    public void sendImage(File f){
        mPictureCommunicator.sendPicture(f);
    }

    public void sendImage(String encodedImage){
        mPictureCommunicator.sendPicture(encodedImage,page_id);
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
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void saveToFile(File f, Bitmap image){
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(f);
            image.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getCurrentPhotoPath(){
        return mCurrentPhotoPath;
    }

    public void setPageId(String id){
        page_id = id;
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

    public void launchQrCodeIntent(){
        mDelegate.launchQrCodeIntent();
    }

    public void launchCameraIntentWithFile(){
        mDelegate.dispatchTakePictureIntentWithFile();
    }

    public void launchGalleryIntent(){
        mDelegate.dispatchGalleryIntent();
    }



}
