package com.androidiansoft.gaming.yahtzee.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.androidiansoft.gaming.yahtzee.networking.NetworkJobs;

public class NoProfileDetected extends Activity {

	Button login;
	Button createProfile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Access the default SharedPreferences
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);

		if (isNetworkAvailable()) {
			if (preferences.getString(LoginScreen.USERNAME, "").equals("")) {
				// Ask user what they wish to do
			} else {
				boolean verified = NetworkJobs.verifyGCM(this);
				if (verified) {
					Intent in = new Intent(getApplicationContext(),
							GameListFragmentManager.class);
					startActivity(in);
					finish();
				} else {
					Toast toast = Toast
							.makeText(
									getApplicationContext(),
									"There was a problem recognizing your account, please login again or create a new profile",
									Toast.LENGTH_LONG);
					toast.show();
				}
			}
			setContentView(R.layout.no_profile);
			initializeButtons();
			initializeListeners();
		} else {
			Toast toast = Toast
					.makeText(
							getApplicationContext(),
							"This application needs a data connection to run properly, please ensure you have a proper connection to wifi or your data service provider.",
							Toast.LENGTH_LONG);
			toast.show();
			finish();
		}
	}

	private void initializeListeners() {
		login.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent in = new Intent(getApplicationContext(),
						LoginScreen.class);
				startActivity(in);
				finish();
			}
		});

		createProfile.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent in = new Intent(getApplicationContext(), NewUser.class);
				startActivity(in);
				finish();
			}
		});
	}

	private void initializeButtons() {
		createProfile = (Button) findViewById(R.id.chooseAddProfile);
		login = (Button) findViewById(R.id.chooseLogin);

	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}

}
