package com.wordpress.excelenteadventura.steamgamefinder;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by David-local on 12/14/2016.
 */

public class FriendsListAdaptor extends ArrayAdapter<SteamFriend> {

    public FriendsListAdaptor(Activity context, ArrayList<SteamFriend> friends) {
        super(context, 0, friends);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.friend_list_item, parent, false);
        }

        // Get the SteamFriend object located at this position in the list
        SteamFriend currentFriend = getItem(position);

        // Populate the imageView
        ImageView friendImage = (ImageView) listItemView.findViewById(R.id.friend_icon);
        friendImage.setImageResource(currentFriend.getImageResourceId());


        // Populate the two text views - users name and online status
        TextView friendName = (TextView) listItemView.findViewById(R.id.text_friend_name);
        friendName.setText(currentFriend.getUserName());
        TextView onlineStatus = (TextView) listItemView.findViewById(R.id.text_online_status);
        onlineStatus.setText(currentFriend.getOnlineString());
        // Set colour depending on online status
        if (currentFriend.getOnlineStatus() == 0) {
            friendName.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            onlineStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        } else {
            friendName.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            onlineStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        }

        // Return the whole list item layout so that it can be shown in the listview.
        return listItemView;
    }
}
