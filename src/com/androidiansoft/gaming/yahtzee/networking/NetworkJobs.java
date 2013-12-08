package com.androidiansoft.gaming.yahtzee.networking;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.androidiansoft.gaming.yahtzee.activities.LoginScreen;
import com.androidiansoft.gaming.yahtzee.activities.XMLParser;
import com.androidiansoft.gaming.yahtzee.data.CommonUtilities;
import com.androidiansoft.gaming.yahtzee.data.Game;
import com.androidiansoft.gaming.yahtzee.data.Turn;
import com.androidiansoft.gaming.yahtzee.data.User;
import com.google.android.gcm.GCMRegistrar;

public class NetworkJobs {

	private static final String SCORES_URL = CommonUtilities.SERVER_URL
			+ "turns/findSelectedScores/";
	private static final String CREATE_ROLL = CommonUtilities.SERVER_URL
			+ "rolls/create";
	private static final String CREATE_TURN = CommonUtilities.SERVER_URL
			+ "turns/create";
	private static final String UPDATE_TURN = CommonUtilities.SERVER_URL
			+ "turns/update";
	private static final String UPDATE_GAME = CommonUtilities.SERVER_URL
			+ "games/update";
	private static final String GET_NULL = CommonUtilities.SERVER_URL
			+ "turns/findTurnsNull/";
	private static final String UPDATE_FINAL = CommonUtilities.SERVER_URL
			+ "games/updateFinal/";
	private static final String GET_GAME = CommonUtilities.SERVER_URL
			+ "games/";
	static AsyncTask<Void, Void, Void> mRegisterTask;
	private static final int MAX_ATTEMPTS = 5;
	private static final int BACKOFF_MILLI_SECONDS = 2000;
	private static final Random random = new Random();
	static boolean verified;

	public static boolean verifyGCM(final Context context) {
		verified = true;
		GCMRegistrar.checkDevice(context);
		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(context);
		final String regId = GCMRegistrar.getRegistrationId(context);
		if (regId.equals("")) {
			// Automatically registers application on startup.
			GCMRegistrar.register(context, CommonUtilities.SENDER_ID);
		} else {
			// Device is already registered on GCM, check server.
			if (GCMRegistrar.isRegisteredOnServer(context)) {
				Log.v("GCM", "Already Registered");
				// Skips registration.
			} else {
				// Try to register again, but not in the UI thread.
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of AsyncTask instead of a raw thread.
			}
		}
		return verified;
	}

	// Place data in hashmap and send request to service
	static public void createUser(String url, User newUser) {

		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
			nameValuePairs.add(new BasicNameValuePair("first", newUser
					.getFirstname()));
			nameValuePairs.add(new BasicNameValuePair("last", newUser
					.getLastname()));
			nameValuePairs.add(new BasicNameValuePair("email", newUser
					.getEmail()));
			nameValuePairs.add(new BasicNameValuePair("username", newUser
					.getUsername()));
			nameValuePairs.add(new BasicNameValuePair("password", newUser
					.getPassword()));
			nameValuePairs.add(new BasicNameValuePair("gcmid", newUser
					.getGcmID()));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			httpClient.execute(httpPost);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void updateDataFromTurn(int gameId, int scoreChoice,
			int points, int user, int turn, int total, int valid, int conBonus, int creBonus) {
		Log.v("INSIDE SYNC", "Syncing Turn");
		Turn newTurn = new Turn();
		newTurn.setGameId(gameId);
		newTurn.setUserId(user);
		newTurn.setScoreSelected(scoreChoice);
		newTurn.setPointsForScore(points);
		// XMLParser.updateTurn(UPDATE_TURN, newTurn);
		Game newGame = new Game();
		newGame.setId(gameId);
		newGame.setTurn(turn);
		newGame.setValid(valid);
		newGame.setConBonus(conBonus);
		newGame.setCreBonus(creBonus);
		// Setting created points just to pass total variable to XML parser,
		// it's really the total points for ONE of the players, idk which one
		newGame.setCrePts(total);
		XMLParser.createTurnAndUpdateGame(CREATE_TURN, newTurn, newGame);
	}
}
