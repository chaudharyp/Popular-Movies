package app.com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private MovieAdapter moviesAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        moviesAdapter = new MovieAdapter(getContext(), new ArrayList<Movie>());
        GridView gridView = (GridView) rootView.findViewById(R.id.movies_grid);
        gridView.setAdapter(moviesAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = moviesAdapter.getItem(position);

                Intent intent = new Intent(getContext(), MovieDetailActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, movie.getMovieId());
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        new FetchMovies().execute();
    }

    private class FetchMovies extends AsyncTask<Void, Void, ArrayList<Movie>> {

        @Override
        protected ArrayList<Movie> doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                final String baseUri = "http://api.themoviedb.org/3/discover/movie";

                final String sortByParam = "sort_by";
                final String apiKeyParam = "api_key";

                String sortBySetting = PreferenceManager.getDefaultSharedPreferences(getContext())
                                                        .getString(getString(R.string.pref_sortBy_key), getString(R.string.pref_sortBy_defaultVal));
                final String sortByVal = sortBySetting + ".desc";
                final String apiKeyVal = BuildConfig.THE_MOVIE_DB_API_KEY;
                Uri fetchMoviesUri = Uri.parse(baseUri)
                                        .buildUpon()
                                        .appendQueryParameter(sortByParam, sortByVal)
                                        .appendQueryParameter(apiKeyParam, apiKeyVal)
                                        .build();

                URL url = new URL(fetchMoviesUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuffer stringBuffer = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuffer.append(line + "\n");
                }

                if (stringBuffer.length() == 0) {
                    return null;
                }

                String moviesJsonStr = stringBuffer.toString();
                ArrayList<Movie> moviesList = getMoviesListFromJsonStr(moviesJsonStr);
                return moviesList;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            if (movies.isEmpty()) return;
            moviesAdapter.clear();
            moviesAdapter.addAll(movies);
        }

        private ArrayList<Movie> getMoviesListFromJsonStr(String moviesJsonStr) throws JSONException {
            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray("results");
            ArrayList<Movie> moviesList = new ArrayList<>();
    
            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject movieJsonObj = moviesArray.getJSONObject(i);
                Movie movie = new Movie(movieJsonObj.getInt("id"));
                movie.setPosterPath(movieJsonObj.getString("poster_path"));
                moviesList.add(movie);
            }
            return moviesList;
        }
    }
}
