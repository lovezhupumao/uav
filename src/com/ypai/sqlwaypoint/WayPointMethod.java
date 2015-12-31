package com.ypai.sqlwaypoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.ypai.sqlwaypoint.WayPointContract.WayPoint;

import android.R.integer;
import android.R.string;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class WayPointMethod {

	public static long addwaypoint(WayPointDbHelper mDbHelper,double altitude,double latitude,double longitude) {
		SQLiteDatabase db;
		db=mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(WayPoint.COLUMN_NAME_POINT_ALTITUDE,altitude+"");
		values.put(WayPoint.COLUMN_NAME_POINT_LATITUDE, latitude+"");
		values.put(WayPoint.COLUMN_NAME_POINT_LONGITUDE, longitude+"");
		long newRowId;
		newRowId = db.insert(WayPoint.TABLE_NAME,WayPoint.COLUMN_NAME_POINT_ALTITUDE,values);
		return newRowId;
	}
	public static int deletewaypoint(WayPointDbHelper mDbHelper,double altitude, double latitude,double longitude) {
		SQLiteDatabase db;
		int newRowId;
		db=mDbHelper.getWritableDatabase();
		String where=WayPoint.COLUMN_NAME_POINT_ALTITUDE+"=? and "+ WayPoint.COLUMN_NAME_POINT_LATITUDE +"=? and "+WayPoint.COLUMN_NAME_POINT_LONGITUDE+"=?" ;
		String[] whereValue={Double.toString(altitude), Double.toString(latitude),Double.toString(longitude)};
		newRowId=db.delete(WayPoint.TABLE_NAME, where, whereValue);
		//db.update(WayPoint.TABLE_NAME, values, whereClause, whereArgs);
		//String sqlstring="update "+WayPoint.TABLE_NAME +" set "+ WayPoint._ID +"="+WayPoint._ID+"-1 where " +WayPoint._ID+">?";
		//db.rawQuery(sqlstring,new String[] {num+""});
		return newRowId;
	}
	public static int deletewaypoint(WayPointDbHelper mDbHelper) {
		SQLiteDatabase db;
		int newRowId;
		db=mDbHelper.getWritableDatabase();
		newRowId=db.delete(WayPoint.TABLE_NAME,null,null);
		return newRowId;
	}
	public static Cursor selectwaypoint(WayPointDbHelper mDbHelper) {
		SQLiteDatabase db;
		db=mDbHelper.getReadableDatabase();
		Cursor c=db.query(WayPoint.TABLE_NAME, null, null, null, null, null, null);
		return c;
	}
}
