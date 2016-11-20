package com.prasi.popularmovies;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.prasi.popularmovies.api.MovieTrailer;
import com.prasi.popularmovies.api.MovieTrailersResponse;
import com.prasi.popularmovies.api.TheMovieDb;
import com.prasi.popularmovies.data.MovieContract;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by User on 19-11-2016.
 */
public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    private static final int MOVIE_DETAIL_LOADER = 1;
    public static final String MOVIE_ID = "MOVIE_ID";

    private static final String[] MOVIE_DETAIL_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_BACKDROP_PATH
    };

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
    private boolean reviewButtonPressed = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            movieId = savedInstanceState.getLong(MOVIE_ID);
            super.onCreateView(inflater,container,savedInstanceState);
        }

        Bundle arguments = getArguments();
        if(arguments != null)
            movieId = arguments.getLong(MOVIE_ID);
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this,rootView);
        trailerListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        trailerListView.setAdapter(new MovieTrailerAdapter(new ArrayList<MovieTrailer>()));
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        getTrailerList(movieId);
        if(movieId != -1)
            Utility.triggerLoader(getLoaderManager(),MOVIE_DETAIL_LOADER,this);
    }

    private void initializeReviewButton(final long movieId, final String movieTitle, Button reviewButton) {
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewButtonPressed = true;
                Intent reviewIntent = new Intent(getActivity(), MovieReviewActivity.class);
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
        collapsingToolbar.setCollapsedTitleTypeface(Typeface.SANS_SERIF);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedTextStyle);
        collapsingToolbar.setExpandedTitleTypeface(Typeface.SANS_SERIF);
    }

    private void getTrailerList(long movieId) {
        if(movieId == -1)
            return;

        TheMovieDb theMovieDb = Utility.getTheMovieDb();
        Call<MovieTrailersResponse> callTrailerList = theMovieDb.getMovieTrailers(movieId,Utility.THE_MOVIE_DB_API_KEY);
        callTrailerList.enqueue(new Callback<MovieTrailersResponse>() {
            @Override
            public void onResponse(Call<MovieTrailersResponse> call, Response<MovieTrailersResponse> response) {
                MovieTrailersResponse trailersResponse = response.body();
                List<MovieTrailer> trailers = trailersResponse.getListOfTrailers();
                MovieTrailerAdapter trailerAdapter = (MovieTrailerAdapter)trailerListView.getAdapter();
                trailerAdapter.setData(trailers);
                trailerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<MovieTrailersResponse> call, Throwable t) {
                Log.e(LOG_TAG, "onFailure: ", t);
            }
        });
    }

    private void onFavouriteButtonClicked(long movieId) {
        if(Utility.isAFavouriteMovie(getActivity(), movieId)) {
            Utility.removeFromFavourites(getActivity(),movieId);
        } else {
            Utility.addMovieToFavourites(getActivity(),movieId);
            Toast.makeText(getActivity(),"The movie has been marked your favourite!!",Toast.LENGTH_SHORT).show();
        }
        checkAndShowIfFavourite(movieId);
    }

    private void checkAndShowIfFavourite(long movieId) {
        if(Utility.isAFavouriteMovie(getActivity(), movieId)) {
            favouriteButton.setImageResource(R.drawable.ic_favorite_white_24dp);
            favouriteButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorFavourite)));
        } else {
            favouriteButton.setImageResource(R.drawable.ic_favorite_black_24dp);
            favouriteButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorBright)));
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(movieId == -1)
            return null;
        return new CursorLoader(getContext(),
                MovieContract.MovieEntry.buildMovieUri(movieId),
                MOVIE_DETAIL_COLUMNS,
                MovieContract.MovieEntry._ID + " = ? ",
                new String[]{String.valueOf(movieId)},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null && !data.isClosed() && data.moveToFirst()) {
            String movieTitleText = data.getString(COL_ORIGINAL_TITLE);
            movieTitle.setText(movieTitleText);
            Utility.loadImageInto(getContext(),data.getString(COL_POSTER_PATH), null, movieThumbnailPoster);
            Utility.loadImageInto(getContext(),data.getString(COL_BACKDROP_PATH), "w500", movieBackdrop);
            setCollapsingToolbarParameters(data, collapsingToolbar);
            initializeFavouriteButton(movieId, favouriteButton);
            initializeReviewButton(movieId, movieTitleText, reviewButton);
            movieOverview.setText(data.getString(COL_OVERVIEW));
            movieRating.setRating(Float.parseFloat(data.getString(COL_VOTE_AVERAGE))/2);
            movieReleaseDate.setText(Utility.getYear(data.getString(COL_RELEASE_DATE)));
            data.close();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(reviewButtonPressed) {
            outState.putLong(MOVIE_ID, movieId);
            reviewButtonPressed = false;
        } else {
            movieId = -1;
            outState.clear();
        }
        super.onSaveInstanceState(outState);
    }

    public void onMovieChanged(long movieId) {
        this.movieId = movieId;
        Utility.triggerLoader(getLoaderManager(),MOVIE_DETAIL_LOADER,this);
        getTrailerList(movieId);
    }
}
