package com.wordpress.excelenteadventura.steamgamefinder.Classes;

import com.wordpress.excelenteadventura.steamgamefinder.Utilities.Downloader;

import java.io.Serializable;
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
public class MainUser extends SteamUser implements Serializable{
    private HashMap<String, SteamFriend> friendsMap;
    private Map<String, SteamGameCombined> gamesInCommonMap;
    
    
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
        if (friendsMap == null) return null;
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
     * Searches friendsMap for a particular friend with ID friendID
     * @param friendID - name of friend to search for
     * @return SteamFriend if found, otherwise returns null
     */
    public SteamFriend getFriend(String friendID) {
        return getFriendsMap().get(friendID);
    }


    /**
     * Gets a list of all the games that the user has in common with the friends
     * in friends.
     * @param friends - an ArrayList of users that we are comparing to
     */
    public void findGamesInCommon(List<String> friends) {
        if (friends == null) throw new java.lang.NullPointerException("Null friends list.");
        // Get Map of users games
        Map<String, SteamGame> mainUserGames = getGameMap();
        if (mainUserGames == null) throw new java.lang.NullPointerException("Users games list not created yet");

        Map<String, SteamGameCombined> gamesInCommon = new HashMap<String, SteamGameCombined>();
        // Loop over users games adding them to gamesInCommon.
        for (String gameName: mainUserGames.keySet()) {
            // Create new combinedGame object
            SteamGameCombined combinedGame = new SteamGameCombined(mainUserGames.get(gameName));
            // Add mainUser to timeBreakdown and update (total) minutes played
            combinedGame.addUserToTimeBreakdown(this, mainUserGames.get(gameName).getMinutesPlayed());
            // Add combined game to gamesInCommon Map
            gamesInCommon.put(gameName, combinedGame);
        }


        // For each friend in friends
        for (String friendName: friends) {
            SteamFriend friend = getFriend(friendName);
            // Make sure that friend is actually one of users friends
            if (friend == null) throw new java.lang.NullPointerException();

            // Get the friends games list
            Downloader.setGameData(friend);

            Map<String, SteamGame> friendGames = friend.getGameMap();
            // For each game in gamesInCommon check if its in the friends games map. If they have the
            // game add it to gamesInCommonUpdated map.
            Map<String, SteamGameCombined> gamesInCommonUpdated = new HashMap<String, SteamGameCombined>();
            for (String gameName: gamesInCommon.keySet()) {
                // If the friend has the game, add it to gamesInCommonUpdated
                if (friendGames.containsKey(gameName)) {
                    SteamGameCombined game = gamesInCommon.get(gameName);
                    // Add friend to timeBreakdown and update (total) minutes played
                    game.addUserToTimeBreakdown(friend, friendGames.get(gameName).getMinutesPlayed());
                    // Add game to new gamesInCommon Map
                    gamesInCommonUpdated.put(gameName, game);
                }
            }
            // replace gamesInCommon with gamesInCommonUpdated
            gamesInCommon = new HashMap<String, SteamGameCombined>(gamesInCommonUpdated);
        }
        gamesInCommonMap = new HashMap<String, SteamGameCombined>(gamesInCommon);
//        return gamesInCommon;
//        // Convert to an ArrayList of SteamGame objects and then sort alphabetically and by most time played.
//        List<SteamGame> commonGamesList = new ArrayList<SteamGame>(gamesInCommon.values());
//        Collections.sort(commonGamesList);
//        Collections.sort(commonGamesList, SteamGame.BY_TIMEPLAYED);
//        return commonGamesList;
    }

    /**
     * Gets the games in common as an ArrayList that is sorted by time played.
     * @return ArrayList of steam games.
     */
    public List<SteamGame> getGamesInCommonList() {
        if (gamesInCommonMap == null) return null;
        // Convert to an ArrayList of SteamGame objects and then sort alphabetically and by most time played.
        List<SteamGame> commonGamesList = new ArrayList<SteamGame>(gamesInCommonMap.values());
        Collections.sort(commonGamesList);
        Collections.sort(commonGamesList, SteamGame.BY_TIMEPLAYED);
        return commonGamesList;
    }

    /**
     * Getter for gamesInCommonMap
     * @return HashMap containing the games in common.
     */
    public Map<String, SteamGameCombined> getGamesInCommonMap() {
        if (gamesInCommonMap == null) return null;
        return new HashMap<String, SteamGameCombined>(gamesInCommonMap);
    }

//    /**
//     * Gets a list of all the games that the user has in common with the friends
//     * in friends.
//     * @param friends - an ArrayList of users that we are comparing to
//     * @return list of the games the users have in common
//     */
//    public List<SteamGame> getGamesInCommon(List<String> friends) {
//        if (friends == null) throw new java.lang.NullPointerException("Null friends list.");
//        // Get Map of users games
//        Map<String, SteamGame> gamesInCommon = getGameMap();
//        if (gamesInCommon == null) throw new java.lang.NullPointerException("Users games list not created yet");
//
//        // For each friend in friends
//        for (String friendName: friends) {
//            SteamFriend friend = getFriend(friendName);
//            // Make sure that friend is actually one of users friends
//            if (friend == null) throw new java.lang.NullPointerException();
//            // Get the friends games list
//            Downloader.setGameData(friend);
//            Map<String, SteamGame> friendGames = friend.getGameMap();
//            // For each game in gamesInCommon check if its in the friends games map. If they have the
//            // game add it to gamesInCommonUpdated map.
//            Map<String, SteamGame> gamesInCommonUpdated = new HashMap<String, SteamGame>();
//            for (String gameName: gamesInCommon.keySet()) {
//                // If the friend has the game, add it to gamesInCommonUpdated
//                if (friendGames.containsKey(gameName)) {
//                    SteamGame game = gamesInCommon.get(gameName);
//                    // Update total time played.
//                    game.setMinutesPlayed(game.getMinutesPlayed() + friendGames.get(gameName).getMinutesPlayed());
//                    // Add game to new gamesInCommon Map
//                    gamesInCommonUpdated.put(gameName, game);
//
//                    SteamGameCombined combinedGame = new SteamGameCombined(gamesInCommon.get(gameName));
//                    combinedGame.addUserToTimeBreakdown(friend, game.getMinutesPlayed());
//                    // Add game to new gamesInCommon Map
//                    gamesInCommonUpdated.put(gameName, combinedGame);
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
