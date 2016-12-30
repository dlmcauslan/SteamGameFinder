package com.wordpress.excelenteadventura.steamgamefinder.Classes;

/**
 * A class that extends SteamUser which is used to store data about the users
 * friends.
 * @author DLMcAuslan
 */
public class GameUser extends SteamUser{
    private int gameMinutesPlayed;

    public GameUser(String steamID) {
        super(steamID);
    }

    public GameUser(SteamUser user) {
        super(user);
    }

    public void setGameMinutesPlayed(int minutesPlayed) {
        gameMinutesPlayed = minutesPlayed;
    }

    public int getGameMinutesPlayed() {
        return gameMinutesPlayed;
    }
}
