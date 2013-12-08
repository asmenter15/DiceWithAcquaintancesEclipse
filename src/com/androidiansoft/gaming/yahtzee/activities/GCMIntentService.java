package com.androidiansoft.gaming.yahtzee.activities;

import java.util.ArrayList;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.androidiansoft.gaming.yahtzee.data.CommonUtilities;
import com.androidiansoft.gaming.yahtzee.data.DBHelper;
import com.androidiansoft.gaming.yahtzee.fragments.Scoreboard;
import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	public GCMIntentService() {
		super(CommonUtilities.SENDER_ID);
	}

	@Override
	protected void onRegistered(Context arg0, String registrationId) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(arg0);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("gcmid", registrationId);
		editor.commit();
		Log.v("GCM", "Registered: " + registrationId);
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
	}

	@Override
	protected void onMessage(Context arg0, Intent intent) {
		// If serverGameId needs to be updated / New Game was created
		Log.v("GCM", "Inside GCM RECEIVE");

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(arg0);

		// Inserting the server_id where the server id is 0, because this should
		// be the only one with a 0
		if (intent.getStringExtra("serverGameIdCre") != (null)) {
			ContentValues gameTable = new ContentValues();
			gameTable.put(DBHelper.COLUMN_SERVER_ID,
					intent.getStringExtra("serverGameIdCre") + "");

			ArrayList<String> ids = new ArrayList<String>();
			Cursor c = getContentResolver().query(Provider.GAME_URI,
					new String[] { DBHelper.COLUMN_SERVER_ID },
					DBHelper.COLUMN_SERVER_ID + " LIKE ?",
					new String[] { "disconnected%" }, null);

			c.moveToFirst();
			while (!c.isAfterLast()) {
				ids.add(c.getString(0));
				c.moveToNext();
			}
			c.close();

			int finalId = 1;

			if (!ids.isEmpty()) {
				StringBuilder sb = new StringBuilder(ids.get(0));
				int id = Integer.parseInt(sb.substring(12));
				finalId = id;
				/*
				 * if (ids.size() != 1) { for (int i = 1; i < ids.size(); i++) {
				 * StringBuilder sb1 = new StringBuilder(ids.get(i)); int id1 =
				 * Integer.parseInt(sb1.substring(12)); if (id1 > id) { finalId
				 * = id1; id1 = id; } else { finalId = id; } } } else { finalId
				 * = id; }
				 */}

			Cursor c1 = getContentResolver().query(Provider.GAME_URI, null,
					DBHelper.COLUMN_SERVER_ID + " = ?",
					new String[] { "disconnected" + finalId }, null);

			getContentResolver().update(Provider.GAME_URI, gameTable,
					DBHelper.COLUMN_SERVER_ID + " = ?",
					new String[] { "disconnected" + finalId });

			c1.moveToFirst();
			int andGameID = c1.getInt(c1
					.getColumnIndexOrThrow(DBHelper.COLUMN_ID));
			Cursor c2 = getContentResolver().query(Provider.TURN_URI, null,
					DBHelper.COLUMN_GAME_ID + " = ?",
					new String[] { andGameID + "" }, null);
			c2.moveToFirst();
			Bundle extras = new Bundle();
			if (!c2.isAfterLast()) {
				AccountManager am = AccountManager.get(getApplicationContext());
				Account[] accounts = am
						.getAccountsByType(CommonUtilities.ACCOUNT_TYPE);
				
				c1.moveToFirst();
				int creUser = c1.getInt(c1.getColumnIndexOrThrow(DBHelper.COLUMN_CRE_USER));
				int conUser = c1.getInt(c1.getColumnIndexOrThrow(DBHelper.COLUMN_CON_USER));
				int creTotal = c1.getInt(c1.getColumnIndexOrThrow(DBHelper.COLUMN_CRE_PTS));
				int conTotal = c1.getInt(c1.getColumnIndexOrThrow(DBHelper.COLUMN_CON_PTS));
				
				if (creUser == sp.getInt(LoginScreen.ID, 0)) {
					gameTable.put(DBHelper.COLUMN_TURN, conUser);
					extras.putInt("totalpoints", creTotal);
					extras.putInt("turn", conUser);
				} else if (conUser == sp.getInt(LoginScreen.ID, 0)) {
					gameTable.put(DBHelper.COLUMN_TURN, creUser);
					extras.putInt("totalpoints", conTotal);
					extras.putInt("turn", creUser);
				}
				c1.close();
				extras.putString("synctype", "turnpost");
				extras.putInt("user", sp.getInt(LoginScreen.ID, 0));
				extras.putInt("gameid", Integer.parseInt(intent
						.getStringExtra("serverGameIdCre")));
				extras.putInt("points", Scoreboard.count);
				extras.putInt("scorechoice", Scoreboard.scoreChoice);
				extras.putInt("valid", 1);
				extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
				extras.putBoolean(ContentResolver.SYNC_EXTRAS_FORCE, true);
				ContentResolver.requestSync(accounts[0], Provider.AUTHORITY,
						extras);
			}
			c2.close();
			Log.v("GCM", "UPDATED");
		} else if (intent.getStringExtra("gameConCreate") != (null)) {
			ContentValues gamesTable = new ContentValues();

			gamesTable.put(DBHelper.COLUMN_CRE_USER,
					intent.getStringExtra("creuser"));
			gamesTable.put(DBHelper.COLUMN_CON_USER,
					intent.getStringExtra("conuser"));
			gamesTable.put(DBHelper.COLUMN_GAME_NAME,
					intent.getStringExtra("gamename"));
			gamesTable.put(DBHelper.COLUMN_VALID,
					intent.getStringExtra("valid"));
			gamesTable.put(DBHelper.COLUMN_SINGLE,
					intent.getStringExtra("single"));
			gamesTable.put(DBHelper.COLUMN_OPP_FIRST,
					intent.getStringExtra("oppfirst"));
			gamesTable.put(DBHelper.COLUMN_OPP_LAST,
					intent.getStringExtra("opplast"));
			gamesTable.put(DBHelper.COLUMN_OPP_USERNAME,
					intent.getStringExtra("oppusername"));
			gamesTable.put(DBHelper.COLUMN_CRE_PTS, 0);
			gamesTable.put(DBHelper.COLUMN_CON_PTS, 0);
			gamesTable.put(DBHelper.COLUMN_CRE_BONUS, 0);
			gamesTable.put(DBHelper.COLUMN_CON_BONUS, 0);
			gamesTable.put(DBHelper.COLUMN_SERVER_ID,
					intent.getStringExtra("serverid"));

			getContentResolver().insert(Provider.GAME_URI, gamesTable);

		} else if (intent.getStringExtra("getTurnData") != null) {
			// Update Turn

			Cursor c2 = getContentResolver().query(Provider.GAME_URI, null,
					DBHelper.COLUMN_SERVER_ID + " = ?",
					new String[] { intent.getStringExtra("game") + "" }, null);
			c2.moveToFirst();
			String gameName = c2.getString(c2
					.getColumnIndexOrThrow(DBHelper.COLUMN_GAME_NAME));
			int andGameID = c2.getInt(c2
					.getColumnIndexOrThrow(DBHelper.COLUMN_ID));

			ContentValues turnTable = new ContentValues();

			turnTable.put(DBHelper.COLUMN_GAME_ID, andGameID);
			turnTable.put(DBHelper.COLUMN_USER, intent.getStringExtra("user"));
			turnTable.put(DBHelper.COLUMN_SCORE_SEL,
					intent.getStringExtra("scoreselection"));
			turnTable.put(DBHelper.COLUMN_PTS_SCORE,
					intent.getStringExtra("points"));

			getContentResolver().insert(Provider.TURN_URI, turnTable);

			// Update Game
			int conTotal = c2.getInt(c2
					.getColumnIndexOrThrow(DBHelper.COLUMN_CON_PTS));
			int creTotal = c2.getInt(c2
					.getColumnIndexOrThrow(DBHelper.COLUMN_CRE_PTS));

			
			ContentValues gameTable = new ContentValues();

			if (intent.getStringExtra("turn").equals(
					sp.getInt(LoginScreen.ID, 0) + "")
					&& c2.getInt(c2.getColumnIndexOrThrow(DBHelper.COLUMN_CON_USER)) == (sp.getInt(LoginScreen.ID, 0))) {
				
				if (Integer.parseInt(intent.getStringExtra("creBonus")) == 1) {
					gameTable.put(DBHelper.COLUMN_CRE_PTS, Integer.parseInt(intent.getStringExtra("totalpoints")) + 35);
				} else {
					gameTable.put(DBHelper.COLUMN_CRE_PTS,
							intent.getStringExtra("totalpoints"));
				}
				if(Integer.parseInt(intent.getStringExtra("conBonus")) == 1) {
					gameTable.put(DBHelper.COLUMN_CRE_PTS, conTotal + 35);
				} else {
					// Don't change this users score because they didn't get the bonus
				}
			} else if (intent.getStringExtra("turn").equals(
					sp.getInt(LoginScreen.ID, 0) + "")
					&& c2.getInt(c2.getColumnIndexOrThrow(DBHelper.COLUMN_CRE_USER)) == (sp.getInt(LoginScreen.ID, 0))) {
				
				if (Integer.parseInt(intent.getStringExtra("conBonus")) == 1) {
					gameTable.put(DBHelper.COLUMN_CON_PTS, Integer.parseInt(intent.getStringExtra("totalpoints")) + 35);
				} else {
					gameTable.put(DBHelper.COLUMN_CON_PTS,
							intent.getStringExtra("totalpoints"));
				}
				if(Integer.parseInt(intent.getStringExtra("creBonus")) == 1) {
					gameTable.put(DBHelper.COLUMN_CRE_PTS, creTotal + 35);
				} else {
					// Don't change this users score because they didn't get the bonus
				}
			}
			
			c2.close();
			gameTable.put(DBHelper.COLUMN_TURN, intent.getStringExtra("turn"));

			gameTable
					.put(DBHelper.COLUMN_VALID, intent.getStringExtra("valid"));

			gameTable.put(DBHelper.COLUMN_CON_BONUS,
					intent.getStringExtra("conBonus"));
			gameTable.put(DBHelper.COLUMN_CRE_BONUS,
					intent.getStringExtra("creBonus"));

			getContentResolver().update(Provider.GAME_URI, gameTable,
					DBHelper.COLUMN_SERVER_ID + " = ?",
					new String[] { intent.getStringExtra("game") });

			Cursor c = getContentResolver().query(
					Provider.TURN_URI,
					null,
					DBHelper.COLUMN_USER + " = ? AND "
							+ DBHelper.COLUMN_GAME_ID + " = ?",
					new String[] { sp.getInt(LoginScreen.ID, 0) + "",
							andGameID + "" }, null);
			boolean notFirstTurn = c.moveToFirst();
			c.close();
			Cursor c1 = getContentResolver().query(
					Provider.GAME_URI,
					new String[] { DBHelper.COLUMN_OPP_FIRST,
							DBHelper.COLUMN_OPP_LAST },
					DBHelper.COLUMN_SERVER_ID + " = ?",
					new String[] { intent.getStringExtra("game") + "" }, null);

			String oppFirst = null;
			String oppLast = null;
			c1.moveToFirst();
			if (!c1.isAfterLast()) {
				oppFirst = c1.getString(0);
				oppLast = c1.getString(1);
			}
			c1.close();

			if (!notFirstTurn) {
				generateNotificationNewGame(arg0, oppFirst + " " + oppLast);
			} else {
				generateNotificationTurn(arg0, gameName);
			}
		}
	}

	private void generateNotificationTurn(Context arg0, String string) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				arg0)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("It's your turn to play!")
				.setContentText("Game: " + string)
				.setAutoCancel(true)
				.setDefaults(
						Notification.DEFAULT_LIGHTS
								| Notification.DEFAULT_SOUND
								| Notification.DEFAULT_VIBRATE);
		Intent resultIntent = new Intent(this, GameListFragmentManager.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack
		stackBuilder.addParentStack(GameListFragmentManager.class);
		// Adds the Intent to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		// Gets a PendingIntent containing the entire back stack
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(1, mBuilder.build());
	}

	private void generateNotificationNewGame(Context arg0, String stringExtra) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				arg0)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("You have a new game!")
				.setContentText("VS. " + stringExtra)
				.setAutoCancel(true)
				.setDefaults(
						Notification.DEFAULT_LIGHTS
								| Notification.DEFAULT_SOUND
								| Notification.DEFAULT_VIBRATE);
		Intent resultIntent = new Intent(this, GameListFragmentManager.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack
		stackBuilder.addParentStack(GameListFragmentManager.class);
		// Adds the Intent to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		// Gets a PendingIntent containing the entire back stack
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(1, mBuilder.build());
	}

	@Override
	protected void onError(Context arg0, String errorId) {
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		return super.onRecoverableError(context, errorId);
	}
}
