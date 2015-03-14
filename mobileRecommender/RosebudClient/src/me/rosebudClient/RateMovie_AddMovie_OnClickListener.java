package me.rosebudClient;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

import com.moviesearch.MoviesListActivity;
import com.moviesearch.model.Movie;
import com.moviesearch.services.GenericSeeker;
import com.moviesearch.services.MovieSeeker;

/**
 * makes the connection to the external activity, which allows the user to
 * search for movies from IMDB. The method addMovie is executed when the
 * "Add Movie" button on the "Rate Movie" Tab is clicked.
 * 
 * 
 */
public class RateMovie_AddMovie_OnClickListener implements View.OnClickListener {

	Context context;
	Activity activity;

	public RateMovie_AddMovie_OnClickListener(Activity activity) {
		super();
		this.activity = activity;
	}

	@Override
	public void onClick(View v) {
		context = v.getContext();
		addMovie(v);
	}

	public void addMovie(View view) {

		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle("Movie Title");
		alert.setMessage("");

		// Set an EditText view to get user input
		final EditText searchEditText = new EditText(context);
		alert.setView(searchEditText);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String query = searchEditText.getText().toString();
				performSearch(query);
				// Do something with value!
			}
		});
		searchEditText.setOnFocusChangeListener(new DftTextOnFocusListener(context.getString(R.string.search)));

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
			}
		});

		alert.show();

	}

	private GenericSeeker<Movie> movieSeeker = new MovieSeeker();
	private ProgressDialog progressDialog;

	private static final String EMPTY_STRING = "";

	private class DftTextOnFocusListener implements OnFocusChangeListener {

		private String defaultText;

		public DftTextOnFocusListener(String defaultText) {
			this.defaultText = defaultText;
		}

		public void onFocusChange(View v, boolean hasFocus) {
			if (v instanceof EditText) {
				EditText focusedEditText = (EditText) v;
				// handle obtaining focus
				if (hasFocus) {
					if (focusedEditText.getText().toString().equals(defaultText)) {
						focusedEditText.setText(EMPTY_STRING);
					}
				}
				// handle losing focus
				else {
					if (focusedEditText.getText().toString().equals(EMPTY_STRING)) {
						focusedEditText.setText(defaultText);
					}
				}
			}
		}

	}

	private void performSearch(String query) {
		if (query.compareTo("") != 0) {
			progressDialog = ProgressDialog.show(context, "Please wait...", "Retrieving data...", true, true);

			PerformMovieSearchTask task = new PerformMovieSearchTask();
			task.execute(query);
			progressDialog.setOnCancelListener(new CancelTaskOnCancelListener(task));

		}

	}

	private class CancelTaskOnCancelListener implements OnCancelListener {
		private AsyncTask<?, ?, ?> task;

		public CancelTaskOnCancelListener(AsyncTask<?, ?, ?> task) {
			this.task = task;
		}

		@Override
		public void onCancel(DialogInterface dialog) {
			if (task != null) {
				task.cancel(true);
			}
		}
	}

	private class PerformMovieSearchTask extends AsyncTask<String, Void, ArrayList<Movie>> {

		@Override
		protected ArrayList<Movie> doInBackground(String... params) {
			String query = params[0];
			return movieSeeker.find(query, 10);
		}

		@Override
		protected void onPostExecute(final ArrayList<Movie> result) {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (progressDialog != null) {
						progressDialog.dismiss();
						progressDialog = null;
					}
					Intent intent = new Intent(context, MoviesListActivity.class);
					intent.putExtra("movies", result);
					activity.startActivityForResult(intent, R.requestCode.ADD_MOVIE);
				}
			});
		}

	}

}
