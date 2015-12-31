package com.ypai.sqlwaypoint;



import com.ypai.sqlwaypoint.WayPointContract.WayPoint;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WayPointDbHelper extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "WayPoint.db";
	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";
	private static final String SQL_CREATE_ENTRIES = "CREATE TABLE "
			+ WayPoint.TABLE_NAME + " (" + WayPoint._ID
			+ " INTEGER PRIMARY KEY" +  COMMA_SEP +WayPoint.COLUMN_NAME_POINT_ALTITUDE + TEXT_TYPE
			+ COMMA_SEP + WayPoint.COLUMN_NAME_POINT_LATITUDE + TEXT_TYPE+COMMA_SEP+WayPoint.COLUMN_NAME_POINT_LONGITUDE + TEXT_TYPE +" )";

	private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
			+ WayPoint.TABLE_NAME;

	public WayPointDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(SQL_CREATE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
	}

}
