package app.com.example.android.popularmovies;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Movie[] movies = new Movie[]{
                new Movie(R.mipmap.ic_launcher),
                new Movie(R.mipmap.ic_launcher),
                new Movie(R.mipmap.ic_launcher)
        };
        ArrayList<Movie> moviesList = new ArrayList<>(Arrays.asList(movies));
        MovieAdapter moviesAdapter = new MovieAdapter(getContext(), moviesList);
        GridView gridView = (GridView) rootView.findViewById(R.id.movies_grid);
        gridView.setAdapter(moviesAdapter);

        return rootView;
    }
}
