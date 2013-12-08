package com.androidiansoft.gaming.yahtzee.activities;

import com.androidiansoft.gaming.yahtzee.data.DBHelper;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class Provider extends ContentProvider {

	// Used for the UriMacher
	private static final int GAME = 1;
	private static final int TURN = 2;
	private static final int ROLL = 3;
	public static final String SYNC_TYPE = "synctype";
	static Context context;
	static DBHelper dbHelper;
	static SQLiteDatabase db;
	public static final String AUTHORITY = "com.androidiansoft.gaming.yahtzee.contentprovider";
	public static final Uri GAME_URI = Uri.parse("content://" + AUTHORITY + "/"
			+ DBHelper.TABLE_GAME);
	public static final Uri TURN_URI = Uri.parse("content://" + AUTHORITY + "/"
			+ DBHelper.TABLE_TURN);
	public static final Uri ROLL_URI = Uri.parse("content://" + AUTHORITY + "/"
			+ DBHelper.TABLE_ROLL);
	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);

	static {
		sURIMatcher.addURI(AUTHORITY, DBHelper.TABLE_GAME, GAME);
		sURIMatcher.addURI(AUTHORITY, DBHelper.TABLE_TURN, TURN);
		sURIMatcher.addURI(AUTHORITY, DBHelper.TABLE_ROLL, ROLL);
	}

	@Override
	public boolean onCreate() {
		dbHelper = new DBHelper(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		// Uisng SQLiteQueryBuilder instead of query() method
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case GAME:
			queryBuilder.setTables(DBHelper.TABLE_GAME);
			break;
		case TURN:
			// Adding the ID to the original query
			queryBuilder.setTables(DBHelper.TABLE_TURN);
			break;
		case ROLL:
			queryBuilder.setTables(DBHelper.TABLE_ROLL);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, null, null, sortOrder);
		// Make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
		long id = 0;
		switch (uriType) {
		case GAME:
			id = sqlDB.insert(DBHelper.TABLE_GAME, null, values);
			break;
		case ROLL:
			id = sqlDB.insert(DBHelper.TABLE_ROLL, null, values);
			break;
		case TURN:
			id = sqlDB.insert(DBHelper.TABLE_TURN, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(uri + "/" + id);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
		int rowsDeleted = 0;
		switch (uriType) {
		case GAME:
			rowsDeleted = sqlDB.delete(DBHelper.TABLE_GAME, selection,
					selectionArgs);
			break;
		case TURN:
			rowsDeleted = sqlDB.delete(DBHelper.TABLE_TURN, selection,
					selectionArgs);			
			break;
		case ROLL:
			rowsDeleted = sqlDB.delete(DBHelper.TABLE_ROLL, selection,
					selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
		int rowsUpdated = 0;
		switch (uriType) {
		case GAME:
			rowsUpdated = sqlDB.update(DBHelper.TABLE_GAME, values,
					selection, selectionArgs);
			break;
		case TURN:
			rowsUpdated = sqlDB.update(DBHelper.TABLE_TURN, values,
					selection, selectionArgs);			
			break;
		case ROLL:
			rowsUpdated = sqlDB.update(DBHelper.TABLE_ROLL, values,
					selection, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}

	@Override
	public String getType(Uri arg0) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
