package com.wordpress.excelenteadventura.steamgamefinder;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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

        // Get a reference to the LoaderManager callbacks
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
                    // Initialize the mainUserLoader
                    getLoaderManager().restartLoader(MAIN_USER_LOADER, null, mLoaderCallbacks);
                } else {
                    // Popup toast with no connection error message
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Popup to explain how to find steam ID
        final LinearLayout mLayout = (LinearLayout) findViewById(R.id.activity_login);
        TextView helpText = (TextView) findViewById(R.id.login_help);

        // Set a click listener for the text view
        helpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "Text clicked");
                // Initialize a new instance of LayoutInflater service
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);

                // Inflate the popup view
                View popupView = inflater.inflate(R.layout.help_popup, null);

                /*
                    public PopupWindow (View contentView, int width, int height)
                        Create a new non focusable popup window which can display the contentView.
                        The dimension of the window must be passed to this constructor.

                        The popup does not provide any background. This should be handled by
                        the content view.

                    Parameters
                        contentView : the popup's content
                        width : the popup's width
                        height : the popup's height
                */
                // Initialize a new instance of popup window
                final PopupWindow mPopupWindow = new PopupWindow(popupView, android.app.ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);

                // Set an elevation value for popup window
                // Call requires API level 21
                if(Build.VERSION.SDK_INT>=21){
                    mPopupWindow.setElevation(5.0f);
                }

                // When clicking on the background login activity close the popup
                mLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Dismiss the popup window
                        mPopupWindow.dismiss();
                    }
                });

                // When clicking on the popup close it
                popupView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Dismiss the popup window
                        mPopupWindow.dismiss();
                    }
                });

                /*
                    public void showAtLocation (View parent, int gravity, int x, int y)
                        Display the content view in a popup window at the specified location. If the
                        popup window cannot fit on screen, it will be clipped.
                        Learn WindowManager.LayoutParams for more information on how gravity and the x
                        and y parameters are related. Specifying a gravity of NO_GRAVITY is similar
                        to specifying Gravity.LEFT | Gravity.TOP.

                    Parameters
                        parent : a parent view to get the getWindowToken() token from
                        gravity : the gravity which controls the placement of the popup window
                        x : the popup's x location offset
                        y : the popup's y location offset
                */
                // Finally, show the popup window at the center location of root relative layout
                mPopupWindow.showAtLocation(mLayout, Gravity.CENTER,0,0);
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
     * @param downloadSuccessful Boolean whether the data was downloaded successfully or not.
     */
    @Override
    public void onLoadFinished(Loader<Boolean> loader, Boolean downloadSuccessful) {
        if (loader.getId() == MAIN_USER_LOADER) {
            // If user data not found, pop up a toast to let them know the userID is incorrect
            if (!downloadSuccessful) {
                Log.d(LOG_TAG, "Main user loader returned false");
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
