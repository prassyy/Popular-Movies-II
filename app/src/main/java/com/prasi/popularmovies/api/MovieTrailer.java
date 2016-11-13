package com.prasi.popularmovies.api;

import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 12-11-2016.
 */
public class MovieTrailer {
    @SerializedName("name") private String trailerName;

    @SerializedName("key") private String youtubeVideoKey;

    public String getTrailerName() {
        return trailerName;
    }

    public String getYoutubeVideoKey() {
        return youtubeVideoKey;
    }
}
