package me.rosebudClient;

import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.ActionBar.Tab;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;




public class RosebudClientBase extends FragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Notice that setContentView() is not used, because we use the root
		// android.R.id.content as the container for each fragment

		// setup action bar for tabs
		
		//ActionBar actionBar = getSupportActionBar();
		ActionBar actionBar = getSupportActionBar();
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
		private final FragmentActivity mActivity;
		private final String mTag;
		private final Class<T> mClass;
		private final Bundle mArgs;
		private Fragment mFragment;

		public TabListener(FragmentActivity activity, String tag, Class<T> clz) {
			this(activity, tag, clz, null);
		}

		public TabListener(FragmentActivity activity, String tag, Class<T> clz, Bundle args) {
			mActivity = activity;
			mTag = tag;
			mClass = clz;
			mArgs = args;

			// Check to see if we already have a fragment for this tab, probably
			// from a previously saved state. If so, deactivate it, because our
			// initial state is that a tab isn't shown.
			mFragment = mActivity.getSupportFragmentManager().findFragmentByTag(mTag);
			if (mFragment != null && !mFragment.isDetached()) {
				FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
				ft.detach(mFragment);
				ft.commit();
			}
		}

		public void onTabSelected(Tab tab, FragmentTransaction ignoredFt) {
			FragmentManager fragMgr = ((FragmentActivity)mActivity).getSupportFragmentManager();
		    FragmentTransaction ft = fragMgr.beginTransaction();

		    // Check if the fragment is already initialized
		    if (mFragment == null) {
		        // If not, instantiate and add it to the activity
		        mFragment = Fragment.instantiate(mActivity, mClass.getName());

		        ft.add(android.R.id.content, mFragment, mTag);
		    } else {
		        // If it exists, simply attach it in order to show it
		        ft.attach(mFragment);
		    }

		    ft.commit();
		}

		public void onTabUnselected(Tab tab, FragmentTransaction ignoredFt) {
			FragmentManager fragMgr = ((FragmentActivity)mActivity).getSupportFragmentManager();
		    FragmentTransaction ft = fragMgr.beginTransaction();
			
			if (mFragment != null) {
				ft.detach(mFragment);
			}
		}

		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			//do nothing
		}

	}
}
