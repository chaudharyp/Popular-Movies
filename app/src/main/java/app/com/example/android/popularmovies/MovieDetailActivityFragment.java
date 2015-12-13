package app.com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
public class MovieDetailActivityFragment extends Fragment {
    private final String LOG_TAG = MovieDetailActivityFragment.class.getSimpleName();

    private View rootView;

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        int movieId = 0;
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            movieId = intent.getIntExtra(Intent.EXTRA_TEXT, 0);
            if (movieId == 0) {
                Toast.makeText(getContext(), "Oops! Some error occurred!", Toast.LENGTH_SHORT)
                     .show();
            } else {
                new FetchMovieDetail().execute(movieId);
            }
        }
        return rootView;
    }

    private class FetchMovieDetail extends AsyncTask<Integer, Void, Movie> {

        @Override
        protected Movie doInBackground(Integer... params) {
            Integer movieId = params[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                final String baseUri = "http://api.themoviedb.org/3/movie";

                final String apiKeyParam = "api_key";

                final String apiKeyVal = BuildConfig.THE_MOVIE_DB_API_KEY;
                Uri fetchMoviesUri = Uri.parse(baseUri)
                                        .buildUpon()
                                        .appendPath(movieId.toString())
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
                Movie movie = getMovieFromJsonStr(moviesJsonStr);
                return movie;
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
        protected void onPostExecute(Movie movie) {
            ImageView posterImageView = (ImageView) rootView.findViewById(R.id.poster);
            Picasso.with(getContext())
                   .load("http://image.tmdb.org/t/p/w780/" + movie.getBackdropPath())
                   .into(posterImageView);

            TextView titleTextView = (TextView) rootView.findViewById(R.id.title_val);
            titleTextView.setText(movie.getOriginalTitle());

            TextView overviewTextView = (TextView) rootView.findViewById(R.id.overview_val);
            overviewTextView.setText(movie.getOverview());

            TextView ratingTextView = (TextView) rootView.findViewById(R.id.rating_val);
            ratingTextView.setText(String.valueOf(movie.getAverageVote()));

            TextView releaseDateTextView = (TextView) rootView.findViewById(R.id.release_date_val);
            releaseDateTextView.setText(movie.getReleaseDate());
        }

        private Movie getMovieFromJsonStr(String moviesJsonStr) throws JSONException {
            JSONObject movieJsonObj = new JSONObject(moviesJsonStr);

            int movieId = movieJsonObj.getInt("id");
            Movie movie = new Movie(movieId);

            String originalTitle = movieJsonObj.getString("original_title");
            movie.setOriginalTitle(originalTitle);

            String posterPath = movieJsonObj.getString("poster_path");
            movie.setPosterPath(posterPath);

            String overview = movieJsonObj.getString("overview");
            movie.setOverview(overview);

            double averageVote = movieJsonObj.getDouble("vote_average");
            movie.setAverageVote(averageVote);

            String releaseDate = movieJsonObj.getString("release_date");
            movie.setReleaseDate(releaseDate);

            String backdropPath = movieJsonObj.getString("backdrop_path");
            movie.setBackdropPath(backdropPath);

            return movie;
        }
    }
}
