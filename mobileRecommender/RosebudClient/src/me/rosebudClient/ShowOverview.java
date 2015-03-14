package me.rosebudClient;

import java.text.SimpleDateFormat;
import java.util.Date;

import transmit.ShowShared;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
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
	ListAdapter mListAdapter;
	View mheaderView;
	/**
	 * a date for which the shows are shown. No value in the past allowed
	 */
	Date date;
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
		date = new Date(TODAY);

		// the getshow method fetch the movies from the database
		ShowShared[] shows = networkConnection.getShows(date);
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

	/**
	 * shows the details of a show in a separate dialog. Tickets for that show
	 * can be bought in that dialog.
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		String moviename = ((TextView) v.findViewById(R.id.showList_moviename)).getText().toString();
		long time = (Long) v.findViewById(R.id.showList_time).getTag();
		String hall = ((TextView) v.findViewById(R.id.showList_hall)).getText().toString();
		String price = ((TextView) v.findViewById(R.id.showList_price)).getText().toString();

		// Create and show the dialog.
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.addToBackStack(null);
		MovieDetail movieDetail = new MovieDetail(true, moviename, hall, price, id, time);
		movieDetail.show(ft, "movieDetailDialog");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		setListAdapter(null);
	}

	/**
	 * decrease {@code date} by one day and fetches and shows the shows of that
	 * day
	 * 
	 */
	public void btnLeftClicked(View v) {
		if (changeDate(-dayInMiliSec)) {
			ShowShared[] shows = networkConnection.getShows(date);
			mListAdapter = new ShowArrayAdapter(getActivity(), shows);
			setListAdapter(mListAdapter);
		}
	}

	/**
	 * increase {@code date} by one day and fetches and shows the shows of that
	 * day
	 * 
	 */
	public void btnRightClicked(View v) {
		if (changeDate(dayInMiliSec)) {
			ShowShared[] shows = networkConnection.getShows(date);
			mListAdapter = new ShowArrayAdapter(getActivity(), shows);
			setListAdapter(mListAdapter);
		}
	}

	/**
	 * adds a value in milliseconds to {@code date}, and checks if it is not in
	 * the past. If it is in the past, {@code date} is not overwritten and it
	 * return false. Otherwise {@code date} is overwritten with the new value.
	 * It writes a string with the formatted value of {@code date} to the
	 * TextView {@code txtDate}. If date is today, the text is set to "Today",
	 * if it is tomorrow, the text is set to "Tomorrow", otherwise the format is
	 * "dd/MM/yyyy". Finally true is returned.
	 * 
	 * 
	 * @param miliseconds
	 *            added to {@code date}
	 * @return a string with the formatted value of {@code date}. If date is
	 *         today it returns "Today", if it is tomorrow it return "Tomorrow",
	 *         otherwise the format is "dd/MM/yyyy"
	 */
	private boolean changeDate(long miliseconds) {
		long tmpDate = date.getTime() + miliseconds;
		// the difference of the new value of date and today (in days)
		long difference = ((tmpDate - TODAY) / dayInMiliSec);
		if (difference < 0) {
			return false;
		}
		date = new Date(tmpDate);
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
