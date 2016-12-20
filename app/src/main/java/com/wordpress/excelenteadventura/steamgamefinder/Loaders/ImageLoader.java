package com.wordpress.excelenteadventura.steamgamefinder.Loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.wordpress.excelenteadventura.steamgamefinder.Utilities.Downloader;
import com.wordpress.excelenteadventura.steamgamefinder.Utilities.Utilities;

import java.io.File;

/**
 * Created by David-local on 12/18/2016.
 */

public class ImageLoader extends AsyncTaskLoader<Void> {

    private String sUrl;
//    private String sID;

    public ImageLoader(Context context, String url) {
        super(context);
        sUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public Void loadInBackground() {
        // Get filename from URL
        String imageName = Utilities.urlToFilename(sUrl);

        // Check if file has already been downloaded, if it hasn't download it.
        File file = getContext().getFileStreamPath(imageName);
        if (!file.exists()) {
            Bitmap img = Downloader.downloadImage(sUrl);
            Utilities.saveImage(getContext(), img, imageName);
            Log.v("ImageLoader URL", sUrl);
            Log.v("ImageLoader", "Downloading Image");
        }
        Log.v("ImageLoader fname", imageName);
        return null;
    }
}
