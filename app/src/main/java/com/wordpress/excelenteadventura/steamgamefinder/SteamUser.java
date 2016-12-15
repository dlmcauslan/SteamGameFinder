package com.wordpress.excelenteadventura.steamgamefinder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

//import javax.json.Json;
//import javax.json.JsonArray;
//import javax.json.JsonObject;
//import javax.json.JsonReader;
//
//import org.apache.commons.io.FileUtils;

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
    private URL[] profilePicture;
    private Map<String, SteamGame> gameMap;
    
    protected static final String path = "../Steam Game Finder V2/data/";
    protected static final String APIKey = "D41746705B55F8AC2620F6910ACE56CF";
//    protected final URL playerSummaryData;
//    protected final URL gameStatsData;
    
    public static final Comparator<SteamUser> BY_ONLINESTATUS = new ByOnlineStatus();
    
    /**
     * Class constructor that takes in the users SteamID as a string and creates the URLs necessary
     * to access their data
     * @param steamID
     * @throws MalformedURLException
     */
    public SteamUser(String steamID){
        this.steamID = steamID;
//        try {
//            playerSummaryData = new URL("http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key="
//                    + APIKey + "&steamids=" + steamID + "&format=json");
//            gameStatsData = new URL("http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/?key="
//                    + APIKey + "&steamid=" + steamID + "&include_appinfo=1&format=json");
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
    }


//    /**
//     * Temporary constructor because downloading isn't implemented yet.
//     * Remember to remove from Steamfriend class too, when removing this.
//     * Resource id needs to be fixed too.
//     * @param userName
//     * @param onlineStatus
//     */
//    public SteamUser(String steamID, String userName, int onlineStatus, int imageResourceID) throws MalformedURLException {
//        this.steamID = steamID;
//        this.userName = userName;
//        this.onlineStatus = onlineStatus;
//        this.imageResourceID = imageResourceID;
//        playerSummaryData = new URL("http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key="
//                + APIKey + "&steamids=" + steamID + "&format=json");
//        gameStatsData = new URL("http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/?key="
//                + APIKey + "&steamid=" + steamID + "&include_appinfo=1&format=json");
//    }
    
//    /**
//     * Downloads the users player summary and game data and saves it as JSON files
//     * @throws IOException
//     */
//    public void downloadUserData() throws IOException {
//        String userID = getID();
//        downloadURL(playerSummaryData, new File(path + userID + "SummaryData.json"));
//        downloadURL(gameStatsData, new File(path + userID + "GameData.json"));
//    }
//
//    /**
//     * Helper method to download web data to file
//     * @param url
//     * @param fName - file name to store the data
//     * @throws IOException
//     */
//    protected static void downloadURL(URL url, File fName) throws IOException {
//        FileUtils.copyURLToFile(url, fName);
//    }
    
//    /**
//     * Sets the player summary data, either from web or from file. If there is no
//     * locally hosted data file the data is downloaded, and the data is saved.
//     * @param dataFromWeb - boolean whether the data is coming from the web (true)
//     *                      or from a locally stored file (false)
//     * @throws IOException
//     */
//    public void setUserData(boolean dataFromWeb) throws IOException {
//        // Are we getting the data from the web, or from file
//        InputStream is;
//        String userID = getID();
//        if (!dataFromWeb) {
//            try {
//                is = new FileInputStream(path + userID + "SummaryData.json");
//            } catch(FileNotFoundException f) {
//                downloadURL(playerSummaryData, new File(path + userID + "SummaryData.json"));
//                is = new FileInputStream(path + userID + "SummaryData.json");
//            }
//        } else {
//            is = playerSummaryData.openStream();
//        }
//        // Open JSON reader and set user data from JSON file
//        try (JsonReader rdr = Json.createReader(is)) {
//            JsonObject obj = rdr.readObject();
//            JsonObject user = obj.getJsonObject("response").getJsonArray("players").getJsonObject(0);
//            setUserName(user.getString("personaname"));
//            setOnlineStatus(user.getInt("personastate"));
//            String[] profilePictures = new String[] { user.getString("avatar"),
//                                                      user.getString("avatarmedium"),
//                                                      user.getString("avatarfull") };
//            setProfilePicture(profilePictures);
//        } finally {
//            if (is != null) is.close();
//        }
//    }
    
//    /**
//     * Sets the player game data, either from web or from file. If there is no
//     * locally hosted data file the data is downloaded, and the data is saved.
//     * @param dataFromWeb - boolean whether the data is coming from the web (true)
//     *                      or from a locally stored file (false)
//     * @throws IOException
//     */
//    public void setGameData(boolean dataFromWeb) throws IOException {
//        Map<String, SteamGame> gameMap = new HashMap<String, SteamGame>();
//        // Load game data either from web, or from file
//        InputStream is;
//        String userID = getID();
//        if (!dataFromWeb) {
//            try {
//                is = new FileInputStream(path + userID + "GameData.json");
//            } catch(FileNotFoundException f) {
//                downloadURL(gameStatsData, new File(path + userID + "GameData.json"));
//                is = new FileInputStream(path + userID + "GameData.json");
//            }
//        } else {
//            is = gameStatsData.openStream();
//        }
//
//     // Open JSON reader and set game data from JSON file
//        try (JsonReader rdr = Json.createReader(is)) {
//            JsonObject obj = rdr.readObject();
//            JsonArray jGames = obj.getJsonObject("response").getJsonArray("games");
//            for (JsonObject jGame : jGames.getValuesAs(JsonObject.class)) {
//                String gameName = jGame.getString("name");
//                SteamGame game = new SteamGame(gameName);
//                game.setMinutesPlayed(jGame.getInt("playtime_forever"));
//                game.setID(String.valueOf(jGame.getInt("appid")));
//                game.setIcon(jGame.getString("img_icon_url"));
//                game.setBanner(jGame.getString("img_logo_url"));
//                if (!game.isEmpty()) gameMap.put(gameName, game);
//            }
//        } finally {
//            if (is != null) is.close();
//        }
//        setGameMap(gameMap);
//    }
    
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
     * Setter for users imageResourceID
     * @param imageResourceID
     */
    public void setImageResourceID(int imageResourceID) {
        this.imageResourceID = imageResourceID;
    }


    /**
     * Getter for users imageResourceId.
     * @return int representing resourceID
     */
    public int getImageResourceId() {
        return imageResourceID;
    }

    /**
     * Setter for the usersProfilePicture
     * @param addresses - array of strings containing URLs where the {small, medium, large} version of the
     *                  profile picture is stored online.
     * @throws MalformedURLException
     */
    public void setProfilePicture(String[] addresses) throws MalformedURLException {
        if (addresses == null) throw new java.lang.NullPointerException();
        profilePicture = new URL[addresses.length];
        for (int i = 0; i < addresses.length; i++) {
            profilePicture[i] = new URL(addresses[i]);
        }
    }
    
    /**
     * Getter for the usersProfilePicture
     * @return URL array where links to the small, medium and large profile picture is stored
     */
    public URL[] getProfilePicture() {
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
