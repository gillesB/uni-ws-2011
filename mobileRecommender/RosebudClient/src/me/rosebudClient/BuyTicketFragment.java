package me.rosebudClient;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BuyTicketFragment extends DialogFragment {

	String moviename;
	long showID;
	TextView edtAmount;

	public BuyTicketFragment(String moviename, long showID) {
		super();
		this.moviename = moviename;
		this.showID = showID;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL, 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().setTitle(moviename);
		View v = inflater.inflate(R.layout.buy_ticket_dialog, container, false);

		Button btnCancel = (Button) v.findViewById(R.id.buyTickets_btnCancel);
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().onBackPressed();
			}
		});

		Button btnBuy = (Button) v.findViewById(R.id.buyTickets_btnBuy);
		btnBuy.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnBuyClicked();
			}
		});

		edtAmount = (TextView) v.findViewById(R.id.buyTickets_edtAmount);
		return v;
	}

	/**
	 * connect to server to buy a certain amount of tickets if successful go to
	 * "my tickets" tab otherwise print error message
	 */
	public void btnBuyClicked() {
		int amount = Integer.parseInt(edtAmount.getText().toString());
		if (NetworkSingleton.getInstance().buyTickets(showID, amount)) {
			// transaction was successful.
			Context context = getActivity().getApplicationContext();
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(context, R.string.buyTicket_Successful, duration);
			toast.show();

			ActionBar actionbar = getActivity().getActionBar();
			// the tab with index 2 is the "My Tickets" fragment
			Tab myTicketsTab = actionbar.getTabAt(2);
			actionbar.selectTab(myTicketsTab);

			// remove all dialogs
			// Create new fragment and transaction
			FragmentTransaction transaction = getFragmentManager().beginTransaction();

			// Replace whatever is in the fragment_container view with this
			// fragment,
			// and add the transaction to the back stack
			transaction.remove(getFragmentManager().findFragmentByTag("buyTicketDialog"));
			transaction.remove(getFragmentManager().findFragmentByTag("movieDetailDialog"));
			transaction.addToBackStack(null);

			// Commit the transaction
			transaction.commit();

		} else {
			Context context = getActivity().getApplicationContext();
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(context, R.string.buyTicket_not_Successful, duration);
			toast.show();
		}
	}

}
