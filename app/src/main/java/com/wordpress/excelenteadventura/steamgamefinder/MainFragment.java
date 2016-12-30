package com.wordpress.excelenteadventura.steamgamefinder;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wordpress.excelenteadventura.steamgamefinder.Adapters.FriendsListAdapter;
import com.wordpress.excelenteadventura.steamgamefinder.Classes.MainUser;
import com.wordpress.excelenteadventura.steamgamefinder.Classes.SteamFriend;
import com.wordpress.excelenteadventura.steamgamefinder.Loaders.FriendLoader;
import com.wordpress.excelenteadventura.steamgamefinder.Loaders.MainUserLoader;
import com.wordpress.excelenteadventura.steamgamefinder.Utilities.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by DLMcAuslan on 12/13/2016.
 * Main fragment that controls the main screen of the app, which loads the main user and their friends
 * data. The allows the user to select which friends they want to compare data to, and load the
 * game comparison screen.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Boolean> {
    private MainUser mMainUser;
    private View mFragmentView;
    private FriendsListAdapter mFriendsAdapter;
    private Map<String, SteamFriend> mFriendCompare = new HashMap<>();
    private TextView mEmptyStateTextView;

    private static final String LOG_TAG = MainFragment.class.getSimpleName();
    private static final int MAIN_USER_LOADER = 1;
    private static final int FRIEND_LOADER = 2;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This line enables the fragment to handle menu events
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * onCreateView method for the main fragment. Inflates the layout, creates the main user object,
     * checks the network connectivity, and only loads data if connected. initialises the progress
     * indicator when the data is downloading. Also initialises the loaders to download the main
     * user data and the friend data.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentView = inflater.inflate(R.layout.fragment_main, container, false);

        // Load the main users userName and image from sharedPrefs
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String userName = prefs.getString(getString(R.string.pref_main_username),"");
        String imageFileName = prefs.getString(getString(R.string.pref_main_image_filename),"");
        // Only load the sharedPrefs if they exist
        if (!userName.equals("") && !imageFileName.equals("")) {
            setMainUserData(userName, imageFileName);
            Log.d(LOG_TAG, "Loading shared prefs");
        }

        // Create main user
        String mainUserID = prefs.getString(getString(R.string.pref_main_userID),getString(R.string.setting_default_user_ID));
        mMainUser = new MainUser(mainUserID);


        // Setup Floating Action Button to open next activity
        FloatingActionButton fab = (FloatingActionButton) mFragmentView.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Only launch the GamesInCommonActivity if at least one friend has been selected.
                if (!mFriendCompare.isEmpty()) {
                    // Launches GamesInCommonActivity.
                    Intent intent = new Intent(getActivity(), GamesInCommonActivity.class);
                    // Set data on the intent to be passed through
                    intent.putExtra("MainUser", mMainUser);
                    // TODO: I don't really like how I've done this, some refactoring could be in order.
                    intent.putStringArrayListExtra("FriendsToFind", new ArrayList<String>(mFriendCompare.keySet()));
                    // Start intent
                    startActivity(intent);
                } else {
                    // Otherwise set a toast to alert the user
                    Toast toast = Toast.makeText(getActivity(), "You must choose at least one friend to compare games with!", Toast.LENGTH_SHORT);
                    LinearLayout layout = (LinearLayout) toast.getView();
                    if (layout.getChildCount() > 0) {
                        TextView tv = (TextView) layout.getChildAt(0);
                        tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                    }
                    toast.show();
                }
            }
        });

        // Find a reference to the FriendsList view in the layout and set empty state
        ListView friendsListView = (ListView) mFragmentView.findViewById(R.id.friends_list_view);
        mEmptyStateTextView = (TextView) mFragmentView.findViewById(R.id.empty_view);
        friendsListView.setEmptyView(mEmptyStateTextView);

        // Get a reference to the connectivity manager to check network state
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection fetch data, otherwise display error
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager
            LoaderManager loaderManager = getLoaderManager();
            // Initialize the mainUserLoader
            loaderManager.initLoader(MAIN_USER_LOADER, null, this);

        } else {
            // Set loading indicator to gone
            View loadingIndicator = mFragmentView.findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            // update empty stat with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

        return mFragmentView;
    }

    /**
     * Sets the main user name and image after it has been downloaded by the MainUserLoader.
     */
    private void setMainUserData(String userName, String imageFileName) {
        // Find main user text view and set userName
        TextView mainUserText = (TextView) mFragmentView.findViewById(R.id.text_main_user);
        mainUserText.setText(userName);

        // Load the users image to the main user imageview
        ImageView imageView = (ImageView) mFragmentView.findViewById(R.id.main_user_icon);
        Utilities.loadImageToView(imageFileName, getActivity(), imageView);
    }

    /**
     * Sets the friend name, online status, and image to the listView after it has been
     * downloaded by the friendLoader.
     */
    private void setFriendsData() {
        // Get friends ArrayList
        ArrayList<SteamFriend> friends = mMainUser.getFriendsList();

        // Create a new friendslist adapter whose source is a list of SteamFriends.
        mFriendsAdapter = new FriendsListAdapter(getActivity(), friends);

        // Get a reference to the listview and attach the adapter to it.
        ListView listView = (ListView) mFragmentView.findViewById(R.id.friends_list_view);
        listView.setAdapter(mFriendsAdapter);

        // Set onClickListener for listview. Not sure if I want it here or in the onCreateView method
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Get the friend object related to the clicked item
                SteamFriend friend = mFriendsAdapter.getItem(position);
                // If the friend is not in the friendCompare map add them, otherwise remove them.
                // Background colour is changed in touch_selector.xml and styles.xml files
                if (mFriendCompare.containsKey(friend.getID())) {
                    mFriendCompare.remove(friend.getID());
                } else {
                    mFriendCompare.put(friend.getID(), friend);
                }
            }
        });
    }

    /**
     * onCreateLoader method for data loaders
     * @param id - id of the loader to be used.
     * @param args
     * @return Always return null
     */
    @Override
    public Loader<Boolean> onCreateLoader(int id, Bundle args) {
        if (id == MAIN_USER_LOADER) {
            return new MainUserLoader(getActivity(), mMainUser);
        } else if (id == FRIEND_LOADER) {
            return new FriendLoader(getActivity(), mMainUser);
        } else {
            return null;
        }
    }

    /**
     * Set the data after the loader has finished downloading it.
     * @param loader
     * @param downloadSuccessful Boolean whether the data was downloaded successfully or not.
     */
    @Override
    public void onLoadFinished(Loader<Boolean> loader, Boolean downloadSuccessful) {
        if (loader.getId() == MAIN_USER_LOADER) {
            // If user data not found, set the background message to let them know the userID is incorrect
            // and don't change the user data
            if (!downloadSuccessful) {
                Log.d(LOG_TAG, "Main user loader returned false");
                // Set loading indicator to gone
                View loadingIndicator = mFragmentView.findViewById(R.id.loading_indicator);
                loadingIndicator.setVisibility(View.GONE);
                // update empty state with no user data message
                mEmptyStateTextView.setText(R.string.incorrect_userID);
            } else {
                // Set main user data only if the downloaded data is different than that in the sharedPrefs
                String userName = mMainUser.getUserName();
                String imageFileName = Utilities.urlToFilename(mMainUser.getProfilePicture());
                String userID = mMainUser.getID();

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String prefsUserName = prefs.getString(getString(R.string.pref_main_username), "");
                String prefsImageFileName = prefs.getString(getString(R.string.pref_main_image_filename), "");
                String prefsUserID = prefs.getString(getString(R.string.pref_main_userID), getString(R.string.setting_default_user_ID));

                Log.d(LOG_TAG, userID + " " + prefsUserID);
                Log.d(LOG_TAG, userName + " " + prefsUserName);

                // User data is different
                if (!userID.equals(prefsUserID) || !userName.equals(prefsUserName) || !imageFileName.equals(prefsImageFileName)) {
                    setMainUserData(userName, imageFileName);
                    // Save user data as shared preference
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(getString(R.string.pref_main_username), userName);
                    editor.putString(getString(R.string.pref_main_image_filename), imageFileName);
                    editor.apply();
                    Log.d(LOG_TAG, "Setting shared preferences");
                }

                // Initialize the FriendLoader
                getLoaderManager().initLoader(FRIEND_LOADER, null, this);
            }
        } else if (loader.getId() == FRIEND_LOADER) {
            // Hide loading indicator
            View loadingIndicator = mFragmentView.findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            if (!downloadSuccessful) {
                Log.d(LOG_TAG, "Friend loader returned false");
                // update empty state with no connection error message
                mEmptyStateTextView.setText(R.string.no_friend_data);
            } else {
                // Set friends data
                setFriendsData();
            }
        }
    }


    @Override
    public void onLoaderReset(Loader<Boolean> loader) {
//        if (loader.getId() == FRIEND_TEXT_LOADER) {
//            mFriendsAdapter.clear();
//        }
    }



}
