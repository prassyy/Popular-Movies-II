package com.prasi.popularmovies.data;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.prasi.popularmovies.data.MovieContract.PATH_FAVOURITES;
import static com.prasi.popularmovies.data.MovieContract.PATH_MOST_VOTES;
import static com.prasi.popularmovies.data.MovieContract.PATH_POPULARITY;

/**
 * Created by User on 04-08-2016.
 */
public class MovieProvider extends ContentProvider {
    private static final String LOG_TAG = MovieProvider.class.getSimpleName();
    private static final UriMatcher movieUriMatcher = buildUriMatcher();

    private static final int MOVIE_WITH_ID = 100;
    private static final int MOVIE = 101;
    private static final int POPULAR_MOVIES = 102;
    private static final int MOST_VOTED_MOVIES = 103;
    private static final int FAVOURITE_MOVIES = 104;

    private MovieDataHelper sqlOpenHelper;

    private static UriMatcher buildUriMatcher() {
        String content = MovieContract.CONTENT_AUTHORITY;

        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(content,MovieContract.PATH_MOVIE+"/#",MOVIE_WITH_ID);
        matcher.addURI(content,MovieContract.PATH_MOVIE,MOVIE);
        matcher.addURI(content,MovieContract.PATH_SORT_ORDER+"/"+PATH_POPULARITY,POPULAR_MOVIES);
        matcher.addURI(content,MovieContract.PATH_SORT_ORDER+"/"+PATH_MOST_VOTES,MOST_VOTED_MOVIES);
        matcher.addURI(content,MovieContract.PATH_SORT_ORDER+"/"+PATH_FAVOURITES,FAVOURITE_MOVIES);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        sqlOpenHelper = new MovieDataHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (movieUriMatcher.match(uri)) {
            case MOVIE_WITH_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_DIR_TYPE;
            case POPULAR_MOVIES:
                return MovieContract.PopularMoviesEntry.CONTENT_DIR_TYPE;
            case MOST_VOTED_MOVIES:
                return MovieContract.MostVotedMoviesEntry.CONTENT_DIR_TYPE;
            case FAVOURITE_MOVIES:
                return MovieContract.FavouriteMoviesEntry.CONTENT_DIR_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase movieDatabase = sqlOpenHelper.getReadableDatabase();
        Cursor returnCursor;
        Log.d(LOG_TAG,uri.toString());
        switch(movieUriMatcher.match(uri)) {
            case MOVIE_WITH_ID:
                returnCursor = movieDatabase.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case POPULAR_MOVIES:
                returnCursor = getPopularMovies(uri,projection,selection,selectionArgs,sortOrder);
                break;
            case MOST_VOTED_MOVIES:
                returnCursor = getMostVotedMovies(uri,projection,selection,selectionArgs,sortOrder);
                break;
            case FAVOURITE_MOVIES:
                returnCursor = getFavouriteMovies(uri,projection,selection,selectionArgs,sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        returnCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return returnCursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch(movieUriMatcher.match(uri)) {
            case FAVOURITE_MOVIES:
                return insertIntoFavouriteMovies(values);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch(movieUriMatcher.match(uri)) {
            case FAVOURITE_MOVIES:
                return deleteFromFavouriteMovies(selection, selectionArgs);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch(movieUriMatcher.match(uri)) {
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = sqlOpenHelper.getWritableDatabase();
        int returnCount = 0;
        switch(movieUriMatcher.match(uri)) {
            case MOVIE:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(MovieContract.MovieEntry.TABLE_NAME, null, value,SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                    db.close();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case POPULAR_MOVIES:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(MovieContract.PopularMoviesEntry.TABLE_NAME, null, value,SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                    db.close();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case MOST_VOTED_MOVIES:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(MovieContract.MostVotedMoviesEntry.TABLE_NAME, null, value,SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                    db.close();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case FAVOURITE_MOVIES:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(MovieContract.FavouriteMoviesEntry.TABLE_NAME, null, value,SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                    db.close();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                throw new UnsupportedOperationException("Uri: "+uri);
        }
    }

    private Uri insertIntoFavouriteMovies(ContentValues values) {
        SQLiteDatabase db = sqlOpenHelper.getWritableDatabase();
        db.beginTransaction();
        long _id = db.insert(MovieContract.FavouriteMoviesEntry.TABLE_NAME, null, values);
        db.endTransaction();
        db.close();
        if(_id == -1) {
            return null;
        }
        return MovieContract.FavouriteMoviesEntry.buildFavouriteMovieList();
    }

    private int deleteFromFavouriteMovies(String selection, String[] selectionArgs) {
        SQLiteDatabase db = sqlOpenHelper.getWritableDatabase();
        db.beginTransaction();
        int deleteCount = db.delete(MovieContract.FavouriteMoviesEntry.TABLE_NAME, selection, selectionArgs);
        db.endTransaction();
        db.close();
        return deleteCount;
    }

    private Cursor getFavouriteMovies(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder favouriteMoviesQueryBuilder = new SQLiteQueryBuilder();
        String movieTable = MovieContract.MovieEntry.TABLE_NAME;
        String favouriteMoviesTable = MovieContract.FavouriteMoviesEntry.TABLE_NAME;

        favouriteMoviesQueryBuilder.setTables(
                movieTable+" INNER JOIN "+ favouriteMoviesTable+
                        " ON "+ movieTable+"."+ MovieContract.MovieEntry._ID+
                        " = "+favouriteMoviesTable+"."+ MovieContract.FavouriteMoviesEntry.COLUMN_MOVIE_ID
        );
        return favouriteMoviesQueryBuilder.query(sqlOpenHelper.getReadableDatabase(),
                projection,selection,selectionArgs,null,null,sortOrder);
    }

    private Cursor getMostVotedMovies(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder mostVotedMoviesQueryBuilder = new SQLiteQueryBuilder();
        String movieTable = MovieContract.MovieEntry.TABLE_NAME;
        String mostVotedMoviesTable = MovieContract.MostVotedMoviesEntry.TABLE_NAME;

        mostVotedMoviesQueryBuilder.setTables(
                movieTable+" INNER JOIN "+ mostVotedMoviesTable+
                        " ON "+ movieTable+"."+ MovieContract.MovieEntry._ID+
                        " = "+mostVotedMoviesTable+"."+ MovieContract.MostVotedMoviesEntry.COLUMN_MOVIE_ID
        );
        return mostVotedMoviesQueryBuilder.query(sqlOpenHelper.getReadableDatabase(),
                projection,selection,selectionArgs,null,null,sortOrder);
    }

    private Cursor getPopularMovies(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder popularMoviesQueryBuilder = new SQLiteQueryBuilder();
        String movieTable = MovieContract.MovieEntry.TABLE_NAME;
        String popularMoviesTable = MovieContract.PopularMoviesEntry.TABLE_NAME;

        popularMoviesQueryBuilder.setTables(
                movieTable+" INNER JOIN "+ popularMoviesTable+
                        " ON "+ movieTable+"."+ MovieContract.MovieEntry._ID+
                        " = "+popularMoviesTable+"."+ MovieContract.PopularMoviesEntry.COLUMN_MOVIE_ID
        );
        return popularMoviesQueryBuilder.query(sqlOpenHelper.getReadableDatabase(),
                projection,selection,selectionArgs,null,null,sortOrder);
    }
}