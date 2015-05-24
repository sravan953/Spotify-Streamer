package com.biryanistudio.spotifystreamer;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Sravan on 23-May-15.
 */
public class DataHolder {
    public static List<Artist> artistsList;
    public static String artistID;
    public static List<Track> topTracksList;
    public static String mediaURL;
    public static int current;
    public static boolean twoPane;
}
