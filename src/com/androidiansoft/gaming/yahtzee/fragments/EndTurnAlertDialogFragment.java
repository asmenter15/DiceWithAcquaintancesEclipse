package com.androidiansoft.gaming.yahtzee.fragments;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SyncInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.EditText;
import android.widget.Toast;

import com.androidiansoft.gaming.yahtzee.activities.LoginScreen;
import com.androidiansoft.gaming.yahtzee.activities.Provider;
import com.androidiansoft.gaming.yahtzee.activities.Stats;
import com.androidiansoft.gaming.yahtzee.activities.TurnData;
import com.androidiansoft.gaming.yahtzee.data.CommonUtilities;
import com.androidiansoft.gaming.yahtzee.data.DBHelper;

public class EndTurnAlertDialogFragment extends DialogFragment {

	Toast mToast;
	EditText input;
	SharedPreferences preferences;
	int myTotal;
	int oppTotal;
	int turnScore;

	// Positive Button
	OnClickListener positiveListener = new OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			updateTurn();
		}
	};

	/**
	 * This is a callback method which will be executed on creating this
	 * fragment
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		preferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity()
						.getApplicationContext());
		Bundle args = new Bundle();
		args = getArguments();
		myTotal = args.getInt("myTotal");
		oppTotal = args.getInt("oppTotal");
		turnScore = args.getInt("turnScore");
		AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
		b.setTitle("Turn Complete");
		b.setMessage("Your score for this turn was " + turnScore
				+ "\n\nYour total score so far is " + myTotal
				+ "\n\nYour opponents total score so far is " + oppTotal + "\n");
		b.setPositiveButton("Ok", positiveListener);
		b.setCancelable(false);
		AlertDialog d = b.create();

		return d;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		updateTurn();
		super.onCancel(dialog);
	}

	// Update turn entry in database
	private void updateTurn() {
		AccountManager am = AccountManager.get(getActivity()
				.getApplicationContext());
		Account[] accounts = am.getAccountsByType(CommonUtilities.ACCOUNT_TYPE);
		ContentValues turnTable = new ContentValues();

		turnTable.put(DBHelper.COLUMN_SCORE_SEL, Scoreboard.scoreChoice);
		turnTable.put(DBHelper.COLUMN_PTS_SCORE, Scoreboard.count);

		getActivity().getContentResolver().update(Provider.TURN_URI, turnTable,
				DBHelper.COLUMN_ID + " = ?",
				new String[] { Scoreboard.turnId + "" });

		// Need to update game
		Cursor c = getActivity().getContentResolver().query(
				Provider.GAME_URI,
				new String[] { DBHelper.COLUMN_CRE_USER,
						DBHelper.COLUMN_CON_USER, DBHelper.COLUMN_CRE_PTS,
						DBHelper.COLUMN_CON_PTS }, DBHelper.COLUMN_ID + " = ?",
				new String[] { Scoreboard.androidGameId + "" }, null);

		ContentValues gameTable = new ContentValues();
		Bundle extras = new Bundle();

		int valid = 1;

		c.moveToFirst();
		if (!c.isAfterLast()) {
			int creUser = c.getInt(0);
			int conUser = c.getInt(1);
			int creTotal = c.getInt(2);
			int conTotal = c.getInt(3);
			// int crePoints = c.getInt(2) + Scoreboard.total;
			// int conPoints = c.getInt(3) + Scoreboard.total;
			if (creUser == preferences.getInt(LoginScreen.ID, 0)) {
				gameTable.put(DBHelper.COLUMN_TURN, conUser);
				gameTable.put(DBHelper.COLUMN_CRE_PTS, myTotal + "");
				extras.putInt("totalpoints", myTotal);
				extras.putInt("turn", conUser);
			} else if (conUser == preferences.getInt(LoginScreen.ID, 0)) {
				gameTable.put(DBHelper.COLUMN_TURN, creUser);
				gameTable.put(DBHelper.COLUMN_CON_PTS, myTotal + "");
				extras.putInt("totalpoints", myTotal);
				extras.putInt("turn", creUser);
			}

			if (Scoreboard.myScores.size() == 12
					&& Scoreboard.oppScores.size() == 13
					|| Scoreboard.myScores.size() == 13
					&& Scoreboard.oppScores.size() == 12) {
				
				Scoreboard.upperExtraPoints();
				if (creUser == preferences.getInt(LoginScreen.ID, 0)) {
					
					extras.putInt("creBonus", Scoreboard.myBonus);
					extras.putInt("conBonus", Scoreboard.oppBonus);
					gameTable.put(DBHelper.COLUMN_CRE_BONUS, Scoreboard.myBonus);
					gameTable.put(DBHelper.COLUMN_CON_BONUS, Scoreboard.oppBonus);
					
					if(Scoreboard.oppBonus == 1) {
						gameTable.put(DBHelper.COLUMN_CON_PTS, conTotal + 35);
					}
					if(Scoreboard.myBonus == 1) {
						gameTable.put(DBHelper.COLUMN_CRE_PTS, myTotal + 35);
					}
				} else if (conUser == preferences.getInt(LoginScreen.ID, 0)) {
					
					extras.putInt("creBonus", Scoreboard.oppBonus);
					extras.putInt("conBonus", Scoreboard.myBonus);
					gameTable.put(DBHelper.COLUMN_CON_BONUS, Scoreboard.myBonus);
					gameTable.put(DBHelper.COLUMN_CRE_BONUS, Scoreboard.oppBonus);
					
					if(Scoreboard.oppBonus == 1) {
						gameTable.put(DBHelper.COLUMN_CRE_PTS, creTotal + 35);
					}
					if(Scoreboard.myBonus == 1) {
						gameTable.put(DBHelper.COLUMN_CON_PTS, myTotal + 35);
					}
				}
				
				// TurnData.updateGameFinal(Scoreboard.androidGameId,
				// Scoreboard.total, TurnData.oppTotalScore,
				// Scoreboard.myBonus, Scoreboard.oppBonus);
				// Scoreboard.myScores.clear();
				// Scoreboard.oppTotal = 0;
				// Scoreboard.total = 0;
				// TurnData.oppScores.clear();
				// TurnData.createdUser = false;

				valid = 2;

			}

			gameTable.put(DBHelper.COLUMN_VALID, valid);

			getActivity().getContentResolver().update(Provider.GAME_URI,
					gameTable, DBHelper.COLUMN_ID + " = ?",
					new String[] { Scoreboard.androidGameId + "" });
		}

		try {
			int servID = Integer.parseInt(Scoreboard.serverGameId);
			// This will create syncadapter to post turn data
			extras.putString("synctype", "turnpost");
			extras.putInt("user", preferences.getInt(LoginScreen.ID, 0));
			extras.putInt("gameid", servID);
			extras.putInt("points", Scoreboard.count);
			extras.putInt("scorechoice", Scoreboard.scoreChoice);
			extras.putInt("valid", valid);
			extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
			extras.putBoolean(ContentResolver.SYNC_EXTRAS_FORCE, true);
			ContentResolver
					.requestSync(accounts[0], Provider.AUTHORITY, extras);
		} catch (NumberFormatException ne) {
			// DONT POST
		}
		getActivity().finish();

	}

}