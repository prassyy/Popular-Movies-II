package com.prasi.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.widget.ImageView;

import com.prasi.popularmovies.api.MovieDetail;
import com.prasi.popularmovies.api.TheMovieDb;
import com.prasi.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Utility {

    public static String getPreferredSortOrder(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.movie_sort_order_key),
                context.getString(R.string.movie_pref_sort_popular));
    }

    public static void persistMoviesList(Context context, List<MovieDetail> movieDetailsList) {
        Vector<ContentValues> movieValuesVector = new Vector<>(movieDetailsList.size());

        for (MovieDetail movie:movieDetailsList) {
            ContentValues movieValues = new ContentValues();

            movieValues.put(MovieContract.MovieEntry._ID, movie.getId());
            movieValues.put(MovieContract.MovieEntry.COLUMN_ADULT, movie.getAdult());
            movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, movie.getVoteCount());
            movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, movie.getPopularity());
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
            movieValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, movie.getBackdrop_path());
            movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());

            movieValuesVector.add(movieValues);
        }
        if(movieValuesVector.size()>0) {
            ContentValues[] movieValuesArray = new ContentValues[movieValuesVector.size()];
            movieValuesVector.toArray(movieValuesArray);
            if(context != null)
                context.getApplicationContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.buildMovieUri()
                        ,movieValuesArray);
        }
    }

    public static void persistSortOrderList(Context context, List<MovieDetail> movieDetailsList, String sortBy) {
        Vector<ContentValues> movieSortListVector = new Vector<>(movieDetailsList.size());
        int movieSortIndex = 1;

        for (MovieDetail movie:movieDetailsList) {
            ContentValues movieSortListValues = new ContentValues();

            MovieContract.SortedMoviesEntry tableEntry = MovieContract.getTableEntryForSortOrder(sortBy);
            movieSortListValues.put(tableEntry._ID,movieSortIndex++);
            movieSortListValues.put(tableEntry.COLUMN_MOVIE_ID,movie.getId());

            movieSortListVector.add(movieSortListValues);
        }
        if(movieSortListVector.size()>0) {
            ContentValues[] movieSortValuesArray = new ContentValues[movieSortListVector.size()];
            movieSortListVector.toArray(movieSortValuesArray);
            if(context != null)
                context.getApplicationContext().getContentResolver().bulkInsert(MovieContract.buildMovieListUri(sortBy)
                    ,movieSortValuesArray);
        }
    }

    public static void addMovieToFavourites(Context context, long movieId) {
        ContentValues favouriteMoviesValues = new ContentValues();
        MovieContract.SortedMoviesEntry favouritesTableEntry = MovieContract.getTableEntryForSortOrder("favourites");
        favouriteMoviesValues.put(favouritesTableEntry.COLUMN_MOVIE_ID,movieId);
        if(context != null)
            context.getContentResolver().insert(MovieContract.buildMovieListUri("favourites"),favouriteMoviesValues);
    }

    public static void loadImageInto(Context context, String posterPath, String imageSize, ImageView imageView) {
        final String TMDB_POSTER_BASEURL = "http://image.tmdb.org/t/p/";
        final String TMDB_IMAGE_SIZE = "w185/";
        String posterUrl;

        if(imageSize == null)
            posterUrl = Uri.parse(TMDB_POSTER_BASEURL).buildUpon()
                .appendEncodedPath(TMDB_IMAGE_SIZE)
                .appendEncodedPath(posterPath)
                .build().toString();
        else
            posterUrl = Uri.parse(TMDB_POSTER_BASEURL).buildUpon()
                    .appendEncodedPath(imageSize)
                    .appendEncodedPath(posterPath)
                    .build().toString();

        if(context != null)
            Picasso.with(context)
                .load(posterUrl)
                .placeholder(R.mipmap.clapboard)
                .error(R.mipmap.clapboard)
                .into(imageView);
    }

    public static boolean isAFavouriteMovie(Context context, long movieId) {
        Cursor favouriteMovie = null;
        if(context != null)
            favouriteMovie = context.getContentResolver().query(MovieContract.FavouriteMoviesEntry.buildFavouriteMovieList(),
                new String[]{MovieContract.FavouriteMoviesEntry.COLUMN_MOVIE_ID},
                MovieContract.FavouriteMoviesEntry.COLUMN_MOVIE_ID+" = ? ",
                new String[]{String.valueOf(movieId)},
                null);

        if(favouriteMovie!= null && favouriteMovie.moveToFirst()) {
            favouriteMovie.close();
            return true;
        }
        favouriteMovie.close();
        return false;
    }

    public static void removeFromFavourites(Context context, long movieId) {
        MovieContract.SortedMoviesEntry favouritesTableEntry = MovieContract.getTableEntryForSortOrder("favourites");
        if(context != null)
            context.getContentResolver().delete(MovieContract.buildMovieListUri("favourites"),
                favouritesTableEntry.COLUMN_MOVIE_ID + " = ? ",
                new String[] {String.valueOf(movieId)});
    }

    public static TheMovieDb getTheMovieDb() {
        final String MOVIE_DETAILS_BASE_URL = "http://api.themoviedb.org/3/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MOVIE_DETAILS_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(TheMovieDb.class);
    }

    public static String getYear(String date) {
        SimpleDateFormat dateGivenFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateNewFormat = new SimpleDateFormat("yyyy");
        try {
            Date formattedDate = dateGivenFormat.parse(date);
            return dateNewFormat.format(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static void triggerLoader(LoaderManager loaderManager, int loaderId, LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks) {
        if(loaderManager.getLoader(loaderId) == null)
            loaderManager.initLoader(loaderId,null,loaderCallbacks);
        else
            loaderManager.restartLoader(loaderId,null,loaderCallbacks);
    }
}
