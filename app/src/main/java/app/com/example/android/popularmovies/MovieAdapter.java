package app.com.example.android.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by chaudharyp on 12/12/15.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {
    private final String LOG_TAG = MovieAdapter.class.getSimpleName();

    public MovieAdapter(Context context, List<Movie> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                                        .inflate(R.layout.movie_grid_item, parent, false);
        }

        ImageView movieImageView = (ImageView) convertView.findViewById(R.id.movie_img);
        movieImageView.setImageResource(movie.image);

        return convertView;
    }
}
