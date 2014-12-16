package com.aml.locationreminder.databasecomponent;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class LocationReminderDatabaseAccessUtility extends ContentProvider {
	protected static String DB_NAME = "LocationReminder.db";
	protected static final int DB_VERSION = 1;
	LocationReminderDatabaseHandler dbHandler;
	public static String authority = "com.aml.locationreminder.databasecomponent.provider";
	public static Uri CONTENT_URI = Uri.parse("content://" + authority
			+ "/LocationReminder");

	@Override
	public boolean onCreate() {
		if (dbHandler == null)
			dbHandler = new LocationReminderDatabaseHandler(getContext(),
					DB_NAME, null, DB_VERSION);
		if (dbHandler == null)
			return false;

		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor cursor = dbHandler.queryData();
		
		return cursor;
	}

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		dbHandler.delete(CONTENT_URI, arg1, arg2);
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		dbHandler.insert(uri, values);
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
