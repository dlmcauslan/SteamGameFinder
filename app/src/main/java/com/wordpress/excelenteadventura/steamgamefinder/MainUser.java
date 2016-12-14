package com.wordpress.excelenteadventura.steamgamefinder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import javax.json.Json;
//import javax.json.JsonArray;
//import javax.json.JsonObject;
//import javax.json.JsonReader;

/**
 * A class that extends SteamUser which is used to store data about the main User.
 * Main user has extra data associated with it, such as a list of their friends.
 * @author DLMcAuslan
 */
public class MainUser extends SteamUser{
    private final URL friendsListData;
    private HashMap<String, SteamFriend> friendsMap;
    
    /**
     * Class constructor that takes in the users SteamID as a string and creates the URLs necessary
     * to access their data
     * @param steamID
     * @throws MalformedURLException
     */
    public MainUser(String steamID) throws MalformedURLException {
        super(steamID);
        friendsListData = new URL("http://api.steampowered.com/ISteamUser/GetFriendList/v0001/?key=" 
                + APIKey + "&steamid=" + steamID + "&relationship=friend&format=json");
    }
    
//    /**
//     * Method to download the users data. We override this because the main User also has
//     * to download the friendsList data, which a steamFriend user does not.
//     */
//    @Override
//    public void downloadUserData() throws IOException {
//        String userID = getID();
//        downloadURL(playerSummaryData, new File(path + userID + "SummaryData.json"));
//        downloadURL(friendsListData, new File(path + userID + "FriendsData.json"));
//        downloadURL(gameStatsData, new File(path + userID + "GameData.json"));
//    }
    
    /**
     * Getter for friendsMap
     * @return a HashMap containing SteamFriend objects that are the users friends where the key is the friends username
     */
    public Map<String, SteamFriend> getFriendsMap() {
        return new HashMap<String, SteamFriend>(friendsMap);
    }
    
    /**
     * Setter for friendsMap.
     * @param friendMap
     */
    private void setFriendMap(Map<String, SteamFriend> friendMap) {
        if (friendMap == null) throw new java.lang.NullPointerException();
        this.friendsMap = new HashMap<String, SteamFriend>(friendMap);           
    }
    
    /**
     * Gets a list of users friends sorted alphabetically and by online status.
     * @return an ArrayList containing SteamFriend objects that are the users friends.
     */
    public List<SteamFriend> getFriendsList() {
        List<SteamFriend> friendsList = new ArrayList<SteamFriend>(getFriendsMap().values());
        Collections.sort(friendsList);
        Collections.sort(friendsList, SteamUser.BY_ONLINESTATUS);
        return friendsList;
    }
    
//    /**
//     * Sets the users friendsList data, either from web or from file. If there is no
//     * locally hosted data file the data is downloaded, and the data is saved.
//     * @param dataFromWeb - boolean whether the data is coming from the web (true)
//     *                      or from a locally stored file (false)
//     * @throws IOException
//     */
//    public void setFriendsData(boolean dataFromWeb) throws IOException {
//        Map<String, SteamFriend> friendMap = new HashMap<String, SteamFriend>();
//        // Are we getting the data from the web, or from file
//        InputStream is;
//        String userID = getID();
//        if (!dataFromWeb) {
//            try {
//                is = new FileInputStream(path + userID + "FriendsData.json");
//            } catch(FileNotFoundException f) {
//                downloadURL(friendsListData, new File(path + userID + "Friendsdata.json"));
//                is = new FileInputStream(path + userID + "FriendsData.json");
//            }
//        } else {
//            is = friendsListData.openStream();
//        }
//
//        // Open JSON reader and set friends data from JSON file
//        try(JsonReader rdr = Json.createReader(is)) {
//            JsonObject obj = rdr.readObject();
//            JsonArray friends = obj.getJsonObject("friendslist").getJsonArray("friends");
//            for (JsonObject jFriend : friends.getValuesAs(JsonObject.class)) {
//                String steamID = jFriend.getString("steamid");
//                SteamFriend friend = new SteamFriend(steamID);
//                friend.setUserData(dataFromWeb); // Now friend data should automatically download on first attempt at getting from file,
//                // Note that this means online status won't be current.
//                friendMap.put(friend.getUserName(), friend);
//                /////////////////
//                // Do I add friends games now or later!
//                ///////////////
//             }
//        } finally {
//            if (is != null) is.close();
//        }
//        setFriendMap(friendMap);
//    }
    
    /**
     * Searches friendsMap for a particular friend with name friendName 
     * @param friendName - name of friend to search for
     * @return SteamFriend if found, otherwise returns null
     */
    private SteamFriend getFriend(String friendName) {
        return getFriendsMap().get(friendName);
    }
          
//    /**
//     * Gets a list of all the games that the user has in common with the friends
//     * in friends.
//     * @param friends - an ArrayList of users that we are comparing to
//     * @return list of the games the users have in common
//     * @throws IOException
//     */
//    public List<SteamGame> getGamesInCommon(ArrayList<String> friends) throws IOException {
//        if (friends == null) throw new java.lang.NullPointerException("Null friends list.");
//        // Get Map of users games
//        Map<String, SteamGame> gamesInCommon = getGameMap();
//        if (gamesInCommon == null) throw new java.lang.NullPointerException("Users games list not created yet");
//
//        // For each friend in friends
//        for (String friendName: friends) {
//            SteamFriend friend = getFriend(friendName);
//            // Make sure that friend is actually on of users friends
//            if (friend == null) throw new java.lang.NullPointerException();
//            // Get the friends games list
//            friend.setGameData(true);
//            Map<String, SteamGame> friendGames = friend.getGameMap();
//            // For each game in gamesInCommon check if its in the friends games map. If they have the
//            // game add it to gamesInCommonUpdated map.
//            Map<String, SteamGame> gamesInCommonUpdated = new HashMap<String, SteamGame>();
//            for (String gameName: gamesInCommon.keySet()) {
//                // If the friend has the game, add it to gamesInCommonUpdated
//                if (friendGames.containsKey(gameName)) {
//                    gamesInCommonUpdated.put(gameName, gamesInCommon.get(gameName));
//                }
//            }
//            // replace gamesInCommon with gamesInCommonUpdated
//            gamesInCommon = new HashMap<String, SteamGame>(gamesInCommonUpdated);
//        }
//
//        // Convert to an ArrayList of SteamGame objects and then sort alphabetically and by most time played.
//        List<SteamGame> commonGamesList = new ArrayList<SteamGame>(gamesInCommon.values());
//        Collections.sort(commonGamesList);
//        Collections.sort(commonGamesList, SteamGame.BY_TIMEPLAYED);
//        return commonGamesList;
//    }
}
