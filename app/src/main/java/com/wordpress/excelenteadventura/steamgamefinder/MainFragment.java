package com.wordpress.excelenteadventura.steamgamefinder;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    private MainUser mMainUser;
    private View mFragmentView;
    private Context mContext;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentView = inflater.inflate(R.layout.fragment_main, container, false);
        mContext = mFragmentView.getContext();

        // Setup Floating Action Button to open next activity (not created yet)
        FloatingActionButton fab = (FloatingActionButton) mFragmentView.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Uncomment this and replace newClass.class with the name of the activity we're linking to
                // Not sure whether MainFragment.this is correct either.
//                Intent intent = new Intent(MainFragment.this, newClass.class);
//                startActivity(intent);
            }
        });


        // Create main user object
        String mainUserID = "76561198020975978";    // This is temporary, will be added as a setting later.
        mMainUser = new MainUser(mainUserID);

        // Create new async task and download Main User data
        MainUserProfileAsyncTask task = new MainUserProfileAsyncTask();
        task.execute(mMainUser);

        // TODO: Check if mainUsers image has previously been downloaded, if it has - set it
        // if it hasn't, or the image has changed - start a new Async task to download it, then set it.

        // Create new async task and download Friends data
        // TODO: This is slow so add a progress bar.
        FriendsListAsyncTask friendTask = new FriendsListAsyncTask();
        friendTask.execute(mMainUser);

        // TODO: Check if friends images have previously been downloaded, if they has - set them
        // if they haven't, or the image has changed - start a new Async task to download it, then set it.
        // Need to check the workflow here, figure out how to make this work with the FriendsListAdapter.

//        // Create some fake steam friends to add populate array list
//        SteamFriend s = new SteamFriend("123");
//        s.setUserName("McKnick");
//        s.setOnlineStatus(1);
//        s.setImageResourceID(R.mipmap.ic_launcher);
//        SteamFriend r = new SteamFriend("123");
//        r.setUserName("Rusty");
//        r.setOnlineStatus(0);
//        r.setImageResourceID(R.mipmap.ic_launcher);
//        SteamFriend w = new SteamFriend("123");
//        w.setUserName("El Wicky");
//        w.setOnlineStatus(4);
//        w.setImageResourceID(R.mipmap.ic_launcher);
//        SteamFriend g = new SteamFriend("123");
//        g.setUserName("Geo");
//        g.setOnlineStatus(5);
//        g.setImageResourceID(R.mipmap.ic_launcher);
//
//        ArrayList<SteamFriend> friends = new ArrayList<SteamFriend>();
//        friends.add(s);
//        friends.add(w);
//        friends.add(r);
//        friends.add(g);

        return mFragmentView;
    }

    private void setMainUserText() {
        // Find main user text view and set userName
        TextView mainUserText = (TextView) mFragmentView.findViewById(R.id.text_main_user);
        mainUserText.setText(mMainUser.getUserName());
    }

    private void setFriendsText() {
        // Get friends ArrayList
        ArrayList<SteamFriend> friends = mMainUser.getFriendsList();

        // Create a new friendslist adapter whose source is a list of SteamFriends.
        FriendsListAdapter friendsAdapter = new FriendsListAdapter(getActivity(), friends);

        // Get a reference to the listview and attach the adapter to it.
        ListView listView = (ListView) mFragmentView.findViewById(R.id.friends_list_view);
        listView.setAdapter(friendsAdapter);
    }

    private class MainUserProfileAsyncTask extends AsyncTask<MainUser, Void, Void> {

        @Override
        protected Void doInBackground(MainUser... params) {
            Downloader.setUserData(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mMainUser.getUserName() != null) {
                setMainUserText();
            } else {
                Toast.makeText(mContext, "Error Setting User Data.\nCheck Internet Connection.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class FriendsListAsyncTask extends AsyncTask<MainUser, Void, Void> {

        @Override
        protected Void doInBackground(MainUser... params) {
            Downloader.setFriendsData(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mMainUser.getFriendsMap() != null) {
                setFriendsText();
            } else {
                Toast.makeText(mContext, "Error Setting User Data.\nCheck Internet Connection.", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
