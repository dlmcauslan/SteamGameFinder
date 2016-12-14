package com.wordpress.excelenteadventura.steamgamefinder;

import java.net.MalformedURLException;

/**
 * A class that extends SteamUser which is used to store data about the users
 * friends.
 * @author DLMcAuslan
 */
public class SteamFriend extends SteamUser {

    public SteamFriend(String steamID) throws MalformedURLException {
        super(steamID);
    }

}
