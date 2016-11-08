package com.prasi.popularmovies;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.prasi.popularmovies.data.MovieContract;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MovieDetailFragment())
                    .commit();
        }
    }

    public static class MovieDetailFragment extends Fragment {

        private String LOG_TAG = MovieDetailFragment.class.getSimpleName();

        private static String[] MOVIE_DETAIL_COLUMNS = {
                MovieContract.MovieEntry._ID,
                MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
                MovieContract.MovieEntry.COLUMN_POSTER_PATH,
                MovieContract.MovieEntry.COLUMN_OVERVIEW,
                MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE
        };
        private static final int COL_MOVIE_ID = 0;
        private static final int COL_ORIGINAL_TITLE = 1;
        private static final int COL_POSTER_PATH = 2;
        private static final int COL_OVERVIEW = 3;
        private static final int COL_VOTE_AVERAGE = 4;
        private static final int COL_RELEASE_DATE = 5;

        @BindView(R.id.movie_title) TextView movieTitle;
        @BindView(R.id.movie_thumbnail) ImageView movieThumbnailPoster;
        @BindView(R.id.movie_overview) TextView movieOverview;
        @BindView(R.id.user_rating) TextView movieRating;
        @BindView(R.id.release_date) TextView movieReleaseDate;

        public MovieDetailFragment() {
            setHasOptionsMenu(false);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            ButterKnife.bind(this,rootView);

            long movieId = getActivity().getIntent().getLongExtra("movie_id",1);
            Cursor movieDetailCursor = getActivity().getContentResolver().query(MovieContract.MovieEntry.buildMovieUri(movieId),
                    MOVIE_DETAIL_COLUMNS,
                    MovieContract.MovieEntry._ID+" = ? ",
                    new String[]{String.valueOf(movieId)},
                    null);

            if(movieDetailCursor != null) {
                movieDetailCursor.moveToFirst();
                movieTitle.setText(movieDetailCursor.getString(MovieDetailFragment.COL_ORIGINAL_TITLE));
                Utility.loadImageInto(getActivity(),movieDetailCursor.getString(MovieDetailFragment.COL_POSTER_PATH),movieThumbnailPoster);
                movieOverview.setText(movieDetailCursor.getString(MovieDetailFragment.COL_OVERVIEW));
                movieRating.setText(getResources().getString(R.string.rating_heading, movieDetailCursor.getString(MovieDetailFragment.COL_VOTE_AVERAGE)));
                movieReleaseDate.setText(getResources().getString(R.string.release_date_heading, movieDetailCursor.getString(MovieDetailFragment.COL_RELEASE_DATE)));
                movieDetailCursor.close();
            }
            return rootView;
        }
    }
}
