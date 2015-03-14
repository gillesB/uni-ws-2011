package me.rosebudClient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

	NetworkSingleton networkConnection;
	boolean mBound = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		networkConnection = NetworkSingleton.getInstance();
		setContentView(R.layout.login);
		Log.d("login", "onCreate");
	}

	/**
	 * logs out every time this activity is resumed this avoids a lot of bugs,
	 * for example when the server is down while the client is running
	 */
	@Override
	protected void onResume() {
		super.onResume();
		networkConnection.logout();
	}

	/**
	 * tries to login with the entered username and password if successful go to
	 * "Buy Ticket" tab otherwise shows error message 
	 */
	public void btnLoginClicked(View v) {

		String username = ((TextView) findViewById(R.id.edtUsername)).getText().toString();
		String password = ((TextView) findViewById(R.id.edtPassword)).getText().toString();
		String serverIP = ((TextView) findViewById(R.id.edtServerIP)).getText().toString();

		int loginReturnValue = networkConnection.login(serverIP, username, password);

		if (loginReturnValue == 0) {
			Intent intent = new Intent(this, RosebudClientBase.class);
			startActivity(intent);
		} else if (loginReturnValue == 1) {
			Toast.makeText(this, R.string.networkProblem, Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, R.string.login_not_successful, Toast.LENGTH_LONG).show();
		}

	}
}
