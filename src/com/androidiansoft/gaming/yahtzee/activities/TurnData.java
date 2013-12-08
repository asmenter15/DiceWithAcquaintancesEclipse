package com.androidiansoft.gaming.yahtzee.activities;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.SparseIntArray;

import com.androidiansoft.gaming.yahtzee.data.CommonUtilities;
import com.androidiansoft.gaming.yahtzee.data.Game;
import com.androidiansoft.gaming.yahtzee.data.Roll;
import com.androidiansoft.gaming.yahtzee.data.Turn;

public class TurnData extends Activity {

	static AsyncTask<String, Void, SparseIntArray> asyncTask;
	static AsyncTask<String, Void, Void> asyncTask1;
	private static final String SCORES_URL = CommonUtilities.SERVER_URL + "turns/findSelectedScores/";
	private static final String CREATE_ROLL = CommonUtilities.SERVER_URL + "rolls/create";
	private static final String CREATE_TURN = CommonUtilities.SERVER_URL + "turns/create";
	private static final String UPDATE_TURN = CommonUtilities.SERVER_URL + "turns/update";
	private static final String UPDATE_GAME = CommonUtilities.SERVER_URL + "games/update";
	private static final String GET_NULL = CommonUtilities.SERVER_URL + "turns/findTurnsNull/";
	private static final String UPDATE_FINAL = CommonUtilities.SERVER_URL + "games/updateFinal/";
	private static final String GET_GAME = CommonUtilities.SERVER_URL + "games/";
	private static final String SELECTED = "scoreselection";
	private static final String GAME = "game";
	private static final String TURN = "turn";
	private static final String ID = "id";
	private static final String PTS_FOR_SCORE = "pointsforscore";
	private static final String CON_USER = "connectedUser";
	private static final String CRE_USER = "createdUser";
	static NodeList nl;
	static NodeList nl1;
	public static int myTotalScore;
	public static int oppTotalScore;
	static StringBuffer sb;
	public static SparseIntArray myScores = new SparseIntArray();
	public static SparseIntArray oppScores = new SparseIntArray();
	public static int oppId = 0;
	static int id = 0;
	static String strCre;
	static String strCon;
	public static String strTrn;
	static String strTurn;
	public static boolean createdUser = false;
	public static boolean dataPull = false;
	public static boolean dataPost = false;
	static ProgressDialog pdia;

	// Get data before turn so scoreboard can be populated
	public static SparseIntArray getDataForTurn(final int gameId)
			throws InterruptedException, ExecutionException, TimeoutException {
		asyncTask = new AsyncTask<String, Void, SparseIntArray>() {

			@Override
			protected void onPreExecute() {
				// pdia.setMessage("Loading Game Data...");
				// pdia.show();
				super.onPreExecute();
			}

			@Override
			protected SparseIntArray doInBackground(String... params) {

				createdUser = false;
				myScores.clear();
				myTotalScore = 0;
				oppTotalScore = 0;
				oppScores.clear();
				sb = new StringBuffer();
				sb.append(SCORES_URL);
				sb.append(gameId + "&" + LoginScreen.preferences.getInt(LoginScreen.ID, 0));

				XMLParser parser = new XMLParser();
				String xml = parser.getXmlFromUrlGet(sb.toString());
				Document document = parser.getDomElement(xml);
				nl = document.getElementsByTagName(TURN);
				int temp = 0;
				for (int t = 0; t < nl.getLength(); t++) {

					Element e = (Element) nl.item(t);
					String strSel = parser.getValue(e, ID, SELECTED);
					String strPts = parser.getValue(e, PTS_FOR_SCORE, TURN);

					if (strSel == null || strPts == null) {
						// Do nothing
					} else {
						myScores.put(Integer.parseInt(strSel),
								Integer.parseInt(strPts));
						temp += Integer.parseInt(strPts);
					}
				}
				myTotalScore = temp;

				sb = new StringBuffer();
				sb.append(GET_GAME);
				sb.append(gameId);
				XMLParser parser1 = new XMLParser();
				String xml1 = parser1.getXmlFromUrlGet(sb.toString());
				Document document1 = parser.getDomElement(xml1);
				nl = document1.getElementsByTagName(GAME);

				for (int t = 0; t < nl.getLength(); t++) {

					Element e = (Element) nl.item(t);
					strCre = parser.getValue(e, ID, CRE_USER);
					strCon = parser.getValue(e, ID, CON_USER);
				}

				if (strCre != null && strCon != null) {
					if (Integer.parseInt(strCre) == (LoginScreen.preferences.getInt(LoginScreen.ID, 0))) {
						createdUser = true;
						oppId = Integer.parseInt(strCon);
					} else {
						createdUser = false;
						oppId = Integer.parseInt(strCre);
					}
				}

				sb = new StringBuffer();

				sb.append(SCORES_URL);
				sb.append(gameId + "&" + oppId);

				XMLParser parser2 = new XMLParser();
				String xml2 = parser2.getXmlFromUrlGet(sb.toString());
				Document document2 = parser.getDomElement(xml2);
				nl = document2.getElementsByTagName(TURN);
				int temp1 = 0;
				for (int t = 0; t < nl.getLength(); t++) {

					Element e = (Element) nl.item(t);
					String strSel1 = parser.getValue(e, ID, SELECTED);
					String strPts1 = parser.getValue(e, PTS_FOR_SCORE, TURN);

					if (strSel1 == null || strPts1 == null) {
						// Do nothing
					} else {
						oppScores.put(Integer.parseInt(strSel1),
								Integer.parseInt(strPts1));
						temp1 += Integer.parseInt(strPts1);
					}
				}
				oppTotalScore = temp1;

				return myScores;

			}

			@Override
			protected void onPostExecute(SparseIntArray result) {
				super.onPostExecute(result);
			}
		};
		asyncTask.execute(SCORES_URL).get(30000, TimeUnit.MILLISECONDS);
		dataPull = true;
		return myScores;
	}

	// Create Turn entry in database with blank data so it can be updated after
	// the turn
	public static void postDataFromTurn(final int gameId)
			throws InterruptedException, ExecutionException, TimeoutException {
		asyncTask1 = new AsyncTask<String, Void, Void>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected Void doInBackground(String... params) {
				Turn newTurn = new Turn();
				newTurn.setGameId(gameId);
				newTurn.setUserId(LoginScreen.preferences.getInt(LoginScreen.ID, 0));
				//XMLParser.createTurn(CREATE_TURN, newTurn);
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				// pdia.dismiss();
				super.onPostExecute(result);
			}
		};
		asyncTask1.execute(SCORES_URL).get(30000, TimeUnit.MILLISECONDS);
		dataPost = true;
	}

	// Update turn entry in database with rolls and selected score
	public static void updateDataFromTurn(final int gameId,
			final int scoreChoice, final int points, final int turn)
			throws InterruptedException, ExecutionException, TimeoutException {
		asyncTask1 = new AsyncTask<String, Void, Void>() {

			@Override
			protected void onPreExecute() {
				// pdia.setMessage("Finishing Turn...");
				// pdia.show();
				super.onPreExecute();
			}

			@Override
			protected Void doInBackground(String... params) {
				Turn newTurn = new Turn();
				newTurn.setGameId(gameId);
				newTurn.setUserId(LoginScreen.preferences.getInt(LoginScreen.ID, 0));
				newTurn.setId(Integer.parseInt(strTurn));
				newTurn.setScoreSelected(scoreChoice);
				newTurn.setPointsForScore(points);
				XMLParser.updateTurn(UPDATE_TURN, newTurn);
				Game newGame = new Game();
				newGame.setId(gameId);
				if (turn == 0) {
					newGame.setTurn(1);
				} else if (turn == 1) {
					newGame.setTurn(0);
				}
				XMLParser.updateGame(UPDATE_GAME, newGame);
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				// pdia.dismiss();
				super.onPostExecute(result);
			}
		};
		asyncTask1.execute(SCORES_URL).get(30000, TimeUnit.MILLISECONDS);
	}

	// Update game entry in database with scores and valid field
	public static void updateGameFinal(final int gameId, final int myTotal,
			final int oppTotal, final int myBonus, final int oppBonus)
			throws InterruptedException, ExecutionException, TimeoutException {
		asyncTask1 = new AsyncTask<String, Void, Void>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected Void doInBackground(String... params) {
				sb = new StringBuffer();
				sb.append(UPDATE_FINAL);

				Game newGame = new Game();

				newGame.setCrePts(oppTotal);
				newGame.setConPts(myTotal);
				newGame.setConBonus(myBonus);
				newGame.setCreBonus(oppBonus);
				newGame.setId(gameId);
				newGame.setValid(0);
				XMLParser.updateFinalGame(UPDATE_FINAL, newGame);
				return null;
			}
		};
		asyncTask1.execute(SCORES_URL).get(30000, TimeUnit.MILLISECONDS);
	}

	// On every roll create roll entry in database
	public static void postDataFromRoll(final int gameId, final int die1,
			final int die2, final int die3, final int die4, final int die5,
			final int rollNum) throws InterruptedException, ExecutionException,
			TimeoutException {
		asyncTask1 = new AsyncTask<String, Void, Void>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected Void doInBackground(String... params) {
				strTurn = null;
				sb = new StringBuffer();
				sb.append(GET_NULL);
				sb.append(gameId + "&" + LoginScreen.preferences.getInt(LoginScreen.ID, 0));

				XMLParser parser = new XMLParser();
				String xml = parser.getXmlFromUrlGet(sb.toString());
				Document document = parser.getDomElement(xml);
				nl = document.getElementsByTagName(TURN);
				for (int t = 0; t < nl.getLength(); t++) {

					Element e = (Element) nl.item(t);
					strTrn = parser.getValue(e, ID, TURN);
					if (strTrn == null) {
						// Do nothing
					} else {
						strTurn = strTrn;
					}
				}

				Roll newRoll = new Roll();
				newRoll.setDie1(die1);
				newRoll.setDie2(die2);
				newRoll.setDie3(die3);
				newRoll.setDie4(die4);
				newRoll.setDie5(die5);
				newRoll.setRollNum(rollNum);
				newRoll.setTurn(Integer.parseInt(strTurn));
				XMLParser.createRoll(CREATE_ROLL, newRoll);
				return null;
			}
		};
		asyncTask1.execute(SCORES_URL).get(30000, TimeUnit.MILLISECONDS);
	}
}
