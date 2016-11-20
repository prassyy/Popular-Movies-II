package com.prasi.popularmovies;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Created by User on 06-11-2016.
 */
public class MovieDetailActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putLong(MovieDetailFragment.MOVIE_ID, getIntent().getLongExtra(MovieDetailFragment.MOVIE_ID,-1));
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_container,fragment)
                    .commit();
        }
    }
}
