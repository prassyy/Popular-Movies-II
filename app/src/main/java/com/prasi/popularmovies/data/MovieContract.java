package com.prasi.popularmovies.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by User on 03-08-2016.
 */
public class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.prasi.popularmovies";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";
    public static final String PATH_SORT_ORDER = "sort_order";
    public static final String PATH_POPULARITY = "popularity";
    public static final String PATH_FAVOURITES = "favourite";
    public static final String PATH_MOST_VOTES = "vote_count";

    public static Uri buildMovieListUri(String sortBy) {
        String sortOrder = (sortBy.contains(PATH_POPULARITY)?PATH_POPULARITY:
                (sortBy.contains(PATH_MOST_VOTES)?PATH_MOST_VOTES:
                        (sortBy.contains(PATH_FAVOURITES)?PATH_FAVOURITES:" ")));
        switch (sortOrder) {
            case PATH_POPULARITY:
                return PopularMoviesEntry.buildPopularMovieListUri();
            case PATH_MOST_VOTES:
                return MostVotedMoviesEntry.buildMostVotedMovieList();
            case PATH_FAVOURITES:
                return FavouriteMoviesEntry.buildFavouriteMovieList();
            default:
                return null;
        }
    }

    public static SortedMoviesEntry getTableEntryForSortOrder(String sortBy) {
        switch (sortBy){
            case "popularity.desc":
                return new PopularMoviesEntry();
            case "vote_count.desc":
                return new MostVotedMoviesEntry();
            case "favourite":
                return new FavouriteMoviesEntry();
            default:
                return null;
        }
    }

    public static final class MovieEntry implements BaseColumns{

        public static final String TABLE_NAME = "movies";

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String COLUMN_POSTER_PATH = "poster_path";

        public static final String COLUMN_ADULT = "adult";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_VOTE_COUNT = "vote_count";

        public static Uri buildMovieUri() {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();
        }

        public static Uri buildMovieUri(long id) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).appendPath(String.valueOf(id)).build();
        }

    }
    public static class SortedMoviesEntry implements BaseColumns {
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SORT_ORDER).build();
    }

    public static final class PopularMoviesEntry extends SortedMoviesEntry {
        public static final String TABLE_NAME = "popular_movies";

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POPULARITY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POPULARITY;

        public static Uri buildPopularMovieListUri() {
            return CONTENT_URI.buildUpon().appendPath(PATH_POPULARITY).build();
        }
    }

    public static final class MostVotedMoviesEntry extends SortedMoviesEntry {
        public static final String TABLE_NAME = "most_voted_movies";

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOST_VOTES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOST_VOTES;

        public static Uri buildMostVotedMovieList() {
            return CONTENT_URI.buildUpon().appendPath(PATH_MOST_VOTES).build();
        }
    }

    public static final class FavouriteMoviesEntry extends SortedMoviesEntry {
        public static final String TABLE_NAME = "favourite_movies";

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVOURITES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVOURITES;

        public static Uri buildFavouriteMovieList() {
            return CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITES).build();
        }
    }
}
