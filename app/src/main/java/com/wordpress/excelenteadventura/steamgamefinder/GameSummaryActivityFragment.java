package com.wordpress.excelenteadventura.steamgamefinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.wordpress.excelenteadventura.steamgamefinder.Adapters.GameUserListAdapter;
import com.wordpress.excelenteadventura.steamgamefinder.Classes.GameUser;
import com.wordpress.excelenteadventura.steamgamefinder.Classes.SteamGameCombined;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class GameSummaryActivityFragment extends Fragment {
    private static final String LOG_TAG = GameSummaryActivityFragment.class.getSimpleName();

    public GameSummaryActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mGameSummaryFragment = inflater.inflate(R.layout.fragment_game_summary, container, false);

        // Get the intent, and the data associated with it
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("GameCombined")) {
            // Get the main User
            SteamGameCombined game = (SteamGameCombined) intent.getSerializableExtra("GameCombined");

            // Test that data has been passed through correctly
            Log.d(LOG_TAG, "Total time: " + String.valueOf(game.getMinutesPlayed()));
            ArrayList<GameUser> timeBreakdown = game.getGameUsers();
            for (GameUser user : timeBreakdown) {
                Log.d(LOG_TAG, user.getUserName() + " time played: " + String.valueOf(user.getGameMinutesPlayed()));
            }

            // Create a new gameslist adapter whose source is a list of Steam Games.
            final GameUserListAdapter mUserAdapter = new GameUserListAdapter(getActivity(), timeBreakdown);

            // Get a reference to the listview and attach the adapter to it.
            ListView listView = (ListView) mGameSummaryFragment.findViewById(R.id.users_list_view);
            listView.setAdapter(mUserAdapter);

        }

        return mGameSummaryFragment;
    }
}
