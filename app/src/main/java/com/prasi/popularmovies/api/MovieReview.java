package com.prasi.popularmovies.api;

import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 13-11-2016.
 */
public class MovieReview {
    @SerializedName("author") String reviewAuthor;
    @SerializedName("content") String reviewContent;
    @SerializedName("url") String reviewUrl;

    public String getReviewAuthor() {
        return reviewAuthor;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public String getReviewUrl() {
        return reviewUrl;
    }
}
