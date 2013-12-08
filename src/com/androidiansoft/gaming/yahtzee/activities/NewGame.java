package com.androidiansoft.gaming.yahtzee.activities;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.androidiansoft.gaming.yahtzee.data.CommonUtilities;
import com.androidiansoft.gaming.yahtzee.data.DBHelper;
import com.androidiansoft.gaming.yahtzee.data.Game;
import com.androidiansoft.gaming.yahtzee.data.User;

public class NewGame extends Activity {

	ArrayList<User> users = new ArrayList<User>();
	Button createGame;
	EditText gameName;
	RadioGroup radioGroup;
	RadioButton chosenButton;
	RadioButton single;
	RadioButton multi;
	AutoCompleteTextView usernames;
	AsyncTask<String, Void, Void> asyncTask1;
	private static final String USERS_URL = CommonUtilities.SERVER_URL
			+ "users/findUsers";
	private static final String GAME_URL = CommonUtilities.SERVER_URL
			+ "games/create";
	AsyncTask<String, Void, ArrayList<User>> asyncTask;
	StringBuffer strBuf = new StringBuffer();
	private static final String USER = "user";
	private static final String EMAIL = "email";
	private static final String FIRST_NAME = "firstname";
	private static final String LAST_NAME = "lastname";
	private static final String ID = "id";
	private static final String WINS = "wins";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	ArrayList<String> usernameArray = new ArrayList<String>();
	NodeList nl;
	Toast mToast;
	boolean found;
	public static Game newGame;
	ProgressDialog pdia;
	SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_game);
		initializeButtons();
		initializeListeners();
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		try {
			getUsernames();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	}

	private void initializeButtons() {
		createGame = (Button) findViewById(R.id.createGame);
		gameName = (EditText) findViewById(R.id.gameName);
		radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
		usernames = (AutoCompleteTextView) findViewById(R.id.findUser);
		single = (RadioButton) findViewById(R.id.singlePlayer);
		multi = (RadioButton) findViewById(R.id.multiPlayer);
		usernames.setEnabled(false);
		pdia = new ProgressDialog(this);
	}

	private void initializeListeners() {
		multi.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				usernames.setEnabled(true);
			}
		});

		single.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				usernames.setEnabled(false);
			}
		});

		createGame.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				// Check if any fields are empty
				int selectedId = radioGroup.getCheckedRadioButtonId();
				chosenButton = (RadioButton) findViewById(selectedId);
				if (!usernames.isEnabled()
						&& !gameName.getText().toString().equals("")) {
					mToast = Toast.makeText(getApplicationContext(),
							"Single Player mode is not working yet",
							Toast.LENGTH_LONG);
					mToast.show();
					// createGameTask();
				} else if (!usernames.getText().toString().equals("")
						&& !gameName.getText().toString().equals("")
						&& usernames.isEnabled()) {

					// Add the game

					newGame = new Game();
					chosenButton = (RadioButton) findViewById(selectedId);

					// If single player
					if (!usernames.isEnabled()) {
						newGame.setConnectedUser(0);
						newGame.setSinglePlayer(1);
						// If multi player
					} else {
						boolean found = false;
						for (int i = 0; i < users.size(); i++) {
							if (users
									.get(i)
									.getUsername()
									.equals(usernames.getText().toString()
											.trim())) {
								found = true;
								newGame.setConnectedUser(users.get(i).getId());
								newGame.setSinglePlayer(0);
								newGame.setOppFirstName(users.get(i)
										.getFirstname());
								newGame.setOppLastName(users.get(i)
										.getLastname());
								newGame.setOppUsername(users.get(i)
										.getUsername());
								newGame.setValid(1);
							}
						}
						if (found == false) {
							mToast = Toast.makeText(getApplicationContext(),
									"The chosen opponent does not exist..",
									Toast.LENGTH_LONG);
							mToast.show();

						}
						newGame.setName(gameName.getText().toString());
						newGame.setCreatedUser(preferences.getInt(
								LoginScreen.ID, 0));

						// Database add game
						ContentValues gamesTable = new ContentValues();

						gamesTable.put(DBHelper.COLUMN_CRE_USER,
								preferences.getInt(LoginScreen.ID, 0));
						gamesTable.put(DBHelper.COLUMN_CON_USER,
								newGame.getConnectedUser());
						gamesTable.put(DBHelper.COLUMN_GAME_NAME,
								newGame.getName());
						gamesTable.put(DBHelper.COLUMN_VALID,
								newGame.getValid());
						gamesTable.put(DBHelper.COLUMN_SINGLE,
								newGame.getSinglePlayer());
						gamesTable.put(DBHelper.COLUMN_OPP_FIRST,
								newGame.getOppFirstName());
						gamesTable.put(DBHelper.COLUMN_OPP_LAST,
								newGame.getOppLastName());
						gamesTable.put(DBHelper.COLUMN_OPP_USERNAME,
								newGame.getOppUsername());
						gamesTable.put(DBHelper.COLUMN_CRE_PTS, 0);
						gamesTable.put(DBHelper.COLUMN_CON_PTS, 0);
						gamesTable.put(DBHelper.COLUMN_CRE_BONUS, 0);
						gamesTable.put(DBHelper.COLUMN_CON_BONUS, 0);
						gamesTable.put(DBHelper.COLUMN_TURN,
								newGame.getCreatedUser());

						Uri id = getContentResolver().insert(Provider.GAME_URI,
								gamesTable);

						long insertid = Integer.parseInt(id
								.getLastPathSegment());

						gamesTable = new ContentValues();

						gamesTable.put(DBHelper.COLUMN_SERVER_ID,
								"disconnected" + insertid);

						getContentResolver().update(Provider.GAME_URI,
								gamesTable, DBHelper.COLUMN_ID + " = ?",
								new String[] { insertid + "" });
					}
					createGameTask();
				} else if (usernames.getText().toString().equals("")
						|| gameName.getText().toString().equals("")) {
					mToast = Toast.makeText(getApplicationContext(),
							"Fill out ALL the fields please..",
							Toast.LENGTH_LONG);
					mToast.show();
				} else if (usernames.getText().toString().trim() == preferences
						.getString(LoginScreen.USERNAME, "")) {
					mToast = Toast.makeText(getApplicationContext(),
							"You can't verse yourself... Derp",
							Toast.LENGTH_LONG);
					mToast.show();
				}
			}
		});
	}

	// Get usernames for autocomplete box
	private void getUsernames() throws InterruptedException,
			ExecutionException, TimeoutException {

		asyncTask = new AsyncTask<String, Void, ArrayList<User>>() {

			@Override
			protected void onPreExecute() {
				pdia.setMessage("Loading...");
				pdia.show();
				super.onPreExecute();
			}

			@Override
			protected ArrayList<User> doInBackground(String... params) {

				XMLParser parser = new XMLParser();

				String xml = parser.getXmlFromUrlGet(USERS_URL);
				// Getting DOM element
				Document document = parser.getDomElement(xml);
				// Getting elements based on DocData tag
				nl = document.getElementsByTagName(USER);

				// Looping through all users
				for (int t = 0; t < nl.getLength(); t++) {

					Element e = (Element) nl.item(t);

					String strUser = parser.getValue(e, USERNAME, USER);
					if (!strUser.equals(preferences.getString(
							LoginScreen.USERNAME, ""))) {
						User newUser = new User();

						String strId3 = parser.getValue(e, ID, USER);
						newUser.setId(Integer.parseInt(strId3));

						String strFirst = parser.getValue(e, FIRST_NAME, USER);
						newUser.setFirstname(strFirst);

						String strLast = parser.getValue(e, LAST_NAME, USER);
						newUser.setLastname(strLast);

						newUser.setUsername(strUser);

						/*
						 * String strEmail = parser.getValue(e, EMAIL, USER);
						 * newUser.setEmail(strEmail); DONT NEED THESE FIELDS
						 * String strWins = parser.getValue(e, WINS, USER);
						 * newUser.setWins(Integer.parseInt(strWins));
						 * 
						 * String strPass = parser.getValue(e, PASSWORD, USER);
						 * newUser.setPassword(strPass);
						 */
						users.add(newUser);
					}
				}
				return users;

			}

			@Override
			protected void onPostExecute(ArrayList<User> result) {
				for (int i = 0; i < users.size(); i++) {
					String temp = users.get(i).getUsername().toString().trim();
					usernameArray.add(temp);
				}
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						getApplicationContext(),
						android.R.layout.simple_dropdown_item_1line,
						usernameArray);
				usernames.setAdapter(adapter);
				usernames.setThreshold(1);
				pdia.dismiss();
				super.onPostExecute(result);
			}
		};
		asyncTask.execute(USERS_URL).get(30000, TimeUnit.MILLISECONDS);
	}

	// Post data for game to database
	public void createGameTask() {

		asyncTask1 = new AsyncTask<String, Void, Void>() {

			@Override
			protected void onPreExecute() {
				pdia.setMessage("Creating Game...");
				pdia.show();
				super.onPreExecute();
			}

			@Override
			protected Void doInBackground(String... params) {
				// Server create game
				XMLParser.createGame(GAME_URL, newGame);

				setResult(Activity.RESULT_OK);
				finish();
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				pdia.dismiss();
				super.onPostExecute(result);
			}
		};
		asyncTask1.execute();
	}
}
