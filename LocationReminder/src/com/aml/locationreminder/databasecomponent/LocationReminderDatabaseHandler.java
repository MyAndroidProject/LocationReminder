package com.aml.locationreminder.databasecomponent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class LocationReminderDatabaseHandler extends SQLiteOpenHelper implements
		Runnable {

	protected SQLiteDatabase mbDataBase;

	protected static final String table_name = "LocationReminderTable";

	public static class UserTable {
		public static String id = "_id";
		public static String latitude = "latitude";
		public static String longitude = "longitude";

	}

	protected LocationReminderDatabaseHandler(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		Thread t = new Thread(this);
		t.start();

		try {
			if (t.isAlive()) {
				t.join();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onCreate(SQLiteDatabase db) {

		String str = "create table if not exists " + table_name + " ( "
				+ UserTable.id + " TEXT, " + UserTable.latitude + " DOUBLE,"
				+ UserTable.longitude + " DOUBLE);";
		db.execSQL(str);
	}

	public void run() {
		mbDataBase = getWritableDatabase();
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		mbDataBase.execSQL("DROP TABLE IF EXISTS " + table_name);
		onCreate(mbDataBase);
	}

	protected SQLiteDatabase getDb() {
		return mbDataBase;
	}

	protected void insert(Uri uri, ContentValues values) {

		mbDataBase.insert(table_name, null, values);
	}

	protected void delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		if (arg1 != null) {
			String dl = arg1 + "= '" + arg2[0] + "'";
			mbDataBase.delete(table_name, dl, null);
		} else {
			mbDataBase.delete(table_name, null, null);
		}

	}

	public Cursor queryData() {
		String selectquery = "select * from "+ table_name ;
		Cursor cursor = mbDataBase.rawQuery(selectquery, null);
		return cursor;
	}

/*	@Override
	public synchronized void close() {
		// TODO Auto-generated method stub
		super.close();
		if (mbDataBase.isOpen()) {
			mbDataBase.close();
		}
	}
*/	
	
}
