package com.awesome.byunghwa.app.mytvapplication.util;

import java.net.MalformedURLException;
import java.net.URL;

public class Config {
    public static final URL BASE_URL_POPULARITY;
    public static final URL BASE_URL_VOTE_AVERAGE;

    static {
        URL url_popularity = null;
        URL url_vote_average = null;
        try {
            url_popularity = new URL("http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=YOUR_API_KEY" );
            url_vote_average = new URL("http://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&api_key=YOUR_API_KEY" );
        } catch (MalformedURLException ignored) {
            // TODO: throw a real error
        }

        BASE_URL_POPULARITY = url_popularity;
        BASE_URL_VOTE_AVERAGE = url_vote_average;
    }
}
