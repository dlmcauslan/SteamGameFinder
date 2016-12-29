package com.wordpress.excelenteadventura.steamgamefinder;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.wordpress.excelenteadventura.steamgamefinder.Classes.MainUser;
import com.wordpress.excelenteadventura.steamgamefinder.Classes.SteamGame;
import com.wordpress.excelenteadventura.steamgamefinder.Loaders.GameLoader;

import java.util.List;

/**
 * Created by DLMcAuslan on 12/18/2016.
 * Games in common fragment that shows a List of the games that the friends selected in the
 * Main fragment have in common.
 */
public class GamesInCommonFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<SteamGame>>{

    private static final String LOG_TAG = GamesInCommonFragment.class.getSimpleName();
    public static final int GAME_LOADER = 3;

    private List<String> mFriendsToCompare;
    private MainUser mMainUser;
    private View mFragmentView;
    private TextView mEmptyStateTextView;

    public GamesInCommonFragment() {
    }

    /**
     * onCreateView method for the games in common fragment. Inflates the layout, gets data from the
     * passed intent of the mainUser object and the List of friends to compare games with.
     * Checks the network connectivity, and only loads data if connected.
     * Initialises the progress indicator when the data is downloading.
     * Initialises the loaders to download the game data for the users that are being compared
     * and gets the games In Common and sets them to the list view in order of most total time
     * played.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mFragmentView = inflater.inflate(R.layout.fragment_games_in_common, container, false);

        // Get the intent, and the data associated with it
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("MainUser")) {
            // Get the main User
            mMainUser = (MainUser) intent.getSerializableExtra("MainUser");
            // Get the friends to compare with
            if (intent.hasExtra("FriendsToFind")) {
                mFriendsToCompare = intent.getStringArrayListExtra("FriendsToFind");
            }
        }

        // Find a reference to the GamesList view in the layout and set empty state
        ListView gamesListView = (ListView) mFragmentView.findViewById(R.id.games_list_view);
        mEmptyStateTextView = (TextView) mFragmentView.findViewById(R.id.games_empty_view);
        gamesListView.setEmptyView(mEmptyStateTextView);

        // Get a reference to the connectivity manager to check network state
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection fetch data, otherwise display error
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the FriendLoader
            loaderManager.initLoader(GAME_LOADER, null, this);
        } else {
            // Set loading indicator to gone
            View loadingIndicator = mFragmentView.findViewById(R.id.games_loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            // update empty stat with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }


        return mFragmentView;
    }


    /**
     * Sets the game name, total time played, and banner image to the listView after it has been
     * downloaded by the GameLoader.
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


    /**
     * onCreateLoader method for game loader
     * @param id - id of the loader to be used.
     * @param args
     * @return Always return null
     */
    @Override
    public Loader<List<SteamGame>> onCreateLoader(int id, Bundle args) {
        return new GameLoader(getActivity(), mMainUser, mFriendsToCompare);
    }

    /**
     * Set the game data after the loader has finished downloading it.
     * @param loader
     * @param gamesInCommon - the list of SteamGame objects that represent the games the users have
     *                      in common.
     */
    @Override
    public void onLoadFinished(Loader<List<SteamGame>> loader, List<SteamGame> gamesInCommon) {
//        Log.d(LOG_TAG, "Games In Common");
//        for (SteamGame game: gamesInCommon) {
//            Log.d(LOG_TAG, game.getName() + ", time played: " + game.getTimePlayed());
//        }
        // Hide loading indicator
        View loadingIndicator = mFragmentView.findViewById(R.id.games_loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        // If number of games in common is 0, set the empty state to reflect this. Otherwise set games data
        if (gamesInCommon == null) {
            mEmptyStateTextView.setText(R.string.no_games_data);
        }
        else if (gamesInCommon.size() == 0) {
            mEmptyStateTextView.setText(R.string.no_games_in_common);
        } else {
            // Set games data
            setGamesData(gamesInCommon);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<SteamGame>> loader) {

    }
}
