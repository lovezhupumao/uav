package com.ypai.uav.MyView;

import java.sql.Struct;

import android.graphics.Point;
import android.graphics.PointF;

public class UAVMethod {

	private static final double TRAN_CONTANT = 0.001797;//200m¾­Î³²î
	private static final double WIDTH = 800;
	private static final double HEIGHT = 800;
	private static final double X_RATIO = TRAN_CONTANT / (WIDTH);
	private static final double Y_RATIO = TRAN_CONTANT / (HEIGHT);

	public static PointF gpsTodp(double latitude, double longitude) {
		PointF point = new PointF();
		point.x = (float) (longitude / X_RATIO );
		point.y = (float) (latitude / Y_RATIO );

		return point;
	}

	public static GPSLocation dpTogps(PointF pointF) {
		GPSLocation gps=new GPSLocation();
		gps.latitude=pointF.y*Y_RATIO;
		gps.longitude=pointF.x*X_RATIO;
		return gps;
	}
}

 
