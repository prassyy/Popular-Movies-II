package com.prasi.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.ListView;

import com.prasi.popularmovies.data.MovieContract;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private static final String SELECTED_KEY = "MOVIES";
    private int mPosition = GridView.INVALID_POSITION;

    private MovieThumbnailAdapter movieListAdapter;
    @BindView(R.id.movie_thumbnail_gridview) GridView movieThumbnailGrid;

    private ArrayList<MovieDetail> movieDetailList;
    private static String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME+"."+MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH
    };
    static final int COL_MOVIE_ID = 0;
    static final int COL_POSTER_PATH = 1;

    public MainActivityFragment() {
        movieDetailList = new ArrayList<>();
    }

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
    public void onStart() {
        super.onStart();
        updateMovieList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        movieListAdapter = new MovieThumbnailAdapter(getContext(),null,0);

        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_main, container, false);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),5));
        recyclerView.setAdapter(movieListAdapter);

        getLayoutInflater(savedInstanceState).inflate(R.layout.grid_view, container);
        if(savedInstanceState!=null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        return recyclerView;
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
        if (mPosition != ListView.INVALID_POSITION) {
            movieThumbnailGrid.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieListAdapter.loadCursor(null);
    }
}
