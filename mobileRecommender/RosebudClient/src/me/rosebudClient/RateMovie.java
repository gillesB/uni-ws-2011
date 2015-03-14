package me.rosebudClient;

import transmit.RateMovieShared;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class RateMovie extends ListFragment {

	NetworkSingleton networkConnection;
	boolean mBound = false;
	View mfooterView;
	RateMovieArrayAdapter mListAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		networkConnection = NetworkSingleton.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);

		mfooterView = inflater.inflate(R.layout.rate_movie_list_footer, null);

		View v = inflater.inflate(R.layout.list_view, container, false);

		Button btnAddMovie = (Button) mfooterView.findViewById(R.id.ratemovie_footer_btnAddMovie);
		btnAddMovie.setOnClickListener(new RateMovie_AddMovie_OnClickListener(getActivity()));

		return v;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getListView().addFooterView(mfooterView);
		setListAdapter(mListAdapter);
	}

	/**
	 * fetches the movies a user has seen from the server and shows them as a
	 * list
	 */
	@Override
	public void onResume() {
		super.onResume();

		RateMovieShared[] movies = networkConnection.getSeenMovies();
		mListAdapter = new RateMovieArrayAdapter(getActivity(), movies);
		setListAdapter(mListAdapter);

	}

	/**
	 * shows the details of a movie in a separate dialog. This movie can be
	 * rated by the user on that dialog.
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		String moviename = ((TextView) v.findViewById(R.id.ratemovie_txtMoviename)).getText().toString();
		int userRating = (Integer) ((TextView) v.findViewById(R.id.ratemovie_txtMoviename)).getTag();

		// Create and show the dialog.
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.addToBackStack(null);
		MovieDetail movieDetail = new MovieDetail(false, moviename, id, userRating);
		movieDetail.show(ft, "movieDetailDialog");
	}

}
