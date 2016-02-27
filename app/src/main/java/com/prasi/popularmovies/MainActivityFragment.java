package com.prasi.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private MovieThumbnailAdapter movieListAdapter;
    private ArrayList<MovieDetail> movieDetailList;

    public MainActivityFragment() {
        movieDetailList = new ArrayList<>();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", movieDetailList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null || !savedInstanceState.containsKey("movies")){
            movieListAdapter = new MovieThumbnailAdapter(getActivity(), movieDetailList);
            updateMovieList();
        }
        else {
            movieDetailList = savedInstanceState.getParcelableArrayList("movies");
            movieListAdapter = new MovieThumbnailAdapter(getActivity(), movieDetailList);
        }
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
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView movieThumbnail_grid = (GridView) rootView.findViewById(R.id.movie_thumbnail_gridview);
        movieThumbnail_grid.setAdapter(movieListAdapter);

        movieThumbnail_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieDetail clickedMovie = movieListAdapter.getItem(position);
                Intent detailActivityIntent = new Intent(getActivity(), MovieDetailActivity.class).putExtra(Intent.EXTRA_STREAM, clickedMovie);
                startActivity(detailActivityIntent);
            }
        });
        return rootView;
    }

    private void updateMovieList() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        new FetchMovieDetailsTask().execute(sharedPref.getString(getString(R.string.movie_sort_order_key),getString(R.string.movie_pref_sort_popular)));
    }

    public class FetchMovieDetailsTask extends AsyncTask<String, Void, JSONArray> {

        private final String LOG_TAG = FetchMovieDetailsTask.class.getSimpleName();

        @Override
        protected void onPostExecute(JSONArray moviesArray) {
            super.onPostExecute(moviesArray);
            movieListAdapter.clear();

            final String TMDB_POSTER_BASEURL = "http://image.tmdb.org/t/p/";
            final String TMDB_POSTERPATH = "poster_path";
            final String TMDB_ORIGINAL_TITLE = "original_title";
            final String TMDB_OVERVIEW = "overview";
            final String TMDB_VOTE_AVERAGE = "vote_average";
            final String TMDB_RELEASE_DATE = "release_date";
            final String TMDB_IMAGE_SIZE = "w185/";
            try {
                for (int i = 0; i < moviesArray.length(); i++) {
                    String posterPath;
                    String originalTitle;
                    String overview;
                    String voteAverage;
                    String releaseDate;

                    JSONObject movieDetailObject = moviesArray.getJSONObject(i);
                    posterPath = Uri.parse(TMDB_POSTER_BASEURL).buildUpon()
                            .appendEncodedPath(TMDB_IMAGE_SIZE)
                            .appendEncodedPath(movieDetailObject.getString(TMDB_POSTERPATH))
                            .build().toString();
                    originalTitle = movieDetailObject.getString(TMDB_ORIGINAL_TITLE);
                    overview = movieDetailObject.getString(TMDB_OVERVIEW);
                    voteAverage = movieDetailObject.getString(TMDB_VOTE_AVERAGE);
                    releaseDate = movieDetailObject.getString(TMDB_RELEASE_DATE);

                    movieListAdapter.add(new MovieDetail(posterPath,originalTitle,overview,voteAverage,releaseDate));
                }
                movieListAdapter.notifyDataSetChanged();
            } catch(JSONException exception){
                Log.e(LOG_TAG,exception.getMessage());
            }
        }

        @Override
        protected JSONArray doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            final String TMDB_RESULTS = "results";

            String movieJsonStr;
            String sort_by = params[0];
            String apiId = "[API KEY]";  //Please replace this string with the API Key linked to your theMovieDB account

            try {
                final String MOVIE_DETAILS_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_CRITERIA_PARAM = "sort_by";
                final String API_KEY_PARAM = "api_key";

                // Construct the URL for the theMovieDB query
                Uri movieDetailUri = Uri.parse(MOVIE_DETAILS_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_CRITERIA_PARAM,sort_by)
                        .appendQueryParameter(API_KEY_PARAM,apiId).build();

                URL url = new URL(movieDetailUri.toString());
                Log.d(LOG_TAG,"API Call URL: " + url.toString());
                // Create the request to theMovieDB, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();

                JSONObject movieJson = new JSONObject(movieJsonStr);

                return movieJson.getJSONArray(TMDB_RESULTS);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
        }
    }
}
