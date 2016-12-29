package com.wordpress.excelenteadventura.steamgamefinder;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wordpress.excelenteadventura.steamgamefinder.Classes.MainUser;
import com.wordpress.excelenteadventura.steamgamefinder.Loaders.MainUserLoader;
import com.wordpress.excelenteadventura.steamgamefinder.Utilities.Utilities;

public class LoginActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Boolean> {
    private MainUser mMainUser;
    private LoaderManager.LoaderCallbacks<Boolean> mLoaderCallbacks;
    private static final String LOG_TAG = LoginActivity.class.getSimpleName();
    private static final int MAIN_USER_LOADER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get a reference to the LoaderManager
        mLoaderCallbacks = this;

        // Setup Login Button to check user data and open main activity
        final Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Gets string from editText, checks whether userID is valid and downloads data
                EditText editText = (EditText) findViewById(R.id.login_edit_text);
                String userID = editText.getText().toString();
                mMainUser = new MainUser(userID);
                Log.v(LOG_TAG, mMainUser.getID());

                // Get a reference to the connectivity manager to check network state
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                // Get details on currently active default data network
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                // If there is a network connection fetch data, otherwise display error
                if (networkInfo != null && networkInfo.isConnected()) {
                    LoaderManager loaderManager = getLoaderManager();

                    // Initialize the mainUserLoader
                    loaderManager.restartLoader(MAIN_USER_LOADER, null, mLoaderCallbacks);

                } else {
//                    // Set loading indicator to gone
//                    View loadingIndicator = mFragmentView.findViewById(R.id.loading_indicator);
//                    loadingIndicator.setVisibility(View.GONE);
                    // Popup toast with no connection error message
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
//                    mEmptyStateTextView.setText(R.string.no_internet_connection);
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
            return new MainUserLoader(this, mMainUser);
        } else {
            return null;
        }
    }

    /**
     * Set the data after the loader has finished downloading it.
     * @param loader
     * @param b
     */
    @Override
    public void onLoadFinished(Loader<Boolean> loader, Boolean b) {
        if (loader.getId() == MAIN_USER_LOADER) {
            // If user data not found, pop up a toast to let them know the userID is incorrect
            if (!b) {
                Log.d(LOG_TAG, "Main user loader returned false");
//                // Set loading indicator to gone
//                View loadingIndicator = mFragmentView.findViewById(R.id.loading_indicator);
//                loadingIndicator.setVisibility(View.GONE);
                // Create toast with incorrect UserID message.
//                Toast.makeText(getApplicationContext(), R.string.incorrect_userID, Toast.LENGTH_SHORT).show();
                Toast toast = Toast.makeText(getApplicationContext(), R.string.incorrect_userID, Toast.LENGTH_SHORT);
                    LinearLayout layout = (LinearLayout) toast.getView();
                    if (layout.getChildCount() > 0) {
                        TextView tv = (TextView) layout.getChildAt(0);
                        tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                    }
                    toast.show();
            } else {
                // Save user data as shared preference
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                editor.putString(getString(R.string.pref_main_userID), mMainUser.getID());
                editor.putString(getString(R.string.pref_main_username), mMainUser.getUserName());
                editor.putString(getString(R.string.pref_main_image_filename), Utilities.urlToFilename(mMainUser.getProfilePicture()));
                editor.apply();
                Log.d(LOG_TAG, "Setting shared preferences");

                // Start intent to jump to main activity
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }


    @Override
    public void onLoaderReset(Loader<Boolean> loader) {

    }

}
