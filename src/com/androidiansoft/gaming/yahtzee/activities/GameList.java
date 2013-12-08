package com.androidiansoft.gaming.yahtzee.activities;

import java.util.ArrayList;

import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidiansoft.gaming.yahtzee.data.DBHelper;
import com.androidiansoft.gaming.yahtzee.data.Game;

public class GameList extends android.support.v4.app.Fragment {

	ArrayList<Game> games = new ArrayList<Game>();
	StringBuffer sb;
	NodeList nl;
	NodeList nl1;
	NodeList nl2;
	Toast mToast;
	ProgressDialog pdia;
	SharedPreferences preferences;
	ListView myTurn;
	ListView yourTurn;
	Button newGame;
	TextView nameTitle;
	public static Cursor c1 = null;

	public GameList() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View V = inflater.inflate(R.layout.game_main, container, false);
		nameTitle = (TextView) V.findViewById(R.id.nameTitle);
		myTurn = (ListView) V.findViewById(R.id.myTurnList);
		yourTurn = (ListView) V.findViewById(R.id.opponentTurnList);
		newGame = (Button) V.findViewById(R.id.newGame);
		preferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity()
						.getApplicationContext());
		nameTitle.setText(preferences.getString(LoginScreen.USERNAME, ""));
		getGamesListFromDB();
		return V;
	}

	private void getGamesListFromDB() {

		final Cursor c = getActivity().getContentResolver().query(
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
						DBHelper.COLUMN_OPP_USERNAME, DBHelper.COLUMN_TURN, DBHelper.COLUMN_CON_PTS, DBHelper.COLUMN_CRE_PTS },
				"(valid = ?) OR (valid != ? AND turn = ?)",
				new String[] { "2","0",preferences.getInt(LoginScreen.ID, 0) + ""},
				null);

		/*
		 * final Cursor c = getContentResolver().query( Provider.GAME_URI, null,
		 * "turn = ? AND " + DBHelper.COLUMN_CRE_USER + " = ?", new String[] {
		 * preferences.getInt(LoginScreen.ID, 0) + "",
		 * preferences.getInt(LoginScreen.ID, 0) + "" }, null);
		 */
		c1 = getActivity().getContentResolver().query(
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
						DBHelper.COLUMN_OPP_USERNAME, DBHelper.COLUMN_TURN, DBHelper.COLUMN_CON_PTS, DBHelper.COLUMN_CRE_PTS },
				"turn != ? AND valid = ?",
				new String[] { preferences.getInt(LoginScreen.ID, 0) + "", "1" },
				null);

		getActivity().startManagingCursor(c1);
		getActivity().startManagingCursor(c);

		String[] columns1 = new String[] { DBHelper.COLUMN_GAME_NAME,
				DBHelper.COLUMN_OPP_USERNAME, "you", "them" };

		int[] to = new int[] { R.id.gameName, R.id.opponent, R.id.myScore,
				R.id.theirScore };

		@SuppressWarnings("deprecation")
		SimpleCursorAdapter sca = new SimpleCursorAdapter(getActivity()
				.getApplicationContext(), R.layout.game_list_item_green, c,
				columns1, to);

		@SuppressWarnings("deprecation")
		SimpleCursorAdapter sca1 = new SimpleCursorAdapter(getActivity()
				.getApplicationContext(), R.layout.game_list_item_red, c1,
				columns1, to);

		yourTurn.setAdapter(sca1);
		yourTurn.setClickable(false);
		myTurn.setAdapter(sca);

		myTurn.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				c.moveToPosition(position);
				// If game is valid, and turn = my id then it's my turn
				if (c.getInt(c.getColumnIndexOrThrow("valid")) == 1
						&& c.getInt(c.getColumnIndexOrThrow("turn")) == preferences
								.getInt(LoginScreen.ID, 0)) {

					Intent in = new Intent(getActivity()
							.getApplicationContext(), GameFragmentManager.class);
					in.putExtra("androidGameId", c.getInt(c
							.getColumnIndexOrThrow(DBHelper.COLUMN_ID)));
					in.putExtra("single", c.getInt(c
							.getColumnIndexOrThrow(DBHelper.COLUMN_SINGLE)));
					in.putExtra("turn", c.getInt(c
							.getColumnIndexOrThrow(DBHelper.COLUMN_TURN)));
					in.putExtra("serverGameId", c.getString(c
							.getColumnIndexOrThrow(DBHelper.COLUMN_SERVER_ID)));
					in.putExtra("gameName", c.getString(c
							.getColumnIndexOrThrow(DBHelper.COLUMN_GAME_NAME)));
					startActivity(in);

					// Take them to Game Over Activity
				} else if (c.getInt(c.getColumnIndexOrThrow("valid")) == 2) {
					Intent in = new Intent(getActivity()
							.getApplicationContext(), GameOver.class);
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
					
					// If game  is valid and it is not my turn
				} else if (c.getInt(c.getColumnIndexOrThrow("valid")) == 1
						&& c.getInt(c.getColumnIndexOrThrow("turn")) != preferences
								.getInt(LoginScreen.ID, 0)) {
					mToast = Toast.makeText(getActivity()
							.getApplicationContext(),
							"It is not your turn yet.. Be patient. JEEZE",
							Toast.LENGTH_LONG);
					mToast.show();
					getActivity().finish();
				}
			}
		});

		newGame.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent in = new Intent(getActivity().getApplicationContext(),
						NewGame.class);
				startActivityForResult(in, Activity.RESULT_OK);
			}
		});
	}
}
