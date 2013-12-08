package com.androidiansoft.gaming.yahtzee.activities;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidiansoft.gaming.yahtzee.data.CommonUtilities;
import com.androidiansoft.gaming.yahtzee.data.User;
import com.androidiansoft.gaming.yahtzee.networking.NetworkJobs;
import com.google.android.gcm.GCMRegistrar;

public class NewUser extends Activity {

	AsyncTask<String, Void, Void> asyncTask;
	Button addUser;
	EditText username;
	EditText firstname;
	EditText lastname;
	EditText email;
	EditText password;
	public static String URL = CommonUtilities.SERVER_URL + "users/create";
	ProgressDialog pdia;
	SharedPreferences preferences;
	SharedPreferences.Editor editor;
	NodeList nl;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_user);
		NetworkJobs.verifyGCM(getApplicationContext());
		GCMRegistrar.setRegisteredOnServer(getApplicationContext(), true);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		editor = preferences.edit();
		initializeButtons();
		initializeListeners();
	}

	private void initializeButtons() {
		addUser = (Button) findViewById(R.id.addUser);
		username = (EditText) findViewById(R.id.usernameEdit);
		firstname = (EditText) findViewById(R.id.firstNameEnter);
		lastname = (EditText) findViewById(R.id.lastNameEnter);
		email = (EditText) findViewById(R.id.email);
		password = (EditText) findViewById(R.id.passwordAdd);
		pdia = new ProgressDialog(this);
	}

	private void initializeListeners() {
		addUser.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (firstname.getText().toString().equals("")
						|| lastname.getText().toString().equals("")
						|| username.getText().toString().equals("")
						|| email.getText().toString().equals("")
						|| password.getText().toString().equals("")) {
					Toast toast = Toast
							.makeText(
									getApplicationContext(),
									"You must enter values for all fields to create a profile",
									Toast.LENGTH_LONG);
					toast.show();
				} else {
					try {
						AccountManager am = AccountManager
								.get(getApplicationContext());
						Account acc = new Account(
								username.getText().toString(),
								CommonUtilities.ACCOUNT_TYPE);
						am.addAccountExplicitly(acc, password.getText()
								.toString(), null);
						ContentResolver.setIsSyncable(acc, Provider.AUTHORITY, 1);
						ContentResolver.setSyncAutomatically(acc, Provider.AUTHORITY, true);
						createUserTask();
						Intent in = new Intent(getApplicationContext(),
								GameListFragmentManager.class);
						startActivity(in);
						finish();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (TimeoutException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	}

	// Post data to create new user
	public void createUserTask() throws InterruptedException,
			ExecutionException, TimeoutException {

		asyncTask = new AsyncTask<String, Void, Void>() {

			@Override
			protected void onPreExecute() {
				pdia.setMessage("Creating User...");
				pdia.show();
				super.onPreExecute();
			}

			@Override
			protected Void doInBackground(String... params) {
				User newUser = new User();
				newUser.setEmail(email.getText().toString());
				newUser.setFirstname(firstname.getText().toString());
				newUser.setLastname(lastname.getText().toString());
				newUser.setUsername(username.getText().toString());
				newUser.setPassword(password.getText().toString());
				newUser.setGcmID(preferences.getString("gcmid", ""));
				NetworkJobs.createUser(URL, newUser);
				setResult(Activity.RESULT_OK);

				XMLParser parser = new XMLParser();

				String xml = parser.getXmlFromUrlGet(LoginScreen.USERS_URL);
				Document document = parser.getDomElement(xml);
				nl = document.getElementsByTagName(LoginScreen.USER);

				for (int t = 0; t < nl.getLength(); t++) {

					Element e = (Element) nl.item(t);

					String strUser = parser.getValue(e, LoginScreen.USERNAME,
							LoginScreen.USER);
					String strPass = parser.getValue(e, LoginScreen.PASSWORD,
							LoginScreen.USER);

					if (strUser.equals(username.getText().toString()) && strPass.equals(password.getText().toString())) {

						String strId = parser.getValue(e, LoginScreen.ID,
								LoginScreen.USER);
						editor.putInt(LoginScreen.ID, Integer.parseInt(strId));

						String strFirst = parser.getValue(e,
								LoginScreen.FIRST_NAME, LoginScreen.USER);
						editor.putString(LoginScreen.FIRST_NAME, strFirst);

						String strLast = parser.getValue(e,
								LoginScreen.LAST_NAME, LoginScreen.USER);
						editor.putString(LoginScreen.LAST_NAME, strLast);

						editor.putString(LoginScreen.USERNAME, strUser);
						/*
						 * String strEmail = parser.getValue(e, EMAIL, USER);
						 * me.setEmail(strEmail); DONT NEED THESE FIELDS String
						 * strWins = parser.getValue(e, WINS, USER);
						 * me.setWins(Integer.parseInt(strWins));
						 */
						editor.putString(LoginScreen.PASSWORD, strPass);
						editor.commit();
					}
				}

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				pdia.dismiss();
				super.onPostExecute(result);
			}
		};
		asyncTask.execute().get(30000, TimeUnit.MILLISECONDS);
	}
}
