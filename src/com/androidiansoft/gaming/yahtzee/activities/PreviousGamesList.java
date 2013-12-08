package com.androidiansoft.gaming.yahtzee.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.androidiansoft.gaming.yahtzee.data.DBHelper;

public class PreviousGamesList extends Activity {

	SharedPreferences preferences;
	ListView losses;
	ListView wins;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.previous_games_list);
		losses = (ListView) findViewById(R.id.previousLosses);
		wins = (ListView) findViewById(R.id.previousWins);
		
		//LayoutParams lp = new LayoutParams(getWindowManager().getDefaultDisplay().getWidth() / 2, LayoutParams.WRAP_CONTENT);
		//wins.setLayoutParams(lp);
		//losses.setLayoutParams(lp);
		
		preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		initializeCursor();
	}

	private void initializeCursor() {
		final Cursor c = getContentResolver().query(
				Provider.GAME_URI,
				new String[] {
						"CASE WHEN " + DBHelper.COLUMN_CRE_USER + " = "
								+ preferences.getInt(LoginScreen.ID, 0)
								+ " THEN " + DBHelper.COLUMN_CRE_PTS + " ELSE "
								+ DBHelper.COLUMN_CON_PTS
								+ " END AS you, CASE WHEN "
								+ DBHelper.COLUMN_CRE_USER + " = "
								+ preferences.getInt(LoginScreen.ID, 0)
								+ " THEN " + DBHelper.COLUMN_CON_PTS + " ELSE "
								+ DBHelper.COLUMN_CRE_PTS + " END AS them",
						DBHelper.COLUMN_ID, DBHelper.COLUMN_GAME_NAME,
						DBHelper.COLUMN_CON_USER, DBHelper.COLUMN_CRE_USER,
						DBHelper.COLUMN_SERVER_ID, DBHelper.COLUMN_SINGLE,
						DBHelper.COLUMN_VALID, DBHelper.COLUMN_CON_BONUS,
						DBHelper.COLUMN_CRE_BONUS,
						DBHelper.COLUMN_OPP_USERNAME, DBHelper.COLUMN_TURN,  DBHelper.COLUMN_CON_PTS, DBHelper.COLUMN_CRE_PTS },
				"valid = ? AND you > them",new String[] {"0"},null);

		/*
		 * final Cursor c = getContentResolver().query( Provider.GAME_URI, null,
		 * "turn = ? AND " + DBHelper.COLUMN_CRE_USER + " = ?", new String[] {
		 * preferences.getInt(LoginScreen.ID, 0) + "",
		 * preferences.getInt(LoginScreen.ID, 0) + "" }, null);
		 */
		final Cursor c1 = getContentResolver().query(
				Provider.GAME_URI,
				new String[] {
						"CASE WHEN " + DBHelper.COLUMN_CRE_USER + " = "
								+ preferences.getInt(LoginScreen.ID, 0)
								+ " THEN " + DBHelper.COLUMN_CRE_PTS + " ELSE "
								+ DBHelper.COLUMN_CON_PTS
								+ " END AS you, CASE WHEN "
								+ DBHelper.COLUMN_CRE_USER + " = "
								+ preferences.getInt(LoginScreen.ID, 0)
								+ " THEN " + DBHelper.COLUMN_CON_PTS + " ELSE "
								+ DBHelper.COLUMN_CRE_PTS + " END AS them",
						DBHelper.COLUMN_ID, DBHelper.COLUMN_GAME_NAME,
						DBHelper.COLUMN_CON_USER, DBHelper.COLUMN_CRE_USER,
						DBHelper.COLUMN_SERVER_ID, DBHelper.COLUMN_SINGLE,
						DBHelper.COLUMN_VALID, DBHelper.COLUMN_CON_BONUS,
						DBHelper.COLUMN_CRE_BONUS,
						DBHelper.COLUMN_OPP_USERNAME, DBHelper.COLUMN_TURN,  DBHelper.COLUMN_CON_PTS, DBHelper.COLUMN_CRE_PTS },
				"valid = ? AND you <= them",
				new String[] {"0"},
				null);

		startManagingCursor(c1);
		startManagingCursor(c);

		
		
		String[] columns1 = new String[] { DBHelper.COLUMN_GAME_NAME,
				DBHelper.COLUMN_OPP_USERNAME, "you", "them" };

		int[] to = new int[] { R.id.gameName1, R.id.opponent1, R.id.myScore1,
				R.id.theirScore1 };

		@SuppressWarnings("deprecation")
		SimpleCursorAdapter sca = new SimpleCursorAdapter(getApplicationContext(), R.layout.previous_game_list_item, c,
				columns1, to);

		@SuppressWarnings("deprecation")
		SimpleCursorAdapter sca1 = new SimpleCursorAdapter(getApplicationContext(), R.layout.previous_game_list_item, c1,
				columns1, to);

		losses.setAdapter(sca1);
		wins.setAdapter(sca);
		
		wins.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent in = new Intent(getApplicationContext(), GameOver.class);
				in.putExtra("androidGameId", c.getInt(c
						.getColumnIndexOrThrow(DBHelper.COLUMN_ID)));
				in.putExtra("creUser", c.getInt(c
						.getColumnIndexOrThrow(DBHelper.COLUMN_CRE_USER)));
				in.putExtra("creBonus", c.getInt(c
						.getColumnIndexOrThrow(DBHelper.COLUMN_CRE_BONUS)));
				in.putExtra("conBonus", c.getInt(c
						.getColumnIndexOrThrow(DBHelper.COLUMN_CON_BONUS)));
				in.putExtra("serverGameId", c.getString(c
						.getColumnIndexOrThrow(DBHelper.COLUMN_SERVER_ID)));
				in.putExtra("gameName", c.getInt(c
						.getColumnIndexOrThrow(DBHelper.COLUMN_GAME_NAME)));
				in.putExtra("conTotal", c.getInt(c
						.getColumnIndexOrThrow(DBHelper.COLUMN_CON_PTS)));
				in.putExtra("creTotal", c.getInt(c
						.getColumnIndexOrThrow(DBHelper.COLUMN_CRE_PTS)));
				startActivityForResult(in, Activity.RESULT_OK);
			}
		});
		
		losses.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent in = new Intent(getApplicationContext(), GameOver.class);
				in.putExtra("androidGameId", c1.getInt(c1
						.getColumnIndexOrThrow(DBHelper.COLUMN_ID)));
				in.putExtra("creUser", c1.getInt(c1
						.getColumnIndexOrThrow(DBHelper.COLUMN_CRE_USER)));
				in.putExtra("creBonus", c1.getInt(c1
						.getColumnIndexOrThrow(DBHelper.COLUMN_CRE_BONUS)));
				in.putExtra("conBonus", c1.getInt(c1
						.getColumnIndexOrThrow(DBHelper.COLUMN_CON_BONUS)));
				in.putExtra("serverGameId", c1.getString(c1
						.getColumnIndexOrThrow(DBHelper.COLUMN_SERVER_ID)));
				in.putExtra("gameName", c1.getInt(c1
						.getColumnIndexOrThrow(DBHelper.COLUMN_GAME_NAME)));
				in.putExtra("conTotal", c1.getInt(c1
						.getColumnIndexOrThrow(DBHelper.COLUMN_CON_PTS)));
				in.putExtra("creTotal", c1.getInt(c1
						.getColumnIndexOrThrow(DBHelper.COLUMN_CRE_PTS)));
				startActivityForResult(in, Activity.RESULT_OK);
			}
		});
	}
}
