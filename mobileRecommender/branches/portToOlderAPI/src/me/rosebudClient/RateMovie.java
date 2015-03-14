package me.rosebudClient;

import transmit.RateMovieShared;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class RateMovie extends ListFragment {

	NetworkSingleton networkConnection;
	boolean mBound = false;
	// View mheaderView;
	RateMovieArrayAdapter mListAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		networkConnection = NetworkSingleton.getInstance();

		// ListView listView = getListView();
		// View header = getLoggedInView();
		// listView.addHeaderView(header);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);
		RateMovieShared[] movies = networkConnection.getSeenMovies();

		View v = inflater.inflate(R.layout.list_view, container, false);
		mListAdapter = new RateMovieArrayAdapter(getActivity(), movies);
		setListAdapter(mListAdapter);

		// mheaderView = ((RosebudClientBase)getActivity()).getLoggedInView();
		return v;

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		String moviename = ((TextView) v.findViewById(R.id.ratemovie_moviename)).getText().toString();
		int userRating = (Integer) ((TextView) v.findViewById(R.id.ratemovie_moviename)).getTag();

		// Create and show the dialog.
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.addToBackStack(null);
		MovieDetail movieDetail = new MovieDetail(false, moviename, id, userRating);
		movieDetail.show(ft, "movieDetailDialog");
	}

}
