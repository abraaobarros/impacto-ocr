package com.holandago.urbbox.impactoocr.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
    public static final String PAGE_ID_KEY = "id";

    /**
     * Converts a Bitmap to a byteArray
     * @param image
     * @return byte array with jpeg image
     */
    public static byte[] convertToByteArray(Bitmap image){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
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
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
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

    public static JsonObject buildRecognizeParameters(String encondedImage, String id){
        JsonObject parameters = new JsonObject();
        try{
            parameters.addProperty(IMAGE_KEY,encondedImage);
            parameters.addProperty(PAGE_ID_KEY, Integer.valueOf(id));
            parameters.addProperty(TREATIMAGE_KEY, true);
        }catch (NullPointerException e){
            e.printStackTrace();
            return null;
        }
        return parameters;
    }

    public static Bitmap adjustedContrast(Bitmap src, double value)
    {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap

        // create a mutable empty bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());

        // create a canvas so that we can draw the bmOut Bitmap from source bitmap
        Canvas c = new Canvas();
        c.setBitmap(bmOut);

        // draw bitmap to bmOut from src bitmap so we can modify it
        c.drawBitmap(src, 0, 0, new Paint(Color.BLACK));


        // color information
        int A, R, G, B;
        int pixel;
        // get contrast value
        double contrast = Math.pow((100 + value) / 100, 2);

        // scan through all pixels
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                // apply filter contrast for every channel R, G, B
                R = Color.red(pixel);
                R = (int)(((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(R < 0) { R = 0; }
                else if(R > 255) { R = 255; }

                G = Color.green(pixel);
                G = (int)(((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(G < 0) { G = 0; }
                else if(G > 255) { G = 255; }

                B = Color.blue(pixel);
                B = (int)(((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(B < 0) { B = 0; }
                else if(B > 255) { B = 255; }

                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
        return bmOut;
    }

}
