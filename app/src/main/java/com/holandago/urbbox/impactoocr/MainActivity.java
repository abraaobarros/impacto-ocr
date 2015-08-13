package com.holandago.urbbox.impactoocr;

import android.content.Intent;
import android.content.UriMatcher;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.holandago.urbbox.impactoocr.picture.OnPictureFragmentInteractionListener;
import com.holandago.urbbox.impactoocr.picture.manager.PictureManager;
import com.holandago.urbbox.impactoocr.picture.manager.PictureManagerDelegate;


public class MainActivity extends AppCompatActivity implements OnPictureFragmentInteractionListener, PictureManagerDelegate{

    public static final String LOG_TAG = ".MainActivity";

    public static final String AUTHORITY = "com.holandago.urbbox.impactoocr.MainActivity";

    public static final Uri CAMERA_CLICK_URI = Uri.parse(
            "action://"+AUTHORITY+"/camera_click");

    public static final int CAMERA_CLICK = 0;

    static final int REQUEST_IMAGE_CAPTURE = 1;


    // Defines a set of uris allowed with this Activity
    private static final UriMatcher mUriMatcher = buildUriMatcher();

    private final PictureManager mPictureManager = buildPictureManager();

    private static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        //URI for camera click
        uriMatcher.addURI(AUTHORITY, CAMERA_CLICK_URI.getLastPathSegment(), CAMERA_CLICK);

        return uriMatcher;
    }

    public PictureManager buildPictureManager(){
        return new PictureManager(this);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            mPictureManager.onCameraResultOk(data);
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

    public void launchDecidePictureFragment(String encodedImage){

    }

}
