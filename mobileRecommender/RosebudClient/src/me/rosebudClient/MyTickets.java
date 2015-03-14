package me.rosebudClient;

import transmit.TicketShared;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class MyTickets extends ListFragment {

	NetworkSingleton networkConnection;
	boolean mBound = false;
	MyTicketsArrayAdapter mListAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		networkConnection = NetworkSingleton.getInstance();
	}

	/**
	 * fetches the tickets from a user from the server and shows them as a list 
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);
		TicketShared[] tickets = networkConnection.getMyTickets();

		View v = inflater.inflate(R.layout.list_view, container, false);
		mListAdapter = new MyTicketsArrayAdapter(getActivity(), tickets);
		setListAdapter(mListAdapter);

		return v;

	}

	/**
	 * shows the details of a ticket in a separate dialog
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		String moviename = ((TextView) v.findViewById(R.id.ticketList_moviename)).getText().toString();
		long time = (Long) v.findViewById(R.id.ticketList_time).getTag();
		String hall = ((TextView) v.findViewById(R.id.ticketList_hall)).getText().toString();
		String amount = ((TextView) v.findViewById(R.id.ticketList_amount)).getTag().toString();

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.addToBackStack(null);
		MovieDetail movieDetail = new MovieDetail(true, true, moviename, hall, amount, id, time);
		movieDetail.show(ft, "movieDetailDialog");

	}

}
