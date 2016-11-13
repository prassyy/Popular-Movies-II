package com.prasi.popularmovies.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by User on 12-11-2016.
 */
public class MovieTrailersResponse {

    @SerializedName("results")
    private List<MovieTrailer> movieTrailers;

    public List<MovieTrailer> getListOfTrailers() {
        return movieTrailers;
    }
}
