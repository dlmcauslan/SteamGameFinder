package com.wordpress.excelenteadventura.steamgamefinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wordpress.excelenteadventura.steamgamefinder.Classes.MainUser;

/**
 * A placeholder fragment containing a simple view.
 */
public class GamesInCommonFragment extends Fragment {

    private static final String LOG_TAG = GamesInCommonFragment.class.getSimpleName();

    public GamesInCommonFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_games_in_common, container, false);

        // Get the intent, and the data associated with it
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("MainUser")) {
            // Get the main User
            MainUser mMainUser = (MainUser) intent.getSerializableExtra("MainUser");
            Log.v(LOG_TAG, mMainUser.getUserName());
        }
        return rootView;
    }
}
