package com.prasi.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.ImageView;

import com.prasi.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Vector;

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
            context.getApplicationContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.buildMovieUri()
                    ,movieValuesArray);
        }
    }

    public static void persistSortOrderList(Context context, List<MovieDetail> movieDetailsList, String sortBy) {
        Vector<ContentValues> movieSortListVector = new Vector<>(movieDetailsList.size());
        int movieSortIndex = 1;

        for (MovieDetail movie:movieDetailsList) {
            ContentValues movieSortListValues = new ContentValues();

            MovieContract.SortedMoviesEntry TableEntry = MovieContract.getTableEntryForSortOrder(sortBy);
            movieSortListValues.put(TableEntry._ID,movieSortIndex++);
            movieSortListValues.put(TableEntry.COLUMN_MOVIE_ID,movie.getId());

            movieSortListVector.add(movieSortListValues);
        }
        if(movieSortListVector.size()>0) {
            ContentValues[] movieSortValuesArray = new ContentValues[movieSortListVector.size()];
            movieSortListVector.toArray(movieSortValuesArray);
            context.getApplicationContext().getContentResolver().bulkInsert(MovieContract.buildMovieListUri(sortBy)
                    ,movieSortValuesArray);
        }
    }

    public static void loadImageInto(Context context, String posterPath, ImageView imageView) {
        final String TMDB_POSTER_BASEURL = "http://image.tmdb.org/t/p/";
        final String TMDB_IMAGE_SIZE = "w185/";

        String posterUrl = Uri.parse(TMDB_POSTER_BASEURL).buildUpon()
                .appendEncodedPath(TMDB_IMAGE_SIZE)
                .appendEncodedPath(posterPath)
                .build().toString();

        Picasso.with(context)
                .load(posterUrl)
                .placeholder(R.mipmap.clapboard)
                .error(R.mipmap.clapboard)
                .into(imageView);
    }
}
