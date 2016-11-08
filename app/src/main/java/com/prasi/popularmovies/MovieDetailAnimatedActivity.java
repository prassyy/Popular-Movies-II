package com.prasi.popularmovies;

import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.prasi.popularmovies.data.MovieContract;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 06-11-2016.
 */
public class MovieDetailAnimatedActivity extends AppCompatActivity {

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
    @BindView(R.id.user_rating) TextView movieRating;
    @BindView(R.id.release_date) TextView movieReleaseDate;
    @BindView(R.id.main_collapsing) CollapsingToolbarLayout collapsingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_anim);
        ButterKnife.bind(this);

        long movieId = getIntent().getLongExtra("movie_id",1);
        Cursor movieDetailCursor = getContentResolver().query(MovieContract.MovieEntry.buildMovieUri(movieId),
                MOVIE_DETAIL_COLUMNS,
                MovieContract.MovieEntry._ID+" = ? ",
                new String[]{String.valueOf(movieId)},
                null);

        if(movieDetailCursor != null) {
            movieDetailCursor.moveToFirst();
            movieTitle.setText(movieDetailCursor.getString(COL_ORIGINAL_TITLE));
            Utility.loadImageInto(this,movieDetailCursor.getString(COL_POSTER_PATH),movieThumbnailPoster);
            Utility.loadImageInto(this,movieDetailCursor.getString(COL_BACKDROP_PATH),movieBackdrop);

            collapsingToolbar.setTitle(movieDetailCursor.getString(COL_ORIGINAL_TITLE));
            collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedTextStyle);
            collapsingToolbar.setCollapsedTitleTypeface(Typeface.createFromAsset(getAssets(),"fonts/hobo.otf"));
            collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedTextStyle);
            collapsingToolbar.setExpandedTitleTypeface(Typeface.createFromAsset(getAssets(),"fonts/hobo.otf"));

            movieOverview.setText(movieDetailCursor.getString(COL_OVERVIEW));
            movieRating.setText(getResources().getString(R.string.rating_heading, movieDetailCursor.getString(COL_VOTE_AVERAGE)));
            movieReleaseDate.setText(getResources().getString(R.string.release_date_heading, movieDetailCursor.getString(COL_RELEASE_DATE)));
            movieDetailCursor.close();
        }
    }

}
