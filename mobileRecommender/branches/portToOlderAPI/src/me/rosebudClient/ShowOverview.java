package me.rosebudClient;

import java.text.SimpleDateFormat;
import java.util.Date;

import transmit.ShowShared;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ShowOverview extends ListFragment {

	NetworkSingleton networkConnection;
	boolean mBound = false;
	// View mheaderView;
	ListAdapter mListAdapter;
	View mheaderView;
	long date;
	final long dayInMiliSec = 86400000;
	final long TODAY = System.currentTimeMillis();
	TextView txtDate;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		networkConnection = NetworkSingleton.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);
		// TODO change this to real date (Gilles)
		date = TODAY;
		ShowShared[] shows = networkConnection.getShows(new Date(date));
		mListAdapter = new ShowArrayAdapter(getActivity(), shows);

		mheaderView = inflater.inflate(R.layout.shows_list_header, null);

		// set the onClickListeners for the two image buttons increasing or
		// decreasing the date
		ImageButton left = (ImageButton) mheaderView.findViewById(R.id.btnShows_left);
		left.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnLeftClicked(v);
			}
		});
		ImageButton right = (ImageButton) mheaderView.findViewById(R.id.btnShows_right);
		right.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnRightClicked(v);
			}
		});
		txtDate = (TextView) mheaderView.findViewById(R.id.txtShow_Date);
		txtDate.setText(R.string.today);

		View v = inflater.inflate(R.layout.list_view, container, false);
		return v;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getListView().addHeaderView(mheaderView);
		if (mListAdapter == null) {
			mListAdapter = new ShowArrayAdapter(getActivity(), null);
		}
		setListAdapter(mListAdapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		String moviename = ((TextView) v.findViewById(R.id.showList_moviename)).getText().toString();

		long time = (Long) v.findViewById(R.id.showList_time).getTag();

		String hall = ((TextView) v.findViewById(R.id.showList_hall)).getText().toString();

		String price = ((TextView) v.findViewById(R.id.showList_price)).getText().toString();

		// Create and show the dialog.
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.addToBackStack(null);
		MovieDetail movieDetail = new MovieDetail(true, moviename, hall, price, id, time);
		movieDetail.show(ft, "movieDetailDialog");		
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		setListAdapter(null);

	}

	public void btnLeftClicked(View v) {
		if (changeDate(-dayInMiliSec)) {
			ShowShared[] shows = networkConnection.getShows(new Date(date));
			mListAdapter = new ShowArrayAdapter(getActivity(), shows);
			setListAdapter(mListAdapter);
		}
	}

	public void btnRightClicked(View v) {
		if (changeDate(dayInMiliSec)) {
			ShowShared[] shows = networkConnection.getShows(new Date(date));
			mListAdapter = new ShowArrayAdapter(getActivity(), shows);
			setListAdapter(mListAdapter);
		}
	}

	private boolean changeDate(long miliseconds) {
		long tmpDate = date + miliseconds;
		long difference = ((tmpDate - TODAY) / dayInMiliSec);
		if (difference < 0) {
			return false;
		}
		date = tmpDate;
		switch ((int) difference) {
		case 0:
			txtDate.setText(R.string.today);
			break;
		case 1:
			txtDate.setText(R.string.tomorrow);
			break;
		default:
			txtDate.setText(dateFormat.format(date));
		}
		return true;

	}

}
