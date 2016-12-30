package com.wordpress.excelenteadventura.steamgamefinder.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wordpress.excelenteadventura.steamgamefinder.Classes.SteamGame;
import com.wordpress.excelenteadventura.steamgamefinder.R;
import com.wordpress.excelenteadventura.steamgamefinder.Utilities.Utilities;

import java.util.List;

/**
 * Created by DLMcAuslan on 12/14/2016.
 * ArrayAdapter that sets friend data to the respective friend_list_item
 */

public class GamesListAdapter extends ArrayAdapter<SteamGame> {

    /**
     * Class constructor, inherits from super class
     * @param context
     * @param games - ArrayList of steam game objects to populate the ListView
     */
    public GamesListAdapter(Activity context, List<SteamGame> games) {
        super(context, 0, games);
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
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.game_list_item, parent, false);
        }

        // Get the SteamFriend object located at this position in the list
        SteamGame currentGame = getItem(position);

        // Populate the imageView
        ImageView imageView = (ImageView) listItemView.findViewById(R.id.game_banner);
        Utilities.loadBannerImageToView(currentGame, getContext(), imageView);

        // Populate the two text views - users name and online status
        TextView friendName = (TextView) listItemView.findViewById(R.id.text_game_name);
        friendName.setText(currentGame.getName());
        TextView timePlayed = (TextView) listItemView.findViewById(R.id.text_game_time_played);
        timePlayed.setText(Utilities.getTimePlayed(currentGame.getMinutesPlayed()));

        // Return the whole list item layout so that it can be shown in the listview.
        return listItemView;
    }
}
