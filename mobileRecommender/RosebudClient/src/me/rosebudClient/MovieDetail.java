package me.rosebudClient;

import java.text.SimpleDateFormat;
import java.util.Date;

import transmit.MovieDetailShared;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * MovieDetail is used to show the movie details from a selected movie from a
 * list. As there are 3 kinds of lists ("Buy Tickets", "Rate Movie",
 * "My Tickets"), this class needs a slight different functionality. This is not
 * the proper way to solve things and should be revised. By using the correct
 * constructor, the class is set to choose the right functionality.
 * 
 */
public class MovieDetail extends DialogFragment {

	NetworkSingleton networkConnection;

	private SimpleDateFormat dfm = new SimpleDateFormat("dd/MM/yyyy  HH:mm");
	private boolean showDetailsVisible;
	private String moviename, hall, price, amount;
	private long showID, time, movieID;
	private int userRating;

	/**
	 * the "Rate Movie" constructor
	 * 
	 */
	public MovieDetail(boolean showDetailsVisible, String moviename, long movieID, int userRating) {
		super();
		this.showDetailsVisible = showDetailsVisible;
		this.moviename = moviename;
		this.movieID = movieID;
		this.userRating = userRating;
	}

	/**
	 * the "Buy Tickets" constructor
	 * 
	 */
	public MovieDetail(boolean showDetailsVisible, String moviename, String hall, String price, long showID, long time) {
		super();
		this.showDetailsVisible = showDetailsVisible;
		this.moviename = moviename;
		this.hall = hall;
		this.price = price;
		this.time = time;
		this.showID = showID;
	}

	/**
	 * the "My Tickets" constructor
	 * 
	 */
	public MovieDetail(boolean showDetailsVisible, boolean ticket, String moviename, String hall, String amount,
	long showID, long time) {
		super();
		this.showDetailsVisible = showDetailsVisible;
		this.moviename = moviename;
		this.hall = hall;
		this.amount = amount;
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

		// choose between "Rate Movie" and "Buy Tickets"
		if (showDetailsVisible) {// "Buy Tickets"
			grdlShowDetails.setVisibility(View.VISIBLE);
			grdlUserRating.setVisibility(View.GONE);
			movieDetails = networkConnection.getMovieDetailViaShowID(showID);

			TextView txtHall = (TextView) v.findViewById(R.id.movieDetail_txtHall);
			txtHall.setText(hall);

			TextView txtTime = (TextView) v.findViewById(R.id.movieDetail_txtTime);
			txtTime.setText(dfm.format(new Date(time)));

			Button btnBuyTicket = (Button) v.findViewById(R.id.movieDetail_btnBuyTicket);
			btnBuyTicket.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					btnBuyTicketClicked(v);
				}
			});
			// choose between "My Tickets" and "Buy Tickets"
			if (amount == null) {// "Buy Tickets"
				TextView txtShowList_price = (TextView) v.findViewById(R.id.movieDetail_txtPrice);
				txtShowList_price.setText("Price: " + price);
			} else {// "My Tickets"
				TextView txtShowList_price = (TextView) v.findViewById(R.id.movieDetail_txtPrice);
				txtShowList_price.setText("Amount: " + amount);
				btnBuyTicket.setVisibility(View.GONE);
			}

		} else {// "Rate Movie"
			grdlShowDetails.setVisibility(View.GONE);
			grdlUserRating.setVisibility(View.VISIBLE);
			movieDetails = networkConnection.getMovieDetailViaMovieID(movieID);

			// the spinner contains the ratings
			// position 0 = rating value 1
			// position 1 = rating value 2
			// etc...
			Spinner spinner = (Spinner) v.findViewById(R.id.movieDetail_spnRating);
			ArrayAdapter<CharSequence> adapter =
			ArrayAdapter.createFromResource(getActivity(), R.array.ratings, android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(adapter);
			spinner.setSelection(this.userRating - 1);

			Button btnRate = (Button) v.findViewById(R.id.movieDetail_btnRate);
			btnRate.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					btnRateClicked();
				}
			});

		}

		if (movieDetails.image != null) {
			ImageView imgMovie = (ImageView) v.findViewById(R.id.movieDetail_imgMovie);
			Bitmap bitmap = BitmapFactory.decodeByteArray(movieDetails.image, 0, movieDetails.image.length);
			imgMovie.setImageBitmap(bitmap);
		}

		((TextView) v.findViewById(R.id.movieDetail_txtDescription)).setText(movieDetails.description);

		RatingBar averageRating = ((RatingBar) v.findViewById(R.id.movieDetail_ratingBar_averageRating));
		averageRating.setRating(movieDetails.averageRating / 2f);

		return v;

	}

	/**
	 * show a dialog, so that the user can buy one or more tickets for a show
	 * 
	 */
	private void btnBuyTicketClicked(View v) {
		// DialogFragment.show() will take care of adding the fragment
		// in a transaction. We also want to remove any currently showing
		// dialog, so make our own transaction and take care of that here.
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.addToBackStack(null);
		// Create and show the dialog.
		DialogFragment buyTicketFragment = new BuyTicketFragment(moviename, showID);
		buyTicketFragment.show(ft, "buyTicketDialog");
	}

	/**
	 * sends the new rating of a movie to the server, updates the "Rate Movie"
	 * tab and switches to it
	 */
	private void btnRateClicked() {

		Spinner spinner = (Spinner) getView().findViewById(R.id.movieDetail_spnRating);
		int rating = spinner.getSelectedItemPosition() + 1;

		networkConnection.rate(movieID, rating);

		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.remove(getFragmentManager().findFragmentByTag("movieDetailDialog"));
		transaction.addToBackStack(null);
		getFragmentManager().findFragmentByTag("rateMovie").onResume();
		transaction.commit();

	}

}
