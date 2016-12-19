package com.wordpress.excelenteadventura.steamgamefinder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Static Utilities class which holds a bunch of helper functions for image saving and loading etc.
 */
public final class Utilities {
    private static final String LOG_TAG = Utilities.class.getSimpleName();

    public Utilities() {
    }
    
    /**
     * Saves an image to the phones storage
     * @param context
     * @param b - image to save
     * @param imageName - filename to save image as
     */
    public static void saveImage(Context context, Bitmap b, String imageName) {
        FileOutputStream foStream;
        try {
            foStream = context.openFileOutput(imageName, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.PNG, 100, foStream);
            foStream.close();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error saving image.");
            e.printStackTrace();
        }
    }

    /**
     * Loads an image from the phones storage
     * @param context
     * @param imageName - filename of image to load
     * @return - the loaded image.
     */
    public static Bitmap loadImage(Context context, String imageName) {
        Bitmap bitmap = null;
        FileInputStream fiStream;
        try {
            fiStream = context.openFileInput(imageName);
            bitmap = BitmapFactory.decodeStream(fiStream);
            fiStream.close();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error loading image.");
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * From a string that represents an image url, convert it into the filename that will be saved
     * on disk.
     * @param url
     * @return
     */
    public static String urlToFilename(String url) {
        // Get filename from URL
        String[] splits = url.split("/");
        return splits[splits.length - 1];
    }

    /**
     * Loads the users image and sets it to the inputted image View
     * @param user - the Steam User who's image is to be loaded
     * @param context
     * @param imageView - the image view to set the image to.
     */
    public static void loadImageToView(SteamUser user, Context context, ImageView imageView) {
        // Get filename from URL
        String imageUrl = user.getProfilePicture()[2];
        String imageName = urlToFilename(imageUrl);
        // If the file exits, load it and set it to the imageView
        File file = context.getFileStreamPath(imageName);
        if (file.exists()) {
//            Log.v("Image name", imageName);
            imageView.setImageBitmap(loadImage(context, imageName));
        }
    }

}
