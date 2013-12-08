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
import android.content.Context;
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

import com.androidiansoft.gaming.yahtzee.activities.R;
import com.androidiansoft.gaming.yahtzee.data.CommonUtilities;
import com.androidiansoft.gaming.yahtzee.data.User;

public class LoginScreen extends Activity {

	Button login;
	static EditText username;
	static EditText password;
	static String user = "";
	static String pass = "";
	static String strId;
	static AsyncTask<String, Void, Boolean> asyncTask;
	public static final String USERS_URL = CommonUtilities.SERVER_URL
			+ "users/findUsers";
	public static final String USER = "user";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	private static final String EMAIL = "email";
	public static final String FIRST_NAME = "firstname";
	public static final String LAST_NAME = "lastname";
	public static final String ID = "id";
	private static final String WINS = "wins";
	static NodeList nl;
	static Toast mToast;
	static boolean checked;
	static ProgressDialog pdia;
	static SharedPreferences preferences;
	static SharedPreferences.Editor editor;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_page);
		initializeButtons();
		initializeListeners();
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		editor = preferences.edit();
	}

	private void initializeListeners() {
		login.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				try {
					boolean found = checkUsernames(username.getText()
							.toString(), password.getText().toString(),
							LoginScreen.this);
					if (found) {
						AccountManager am = AccountManager
								.get(getApplicationContext());
						Account acc = new Account(
								username.getText().toString(),
								CommonUtilities.ACCOUNT_TYPE);
						am.addAccountExplicitly(acc, password.getText()
								.toString(), null);
						Intent in = new Intent(getApplicationContext(),
								GameList.class);
						startActivity(in);
						finish();
					} else {
						mToast = Toast.makeText(LoginScreen.this,
								"WRONG Username or Password, way to go dummy.",
								Toast.LENGTH_LONG);
						mToast.show();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (TimeoutException e) {
					e.printStackTrace();
				}
			}
		});

	}

	private void initializeButtons() {
		login = (Button) findViewById(R.id.login);
		username = (EditText) findViewById(R.id.usernameEdit);
		password = (EditText) findViewById(R.id.passwordEdit);
	}

	// Get all usernames and verify input
	public static boolean checkUsernames(final String username,
			final String password, final Context context)
			throws InterruptedException, ExecutionException, TimeoutException {

		asyncTask = new AsyncTask<String, Void, Boolean>() {

			@Override
			protected void onPreExecute() {
				pdia = new ProgressDialog(context);
				pdia.setMessage("Verifying username and password...");
				pdia.show();
				super.onPreExecute();
			}

			@Override
			protected Boolean doInBackground(String... params) {

				checked = false;

				XMLParser parser = new XMLParser();

				String xml = parser.getXmlFromUrlGet(USERS_URL);
				Document document = parser.getDomElement(xml);
				nl = document.getElementsByTagName(USER);

				for (int t = 0; t < nl.getLength(); t++) {

					Element e = (Element) nl.item(t);

					String strUser = parser.getValue(e, USERNAME, USER);
					String strPass = parser.getValue(e, PASSWORD, USER);

					if (strUser.equals(username) && strPass.equals(password)) {

						strId = parser.getValue(e, ID, USER);
						editor.putInt(LoginScreen.ID, Integer.parseInt(strId));

						String strFirst = parser.getValue(e, FIRST_NAME, USER);
						editor.putString(LoginScreen.FIRST_NAME, strFirst);

						String strLast = parser.getValue(e, LAST_NAME, USER);
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

						checked = true;

					}
				}
				return checked;

			}

			@Override
			protected void onPostExecute(Boolean result) {
				pdia.dismiss();
				super.onPostExecute(result);
			}
		};
		asyncTask.execute().get(30000, TimeUnit.MILLISECONDS);
		return checked;
	}
}