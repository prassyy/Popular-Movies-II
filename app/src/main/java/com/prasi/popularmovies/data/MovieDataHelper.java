package com.prasi.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by User on 04-08-2016.
 */
public class MovieDataHelper extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "movieList.db";

    public MovieDataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        addMovieTable(db);
        addPopularMoviesTable(db);
        addMostVotedMoviesTable(db);
        addFavouriteMoviesTable(db);
    }

    private void addMovieTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE "+ MovieContract.MovieEntry.TABLE_NAME+" ( "+
                        MovieContract.MovieEntry._ID +" INTEGER PRIMARY KEY, "+
                        MovieContract.MovieEntry.COLUMN_ADULT+" TEXT , "+
                        MovieContract.MovieEntry.COLUMN_BACKDROP_PATH+" TEXT , "+
                        MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE+" TEXT , "+
                        MovieContract.MovieEntry.COLUMN_OVERVIEW+" TEXT , "+
                        MovieContract.MovieEntry.COLUMN_POPULARITY+" REAL , "+
                        MovieContract.MovieEntry.COLUMN_POSTER_PATH+" TEXT , "+
                        MovieContract.MovieEntry.COLUMN_RELEASE_DATE+" TEXT , "+
                        MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE+" REAL , "+
                        MovieContract.MovieEntry.COLUMN_VOTE_COUNT+" INTEGER "+
                        " ); ");
    }

    private void addMostVotedMoviesTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE "+ MovieContract.MostVotedMoviesEntry.TABLE_NAME+" ( "+
                        MovieContract.MostVotedMoviesEntry._ID+" INTEGER PRIMARY KEY, " +
                        MovieContract.MostVotedMoviesEntry.COLUMN_MOVIE_ID + " INTEGER ," +
                        "FOREIGN KEY ("+MovieContract.MostVotedMoviesEntry.COLUMN_MOVIE_ID+") "+
                        "REFERENCES "+ MovieContract.MovieEntry.TABLE_NAME +" ("+ MovieContract.MovieEntry._ID+" )"+
                        " ); ");
    }

    private void addPopularMoviesTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE "+ MovieContract.PopularMoviesEntry.TABLE_NAME+" ( "+
                        MovieContract.PopularMoviesEntry._ID+" INTEGER PRIMARY KEY, "+
                        MovieContract.PopularMoviesEntry.COLUMN_MOVIE_ID +" INTEGER ," +
                        "FOREIGN KEY ("+MovieContract.PopularMoviesEntry.COLUMN_MOVIE_ID+") "+
                        "REFERENCES "+ MovieContract.MovieEntry.TABLE_NAME +" ("+ MovieContract.MovieEntry._ID+" )"+
                        " ); ");
    }

    private void addFavouriteMoviesTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE "+ MovieContract.FavouriteMoviesEntry.TABLE_NAME+" ( "+
                        MovieContract.FavouriteMoviesEntry._ID+" INTEGER PRIMARY KEY, "+
                        MovieContract.PopularMoviesEntry.COLUMN_MOVIE_ID +" INTEGER ," +
                        "FOREIGN KEY ("+MovieContract.FavouriteMoviesEntry.COLUMN_MOVIE_ID+") "+
                        "REFERENCES "+ MovieContract.MovieEntry.TABLE_NAME +" ("+ MovieContract.MovieEntry._ID+" )"+
                        " ); ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.PopularMoviesEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MostVotedMoviesEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavouriteMoviesEntry.TABLE_NAME);
        onCreate(db);
    }
}
