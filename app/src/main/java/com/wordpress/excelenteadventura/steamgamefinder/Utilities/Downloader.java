package com.wordpress.excelenteadventura.steamgamefinder.Utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.wordpress.excelenteadventura.steamgamefinder.BuildConfig;
import com.wordpress.excelenteadventura.steamgamefinder.Classes.MainUser;
import com.wordpress.excelenteadventura.steamgamefinder.Classes.SteamFriend;
import com.wordpress.excelenteadventura.steamgamefinder.Classes.SteamGame;
import com.wordpress.excelenteadventura.steamgamefinder.Classes.SteamUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;


/**
 * Static downloader class which holds the functions for downloading a users summary data, 
 * friend data, and game data.
 * @author DLMcAuslan
 */
public final class Downloader {
    private static final String APIKey = BuildConfig.STEAM_API_KEY;
    private static final String LOG_TAG = Downloader.class.getSimpleName();
    
    public Downloader() {
    }
    
    
    /**
     * Sets the player summary data from the web.
     * @param user - a SteamUser object to add the data too.
     */
    public static Boolean setUserData(SteamUser user) {
        // Gets the URL containing the users player summary data.
        String urlString = "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=" 
                + APIKey + "&steamids=" + user.getID() + "&format=json";

        Log.v(LOG_TAG, urlString);
        URL playerSummaryData = createUrl(urlString);
        
        // Make Http request and set user data from the returned JSON object
        try {
            JSONObject dataObject = makeHttpRequest(playerSummaryData);

            // If dataObject is null, log it, show a toast, and return null.
            if (dataObject == null) {
                String errorMessage = "Error setting user data.\nIs the steamID correct?";
                Log.e(LOG_TAG, errorMessage);
                return false;
            }

            // Get JsonObject containing user data
            JSONObject userObject = dataObject.getJSONObject("response").getJSONArray("players").getJSONObject(0);
            
            // Set the users userName, onlineStatus, and profilePictures
            user.setUserName(userObject.getString("personaname"));
            user.setOnlineStatus(userObject.getInt("personastate"));
            String[] profilePictures = new String[] { userObject.getString("avatar"),
                                                      userObject.getString("avatarmedium"),
                                                      userObject.getString("avatarfull") };
            user.setProfilePicture(profilePictures);      
        } catch (IOException | JSONException e) {
            String errorMessage = "IO/JSONexception Error setting user data.\nIs the steamID correct?";
            Log.e(LOG_TAG, errorMessage, e);
            return false;
        }
        return true;
    }

    
    /**
     * Sets the users friendsList data from web or from file.
     * @param user - a MainUser object to add the data too. Note this method is only callable on the mainUser.
     */
    public static Boolean setFriendsData(MainUser user) {
        // Map containing the users friends. Key - friend ID, value - steamFriend object.
        Map<String, SteamFriend> friendMap = new HashMap<String, SteamFriend>();
        
        // Gets the URL containing the users player summary data.
        String urlString = "http://api.steampowered.com/ISteamUser/GetFriendList/v0001/?key=" 
                + APIKey + "&steamid=" + user.getID() + "&relationship=friend&format=json";
        URL friendsData = createUrl(urlString);

        // Make Http request and set friend data from the returned JSON object
        try {
            JSONObject dataObject = makeHttpRequest(friendsData);

            // If dataObject is null, log it, show a toast, and return null.
            if (dataObject == null) {
                Log.v(LOG_TAG, "JSON object returned was null");
                return false;
            }

            // Get JsonArray containing friend data
            JSONArray friends = dataObject.getJSONObject("friendslist").getJSONArray("friends");
            // Loop over the array, creating friend objects and setting their data
            for (int i = 0; i < friends.length(); i++) {
                JSONObject jFriend = friends.getJSONObject(i);
                String steamID = jFriend.getString("steamid");
                SteamFriend friend = new SteamFriend(steamID);
                // Set user data for friend
                Downloader.setUserData(friend); // Now friend data should automatically download on first attempt at getting from file,
                                                // Note that this means online status won't be current.
                // Add friend to friendMap
                friendMap.put(steamID, friend);
             } 
        }
        catch (IOException | JSONException e) {
            // If IOException, returns without setting user data
            String errorMessage = "Error setting friend data.";
            Log.e(LOG_TAG, errorMessage, e);
            return false;
        }
        
        // Set the users friendMap data
        user.setFriendMap(friendMap);
        return true;
    }
    
    /**
     * Sets the player game data from web.
     * @param user - a SteamUser object to add the data too.
     */
    public static Boolean setGameData(SteamUser user) {
        Map<String, SteamGame> gameMap = new HashMap<String, SteamGame>();
        
        // Gets the URL containing the users game data.
        String urlString = "http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/?key=" 
                + APIKey + "&steamid=" + user.getID() + "&include_appinfo=1&format=json";
        URL gameData = createUrl(urlString);

        // Make Http request and set game data from the returned JSON object
        try {
            JSONObject dataObject = makeHttpRequest(gameData);

            // If dataObject is null, log it, show a toast, and return null.
            if (dataObject == null) {
                Log.v(LOG_TAG, "JSON object returned was null");
                return false;
            }
            Log.d(LOG_TAG, urlString);

            // Get JsonArray containing users game data
            // TODO: handle app crash if user's data is private.
            JSONArray jGames = dataObject.getJSONObject("response").getJSONArray("games");
            // Loop over the array creating a SteamGame object for each game and setting the object data
            for (int i = 0; i < jGames.length(); i++) {
                JSONObject jGame = jGames.getJSONObject(i);
                String gameName = jGame.getString("name");
                // Create game object
                SteamGame game = new SteamGame(gameName);
                // Set game data
                game.setMinutesPlayed(jGame.getInt("playtime_forever"));
                game.setID(String.valueOf(jGame.getInt("appid")));
                game.setIcon(jGame.getString("img_icon_url"));
                game.setBanner(jGame.getString("img_logo_url"));
                // Add the gameObject to gameMap
                if (!game.isEmpty()) gameMap.put(gameName, game);
            }
        } catch (IOException | JSONException e) {
            // If IOException, returns without setting user data
            String errorMessage = "Error getting game data.";
            Log.e(LOG_TAG, errorMessage, e);
            return false;
        }
        // Set the gameMap to the user.
        user.setGameMap(gameMap);
        return true;
    }

    /**
     * Class to download an image from the provided URL.
     * @param sUrl - URL of the image to download.
     * @return Bitmap containing the downloaded image.
     */
    public static Bitmap downloadImage(String sUrl) {
        Bitmap bitmap = null;
        try {
            InputStream inputStream = new URL(sUrl).openStream();   // Download Image from URL
            bitmap = BitmapFactory.decodeStream(inputStream);       // Decode Bitmap
            inputStream.close();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error downloading image.");
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * Checks if the users profile picture has previously been downloaded. If it needs to be redownloaded
     * it does this and saves it to file.
     * @param user SteamUser whose picture needs to be downloaded.
     * @param context
     */
    public static void downloadAndSaveUserImage(SteamUser user, Context context) {
        // Get filename from URL
        String imageUrl = user.getProfilePicture();
        String imageName = Utilities.urlToFilename(imageUrl);

        // Check if file has already been downloaded, if it hasn't download it.
        File file = context.getFileStreamPath(imageName);
        if (!file.exists()) {
            Bitmap img = Downloader.downloadImage(imageUrl);
            Utilities.saveImage(context, img, imageName);
            Log.v(LOG_TAG, imageUrl);
            Log.v(LOG_TAG, "Downloading Image");
        }
    }

    /**
     * Checks if the games banner picture has previously been downloaded. If it needs to be redownloaded
     * it does this and saves it to file.
     * @param game SteamUser whose picture needs to be downloaded.
     * @param context
     */
    public static void downloadAndSaveGameImage(SteamGame game, Context context) {
        // Get filename from URL
        String imageUrl = game.getBanner();
        String imageName = Utilities.urlToFilename(imageUrl);

        // Check if file has already been downloaded, if it hasn't download it.
        File file = context.getFileStreamPath(imageName);
        if (!file.exists()) {
            Bitmap img = Downloader.downloadImage(imageUrl);
            Utilities.saveImage(context, img, imageName);
            Log.v(LOG_TAG, imageUrl);
            Log.v(LOG_TAG, "Downloading Image: " + imageName);
        }
    }


    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }
    
    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static JSONObject makeHttpRequest(URL url) throws IOException {
        JSONObject jsonObject = null;

        // If the URL is null, then return early.
        if (url == null) {
            return jsonObject;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                String jsonResponse = readFromStream(inputStream);
                jsonObject = new JSONObject(jsonResponse);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException | JSONException e) {
            Log.e(LOG_TAG, "Problem retrieving the steam JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonObject;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}
