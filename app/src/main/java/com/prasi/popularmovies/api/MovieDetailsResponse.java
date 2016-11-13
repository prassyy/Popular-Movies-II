package com.prasi.popularmovies.api;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 26-07-2016.
 */
public class MovieDetailsResponse {

    @SerializedName("results")
    List<MovieDetail> movieDetailsList;

    public MovieDetailsResponse() {
        movieDetailsList = new ArrayList<>();
    }

    public List<MovieDetail> getMovieDetailsList() {
        return movieDetailsList;
    }
}
