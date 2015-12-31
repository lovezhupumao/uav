package com.service.uav;

import android.content.Context;
import dji.sdk.api.DJIDrone;
import dji.sdk.api.DJIDroneTypeDef.DJIDroneType;
import dji.sdk.interfaces.DJIDroneTypeChangedCallback;
import dji.sdk.interfaces.DJIGeneralListener;

public class uav {
	public static void checkPermission(Context mContext, DJIGeneralListener mListener) {
		try {
			DJIDrone.checkPermission(mContext, mListener);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void initAPPManager(Context mContext, DJIDroneTypeChangedCallback mCallback) {
		DJIDrone.initAPPManager(mContext,mCallback);
	}

}
