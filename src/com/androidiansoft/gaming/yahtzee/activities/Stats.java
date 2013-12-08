package com.androidiansoft.gaming.yahtzee.activities;

import java.text.DecimalFormat;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.androidiansoft.gaming.yahtzee.data.DBHelper;

public class Stats extends Fragment {

	static TextView wins;
	static TextView losses;
	static TextView topScore;
	static TextView averageScore;
	static TextView winLossRatio;
	Button previousGames;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View V = inflater.inflate(R.layout.stats_screen, container, false);
		wins = (TextView) V.findViewById(R.id.winsText);
		topScore = (TextView) V.findViewById(R.id.topScore);
		losses = (TextView) V.findViewById(R.id.losses);
		averageScore = (TextView) V.findViewById(R.id.averageScore);
		winLossRatio = (TextView) V.findViewById(R.id.winLossRatio);
		previousGames = (Button) V.findViewById(R.id.previousGames);
		initializeListeners();
		initializeCursors(getActivity().getApplicationContext(), GameListFragmentManager.preferences.getInt(LoginScreen.ID, 0));
		return V;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	private void initializeListeners() {
		previousGames.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent in = new Intent(getActivity().getApplicationContext(),
						PreviousGamesList.class);
				startActivity(in);
			}
		});

	}

	public static void initializeCursors(Context context, int loginId) {


		Cursor c1 = context.getContentResolver().query(
				Provider.GAME_URI,
				new String[] {
						"CASE WHEN " + DBHelper.COLUMN_CRE_USER + " = "
								+ loginId
								+ " THEN MAX (" + DBHelper.COLUMN_CRE_PTS
								+ ") ELSE MAX (" + DBHelper.COLUMN_CON_PTS
								+ ") END AS you, CASE WHEN "
								+ DBHelper.COLUMN_CRE_USER + " = "
								+ loginId
								+ " THEN MAX ( " + DBHelper.COLUMN_CON_PTS
								+ ") ELSE MAX (" + DBHelper.COLUMN_CRE_PTS
								+ ") END AS them", DBHelper.COLUMN_ID,
						DBHelper.COLUMN_GAME_NAME, DBHelper.COLUMN_CON_USER,
						DBHelper.COLUMN_CRE_USER, DBHelper.COLUMN_SERVER_ID,
						DBHelper.COLUMN_SINGLE, DBHelper.COLUMN_VALID,
						DBHelper.COLUMN_CON_BONUS, DBHelper.COLUMN_CRE_BONUS,
						DBHelper.COLUMN_OPP_USERNAME, DBHelper.COLUMN_TURN },
				"valid = ?", new String[] { "0" }, "you ASC");
		
		Cursor c = context.getContentResolver().query(
				Provider.GAME_URI,
				new String[] {
						"CASE WHEN " + DBHelper.COLUMN_CRE_USER + " = "
								+ loginId
								+ " THEN " + DBHelper.COLUMN_CRE_PTS
								+ " ELSE " + DBHelper.COLUMN_CON_PTS
								+ " END AS you, CASE WHEN "
								+ DBHelper.COLUMN_CRE_USER + " = "
								+ loginId
								+ " THEN " + DBHelper.COLUMN_CON_PTS
								+ " ELSE " + DBHelper.COLUMN_CRE_PTS
								+ " END AS them ", DBHelper.COLUMN_ID,
						DBHelper.COLUMN_GAME_NAME, DBHelper.COLUMN_CON_USER,
						DBHelper.COLUMN_CRE_USER, DBHelper.COLUMN_SERVER_ID,
						DBHelper.COLUMN_SINGLE, DBHelper.COLUMN_VALID,
						DBHelper.COLUMN_CON_BONUS, DBHelper.COLUMN_CRE_BONUS,
						DBHelper.COLUMN_OPP_USERNAME, DBHelper.COLUMN_TURN },
				"valid = ?", new String[] { "0" }, "you ASC");

		c1.moveToFirst();

		int totalScore = 0;
		while (!c1.isAfterLast()) {
			totalScore = c1.getInt(c1.getColumnIndexOrThrow("you"));
			c1.moveToNext();
		}
		topScore.setText(totalScore + "");

		c.moveToFirst();
		int averageScores = 0;
		int amountOfScores = 0;
		while (!c.isAfterLast()) {
			averageScores += c.getInt(c.getColumnIndexOrThrow("you"));
			amountOfScores++;
			c.moveToNext();
		}

		if (amountOfScores != 0) {
			averageScore.setText(averageScores / amountOfScores + "");
		} else {
			averageScore.setText("0");
		}
		
		DecimalFormat df2 = new DecimalFormat( "#.##" );

		double wonGames = 0;
		double lostGames = 0;
		c.moveToFirst();
		while (!c.isAfterLast()) {
			if (c.getInt(c.getColumnIndexOrThrow("you")) > c.getInt(c
					.getColumnIndexOrThrow("them"))) {
				wonGames++;
			} else if (c.getInt(c.getColumnIndexOrThrow("you")) <= c
					.getInt(c.getColumnIndexOrThrow("them"))
					&& c.getInt(c.getColumnIndexOrThrow("them")) != 0) {
				lostGames++;
			}
			c.moveToNext();
		}
		wins.setTextColor(Color.GREEN);
		losses.setTextColor(Color.RED);
		wins.setText((int)wonGames + "");
		losses.setText((int)lostGames + "");

		double ratio;
		
		if (lostGames == 0) {
			winLossRatio.setText(wonGames + "");
		} else {
			ratio = wonGames / lostGames;
			winLossRatio.setText(df2.format(ratio) + "");
		}
		c1.close();
		c.close();

	}
}
