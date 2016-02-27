package com.prasi.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by User on 27-02-2016.
 */
public class MovieDetailActivity extends ActionBarActivity {

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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class MovieDetailFragment extends Fragment {

        private String LOG_TAG = MovieDetailFragment.class.getSimpleName();

        public MovieDetailFragment() {
            setHasOptionsMenu(false);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            MovieDetail clickedMovieDetail = getActivity().getIntent().getParcelableExtra(Intent.EXTRA_STREAM);

            TextView movieTitle = (TextView)rootView.findViewById(R.id.movie_title);
            movieTitle.setText(clickedMovieDetail.getOriginalTitle());

            ImageView movieThumbnailPoster = (ImageView)rootView.findViewById(R.id.movie_thumbnail);
            Picasso.with(getActivity()).load(clickedMovieDetail.getPosterPath()).into(movieThumbnailPoster);

            TextView movieOverview = (TextView)rootView.findViewById(R.id.movie_overview);
            movieOverview.setText(clickedMovieDetail.getOverview());

            TextView movieRating = (TextView)rootView.findViewById(R.id.user_rating);
            movieRating.setText(getResources().getString(R.string.rating_heading, clickedMovieDetail.getVoteAverage()));

            TextView movieReleaseDate = (TextView)rootView.findViewById(R.id.release_date);
            movieReleaseDate.setText(getResources().getString(R.string.release_date_heading,clickedMovieDetail.getReleaseDate()));

            return rootView;
        }
    }
}
