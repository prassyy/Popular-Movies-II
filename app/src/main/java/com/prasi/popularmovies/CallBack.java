package com.prasi.popularmovies;

/**
 * Created by User on 19-11-2016.
 */

public interface CallBack {
    public void onItemSelected(long movieId);

    public void notifyMovieChanged(long movieId);
}
