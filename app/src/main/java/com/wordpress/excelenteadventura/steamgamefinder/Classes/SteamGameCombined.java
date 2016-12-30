package com.wordpress.excelenteadventura.steamgamefinder.Classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that extends from SteamGame which holds the combined Steam Game data.
 * Including how much of the total time played is from different users.
 * Created by DLM on 12/29/2016.
 */

public class SteamGameCombined extends SteamGame implements Serializable {
    // Map of the steam users that contribute to the total time played for the game, and their
    // individual time played.
//    private Map<SteamUser, Integer> gameUsers;
    private List<GameUser> gameUsers;

    /**
     * Class Constructor
     * @param gameName - String containing the name of the game.
     */
    public SteamGameCombined(String gameName) {
        super(gameName);
        gameUsers = new ArrayList<GameUser>();
    }

    /**
     * Class Constructor
     * @param game - SteamGame object that the new SteamGameCombined is created from.
     */
    public SteamGameCombined(SteamGame game) {
        super(game);
        setMinutesPlayed(0);
        gameUsers = new ArrayList<GameUser>();
    }

    /**
     * Getter for gameUsers map
     * @return gameUsers Hashmap
     */
    public ArrayList<GameUser> getGameUsers() {
        if (gameUsers == null) return null;
        return new ArrayList<GameUser>(gameUsers);
    }

    /**
     * Adds the user and their time played of the game to gameUsers.
     * Also adds their time played to the totalTimePlayed.
     * @param user - the user to add to gameUsers map
     * @param minutesPlayed - number of minutes the user has played the game.
     */
    public void addUserToTimeBreakdown(SteamUser user, int minutesPlayed) {
        GameUser gUser = new GameUser(user);
        gUser.setGameMinutesPlayed(minutesPlayed);
        gameUsers.add(gUser);
        setMinutesPlayed(getMinutesPlayed() + minutesPlayed);
    }
}
