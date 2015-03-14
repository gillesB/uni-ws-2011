package me.rosebudClient;

import java.text.SimpleDateFormat;
import java.util.Date;

import transmit.MovieDetailShared;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.gridlayout.GridLayout;



public class MovieDetail extends DialogFragment {

	NetworkSingleton networkConnection;

	private SimpleDateFormat dfm = new SimpleDateFormat("dd/MM/yyyy  HH:mm");
	private boolean showDetailsVisible;
	private String moviename, hall, price;
	private long showID, time, movieID;
	private int userRating;

	public MovieDetail(boolean showDetailsVisible, String moviename, long movieID, int userRating) {
		super();
		this.showDetailsVisible = showDetailsVisible;
		this.moviename = moviename;
		this.movieID = movieID;
		this.userRating = userRating;
	}

	public MovieDetail(boolean showDetailsVisible, String moviename, String hall, String price, long showID, long time) {
		super();
		this.showDetailsVisible = showDetailsVisible;
		this.moviename = moviename;
		this.hall = hall;
		this.price = price;
		this.time = time;
		this.showID = showID;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_TITLE, 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		networkConnection = NetworkSingleton.getInstance();
		View v = inflater.inflate(R.layout.movie_detail, container, false);

		GridLayout grdlShowDetails = (GridLayout) v.findViewById(R.id.movieDetail_layoutShowInformation);
		GridLayout grdlUserRating = (GridLayout) v.findViewById(R.id.movieDetail_layoutUserRating);
		MovieDetailShared movieDetails;

		TextView txtMoviename = (TextView) v.findViewById(R.id.movieDetail_txtMovieName);
		txtMoviename.setText(moviename);

		if (showDetailsVisible) {
			grdlShowDetails.setVisibility(View.VISIBLE);
			grdlUserRating.setVisibility(View.GONE);
			movieDetails = networkConnection.getMovieDetailViaShowID(showID);

			TextView txtHall = (TextView) v.findViewById(R.id.movieDetail_txtHall);
			txtHall.setText(hall);

			TextView txtTime = (TextView) v.findViewById(R.id.movieDetail_txtTime);
			txtTime.setText(dfm.format(new Date(time)));

			TextView txtShowList_price = (TextView) v.findViewById(R.id.movieDetail_txtPrice);
			txtShowList_price.setText("Price: " + price);

			Button btnBuyTicket = (Button) v.findViewById(R.id.movieDetail_btnBuyTicket);
			btnBuyTicket.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					btnBuyTicketClicked(v);
				}
			});

		} else {
			grdlShowDetails.setVisibility(View.GONE);
			grdlUserRating.setVisibility(View.VISIBLE);
			movieDetails = networkConnection.getMovieDetailViaMovieID(movieID);

			//the spinner contains the ratings
			//position 0 = rating value 1
			//position 1 = rating value 2
			//etc...
			Spinner spinner = (Spinner) v.findViewById(R.id.spinner);
			ArrayAdapter<CharSequence> adapter =
			ArrayAdapter.createFromResource(getActivity(), R.array.ratings, android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(adapter);
			spinner.setSelection(this.userRating-1);
			
			spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
					networkConnection.rate(movieID, pos+1);
					
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// Do nothing					
				}				
			});

		}

		((TextView) v.findViewById(R.id.movieDetail_txtDescription)).setText(movieDetails.description);

		RatingBar averageRating = ((RatingBar) v.findViewById(R.id.movieDetail_ratingBar_averageRating));
		averageRating.setRating(movieDetails.averageRating);

		return v;

	}

	public void btnBuyTicketClicked(View v) {
		showDialog();
	}

	void showDialog() {

		// DialogFragment.show() will take care of adding the fragment
		// in a transaction. We also want to remove any currently showing
		// dialog, so make our own transaction and take care of that here.
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.addToBackStack(null);
		// Create and show the dialog.
		DialogFragment buyTicketFragment = new BuyTicketFragment(moviename, showID);
		buyTicketFragment.show(ft, "buyTicketDialog");
	}

}
