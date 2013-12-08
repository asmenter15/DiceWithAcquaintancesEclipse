package com.androidiansoft.gaming.yahtzee.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	public static final String TABLE_ROLL = "roll";
	public static final String TABLE_TURN = "turn";
	public static final String TABLE_GAME = "game";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_GAME_NAME = "name";
	public static final String COLUMN_CRE_USER = "createduser";
	public static final String COLUMN_CON_USER = "connecteduser";
	public static final String COLUMN_VALID = "valid";
	public static final String COLUMN_SINGLE = "singleplayer";
	public static final String COLUMN_OPP_USERNAME = "oppUsername";
	public static final String COLUMN_OPP_FIRST = "oppFirst";
	public static final String COLUMN_OPP_LAST = "oppLast";
	public static final String COLUMN_CRE_PTS = "createdplayerpts";
	public static final String COLUMN_CON_PTS = "connectedplayerpts";
	public static final String COLUMN_CRE_BONUS = "crebonus";
	public static final String COLUMN_CON_BONUS = "conbonus";
	public static final String COLUMN_TURN_ID = "turnid";
	public static final String COLUMN_TURN = "turn";
	public static final String COLUMN_ROLL_NUMBER = "rollnumber";
	public static final String COLUMN_DIE1 = "die1";
	public static final String COLUMN_DIE2 = "die2";
	public static final String COLUMN_DIE3 = "die3";
	public static final String COLUMN_DIE4 = "die4";
	public static final String COLUMN_DIE5 = "die5";
	public static final String COLUMN_GAME_ID = "gameid";
	public static final String COLUMN_SCORE_SEL = "scoreselected";
	public static final String COLUMN_PTS_SCORE = "pointsforscore";
	public static final String COLUMN_USER = "user";
	public static final String COLUMN_SERVER_ID = "serverid";
	
	private static final String CREATE_GAME_TABLE = "CREATE TABLE "
			+ TABLE_GAME + "(" 
			+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_GAME_NAME + " TEXT NOT NULL, "
			+ COLUMN_CRE_USER + " INTEGER NOT NULL, "
			+ COLUMN_CON_USER + " INTEGER NOT NULL, "
			+ COLUMN_VALID + " INTEGER DEFAULT '1', "
			+ COLUMN_SINGLE + " INTEGER, "
			+ COLUMN_OPP_USERNAME + " TEXT NOT NULL, "
			+ COLUMN_OPP_FIRST + " TEXT NOT NULL, "
			+ COLUMN_OPP_LAST + " TEXT NOT NULL, "
			+ COLUMN_TURN + " INTEGER DEFAULT '0', "
			+ COLUMN_CRE_PTS + " INTEGER, "
			+ COLUMN_SERVER_ID + " TEXT, "
			+ COLUMN_CON_PTS + " INTEGER, "
			+ COLUMN_CON_BONUS + " INTEGER, "
			+ COLUMN_CRE_BONUS + " INTEGER)";
	
	private static final String CREATE_ROLL_TABLE = "CREATE TABLE "
			+ TABLE_ROLL + "(" 
			+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_TURN_ID + " INTEGER NOT NULL, "
			+ COLUMN_ROLL_NUMBER + " INTEGER DEFAULT '1', "
			+ COLUMN_DIE1 + " INTEGER, "
			+ COLUMN_DIE2 + " INTEGER, "
			+ COLUMN_DIE3 + " INTEGER, "
			+ COLUMN_DIE4 + " INTEGER, "
			+ COLUMN_DIE5 + " INTEGER)";
	
	private static final String CREATE_TURN_TABLE = "CREATE TABLE "
			+ TABLE_TURN + "(" 
			+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_GAME_ID + " INTEGER NOT NULL, "
			+ COLUMN_USER + " INTEGER NOT NULL, "
			+ COLUMN_SCORE_SEL + " INTEGER, "
			+ COLUMN_PTS_SCORE + " INTEGER)";
	

	private static final String DATABASE_NAME = "yahtzee";
	private static final int DATABASE_VERSION = 1;

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_GAME_TABLE);
		database.execSQL(CREATE_ROLL_TABLE);
		database.execSQL(CREATE_TURN_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DBHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		//db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
		onCreate(db);
	}

}
