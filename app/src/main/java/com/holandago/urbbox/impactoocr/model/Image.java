package com.holandago.urbbox.impactoocr.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.UnsupportedSchemeException;
import android.util.Base64;

import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by razu on 12/08/15.
 */
public class Image {

    public static final String IMAGE_KEY = "image";
    public static final String TREATIMAGE_KEY = "treatImage";

    /**
     * Converts a Bitmap to a byteArray
     * @param image
     * @return byte array with jpeg image
     */
    public static byte[] convertToByteArray(Bitmap image){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap bitmapFromByteArray(byte[] data){
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    /**
     * Converts a Bitmap to Base64 representation
     * @param image
     * @return Base64 representation of Bitmap
     */
    public static String convertToBase64(Bitmap image){
        byte[] base64 = Base64.encode(Image.convertToByteArray(image),Base64.DEFAULT);
        try {
            return new String(base64, "UTF-8");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Converts a Bitmap to Base64 representation
     * @param byteArray
     * @return Base64 representation of Bitmap
     */
    public static String convertToBase64(byte[] byteArray){
        byte[] base64 = Base64.encode(byteArray,Base64.DEFAULT);
        try {
            return new String(base64, "UTF-8");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Decodes a base64 string to a bitmap
     * @param encodedImage
     * @return Bitmap representation of String
     */
    public static Bitmap decodeBase64StringToBitmap(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static JsonObject buildRecognizeParameters(String encondedImage){
        JsonObject parameters = new JsonObject();
        try{
            parameters.addProperty(IMAGE_KEY,encondedImage);
            parameters.addProperty(TREATIMAGE_KEY, true);
        }catch (NullPointerException e){
            e.printStackTrace();
            return null;
        }
        return parameters;
    }

}
