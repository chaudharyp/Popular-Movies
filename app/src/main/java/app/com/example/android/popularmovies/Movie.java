package app.com.example.android.popularmovies;

/**
 * Created by chaudharyp on 12/12/15.
 */
public class Movie {
    int movieId;
    String posterPath;

    public Movie(int movieId, String posterPath) {
        this.movieId = movieId;
        this.posterPath = posterPath;
    }
}
