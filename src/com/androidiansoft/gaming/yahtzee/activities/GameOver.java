package com.androidiansoft.gaming.yahtzee.activities;

import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import com.androidiansoft.gaming.yahtzee.data.DBHelper;

public class GameOver extends Activity {

	int gameId;
	int creUser;
	static int creBonus;
	static int conBonus;
	static int conPts;
	static int crePts;
	TextView winOrLose;
	TextView myTotal;
	TextView oppTotal;
	TextView myTotalText;
	TextView oppTotalText;
	static AsyncTask<String, Void, Void> asyncTask1;
	static StringBuffer sb;
	static NodeList nl;
	static ProgressDialog pdia;
	SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.final_score);
		gameId = getIntent().getIntExtra("androidGameId", 0);
		creUser = getIntent().getIntExtra("creUser", 0);
		creBonus = getIntent().getIntExtra("creBonus", 0);
		conBonus = getIntent().getIntExtra("conBonus", 0);
		crePts = getIntent().getIntExtra("creTotal", 0);
		conPts = getIntent().getIntExtra("conTotal", 0);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		pdia = new ProgressDialog(this);
		initialize();
		super.onCreate(savedInstanceState);
	}

	private void initialize() {
		winOrLose = (TextView) findViewById(R.id.winOrLose);
		myTotal = (TextView) findViewById(R.id.myTotalScore);
		oppTotal = (TextView) findViewById(R.id.oppTotalScore);
		oppTotalText = (TextView) findViewById(R.id.oppTotal);
		myTotalText = (TextView) findViewById(R.id.myTotal);
		// pdia = new ProgressDialog(this);
		// If I am created user
		if (creUser == preferences.getInt(LoginScreen.ID, 0)) {
			myTotal.setText(crePts + "");
			oppTotal.setText(conPts + "");
			// If I got the bonus, change the text
			if (creBonus == 1) {
				myTotalText.setText(R.string.myTotalBonus);
			}
			// If opponent got the bonus, change the text
			if (conBonus == 1) {
				oppTotalText.setText(R.string.oppTotalBonus);
			}
			// If I won, change text
			if (crePts > conPts) {
				winOrLose.setText(R.string.you_win);
				// If opponent won, change text
			} else {
				winOrLose.setText(R.string.you_lose);
			}
			// I am connected user
		} else {
			myTotal.setText(conPts + "");
			oppTotal.setText(crePts + "");
			// If I got the bonus, change the text
			if (creBonus == 1) {
				oppTotalText.setText(R.string.oppTotalBonus);
			}
			// If opponent got the bonus, change text
			if (conBonus == 1) {
				myTotalText.setText(R.string.myTotalBonus);
			}
			// If I won, change text
			if (conPts > crePts) {
				winOrLose.setText(R.string.you_win);
				// If I lost, change text
			} else {
				winOrLose.setText(R.string.you_lose);
			}
		}

		ContentValues gameTable = new ContentValues();
		gameTable.put(DBHelper.COLUMN_VALID, 0);

		getContentResolver().update(Provider.GAME_URI, gameTable,
				DBHelper.COLUMN_ID + " = ?", new String[] { gameId + "" });
		
		Stats.initializeCursors(getApplicationContext(),preferences.getInt(LoginScreen.ID, 0));
	}

	@Override
	public void onBackPressed() {
		setResult(Activity.RESULT_OK);
		finish();
		super.onBackPressed();
	}
}
