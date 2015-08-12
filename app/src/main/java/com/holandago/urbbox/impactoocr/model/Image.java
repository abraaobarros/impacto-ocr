package com.holandago.urbbox.impactoocr.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by razu on 12/08/15.
 */
public class Image {

    /**
     * Converts a Bitmap to a byteArray
     * @param image
     * @return byte array with jpeg image
     */
    public static byte[] convertToByteArray(Bitmap image){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        return b;
    }

    /**
     * Converts a Bitmap to Base64 representation
     * @param image
     * @return Base64 representation of Bitmap
     */
    public static String convertToBase64(Bitmap image){
        return Base64.encodeToString(Image.convertToByteArray(image), Base64.DEFAULT);
    }

    /**
     * Decodes a base64 string to a bitmap
     * @param encodedImage
     * @return Bitmap representation of String
     */
    public static Bitmap decodeBase64StringToBitmap(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0, bytes.length);
    }

}
