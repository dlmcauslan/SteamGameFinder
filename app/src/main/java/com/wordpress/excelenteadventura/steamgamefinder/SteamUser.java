package com.wordpress.excelenteadventura.steamgamefinder;

import java.net.MalformedURLException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Class which stores information about a steam user such as their
 * steamID, userName, onlineStatus, a link to their profile picture
 * and a Map containing a list of their games.
 * @author DLMcAuslan
 *
 */
public class SteamUser implements Comparable<SteamUser>{
    private final String steamID;
    private String userName;
    private int onlineStatus;
    private int imageResourceID;
    private String[] profilePicture;
    private Map<String, SteamGame> gameMap;
    
    public static final Comparator<SteamUser> BY_ONLINESTATUS = new ByOnlineStatus();
    
    
    /**
     * Class constructor that takes in the users SteamID as a string and creates the URLs necessary
     * to access their data
     * @param steamID
     */
    public SteamUser(String steamID) {
        this.steamID = steamID;
    }
    
    
    /**
     * Getter for users steam ID
     * @return string containing steamID
     */
    public String getID(){
        return steamID;
    }
    
    /**
     * Setter for users userName
     * @param userName
     */
    public void setUserName(String userName) {
        if (userName == null) throw new java.lang.NullPointerException();
        this.userName = userName;
    }
    
    /**
     * Getter for users userName
     * @return string containing userName
     */
    public String getUserName() {
        return userName;
    }
    
    /**
     * Setter for users onlineStatus
     * @param onlineStatus
     */
    public void setOnlineStatus(int onlineStatus) {
        this.onlineStatus = onlineStatus;
    }
    
    /**
     * Getter for users onlineStatus. Probably need to update this in realtime(ish)
     * @return int representing onlineStatus
     */
    public int getOnlineStatus() {
        return onlineStatus;
    }
    
    /**
     * Converts the users online status from an integer to a string containing the
     * relevant status
     * @return string containing the users online status.
     */
    public String getOnlineString() {
        int onlineStatus = getOnlineStatus();
        if (onlineStatus < 0 || onlineStatus > 6) throw new java.lang.IllegalArgumentException();
        String[] status = new String[]{"Offline", "Online", "Busy", "Away", "Snooze",
                "Looking To Trade", "Looking To Play"};
        return status[onlineStatus];
    }
    
    /**
     * Setter for the usersProfilePicture
     * @param addresses - array of strings containing URLs where the {small, medium, large} version of the
     *                  profile picture is stored online.
     */
    public void setProfilePicture(String[] addresses) throws MalformedURLException {
        if (addresses == null) throw new java.lang.NullPointerException();
//        profilePicture = new String[addresses.length];
//        for (int i = 0; i < addresses.length; i++) {
//            profilePicture[i] = addresses[i];
//        }
        profilePicture = addresses.clone();
    }
    
    /**
     * Getter for the usersProfilePicture
     * @return URL array where links to the small, medium and large profile picture is stored
     */
    public String[] getProfilePicture() {
        return profilePicture;
    }

    
    /**
     * Setter for gameMap, contains users list of games, referenced by their
     * name. Makes a new copy of the map.
     * @param gameMap
     */
    public void setGameMap(Map<String, SteamGame> gameMap) {
        if (gameMap == null) throw new java.lang.NullPointerException();
        this.gameMap = new HashMap<String, SteamGame>(gameMap);           
    }
    
    /**
     * Getter for gameMap
     * @return a new HashMap of the users games
     */
    public Map<String, SteamGame> getGameMap() {
        return new HashMap<String, SteamGame>(gameMap);
    }
    
    /**
     * Compares SteamUser objects by their userName. This is the default compare method for SteamUser objects.
     */
    public int compareTo(SteamUser that) {
        return this.userName.toLowerCase().compareTo(that.userName.toLowerCase());
    }
    
    /**
     * Comparator to enable SteamUser objects to be sorted by their online status in descending order.
     */
    private static class ByOnlineStatus implements Comparator<SteamUser>
    {
        public int compare(SteamUser u1, SteamUser u2) {
          int u1Status = u1.getOnlineStatus();
          int u2Status = u2.getOnlineStatus();
          // Set u1(2)Status to 1 if they are Busy(2), Away(3), Snooze(4).
          // Set it to 2 if they are Online(1), Looking to trade(5), Looking to play (6).
          // Otherwise it stays as 0 (Offline).
          if (u1Status >=2 && u1Status <= 4) u1Status = 1;
          else if (u1Status >= 5 || u1Status == 1) u1Status = 2;
          if (u2Status >=2 && u2Status <= 4) u2Status = 1;
          else if (u2Status >= 5 || u2Status == 1) u2Status = 2;
          if (u1Status < u2Status) return +1;
          if (u1Status > u2Status) return -1;
          return 0;
        }
    }
}
