package com.prasi.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MovieListActivity extends AppCompatActivity implements CallBack {

    private static final String DETAIL_FRAGMENT_TAG = "MovieDetailFragment";
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.activity_movie_list);
        if(findViewById(R.id.detail_container) != null ) {
            mTwoPane = true;
            if(savedInstanceState != null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.detail_container, new MovieDetailFragment(),DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this,MovieSettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(long movieId) {
        if(mTwoPane) {
            Bundle args = new Bundle();
            args.putLong(MovieDetailFragment.MOVIE_ID, movieId);
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container,fragment,DETAIL_FRAGMENT_TAG)
                    .commit();
        } else {
            Intent movieDetailsIntent = new Intent(this, MovieDetailActivity.class);
            movieDetailsIntent.putExtra(MovieDetailFragment.MOVIE_ID,movieId);
            startActivity(movieDetailsIntent);
        }
    }

    @Override
    public void notifyMovieChanged(long movieId) {
        if(mTwoPane) {
            MovieDetailFragment detailFragment = (MovieDetailFragment) getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT_TAG);
            if(detailFragment != null) {
                detailFragment.onMovieChanged(movieId);
            }
        }
    }
}