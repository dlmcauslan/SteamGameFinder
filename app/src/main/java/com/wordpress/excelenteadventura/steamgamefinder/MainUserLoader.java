package com.wordpress.excelenteadventura.steamgamefinder;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

/**
 * Created by DLMcAuslan on 12/18/2016.
 * Loader class which is used to download the users data, and save their profile picture to disk.
 */

public class MainUserLoader extends AsyncTaskLoader<Void> {

    private MainUser mMainUser;

    /**
     * Constructor for MainUserloader
     * @param context
     * @param mainUser - the mainUser of the Steam Game Finder app.
     */
    public MainUserLoader(Context context, MainUser mainUser) {
        super(context);
        mMainUser = mainUser;
    }

    /**
     * Force Load the data when the loader is called.
     */
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * loadInBackground method, which downloads the mainUsers data and sets it to the mainUser object.
     * Also downloads the mainUsers profile picture if it has changed.
     * @return Always returns null.
     */
    @Override
    public Void loadInBackground() {
        // Download main user data.
        Downloader.setUserData(mMainUser);
        Log.v("UserName Loader", mMainUser.getUserName());

        // Download user image if required and set it
        Downloader.downloadAndSaveUserImage(mMainUser, getContext());
        return null;
    }
}
