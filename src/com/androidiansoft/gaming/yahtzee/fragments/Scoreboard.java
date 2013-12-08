package com.androidiansoft.gaming.yahtzee.fragments;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.androidiansoft.gaming.yahtzee.activities.LoginScreen;
import com.androidiansoft.gaming.yahtzee.activities.Provider;
import com.androidiansoft.gaming.yahtzee.activities.R;
import com.androidiansoft.gaming.yahtzee.activities.TurnData;
import com.androidiansoft.gaming.yahtzee.data.DBHelper;

public class Scoreboard extends Fragment {

	public static final String ARG_SECTION_NUMBER = "section_number";
	public static int total;
	public static int oppTotal;
	public static int androidGameId;
	public static String serverGameId;
	public static int count = 0;
	public static int turn;
	int single = 0;
	Button choseAces;
	Button choseTwos;
	Button choseThrees;
	Button choseFours;
	Button choseFives;
	Button choseSixes;
	Button chose3ofakind;
	Button chose4ofakind;
	Button choseFullHouse;
	Button choseSmallStraight;
	Button choseLargeStraight;
	Button choseYahtzee;
	Button choseChance;
	TextView acesScore;
	TextView twosScore;
	TextView threesScore;
	TextView foursScore;
	TextView fivesScore;
	TextView sixesScore;
	TextView threeofakindScore;
	TextView fourofakindScore;
	TextView fullhouseScore;
	TextView smallstraightScore;
	TextView largestraightScore;
	TextView yahtzeeScore;
	TextView chanceScore;
	int total1 = 0;
	static SparseIntArray myScores = new SparseIntArray();
	static SparseIntArray oppScores = new SparseIntArray();
	public static int scoreChoice = 0;
	static int myBonus = 0;
	static int oppBonus = 0;
	boolean done = false;
	ProgressDialog pdia;
	SharedPreferences preferences;
	public static long turnId;
	public static int myScore;
	public static int oppScore;

	public Scoreboard() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View V = inflater.inflate(R.layout.scoresheet, container, false);
		choseAces = (Button) V.findViewById(R.id.choseAces);
		choseTwos = (Button) V.findViewById(R.id.choseTwos);
		choseThrees = (Button) V.findViewById(R.id.choseThrees);
		choseFours = (Button) V.findViewById(R.id.choseFours);
		choseFives = (Button) V.findViewById(R.id.choseFives);
		choseSixes = (Button) V.findViewById(R.id.choseSixes);
		chose3ofakind = (Button) V.findViewById(R.id.choseThreeofakind);
		chose4ofakind = (Button) V.findViewById(R.id.choseFourofakind);
		choseFullHouse = (Button) V.findViewById(R.id.choseFullhouse);
		choseSmallStraight = (Button) V.findViewById(R.id.choseSmallstraight);
		choseLargeStraight = (Button) V.findViewById(R.id.choseLargestraight);
		choseYahtzee = (Button) V.findViewById(R.id.choseYahtzee);
		choseChance = (Button) V.findViewById(R.id.choseChance);
		acesScore = (TextView) V.findViewById(R.id.acesScore);
		twosScore = (TextView) V.findViewById(R.id.twosScore);
		threesScore = (TextView) V.findViewById(R.id.threesScore);
		foursScore = (TextView) V.findViewById(R.id.foursScore);
		fivesScore = (TextView) V.findViewById(R.id.fivesScore);
		sixesScore = (TextView) V.findViewById(R.id.sixesScore);
		threeofakindScore = (TextView) V.findViewById(R.id.threeofakindScore);
		fourofakindScore = (TextView) V.findViewById(R.id.fourofakindScore);
		fullhouseScore = (TextView) V.findViewById(R.id.fullhouseScore);
		smallstraightScore = (TextView) V.findViewById(R.id.smallstraightScore);
		largestraightScore = (TextView) V.findViewById(R.id.largestraightScore);
		yahtzeeScore = (TextView) V.findViewById(R.id.yahtzeeScore);
		chanceScore = (TextView) V.findViewById(R.id.chanceScore);
		TurnData.dataPost = false;
		TurnData.dataPull = false;

		// Create turn entry, and get data from previous turns to populate
		// scoreboard
		try {

			//TurnData.createdUser = false;
			myScores.clear();
			oppScores.clear();
			myScore = 0;
			oppScore = 0;
			myBonus = 0;
			oppBonus = 0;

			// Get Turn info for selected scores and add up total score so far
			Cursor c = getActivity().getContentResolver().query(
					Provider.TURN_URI,
					new String[] { DBHelper.COLUMN_SCORE_SEL,
							DBHelper.COLUMN_PTS_SCORE, DBHelper.COLUMN_USER },
					DBHelper.COLUMN_GAME_ID + " = ?",
					new String[] { androidGameId + "" }, null);

			c.moveToFirst();
			while (!c.isAfterLast()) {
				int strSel = c.getInt(0);
				int strPts = c.getInt(1);
				int strUser = c.getInt(2);

				// If turn was not valid / completed (Should fix this when I fix
				// the being able to go back in a turn option)
				if (strUser == preferences.getInt(LoginScreen.ID, 0)) {
					if (strSel != 0) {
						myScores.put(strSel, strPts);
					}
				} else {
					if (strSel != 0) {
						oppScores.put(strSel, strPts);
					}
				}
				c.moveToNext();
			}
			c.close();
			Cursor c1 = getActivity().getContentResolver().query(
					Provider.GAME_URI,
					new String[] { DBHelper.COLUMN_CON_USER,
							DBHelper.COLUMN_CRE_USER, DBHelper.COLUMN_CON_PTS, DBHelper.COLUMN_CRE_PTS },
					DBHelper.COLUMN_ID + " = ?",
					new String[] { androidGameId + "" }, null);

			c1.moveToFirst();
			if (!c1.isAfterLast()) {
				int strCon = c1.getInt(c1
						.getColumnIndexOrThrow(DBHelper.COLUMN_CON_USER));
				int strCre = c1.getInt(c1
						.getColumnIndexOrThrow(DBHelper.COLUMN_CRE_USER));

				// Shouldn't need this IF, but just in case..
				if (strCre != 0 && strCon != 0) {
					if (strCre == preferences.getInt(LoginScreen.ID, 0)) {
						//TurnData.createdUser = true;
						TurnData.oppId = strCon;
						oppScore = c1.getInt(c1.getColumnIndexOrThrow(DBHelper.COLUMN_CON_PTS));
						myScore = c1.getInt(c1.getColumnIndexOrThrow(DBHelper.COLUMN_CRE_PTS));
					} else {
						//TurnData.createdUser = false;
						TurnData.oppId = strCre;
						oppScore = c1.getInt(c1.getColumnIndexOrThrow(DBHelper.COLUMN_CRE_PTS));
						myScore = c1.getInt(c1.getColumnIndexOrThrow(DBHelper.COLUMN_CON_PTS));
					}
				}
				c1.close();
			}

			// Wont needs these anymore because I am pulling from DB

			// myScores = TurnData.getDataForTurn(gameId);
			// TurnData.postDataFromTurn(gameId);

			// Insert Turn into db so we can reference the insertid for every
			// roll
			ContentValues turnTable = new ContentValues();

			turnTable.put(DBHelper.COLUMN_GAME_ID, androidGameId);
			turnTable.put(DBHelper.COLUMN_USER,
					preferences.getInt(LoginScreen.ID, 0));
			Uri insert = getActivity().getContentResolver().insert(
					Provider.TURN_URI, turnTable);
			turnId = Integer.parseInt(insert.getLastPathSegment());

			TurnData.dataPull = true;
			TurnData.dataPost = true;

			while (TurnData.dataPost == false && TurnData.dataPull == false) {
				// DO NOTHING
			}
			initializeListeners();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		return V;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Bundle args = new Bundle();
		args = getArguments();
		androidGameId = args.getInt("androidGameId");
		preferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity()
						.getApplicationContext());
		// single = args.getInt("single");
		turn = args.getInt("turn");
		serverGameId = args.getString("serverGameId");
		super.onCreate(savedInstanceState);
	}

	private void initializeListeners() throws InterruptedException,
			ExecutionException, TimeoutException {
		acesScore.setText("");
		choseAces.setEnabled(true);
		twosScore.setText("");
		choseTwos.setEnabled(true);
		threesScore.setText("");
		choseThrees.setEnabled(true);
		foursScore.setText("");
		choseFours.setEnabled(true);
		fivesScore.setText("");
		choseFives.setEnabled(true);
		sixesScore.setText("");
		choseSixes.setEnabled(true);
		threeofakindScore.setText("");
		chose3ofakind.setEnabled(true);
		fourofakindScore.setText("");
		chose4ofakind.setEnabled(true);
		fullhouseScore.setText("");
		choseFullHouse.setEnabled(true);
		smallstraightScore.setText("");
		choseSmallStraight.setEnabled(true);
		largestraightScore.setText("");
		choseLargeStraight.setEnabled(true);
		yahtzeeScore.setText("");
		choseYahtzee.setEnabled(true);
		chanceScore.setText("");
		choseChance.setEnabled(true);

		// Repopulate scoreboard values with values returned from the
		// getDataforTurn method
		for (int i = 0; i < myScores.size(); i++) {
			int key = myScores.keyAt(i);
			int score = myScores.get(key);
			switch (key) {
			case 1:
				acesScore.setText(score + "");
				choseAces.setEnabled(false);
				break;
			case 2:
				twosScore.setText(score + "");
				choseTwos.setEnabled(false);
				break;
			case 3:
				threesScore.setText(score + "");
				choseThrees.setEnabled(false);
				break;
			case 4:
				foursScore.setText(score + "");
				choseFours.setEnabled(false);
				break;
			case 5:
				fivesScore.setText(score + "");
				choseFives.setEnabled(false);
				break;
			case 6:
				sixesScore.setText(score + "");
				choseSixes.setEnabled(false);
				break;
			case 7:
				threeofakindScore.setText(score + "");
				chose3ofakind.setEnabled(false);
				break;
			case 8:
				fourofakindScore.setText(score + "");
				chose4ofakind.setEnabled(false);
				break;
			case 9:
				fullhouseScore.setText(score + "");
				choseFullHouse.setEnabled(false);
				break;
			case 10:
				smallstraightScore.setText(score + "");
				choseSmallStraight.setEnabled(false);
				break;
			case 11:
				largestraightScore.setText(score + "");
				choseLargeStraight.setEnabled(false);
				break;
			case 12:
				yahtzeeScore.setText(score + "");
				choseYahtzee.setEnabled(false);
				break;
			case 13:
				chanceScore.setText(score + "");
				choseChance.setEnabled(false);
				break;
			}
		}
		total = myScore;
		oppTotal = oppScore;
		Gameboard.resetRoll();

		// Initialize all buttons
		choseAces.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				count = 0;
				if (Gameboard.rollComplete) {
					for (int x = 0; x < 5; x++) {
						if (Gameboard.numbers[x] == 1) {
							count++;
						}
					}
					acesScore.setText(Integer.toString(count));
					choseAces.setEnabled(false);
					total += count;
					scoreChoice = 1;
					try {
						Gameboard.resetRoll();
						showAlert();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					} catch (TimeoutException e) {
						e.printStackTrace();
					}
				}
			}

		});

		choseTwos.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				count = 0;
				if (Gameboard.rollComplete) {
					for (int x = 0; x < 5; x++) {
						if (Gameboard.numbers[x] == 2) {
							count++;
						}
					}
					count *= 2;
					twosScore.setText(Integer.toString(count));
					choseTwos.setEnabled(false);
					total += count;

					scoreChoice = 2;
					try {
						showAlert();
						Gameboard.resetRoll();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					} catch (TimeoutException e) {
						e.printStackTrace();
					}
				}
			}
		});

		choseThrees.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				count = 0;
				if (Gameboard.rollComplete) {
					for (int x = 0; x < 5; x++) {
						if (Gameboard.numbers[x] == 3) {
							count++;
						}
					}
					count *= 3;
					threesScore.setText(Integer.toString(count));
					choseThrees.setEnabled(false);
					total += count;

					scoreChoice = 3;
					try {
						showAlert();
						Gameboard.resetRoll();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					} catch (TimeoutException e) {
						e.printStackTrace();
					}
				}
			}
		});

		choseFours.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				count = 0;
				if (Gameboard.rollComplete) {
					for (int x = 0; x < 5; x++) {
						if (Gameboard.numbers[x] == 4) {
							count++;
						}
					}
					count *= 4;
					foursScore.setText(Integer.toString(count));
					choseFours.setEnabled(false);
					total += count;

					scoreChoice = 4;
					try {
						showAlert();
						Gameboard.resetRoll();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					} catch (TimeoutException e) {
						e.printStackTrace();
					}
				}
			}
		});

		choseFives.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				count = 0;
				if (Gameboard.rollComplete) {
					for (int x = 0; x < 5; x++) {
						if (Gameboard.numbers[x] == 5) {
							count++;
						}
					}
					count *= 5;
					fivesScore.setText(Integer.toString(count));
					choseFives.setEnabled(false);
					total += count;

					scoreChoice = 5;
					try {
						showAlert();
						Gameboard.resetRoll();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					} catch (TimeoutException e) {
						e.printStackTrace();
					}
				}

			}
		});

		choseSixes.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				count = 0;
				if (Gameboard.rollComplete) {
					for (int x = 0; x < 5; x++) {
						if (Gameboard.numbers[x] == 6) {
							count++;
						}
					}
					count *= 6;
					sixesScore.setText(Integer.toString(count));
					choseSixes.setEnabled(false);
					total += count;

					scoreChoice = 6;
					try {
						showAlert();
						Gameboard.resetRoll();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					} catch (TimeoutException e) {
						e.printStackTrace();
					}
				}

			}
		});

		chose3ofakind.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				count = 0;
				if (Gameboard.rollComplete) {
					if (Gameboard.numbers[0] == Gameboard.numbers[1]
							&& Gameboard.numbers[1] == Gameboard.numbers[2]) {
						for (int x = 0; x < 5; x++) {
							count += Gameboard.numbers[x];
						}
						threeofakindScore.setText(Integer.toString(count));
						chose3ofakind.setEnabled(false);
						total += count;

					} else if (Gameboard.numbers[1] == Gameboard.numbers[2]
							&& Gameboard.numbers[2] == Gameboard.numbers[3]) {
						for (int x = 0; x < 5; x++) {
							count += Gameboard.numbers[x];
						}
						threeofakindScore.setText(Integer.toString(count));
						chose3ofakind.setEnabled(false);
						total += count;

					} else if (Gameboard.numbers[2] == Gameboard.numbers[3]
							&& Gameboard.numbers[3] == Gameboard.numbers[4]) {
						for (int x = 0; x < 5; x++) {
							count += Gameboard.numbers[x];
						}
						threeofakindScore.setText(Integer.toString(count));
						chose3ofakind.setEnabled(false);
						total += count;

					} else {
						threeofakindScore.setText(Integer.toString(0));
						chose3ofakind.setEnabled(false);

						total += 0;
					}
					scoreChoice = 7;
					try {
						showAlert();
						Gameboard.resetRoll();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					} catch (TimeoutException e) {
						e.printStackTrace();
					}
				}
			}
		});

		chose4ofakind.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				count = 0;
				if (Gameboard.rollComplete) {
					if (Gameboard.numbers[0] == Gameboard.numbers[1]
							&& Gameboard.numbers[1] == Gameboard.numbers[2]
							&& Gameboard.numbers[2] == Gameboard.numbers[3]) {
						for (int x = 0; x < 5; x++) {
							count += Gameboard.numbers[x];
						}
						fourofakindScore.setText(Integer.toString(count));
						chose4ofakind.setEnabled(false);
						total += count;

					} else if (Gameboard.numbers[1] == Gameboard.numbers[2]
							&& Gameboard.numbers[2] == Gameboard.numbers[3]
							&& Gameboard.numbers[3] == Gameboard.numbers[4]) {
						for (int x = 0; x < 5; x++) {
							count += Gameboard.numbers[x];
						}
						fourofakindScore.setText(Integer.toString(count));
						chose4ofakind.setEnabled(false);
						total += count;

					} else {
						fourofakindScore.setText(Integer.toString(0));
						chose4ofakind.setEnabled(false);
						total += 0;

					}
					scoreChoice = 8;
					try {
						showAlert();
						Gameboard.resetRoll();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					} catch (TimeoutException e) {
						e.printStackTrace();
					}
				}
			}
		});

		choseFullHouse.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				count = 0;
				if (Gameboard.rollComplete) {
					if (Gameboard.numbers[0] == Gameboard.numbers[1]
							&& Gameboard.numbers[1] == Gameboard.numbers[2]
							&& Gameboard.numbers[3] == Gameboard.numbers[4]) {
						count += 25;
						fullhouseScore.setText(Integer.toString(count));
						choseFullHouse.setEnabled(false);
						total += count;

					} else if (Gameboard.numbers[0] == Gameboard.numbers[1]
							&& Gameboard.numbers[2] == Gameboard.numbers[3]
							&& Gameboard.numbers[3] == Gameboard.numbers[4]) {
						count += 25;
						fullhouseScore.setText(Integer.toString(count));
						choseFullHouse.setEnabled(false);
						total += count;

					} else {
						fullhouseScore.setText(Integer.toString(0));
						choseFullHouse.setEnabled(false);

						total += 0;
					}
					scoreChoice = 9;
					try {
						showAlert();
						Gameboard.resetRoll();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					} catch (TimeoutException e) {
						e.printStackTrace();
					}
				}
			}
		});

		choseSmallStraight.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				count = 0;
				int num = getNumUniqueValues();
				int[] uniqueValues = new int[num];
				for (int j = 0, k = 0; j < Gameboard.numbers.length; j++) {
					if (!containsValue(uniqueValues, Gameboard.numbers[j]))
						uniqueValues[k++] = Gameboard.numbers[j];
				}
				if (Gameboard.rollComplete) {
					if (uniqueValues.length > 3 && uniqueValues[0] == 1 && uniqueValues[1] == 2
							&& uniqueValues[2] == 3 && uniqueValues[3] == 4) {
						count += 30;
						smallstraightScore.setText(Integer.toString(count));
						choseSmallStraight.setEnabled(false);
						total += count;

					} else if (uniqueValues.length > 3 && uniqueValues[0] == 2 && uniqueValues[1] == 3
							&& uniqueValues[2] == 4 && uniqueValues[3] == 5) {
						count += 30;
						smallstraightScore.setText(Integer.toString(count));
						choseSmallStraight.setEnabled(false);
						total += count;

					} else if (uniqueValues.length > 3 && uniqueValues[0] == 3 && uniqueValues[1] == 4
							&& uniqueValues[2] == 5 && uniqueValues[3] == 6) {
						count += 30;
						smallstraightScore.setText(Integer.toString(count));
						choseSmallStraight.setEnabled(false);
						total += count;

					} else {
						smallstraightScore.setText(Integer.toString(0));
						choseSmallStraight.setEnabled(false);

						total += 0;
					}
					scoreChoice = 10;
					try {
						showAlert();
						Gameboard.resetRoll();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					} catch (TimeoutException e) {
						e.printStackTrace();
					}
				}

			}
		});

		choseLargeStraight.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				count = 0;
				if (Gameboard.rollComplete) {
					if (Gameboard.numbers[0] == 1 && Gameboard.numbers[1] == 2
							&& Gameboard.numbers[2] == 3
							&& Gameboard.numbers[3] == 4
							&& Gameboard.numbers[4] == 5) {
						count += 40;
						largestraightScore.setText(Integer.toString(count));
						choseLargeStraight.setEnabled(false);
						total += count;

					} else if (Gameboard.numbers[0] == 2
							&& Gameboard.numbers[1] == 3
							&& Gameboard.numbers[2] == 4
							&& Gameboard.numbers[3] == 5
							&& Gameboard.numbers[4] == 6) {
						count += 40;
						largestraightScore.setText(Integer.toString(count));
						choseLargeStraight.setEnabled(false);
						total += count;

					} else {
						largestraightScore.setText(Integer.toString(0));
						choseLargeStraight.setEnabled(false);

						total += 0;
					}
					scoreChoice = 11;
					try {
						showAlert();
						Gameboard.resetRoll();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					} catch (TimeoutException e) {
						e.printStackTrace();
					}
				}
			}
		});

		choseYahtzee.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				count = 0;
				if (Gameboard.rollComplete) {
					if (Gameboard.numbers[0] == Gameboard.numbers[1]
							&& Gameboard.numbers[1] == Gameboard.numbers[2]
							&& Gameboard.numbers[2] == Gameboard.numbers[3]
							&& Gameboard.numbers[3] == Gameboard.numbers[4]) {
						count = 50;
						yahtzeeScore.setText(Integer.toString(count));
						choseYahtzee.setEnabled(false);
						total += count;
					} else {
						yahtzeeScore.setText(Integer.toString(0));
						choseYahtzee.setEnabled(false);
						total += 0;

					}
					scoreChoice = 12;
					try {
						showAlert();
						Gameboard.resetRoll();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					} catch (TimeoutException e) {
						e.printStackTrace();
					}
				}
			}
		});

		choseChance.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				count = 0;
				if (Gameboard.rollComplete) {
					for (int x = 0; x < 5; x++) {
						count += Gameboard.numbers[x];
					}
					chanceScore.setText(Integer.toString(count));
					choseChance.setEnabled(false);
					total += count;

					scoreChoice = 13;
					try {
						showAlert();
						Gameboard.resetRoll();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					} catch (TimeoutException e) {
						e.printStackTrace();
					}
				}
			}

		});
	}

	// Get unique numbers for small straight score
	private static int getNumUniqueValues() {
		int[] values = new int[Gameboard.numbers.length];
		int count = 0;
		for (int j = 0; j < Gameboard.numbers.length; j++) {
			if (!containsValue(values, Gameboard.numbers[j]))
				values[count++] = Gameboard.numbers[j];
		}
		return count;
	}

	// Method for obtaining small straight check
	private static boolean containsValue(int[] values, int numbers) {
		for (int j = 0; j < values.length; j++) {
			if (values[j] != 0 && values[j] == numbers)
				return true;
		}
		return false;
	}

	// Check if bonus was earned and add it
	public static void upperExtraPoints() {
		int temp = 0;
		for (int i = 0; i < 7; i++) {
			temp += myScores.get(i);
		}
		if (temp > 63) {
			myBonus = 1;
			total += 35;
		}
		int temp2 = 0;
		for (int i = 0; i < 7; i++) {
			temp2 += oppScores.get(i);
		}
		if (temp2 > 63) {
			oppBonus = 1;
			oppTotal += 35;
		}
	}

	// Show after turn alert with scores
	public void showAlert() throws InterruptedException, ExecutionException,
			TimeoutException {
		FragmentManager manager = getActivity().getFragmentManager();
		EndTurnAlertDialogFragment alert = new EndTurnAlertDialogFragment();
		Bundle args = new Bundle();
		args.putInt("myTotal", total);
		args.putInt("oppTotal", oppTotal);
		args.putInt("turnScore", count);
		alert.setArguments(args);
		alert.show(manager, "end_game_alert_dialog");
	}
}
