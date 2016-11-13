package com.prasi.popularmovies;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.prasi.popularmovies.api.MovieTrailer;
import com.prasi.popularmovies.api.MovieTrailersResponse;
import com.prasi.popularmovies.api.TheMovieDb;
import com.prasi.popularmovies.data.MovieContract;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by User on 06-11-2016.
 */
public class MovieDetailAnimatedActivity extends AppCompatActivity {

    private static final String LOG_TAG = MovieDetailAnimatedActivity.class.getSimpleName();

    private static String[] MOVIE_DETAIL_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_BACKDROP_PATH
    };
    private static final int COL_MOVIE_ID = 0;
    private static final int COL_ORIGINAL_TITLE = 1;
    private static final int COL_POSTER_PATH = 2;
    private static final int COL_OVERVIEW = 3;
    private static final int COL_VOTE_AVERAGE = 4;
    private static final int COL_RELEASE_DATE = 5;
    private static final int COL_BACKDROP_PATH = 6;

    @BindView(R.id.movie_title) TextView movieTitle;
    @BindView(R.id.movie_thumbnail) ImageView movieThumbnailPoster;
    @BindView(R.id.movie_back_drop) ImageView movieBackdrop;
    @BindView(R.id.movie_overview) TextView movieOverview;
    @BindView(R.id.user_rating) RatingBar movieRating;
    @BindView(R.id.release_date) TextView movieReleaseDate;
    @BindView(R.id.review_button) Button reviewButton;
    @BindView(R.id.main_collapsing) CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.favourite_button) FloatingActionButton favouriteButton;
    @BindView(R.id.trailer_recycler_view) RecyclerView trailerListView;

    private long movieId = -1;

    @Override
    protected void onStart() {
        super.onStart();
        getTrailerList(movieId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_anim);
        ButterKnife.bind(this);

        if(movieId == -1)
            movieId = getIntent().getLongExtra("movie_id",-1);

        Cursor movieDetailCursor = getContentResolver().query(MovieContract.MovieEntry.buildMovieUri(movieId),
                MOVIE_DETAIL_COLUMNS,
                MovieContract.MovieEntry._ID + " = ? ",
                new String[]{String.valueOf(movieId)},
                null);

        if(movieDetailCursor != null && movieDetailCursor.moveToFirst()) {
            String movieTitleText = movieDetailCursor.getString(COL_ORIGINAL_TITLE);
            movieTitle.setText(movieTitleText);
            Utility.loadImageInto(this,movieDetailCursor.getString(COL_POSTER_PATH), null, movieThumbnailPoster);
            Utility.loadImageInto(this,movieDetailCursor.getString(COL_BACKDROP_PATH), "w500",movieBackdrop);
            setCollapsingToolbarParameters(movieDetailCursor, collapsingToolbar);
            initializeFavouriteButton(movieId, favouriteButton);
            initializeReviewButton(movieId, movieTitleText, reviewButton);
            movieOverview.setText(movieDetailCursor.getString(COL_OVERVIEW));
            movieRating.setRating(Float.parseFloat(movieDetailCursor.getString(COL_VOTE_AVERAGE))/2);
            movieReleaseDate.setText(Utility.getYear(movieDetailCursor.getString(COL_RELEASE_DATE)));
            trailerListView.setLayoutManager(new LinearLayoutManager(this));
            movieDetailCursor.close();
        }
    }

    private void initializeReviewButton(final long movieId, final String movieTitle, Button reviewButton) {
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reviewIntent = new Intent(getApplicationContext(), MovieReviewActivity.class);
                reviewIntent.putExtra("movie_id", movieId);
                reviewIntent.putExtra("movie_title",movieTitle);
                startActivity(reviewIntent);
            }
        });
    }

    private void initializeFavouriteButton(final long movieId, FloatingActionButton favouriteButton) {
        checkAndShowIfFavourite(movieId);
        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFavouriteButtonClicked(movieId);
            }
        });
    }

    private void setCollapsingToolbarParameters(Cursor movieDetailCursor, CollapsingToolbarLayout collapsingToolbar) {
        collapsingToolbar.setTitle(movieDetailCursor.getString(COL_ORIGINAL_TITLE));
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedTextStyle);
//        collapsingToolbar.setCollapsedTitleTypeface(Typeface.createFromAsset(getAssets(),"fonts/hobo.otf"));
        collapsingToolbar.setCollapsedTitleTypeface(Typeface.SANS_SERIF);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedTextStyle);
        collapsingToolbar.setExpandedTitleTypeface(Typeface.SANS_SERIF);
    }

    private void getTrailerList(long movieId) {
        if(movieId == -1)
            return;

        TheMovieDb theMovieDb = Utility.getTheMovieDb();
        Call<MovieTrailersResponse> callTrailerList = theMovieDb.getMovieTrailers(movieId,BuildConfig.THE_MOVIEDB_API_KEY);
        callTrailerList.enqueue(new Callback<MovieTrailersResponse>() {
            @Override
            public void onResponse(Call<MovieTrailersResponse> call, Response<MovieTrailersResponse> response) {
                MovieTrailersResponse trailersResponse = response.body();
                List<MovieTrailer> trailers = trailersResponse.getListOfTrailers();
                MovieTrailerAdapter trailerAdapter = new MovieTrailerAdapter(trailers);
                trailerListView.setAdapter(trailerAdapter);
            }

            @Override
            public void onFailure(Call<MovieTrailersResponse> call, Throwable t) {
                Log.e(LOG_TAG, "onFailure: ", t);
            }
        });
    }

    private void onFavouriteButtonClicked(long movieId) {
        if(Utility.isAFavouriteMovie(getApplicationContext(), movieId)) {
            Utility.removeFromFavourites(getApplicationContext(),movieId);
        } else {
            Utility.addMovieToFavourites(getApplicationContext(),movieId);
            Toast.makeText(getApplicationContext(),"The movie has been marked your favourite!!",Toast.LENGTH_SHORT).show();
        }
        checkAndShowIfFavourite(movieId);
    }

    private void checkAndShowIfFavourite(long movieId) {
       if(Utility.isAFavouriteMovie(getApplicationContext(), movieId)) {
            favouriteButton.setImageResource(R.drawable.ic_favorite_white_24dp);
            favouriteButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorFavourite)));
        } else {
            favouriteButton.setImageResource(R.drawable.ic_favorite_black_24dp);
            favouriteButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorBright)));
        }
    }
}
