package com.prasi.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.prasi.popularmovies.data.MovieContract;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    private static String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private static final String SELECTED_KEY = "MOVIES";
    private int mPosition = GridView.INVALID_POSITION;

    private MovieThumbnailAdapter movieListAdapter;
    RecyclerView movieThumbnailRecyclerView;

    private static String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME+"."+MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH
    };
    static final int MOVIE_LOADER = 0;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(SELECTED_KEY, mPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(getActivity(),MovieSettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovieList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this,rootView);

        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(layoutManager);

        movieListAdapter = new MovieThumbnailAdapter(getContext(),null,0);
        recyclerView.setAdapter(movieListAdapter);

        if(savedInstanceState!=null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        return rootView;
    }

    private void updateMovieList() {
        final String sortBy = Utility.getPreferredSortOrder(getContext());
        final String MOVIE_DETAILS_BASE_URL = "http://api.themoviedb.org/3/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MOVIE_DETAILS_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TheMovieDb theMovieDb = retrofit.create(TheMovieDb.class);
        Call<MovieDetailsResponse> callMovieList = theMovieDb.getMovieDetails(
                sortBy,
                BuildConfig.THE_MOVIEDB_API_KEY);

        callMovieList.enqueue(new Callback<MovieDetailsResponse>() {
            @Override
            public void onResponse(Call<MovieDetailsResponse> call, Response<MovieDetailsResponse> response) {
                MovieDetailsResponse movieDetailResponse = response.body();
                List<MovieDetail> movieDetailsList = movieDetailResponse.getMovieDetailsList();
                Utility.persistMoviesList(getContext(), movieDetailsList);
                Utility.persistSortOrderList(getContext(), movieDetailsList,sortBy);
                movieListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<MovieDetailsResponse> call, Throwable t) {
                Log.e(LOG_TAG, "onFailure: ", t);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortBy = Utility.getPreferredSortOrder(getActivity());
        Uri movieListUri = MovieContract.buildMovieListUri(sortBy);
        Log.d("onCreateLoader", movieListUri.toString());

        return new CursorLoader(getActivity(),
                movieListUri,
                MOVIE_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        movieListAdapter.loadCursor(data);
        if (mPosition != GridView.INVALID_POSITION && movieThumbnailRecyclerView!=null)
            movieThumbnailRecyclerView.smoothScrollToPosition(mPosition);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieListAdapter.loadCursor(null);
    }
}
