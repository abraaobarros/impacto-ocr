package com.holandago.urbbox.impactoocr;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.holandago.urbbox.impactoocr.picture.DecidePictureFragment;
import com.holandago.urbbox.impactoocr.picture.OnPictureFragmentInteractionListener;
import com.holandago.urbbox.impactoocr.picture.camera.CameraActivity;
import com.holandago.urbbox.impactoocr.picture.manager.PictureManager;
import com.holandago.urbbox.impactoocr.picture.manager.PictureManagerDelegate;

import java.io.File;
import java.io.IOException;


public class MainActivity extends AppCompatActivity implements OnPictureFragmentInteractionListener, PictureManagerDelegate{

    public static final String LOG_TAG = ".MainActivity";

    public static final String AUTHORITY = "com.holandago.urbbox.impactoocr.MainActivity";

    public static final Uri CAMERA_CLICK_URI = Uri.parse(
            "action://"+AUTHORITY+"/camera_click");

    public static final int CAMERA_CLICK = 0;

    private static final int SELECT_PICTURE = 3;
    static final int REQUEST_TAKE_PHOTO = 2;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_QR_CODE = 5;
    //keep track of cropping intent
    final int PIC_CROP = 4;
    final int PIC_CROP_FILE = 6;

    // Defines a set of uris allowed with this Activity
    private static final UriMatcher mUriMatcher = buildUriMatcher();

    private final PictureManager mPictureManager = buildPictureManager();

    private DecidePictureFragment mDecidePictureFragment;

    private static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        //URI for camera click
        uriMatcher.addURI(AUTHORITY, CAMERA_CLICK_URI.getLastPathSegment(), CAMERA_CLICK);

        return uriMatcher;
    }

    public PictureManager buildPictureManager(){
        return new PictureManager(this,this);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_gallery) {
            mPictureManager.launchGalleryIntent();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            mPictureManager.onCameraResultOk();
        }
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            mPictureManager.onCameraResultOk(data);
        }
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK
                && null != data){
            mPictureManager.onGalleryResultOk(data);
        }
        if (requestCode == PIC_CROP && resultCode == RESULT_OK){
            mPictureManager.onCropResultOk(data);
        }
        if (requestCode == PIC_CROP_FILE && resultCode == RESULT_OK){
            mPictureManager.onCropResultOk();
        }
        if (requestCode == REQUEST_QR_CODE && resultCode == RESULT_OK){
            String result = data.getStringExtra("SCAN_RESULT");
            mPictureManager.setPageId(result);
            mPictureManager.launchCameraIntentWithFile();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri){
        switch (mUriMatcher.match(uri)){
            case CAMERA_CLICK:
                Log.d(LOG_TAG, "Reached the Fragment Interaction");
                mPictureManager.launchQrCodeIntent();
                break;
            default:
                break;
        }
    }


    // ### PictureManagerDelegate methods

    @Override
    public void launchQrCodeIntent(){
        try{
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE","QR_CODE_MODE");
            startActivityForResult(intent,REQUEST_QR_CODE);
        }catch(Exception e){
            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
            startActivity(marketIntent);
            e.printStackTrace();
        }
    }

    @Override
    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }

    @Override
    public void dispatchTakePictureIntentWithFile(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = mPictureManager.createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void dispatchGalleryIntent(){
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, SELECT_PICTURE);
    }

    @Override
    public void performCrop(File f){
        try {
            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(Uri.fromFile(f), "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 0);
            cropIntent.putExtra("aspectY", 0);
//            //indicate output X and Y
//            cropIntent.putExtra("outputX", 1024);
//            cropIntent.putExtra("outputY", 1443);
            //retrieves file so no data should be retrived
            cropIntent.putExtra("return-data", false);

            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = mPictureManager.createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                cropIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(cropIntent, PIC_CROP_FILE);
            }
        }
        catch(ActivityNotFoundException anfe){
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void performCrop(Intent data){
        try {
            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(data.getData(), "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 0);
            cropIntent.putExtra("aspectY", 0);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 500);
            cropIntent.putExtra("outputY", 705);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);
        }
        catch(ActivityNotFoundException anfe){
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void launchDecidePictureFragment(String encodedImage){
        if(mDecidePictureFragment == null){

        }
    }

    @Override
    public void savePictureToGallery(Intent mediaScanIntent){
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    public void sentPicture(JsonObject result){
        String jack;
        String pump;
        try {
            jack = result.getAsJsonPrimitive("jack").getAsString();
        }catch (ClassCastException e){
            jack = "00.00";
        }
        try {
            pump = result.getAsJsonPrimitive("pump").getAsString();
        }catch (ClassCastException e){
            pump = "00.00";
        }
        try {
            String text = "Jack: " + jack;
            text = text + " Pump: " + pump;
            Toast.makeText(this, text, Toast.LENGTH_LONG).show();
        }catch (Exception e){
            sendingFailedWithError(e);
        }
    }

    @Override
    public void sendingFailedWithError(Exception e){
        Toast.makeText(this,"Não foi possível interpretar a imagem",Toast.LENGTH_LONG).show();
    }

}
