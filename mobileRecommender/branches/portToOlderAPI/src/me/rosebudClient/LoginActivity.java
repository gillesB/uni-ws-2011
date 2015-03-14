package me.rosebudClient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

public class LoginActivity extends FragmentActivity {

	NetworkSingleton networkConnection;
	boolean mBound = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		networkConnection = NetworkSingleton.getInstance();
		setContentView(R.layout.login);
	}

	/**
	 * Called when a button is clicked (the button in the layout file attaches
	 * to this method with the android:onClick attribute)
	 */
	public void btnLoginClicked(View v) {
		// Call a method from the LocalService.
		// However, if this call were something that might hang, then this
		// request should
		// occur in a separate thread to avoid slowing down the activity
		// performance.
		String username = ((TextView) findViewById(R.id.edtUsername)).getText().toString();
		String password = ((TextView) findViewById(R.id.edtPassword)).getText().toString();

		if (networkConnection.login(username, password)) {
			Intent intent = new Intent(this, RosebudClientBase.class);
			startActivity(intent);
		}
	}
}
