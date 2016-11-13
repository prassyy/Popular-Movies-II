package com.prasi.popularmovies.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by User on 12-11-2016.
 */
public class MovieReviewsResponse {
    @SerializedName("results")
    private List<MovieReview> movieReviews;

    public List<MovieReview> getMovieReviews() {
        return movieReviews;
    }
}
