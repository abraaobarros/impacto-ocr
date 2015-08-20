package com.holandago.urbbox.impactoocr;

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
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.holandago.urbbox.impactoocr.picture.DecidePictureFragment;
import com.holandago.urbbox.impactoocr.picture.OnPictureFragmentInteractionListener;
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
            mPictureManager.onCameraResultOk(data);
        }
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            mPictureManager.onCameraResultOk();
        }
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK
                && null != data){
            mPictureManager.onGalleryResultOk(data);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri){
        switch (mUriMatcher.match(uri)){
            case CAMERA_CLICK:
                Log.d(LOG_TAG, "Reached the Fragment Interaction");
                mPictureManager.launchCameraIntent();
                break;
            default:
                break;
        }
    }


    // ### PictureManagerDelegate methods

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

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
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
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
    public void launchDecidePictureFragment(String encodedImage){
        if(mDecidePictureFragment == null){

        }
    }

    @Override
    public void sentPicture(JsonObject result){
        JsonArray digits = result.getAsJsonArray("digits");
        String text = "Texto impresso: ";
        for(int i = 0; i< digits.size();i++){
            text = text+" "+digits.get(i).getAsJsonPrimitive().getAsString();
        }
        Toast.makeText(this,text,Toast.LENGTH_LONG).show();
    }

    @Override
    public void sendingFailedWithError(Exception e){
        Toast.makeText(this,"Não foi possível interpretar a imagem",Toast.LENGTH_LONG).show();
    }

}
