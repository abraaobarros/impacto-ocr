package com.holandago.urbbox.impactoocr.picture.manager;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
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
    public PictureCommunicator mPictureCommunicator;
    String mCurrentPhotoPath;
    Bitmap mCurrentImage = null;
    String page_id;

    public PictureManager(PictureManagerDelegate delegate, Context context){
        mDelegate = delegate;
        mPictureCommunicator = new PictureCommunicator(this);
    }

    public void onCameraResultOk(String backupPath){
        if(mCurrentPhotoPath != null) {
            rotateImageIfNecessary(mCurrentPhotoPath);
            performCrop(new File(mCurrentPhotoPath));
        }else{
            mCurrentPhotoPath = backupPath;
            backupPath = null;
            rotateImageIfNecessary(mCurrentPhotoPath);
            performCrop(new File(mCurrentPhotoPath));
        }
    }

    public void onCameraResultOk(Intent data){
        performCrop(data);
    }

    public void performCrop(File f){
        mDelegate.performCrop(f);
    }

    public Bitmap setBitmap(){

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inPurgeable = true;
        bmOptions.inSampleSize = 1;

        return BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
    }

    public void rotateImageIfNecessary(String imagePath){
        int rotate = 90;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inPurgeable = true;
        bmOptions.inSampleSize = 1;
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);
            switch (orientation){
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);
        Bitmap original = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        Bitmap rotatedBitmap = Bitmap.createBitmap(original,0,0,original.getWidth(),original.getHeight(),matrix,true);
        saveToFile(new File(imagePath),rotatedBitmap);
    }

    public void onCropResultOk(String backupPageId, String backupPhotoPath){
        if(page_id == null){
            page_id = backupPageId;
        }
        if(mCurrentPhotoPath == null){
            mCurrentPhotoPath = backupPhotoPath;
        }
        mCurrentImage = setBitmap();
        if(mCurrentImage!=null) {
            Bitmap scaledImage = Bitmap.createScaledBitmap(mCurrentImage, 1024, 1443, true);
            mCurrentImage = null;
            File imageFile = null;
            try {
                imageFile = createImageFile();
            }catch (IOException e){
                e.printStackTrace();
            }
            if(imageFile != null) {
                saveToFile(imageFile, scaledImage);
                addPicToGallery(imageFile);
            }
            imageFile = null;
            sendImage(scaledImage);

        }

    }

    private void addPicToGallery(File imageFile) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(imageFile);
        mediaScanIntent.setData(contentUri);
        mDelegate.savePictureToGallery(mediaScanIntent);
    }

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
        mCurrentPhotoPath = null;
        image = null;
        System.gc();
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
            image.compress(Bitmap.CompressFormat.JPEG, 50, out);
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

    @Override
    public Context getContext(){
        return mDelegate.getContext();
    }



}
