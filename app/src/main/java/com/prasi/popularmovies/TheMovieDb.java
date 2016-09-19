package com.prasi.popularmovies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by User on 25-07-2016.
 */
public interface TheMovieDb {
    final String SORT_CRITERIA_PARAM = "sort_by";
    final String API_KEY_PARAM = "api_key";

    @GET("discover/movie")
    Call<MovieDetailsResponse> getMovieDetails(@Query(SORT_CRITERIA_PARAM)String sortBy,@Query(API_KEY_PARAM)String apiKey);
}
