package com.wordpress.excelenteadventura.steamgamefinder.Loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.wordpress.excelenteadventura.steamgamefinder.Classes.MainUser;
import com.wordpress.excelenteadventura.steamgamefinder.Classes.SteamGame;
import com.wordpress.excelenteadventura.steamgamefinder.Utilities.Downloader;

import java.util.List;

/**
 * Created by DLMcAuslan on 12/18/2016.
 * Loader class which is used to download the users game data, as well as the games of
 * the friends they are comparing to and save all of the game images to disk.
 */

public class GameLoader extends AsyncTaskLoader<List<SteamGame>> {

    private MainUser mMainUser;
    private List<String> mFriendsToCompare;

    /**
     * Constructor for Gameloader
     * @param context
     * @param mainUser - the mainUser of the Steam Game Finder app.
     */
    public GameLoader(Context context, MainUser mainUser, List<String> friendsToCompare) {
        super(context);
        mMainUser = mainUser;
        mFriendsToCompare = friendsToCompare;
    }

    /**
     * Force Load the data when the loader is called.
     */
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * loadInBackground method, which downloads the game data for the main user, then calls the
     * getGamesInCommon method to get a list of SteamGame objects that the users have in common.
     * Then loops over the steam games and downloads their banner image if it is required.
     * @return List of SteamGame objects representing the games that the users have in common.
     */
    @Override
    public List<SteamGame> loadInBackground() {
        // Download main user game data
        Boolean gameDataExists = Downloader.setGameData(mMainUser);

        if (!gameDataExists) {
            return null;
        } else {
            // Get games in common
             mMainUser.findGamesInCommon(mFriendsToCompare);
            List<SteamGame> gamesInCommon = mMainUser.getGamesInCommonList();

            // Loop over games getting their image
            for (SteamGame game : gamesInCommon) {
                // Download game banner image if required and set it
                Downloader.downloadAndSaveGameImage(game, getContext());
            }
            return gamesInCommon;
        }
    }
}
