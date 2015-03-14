package me.rosebudClient;

import transmit.TicketShared;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class MyTickets extends ListFragment {

	NetworkSingleton networkConnection;
	boolean mBound = false;
	// View mheaderView;
	MyTicketsArrayAdapter mListAdapter;

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
		TicketShared[] tickets = networkConnection.getMyTickets();

		View v = inflater.inflate(R.layout.list_view, container, false);
		mListAdapter = new MyTicketsArrayAdapter(getActivity(), tickets);
		setListAdapter(mListAdapter);

		// mheaderView = ((RosebudClientBase)getActivity()).getLoggedInView();
		return v;

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		String moviename = ((TextView) v.findViewById(R.id.showList_moviename)).getText().toString();

		long time = (Long) v.findViewById(R.id.showList_time).getTag();

		String hall = ((TextView) v.findViewById(R.id.showList_hall)).getText().toString();

		String price = ((TextView) v.findViewById(R.id.showList_price)).getText().toString();

		// Create and show the .
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();	
		ft.addToBackStack(null);
		MovieDetail movieDetail = new MovieDetail(true, moviename, hall, price, id, time);
		movieDetail.show(ft, "movieDetailDialog");

	}

}
