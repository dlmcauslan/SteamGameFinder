package com.wordpress.excelenteadventura.steamgamefinder;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    MainUser mMainUser;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_main, container, false);

        // Create some fake steam friends to add populate array list
        SteamFriend s = new SteamFriend("123");
        s.setUserName("McKnick");
        s.setOnlineStatus(1);
        s.setImageResourceID(R.mipmap.ic_launcher);
        SteamFriend r = new SteamFriend("123");
        r.setUserName("Rusty");
        r.setOnlineStatus(0);
        r.setImageResourceID(R.mipmap.ic_launcher);
        SteamFriend w = new SteamFriend("123");
        w.setUserName("El Wicky");
        w.setOnlineStatus(4);
        w.setImageResourceID(R.mipmap.ic_launcher);
        SteamFriend g = new SteamFriend("123");
        g.setUserName("Geo");
        g.setOnlineStatus(5);
        g.setImageResourceID(R.mipmap.ic_launcher);

        ArrayList<SteamFriend> friends = new ArrayList<SteamFriend>();
        friends.add(s);
        friends.add(w);
        friends.add(r);
        friends.add(g);

        // Create a new friendslist adapter whose source is a list of SteamFriends.
        FriendsListAdaptor friendsAdapter = new FriendsListAdaptor(getActivity(), friends);

        // Get a reference to the listview and attach the adapter to it.
        ListView listView = (ListView) fragmentView.findViewById(R.id.friends_list_view);
        listView.setAdapter(friendsAdapter);

        return fragmentView;
    }



}
