package com.wordpress.excelenteadventura.steamgamefinder;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
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
    private static final String APIKey = "D41746705B55F8AC2620F6910ACE56CF";
    private static final String LOG_TAG = Downloader.class.getSimpleName();
    
    public Downloader() {
    }
    
    
    /**
     * Sets the player summary data from the web.
     * @param user - a SteamUser object to add the data too.
     */
    public static void setUserData(SteamUser user) {
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
//                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                return;
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
            // If IOException, returns without setting user data
            // Add a toast to let user know.
            String errorMessage = "Error setting user data.\nIs the steamID correct?";
            Log.e(LOG_TAG, errorMessage, e);
        }
    }
    
    /**
     * Sets the users friendsList data from web or from file.
     * @param user - a MainUser object to add the data too. Note this method is only callable on the mainUser.
     */
    public static void setFriendsData(MainUser user) {
        // Map containing the users friends. Key - friend name, value - steamFriend object.
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
                return;
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
                friendMap.put(friend.getUserName(), friend);
                /////////////////
                // Do I add friends games now or later!
                ///////////////  
             } 
        }
        catch (IOException | JSONException e) {
            // If IOException, returns without setting user data
            // Add a toast to let user know.
            String errorMessage = "Error setting friend data.";
            Log.e(LOG_TAG, errorMessage, e);
//            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Set the users friendMap data
        user.setFriendMap(friendMap);
    }
    
    /**
     * Sets the player game data from web.
     * @param user - a SteamUser object to add the data too.
     */
    public static void setGameData(SteamUser user) {
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
                return;
            }

            // Get JsonArray containing users game data
            JSONArray jGames = dataObject.getJSONObject("response").getJSONArray("games");
            // Loop over the array creating a SteamGame object for each game and setting the object data
//            for (JSONObject jGame : jGames.getValuesAs(JSONObject.class)) {
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
            // Add a toast to let user know.
            String errorMessage = "Error getting game data.";
            Log.e(LOG_TAG, errorMessage, e);
//            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            return;
        }
        // Set the gameMap to the user.
        user.setGameMap(gameMap);
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
