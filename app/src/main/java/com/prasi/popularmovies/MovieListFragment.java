package com.prasi.popularmovies;

import android.content.Intent;
import android.database.Cursor;
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

import com.prasi.popularmovies.api.MovieDetail;
import com.prasi.popularmovies.api.MovieDetailsResponse;
import com.prasi.popularmovies.api.TheMovieDb;
import com.prasi.popularmovies.data.MovieContract;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    private static final String LOG_TAG = MovieListFragment.class.getSimpleName();

    private MovieThumbnailAdapter movieListAdapter;
    private String sortBy = "";
    private boolean sortOrderChanged;
    private boolean firstTimeLoad;

    private static final int MOVIE_LOADER = 0;
    private static String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME+"."+MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        firstTimeLoad = true;
        sortBy = Utility.getPreferredSortOrder(getContext());

        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);
        ButterKnife.bind(this,rootView);

        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);

        movieListAdapter = new MovieThumbnailAdapter(((CallBack)getActivity()),getContext(),null,0);
        recyclerView.setAdapter(movieListAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        String newSortOrder = Utility.getPreferredSortOrder(getActivity());
        if(newSortOrder != null && !newSortOrder.equals(sortBy)) {
            sortOrderChanged = true;
            sortBy = newSortOrder;
        }
        updateMovieList(sortBy);
        Utility.triggerLoader(getLoaderManager(),MOVIE_LOADER,this);
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

    private void updateMovieList(final String sortBy) {
        //This condition is to just prevent API call from running in case if user asks for "Favourites" sort
        //Because all the data needed are already available in the DB
        if(sortBy.equals("favourites"))
            return;
        TheMovieDb theMovieDb = Utility.getTheMovieDb();
        Call<MovieDetailsResponse> callMovieList = theMovieDb.getMovieDetails(
                sortBy,
                BuildConfig.THE_MOVIE_DB_API_KEY);

        callMovieList.enqueue(new Callback<MovieDetailsResponse>() {
            @Override
            public void onResponse(Call<MovieDetailsResponse> call, Response<MovieDetailsResponse> response) {
                MovieDetailsResponse movieDetailResponse = response.body();
                List<MovieDetail> movieDetailsList = movieDetailResponse.getMovieDetailsList();
                Utility.persistMoviesList(getContext(), movieDetailsList);
                Utility.persistSortOrderList(getContext(), movieDetailsList,sortBy);
            }

            @Override
            public void onFailure(Call<MovieDetailsResponse> call, Throwable t) {
                Log.e(LOG_TAG, "onFailure: ", t);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortBy = Utility.getPreferredSortOrder(getContext());

        return new CursorLoader(getContext(),
                MovieContract.buildMovieListUri(sortBy),
                MOVIE_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        movieListAdapter.loadCursor(data);
        movieListAdapter.notifyDataSetChanged();

        if((sortOrderChanged || firstTimeLoad) && data!=null && !data.isClosed() && data.moveToFirst()) {
            long movieId = data.getLong(MovieThumbnailAdapter.COL_MOVIE_ID);
            ((CallBack)getActivity()).notifyMovieChanged(movieId);
            sortOrderChanged = false;
            firstTimeLoad = false;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieListAdapter.loadCursor(null);
    }
}