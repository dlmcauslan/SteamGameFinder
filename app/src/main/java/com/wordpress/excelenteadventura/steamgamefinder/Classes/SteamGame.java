package com.wordpress.excelenteadventura.steamgamefinder.Classes;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Class which stores information about a Steam Game such as
 *  the name of the game, game id, url of the game icon and banner, 
 *  and the number of minutes played by the user
 *  @author DLMcAuslan
 */
public class SteamGame implements Comparable<SteamGame>, Serializable {
    private final String gameName;
    private int minutesPlayed;
    private String gameID;
    private String gameIcon;
    private String gameBanner;
    public static final Comparator<SteamGame> BY_TIMEPLAYED = new ByTimePlayed();
    private static final String BASE_IMAGE_URL = "http://media.steampowered.com/steamcommunity/public/images/apps/";
    
    /** 
     * Class Constructor.
     * @param gameName - String containing the name of the game.
     */
    public SteamGame(String gameName) {
        this.gameName = gameName;
    }

    /**
     * Class constructor
     * @param game - Steam game object to create new object from.
     */
    public SteamGame(SteamGame game) {
        gameName = game.getName();
        minutesPlayed = game.getMinutesPlayed();
        gameID = game.getID();
        gameIcon = game.getIcon();
        gameBanner = game.getBanner();
    }

    /**
     * Getter for gameName
     * @return the name of the Game
     */
    public String getName() {
        return gameName;
    }
    
    /**
     * Setter for number of minutes played
     * @param minutesPlayed
     */
    public void setMinutesPlayed(int minutesPlayed) {
        this.minutesPlayed = minutesPlayed; 
    }
    
    /**
     * Getter for number of minutes played
     * @return amount of time the user has played the game in minutes
     */
    public int getMinutesPlayed() {
        return minutesPlayed;
    }
    
//    /**
//     * Gets the amount of time the user has played.
//     * @return a string containing the amount of time played in days, hours, minutes.
//     */
//    public String getTimePlayed() {
//        int days = minutesPlayed / (24 * 60);
//        int hours = minutesPlayed % (24 * 60) / (60);
//        int minutes = minutesPlayed % 60;
//
//        String timeString;
//        String dayString;
//        String hourString;
//        String minuteString;
//
//        if (days == 1) dayString = days + " day, ";
//        else dayString = days + " days, ";
//        if (hours == 1) hourString = hours + " hour, ";
//        else hourString = hours + " hours, ";
//        if (minutes == 1) minuteString = minutes + " minute.";
//        else minuteString = minutes + " minutes.";
//
//        if (days == 0) timeString = hourString + minuteString;
//        if (days == 0 && hours == 0) timeString =  minuteString;
//        else timeString = dayString + hourString + minuteString;
//
//        return timeString;
//    }
    
    /**
     * Setter for gameID
     * @param gameID
     */
    public void setID(String gameID) {
        this.gameID = gameID; 
    }
    
    /**
     * Getter for gameID
     * @return
     */
    public String getID() {
        return gameID;
    }
    
    /**
     * Setter for the games Icon
     * @param address - string containing URL where the game icon is stored online.
     */
    public void setIcon(String address) {
        if (address == null) throw new java.lang.NullPointerException();
        gameIcon = BASE_IMAGE_URL + getID() + "/" + address + ".jpg";
    }
    
    /**
     * Getter for the gameIcon
     * @return String link to the game Icon picture online
     */
    public String getIcon() {
        return gameIcon;
    }
    
    /**
     * Setter for the games Banner
     * @param address - string containing URL where the game Banner is stored online.
     */
    public void setBanner(String address) {
        if (address == null) throw new java.lang.NullPointerException();
        gameBanner = BASE_IMAGE_URL + getID() + "/" + address + ".jpg";
    }
    
    /**
     * Getter for the gameBanner
     * @return String link to the game Banner picture online
     */
    public String getBanner() {
        return gameBanner;
    }

    /**
     * Checks if the game object is empty
     */
    public boolean isEmpty() {
        if (gameName == null) return true;
        return false;
    }
    
    /**
     * Compares SteamGame objects by their gameName. 
     * This is the default compare method for SteamGame objects.
     */
    public int compareTo(SteamGame that) {
        return this.gameName.compareTo(that.gameName);
    }
    
    /**
     * Comparator to enable SteamGame objects to be sorted by their playtime
     * in descending order.
     */
    private static class ByTimePlayed implements Comparator<SteamGame>
    {
        public int compare(SteamGame g1, SteamGame g2) {
          if (g1.getMinutesPlayed() < g2.getMinutesPlayed()) return +1;
          if (g1.getMinutesPlayed() > g2.getMinutesPlayed()) return -1;
          return 0;
        }
    }
}
