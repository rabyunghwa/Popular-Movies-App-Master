package com.awesome.byunghwa.app.popularmoviesapp2.util;

import java.net.MalformedURLException;
import java.net.URL;

public class Config {

    private URL BASE_URL_POPULARITY;
    private URL BASE_URL_VOTE_AVERAGE;

    Config(int page) {
        URL url_popularity = null;
        URL url_vote_average = null;
        try {
            url_popularity = new URL("http://api.themoviedb.org/3/movie/popular?page=" + page + "&api_key=YOUR_API_KEY" );
            url_vote_average = new URL("http://api.themoviedb.org/3/movie/top_rated?page=" + page + "&api_key=YOUR_API_KEY" );
        } catch (MalformedURLException ignored) {
            // TODO: throw a real error
        }

        BASE_URL_POPULARITY = url_popularity;
        BASE_URL_VOTE_AVERAGE = url_vote_average;
    }

    public URL getBASE_URL_POPULARITY() {
        return BASE_URL_POPULARITY;
    }

    public URL getBASE_URL_VOTE_AVERAGE() {
        return BASE_URL_VOTE_AVERAGE;
    }
}
