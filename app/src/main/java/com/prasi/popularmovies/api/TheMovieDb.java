package com.prasi.popularmovies.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by User on 25-07-2016.
 */
public interface TheMovieDb {
    String SORT_CRITERIA_PARAM = "sort_by";
    String API_KEY_PARAM = "api_key";

    @GET("discover/movie")
    Call<MovieDetailsResponse> getMovieDetails(@Query(SORT_CRITERIA_PARAM)String sortBy, @Query(API_KEY_PARAM)String apiKey);

    @GET("movie/{movie_id}/videos")
    Call<MovieTrailersResponse> getMovieTrailers(@Path("movie_id")long movieId, @Query(API_KEY_PARAM)String apiKey);

    @GET("movie/{movie_id}/reviews")
    Call<MovieReviewsResponse> getMovieReviews(@Path("movie_id")long movieId, @Query(API_KEY_PARAM)String apiKey);
}
