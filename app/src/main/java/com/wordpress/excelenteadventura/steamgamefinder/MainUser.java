package com.wordpress.excelenteadventura.steamgamefinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class that extends SteamUser which is used to store data about the main User.
 * Main user has extra data associated with it, such as a list of their friends.
 * @author DLMcAuslan
 */
public class MainUser extends SteamUser{
    private HashMap<String, SteamFriend> friendsMap;
    
    
    /**
     * Class constructor that takes in the users SteamID as a string and creates the URLs necessary
     * to access their data
     * @param steamID
     */
    public MainUser(String steamID) {
        super(steamID);
    }
    
    
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
    public void setFriendMap(Map<String, SteamFriend> friendMap) {
        if (friendMap == null) throw new java.lang.NullPointerException();
        this.friendsMap = new HashMap<String, SteamFriend>(friendMap);           
    }
    
    /**
     * Gets a list of users friends sorted alphabetically and by online status.
     * @return an ArrayList containing SteamFriend objects that are the users friends.
     */
    public ArrayList<SteamFriend> getFriendsList() {
        ArrayList<SteamFriend> friendsList = new ArrayList<SteamFriend>(getFriendsMap().values());
        Collections.sort(friendsList);
        Collections.sort(friendsList, SteamUser.BY_ONLINESTATUS);
        return friendsList;
    }
    
    /**
     * Searches friendsMap for a particular friend with name friendName 
     * @param friendName - name of friend to search for
     * @return SteamFriend if found, otherwise returns null
     */
    private SteamFriend getFriend(String friendName) {
        return getFriendsMap().get(friendName);
    }
          
    /**
     * Gets a list of all the games that the user has in common with the friends
     * in friends.
     * @param friends - an ArrayList of users that we are comparing to
     * @return list of the games the users have in common
     */
    public List<SteamGame> getGamesInCommon(ArrayList<String> friends) {
        if (friends == null) throw new java.lang.NullPointerException("Null friends list.");
        // Get Map of users games
        Map<String, SteamGame> gamesInCommon = getGameMap();
        if (gamesInCommon == null) throw new java.lang.NullPointerException("Users games list not created yet");
         
        // For each friend in friends
        for (String friendName: friends) {
            SteamFriend friend = getFriend(friendName);
            // Make sure that friend is actually on of users friends
            if (friend == null) throw new java.lang.NullPointerException();
            // Get the friends games list
            Downloader.setGameData(friend);
            Map<String, SteamGame> friendGames = friend.getGameMap();
            // For each game in gamesInCommon check if its in the friends games map. If they have the 
            // game add it to gamesInCommonUpdated map.
            Map<String, SteamGame> gamesInCommonUpdated = new HashMap<String, SteamGame>();
            for (String gameName: gamesInCommon.keySet()) {
                // If the friend has the game, add it to gamesInCommonUpdated
                if (friendGames.containsKey(gameName)) {
                    gamesInCommonUpdated.put(gameName, gamesInCommon.get(gameName));
                }
            }
            // replace gamesInCommon with gamesInCommonUpdated
            gamesInCommon = new HashMap<String, SteamGame>(gamesInCommonUpdated);
        }
        
        // Convert to an ArrayList of SteamGame objects and then sort alphabetically and by most time played.
        List<SteamGame> commonGamesList = new ArrayList<SteamGame>(gamesInCommon.values());
        Collections.sort(commonGamesList);
        Collections.sort(commonGamesList, SteamGame.BY_TIMEPLAYED);
        return commonGamesList;
    }
}
