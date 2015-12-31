package com.ypai.sqlwaypoint;

import android.provider.BaseColumns;

public final class WayPointContract {
	 public WayPointContract() {}

	    /* Inner class that defines the table contents */
	    public static abstract class WayPoint implements BaseColumns {
	        public static final String TABLE_NAME = "waypoint";
	        public static final String COLUMN_NAME_POINT_NUM = "pointnum";
	        public static final String COLUMN_NAME_POINT_ALTITUDE = "pointaltitude";
	        public static final String COLUMN_NAME_POINT_LATITUDE = "pointlatitude";
	        public static final String COLUMN_NAME_POINT_LONGITUDE = "pointlongitude";
			public static final String COLUMN_NAME_UPDATED = "update";
	       
	    }
}
