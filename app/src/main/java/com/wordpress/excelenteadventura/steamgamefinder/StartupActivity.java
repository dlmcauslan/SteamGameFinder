package com.wordpress.excelenteadventura.steamgamefinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class StartupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get userID shared prefs
        // Load the main users userName and image from sharedPrefs
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String userID = prefs.getString(getString(R.string.pref_main_userID),getString(R.string.setting_default_user_ID));


        Intent intent;
        // If mainUserID is the default value (-1) then launch the login screen, otherwise jump
        // straight into the main activity.
        if (userID.equals("-1")) {
            intent = new Intent(this, LoginActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
