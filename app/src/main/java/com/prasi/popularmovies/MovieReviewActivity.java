package com.prasi.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.prasi.popularmovies.api.MovieReview;
import com.prasi.popularmovies.api.MovieReviewsResponse;
import com.prasi.popularmovies.api.TheMovieDb;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by User on 13-11-2016.
 */
public class MovieReviewActivity extends AppCompatActivity{

    @BindView(R.id.review_list) RecyclerView reviewRecyclerView;

    private long movieId = -1;
    private static final String LOG_TAG = MovieReviewActivity.class.getSimpleName();

    @Override
    protected void onStart() {
        super.onStart();
        getReviewsList(movieId);
    }

    /*Had to override this method to make the back button in the title bar to return to the previous activity without crashing*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        ButterKnife.bind(this);

        String movieTitle = getIntent().getStringExtra("movie_title");
        setTitle(getResources().getString(R.string.review_title, movieTitle));
        movieId = getIntent().getLongExtra("movie_id",-1);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getReviewsList(long movieId) {
        if(movieId == -1)
            return; //This is just to skip the API call when preference is "Favourites", as the data is already available in DB
        TheMovieDb theMovieDb = Utility.getTheMovieDb();
        Call<MovieReviewsResponse> callReviewsList = theMovieDb.getMovieReviews(movieId,BuildConfig.THE_MOVIE_DB_API_KEY);
        callReviewsList.enqueue(new Callback<MovieReviewsResponse>() {
            @Override
            public void onResponse(Call<MovieReviewsResponse> call, Response<MovieReviewsResponse> response) {
                MovieReviewsResponse reviewsResponse = response.body();
                List<MovieReview> reviews = reviewsResponse.getMovieReviews();
                MovieReviewsAdapter reviewsAdapter = new MovieReviewsAdapter(reviews);
                reviewRecyclerView.setAdapter(reviewsAdapter);
            }

            @Override
            public void onFailure(Call<MovieReviewsResponse> call, Throwable t) {
                Log.e(LOG_TAG, "onFailure: ", t);
            }
        });
    }
}
