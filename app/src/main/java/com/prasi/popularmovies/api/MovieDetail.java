package com.prasi.popularmovies.api;

import com.google.gson.annotations.SerializedName;

public class MovieDetail {

    @SerializedName("poster_path") private String posterPath;
    @SerializedName("adult") private String adult;
    @SerializedName("overview") private String overview;
    @SerializedName("release_date") private String releaseDate;
    @SerializedName("id") private int id;
    @SerializedName("original_title") private String originalTitle;
    @SerializedName("backdrop_path") private String backdrop_path;
    @SerializedName("popularity") private String popularity;
    @SerializedName("vote_average") private String voteAverage;
    @SerializedName("vote_count") private String voteCount;

    public String getAdult() {
        return adult;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public int getId() {
        return id;
    }

    public String getPopularity() {
        return popularity;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getVoteCount() {
        return voteCount;
    }
}