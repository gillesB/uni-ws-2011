package me.rosebudClient;

import transmit.MovieShared;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The "main" activity of Rosebud. It contains no information or logic itself,
 * but contains only the actionbar, so that the user can navigate.
 * 
 * 
 */
public class RosebudClientBase extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Notice that setContentView() is not used, because we use the root
		// android.R.id.content as the container for each fragment

		// setup action bar for tabs
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		Tab tab = actionBar.newTab();
		tab.setText(R.string.buyTicket);
		tab.setTabListener(new TabListener<ShowOverview>(this, "CurrentMovieOverview", ShowOverview.class));
		actionBar.addTab(tab);

		tab = actionBar.newTab();
		tab.setText(R.string.rateMovie);
		tab.setTabListener(new TabListener<RateMovie>(this, "rateMovie", RateMovie.class));
		actionBar.addTab(tab);

		tab = actionBar.newTab();
		tab.setText(R.string.myTickets);
		tab.setTabListener(new TabListener<MyTickets>(this, "myTickets", MyTickets.class));
		actionBar.addTab(tab);

	}

	public View getLoggedInView() {
		View view = getLayoutInflater().inflate(R.layout.rosebud_base, null);
		((TextView) view.findViewById(R.id.txtUsername)).setText(NetworkSingleton.getInstance().getUsername());
		return view;
	}

	public static class TabListener<T extends Fragment> implements ActionBar.TabListener {
		private final Activity mActivity;
		private final String mTag;
		private final Class<T> mClass;
		private final Bundle mArgs;
		private Fragment mFragment;

		public TabListener(Activity activity, String tag, Class<T> clz) {
			this(activity, tag, clz, null);
		}

		public TabListener(Activity activity, String tag, Class<T> clz, Bundle args) {
			mActivity = activity;
			mTag = tag;
			mClass = clz;
			mArgs = args;

			// Check to see if we already have a fragment for this tab, probably
			// from a previously saved state. If so, deactivate it, because our
			// initial state is that a tab isn't shown.
			mFragment = mActivity.getFragmentManager().findFragmentByTag(mTag);
			if (mFragment != null && !mFragment.isDetached()) {
				FragmentTransaction ft = mActivity.getFragmentManager().beginTransaction();
				ft.detach(mFragment);
				ft.commit();
			}
		}

		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			if (mFragment == null) {
				mFragment = Fragment.instantiate(mActivity, mClass.getName(), mArgs);
				ft.add(android.R.id.content, mFragment, mTag);
			} else {
				ft.attach(mFragment);
			}
		}

		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			if (mFragment != null) {
				ft.detach(mFragment);
			}
		}

		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// do nothing
		}
	}

	/**
	 * handles the information from the external activity "Add Movie". It gets
	 * information about a movie and tries to insert it in the DB. Then it adds
	 * the default rating to that movie for the active user.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == R.requestCode.ADD_MOVIE) {
			if (data.hasExtra(String.valueOf(R.extra.movieSearch_movie))) {
				MovieShared movie = (MovieShared) data.getSerializableExtra(String.valueOf(R.extra.movieSearch_movie));
				NetworkSingleton networkConnection = NetworkSingleton.getInstance();
				networkConnection.insertMovieIntoDB(movie);
				// TODO change to actual rating
				networkConnection.insertRating((long) movie.movieID, 5);
			}
		}
	}
}
