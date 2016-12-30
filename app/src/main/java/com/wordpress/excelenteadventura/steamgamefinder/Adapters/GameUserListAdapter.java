package com.wordpress.excelenteadventura.steamgamefinder.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wordpress.excelenteadventura.steamgamefinder.Classes.GameUser;
import com.wordpress.excelenteadventura.steamgamefinder.R;
import com.wordpress.excelenteadventura.steamgamefinder.Utilities.Utilities;

import java.util.ArrayList;

/**
 * Created by DLMcAuslan on 12/14/2016.
 * ArrayAdapter that sets game user data to the respective user_list_item
 */

public class GameUserListAdapter extends ArrayAdapter<GameUser> {

    /**
     * Class constructor, inherits from super class
     * @param context
     * @param users - ArrayList of game user objects to populate the ListView
     */
    public GameUserListAdapter(Activity context, ArrayList<GameUser> users) {
        super(context, 0, users);
    }

    /**
     * getView method which gets each list-item for the list view and populates it with the friend
     * data
     * @param position - which position of the list view it is
     * @param listItemView - the listItemView to populate
     * @param parent
     * @return Returns the list item view that is populated.
     */
    @Override
    public View getView(int position, View listItemView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.user_list_item, parent, false);
        }

        // Get the SteamFriend object located at this position in the list
        GameUser currentUser = getItem(position);

        // Populate the imageView
        ImageView imageView = (ImageView) listItemView.findViewById(R.id.user_icon);
//        Utilities.loadImageToView(Utilities.urlToFilename(currentUser.getProfilePicture()), getContext(), imageView);

        // Populate the two text views - users name and time played
        TextView friendName = (TextView) listItemView.findViewById(R.id.text_user_name);
        friendName.setText(currentUser.getUserName());
        TextView timePlayed = (TextView) listItemView.findViewById(R.id.text_user_time_played);
        timePlayed.setText(Utilities.getTimePlayed(currentUser.getGameMinutesPlayed()));

        // Return the whole list item layout so that it can be shown in the listview.
        return listItemView;
    }
}
