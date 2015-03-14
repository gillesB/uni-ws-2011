package me.rosebudClient;

import transmit.RateMovieShared;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

public class RateMovieArrayAdapter extends ArrayAdapter<RateMovieShared> {

	private final Context context;
	private final RateMovieShared[] movies;

	public RateMovieArrayAdapter(Context context, RateMovieShared[] movies) {
		super(context, R.layout.show_list_item, movies);
		this.context = context;
		this.movies = movies;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.rate_movie_list_item, parent, false);

		//ImageView imageView = (ImageView) rowView.findViewById(R.id.showList_image);

		TextView txtMoviename = (TextView) rowView.findViewById(R.id.ratemovie_moviename);
		txtMoviename.setText(movies[position].moviename);
		txtMoviename.setTag(movies[position].userRating);

		RatingBar ratingbar = (RatingBar) rowView.findViewById(R.id.ratemovie_ratingbar);
		ratingbar.setRating(movies[position].userRating);
		ratingbar.setVisibility(View.GONE);

		return rowView;
	}

	@Override
	public long getItemId(int position) {
		return (long) movies[position].movieID;
	}
	
	

}
