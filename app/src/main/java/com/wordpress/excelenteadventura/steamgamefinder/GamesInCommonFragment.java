package com.wordpress.excelenteadventura.steamgamefinder;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.wordpress.excelenteadventura.steamgamefinder.Classes.MainUser;
import com.wordpress.excelenteadventura.steamgamefinder.Classes.SteamFriend;
import com.wordpress.excelenteadventura.steamgamefinder.Classes.SteamGame;
import com.wordpress.excelenteadventura.steamgamefinder.Loaders.GameLoader;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class GamesInCommonFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<SteamGame>>{

    private static final String LOG_TAG = GamesInCommonFragment.class.getSimpleName();
    public static final int GAME_LOADER = 3;

    private List<String> mFriendsToCompare;
    private MainUser mMainUser;
    private View mFragmentView;

    public GamesInCommonFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mFragmentView = inflater.inflate(R.layout.fragment_games_in_common, container, false);

        // Get the intent, and the data associated with it
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("MainUser")) {
            // Get the main User
            mMainUser = (MainUser) intent.getSerializableExtra("MainUser");
            Log.v(LOG_TAG, mMainUser.getUserName());
            Log.v(LOG_TAG, "Friends to compare with:");
            // Get the friends to compare with
            if (intent.hasExtra("FriendsToFind")) {
                mFriendsToCompare = intent.getStringArrayListExtra("FriendsToFind");
                for (String fID: mFriendsToCompare) {
                    SteamFriend fr = mMainUser.getFriend(fID);
                    Log.v(LOG_TAG, fr.getUserName() + " : " + fr.getOnlineString());
                }
            }

        }

        // Get a reference to the LoaderManager
        LoaderManager loaderManager = getLoaderManager();

        // Initialize the FriendLoader
        loaderManager.initLoader(GAME_LOADER, null, this);


        return mFragmentView;
    }


    /**
     * Sets the friend name, online status, and image to the listView after it has been
     * downloaded by the friendLoader.
     */
    private void setGamesData(List<SteamGame> gamesInCommon) {

        // Create a new gameslist adapter whose source is a list of Steam Games.
        GamesListAdapter mGamesAdapter = new GamesListAdapter(getActivity(), gamesInCommon);

        // Get a reference to the listview and attach the adapter to it.
        ListView listView = (ListView) mFragmentView.findViewById(R.id.games_list_view);
        listView.setAdapter(mGamesAdapter);

//        // Set onClickListener for listview. Not sure if I want it here or in the onCreateView method
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                // Get the friend object related to the clicked item
//                SteamFriend friend = mGamesAdapter.getItem(position);
//                // If the friend is not in the friendCompare map add them, otherwise remove them.
//                // Background colour is changed in touch_selector.xml and styles.xml files
//                if (mFriendCompare.containsKey(friend.getID())) {
//                    mFriendCompare.remove(friend.getID());
//                } else {
//                    mFriendCompare.put(friend.getID(), friend);
//                }
//                // TODO: maybe it is a good idea to download the users game list here, so its done in the background.
//                // Although will have to see how this works, because the comparisson should be done in the next activity
//                // so a loading indicator can be displayed.
//                // Log statements to check map
//                Log.d(LOG_TAG, "FriendsCompare Map.");
//                for (SteamFriend fr : mFriendCompare.values()) {
//                    Log.d(LOG_TAG, fr.getUserName());
//                }
//            }
//        });
    }


    @Override
    public Loader<List<SteamGame>> onCreateLoader(int id, Bundle args) {
        return new GameLoader(getActivity(), mMainUser, mFriendsToCompare);
    }

    @Override
    public void onLoadFinished(Loader<List<SteamGame>> loader, List<SteamGame> gamesInCommon) {
        Log.d(LOG_TAG, "Games In Common");
        for (SteamGame game: gamesInCommon) {
            Log.d(LOG_TAG, game.getName() + ", time played: " + game.getTimePlayed());
        }
//        // Hide loading indicator
//        View loadingIndicator = mFragmentView.findViewById(R.id.loading_indicator);
//        loadingIndicator.setVisibility(View.GONE);
        // Set friends data
        setGamesData(gamesInCommon);
    }

    @Override
    public void onLoaderReset(Loader<List<SteamGame>> loader) {

    }
}
