package com.wordpress.excelenteadventura.steamgamefinder;

import android.content.AsyncTaskLoader;
import android.content.Context;

/**
 * Created by DLMcAuslan on 12/18/2016.
 * Loader class which is used to download the users friend data, and save all of their profile
 * pictures to disk.
 */

public class FriendLoader extends AsyncTaskLoader<Void> {

    private MainUser mMainUser;

    /**
     * Constructor for Friendloader
     * @param context
     * @param mainUser - the mainUser of the Steam Game Finder app. Friend list should be associated with them
     */
    public FriendLoader(Context context, MainUser mainUser) {
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
     * loadInBackground method, which downloads the friend data and sets it to the mainUser object.
     * Also loops over the mainUser's friends and downloads their profile pictures if required.
     * @return Always returns null.
     */
    @Override
    public Void loadInBackground() {
        // Download friends data.
        Downloader.setFriendsData(mMainUser);

        // Loop over friends getting their image
        for (SteamFriend friend : mMainUser.getFriendsList()) {
            // Download user image if required and set it
            Downloader.downloadAndSaveUserImage(friend, getContext());
        }
        return null;
    }
}
