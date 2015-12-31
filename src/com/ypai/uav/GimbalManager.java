package com.ypai.uav;

import dji.sdk.api.DJIDrone;
import dji.sdk.api.Camera.DJICameraSettingsTypeDef.CameraVisionType;
import dji.sdk.api.Gimbal.DJIGimbalRotation;
import android.provider.SyncStateContract.Constants;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
/*
 * 该类的作用是控制云台的运动，如云台俯仰、旋转、左右运动。
 */
public class GimbalManager implements OnClickListener, OnTouchListener {

	private static boolean bPitchUp = false;
	private static boolean bPitchDown = false;
	private static boolean bRollUp = false;
	private static boolean bRollDown = false;
	private static boolean bYawUp = false;
	private static boolean bYawDown = false;
	private static int sumpitch = 0;
	private static int sumroll = 0;
	private static int sumyaw = 0;
	
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.pitchupbtn:
			pitchUp(event);
			break;
			
		case R.id.pitchdownbtn:
			pitchDown(event);
			break;
			
		case R.id.rollupbtn:
			rollUp(event);
			break;
			
		case R.id.rolldownbtn:
			rollDown(event);
			break;
			
		case R.id.yawupbtn:
			yawUp(event);
			break;
			
		case R.id.yawdownbtn:
			yawDown(event);

			break;
		default:
			break;
		}

		return false;
	}

	
	private void yawDown(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			bYawDown = true;
			sumyaw = sumyaw -50;
			new Thread() {
				public void run() {
					DJIGimbalRotation mYaw;
					if (sumyaw<=-1800) {
						mYaw = new DJIGimbalRotation(false,
								false, false, 0);
					} else {
						 mYaw = new DJIGimbalRotation(true,
								false, false, 50);
					}
					
					
					DJIGimbalRotation mYaw_stop = new DJIGimbalRotation(
							false, false, false, 0);

					while (bYawDown) {
						Log.e("111", "yawdown");
						
						DJIDrone.getDjiGimbal().updateGimbalAttitude(null,
								null, mYaw);

						try {
							Thread.sleep(50);
							
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					DJIDrone.getDjiGimbal().updateGimbalAttitude(null,
							null, mYaw_stop);
				}
			}.start();

		} else if (event.getAction() == MotionEvent.ACTION_UP
				|| event.getAction() == MotionEvent.ACTION_OUTSIDE
				|| event.getAction() == MotionEvent.ACTION_CANCEL) {

			bYawDown = false;

		}
	}

	private void yawUp(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			//50的含义是什么
			bYawUp = true;
			sumyaw = sumyaw + 50;
			new Thread() {
				public void run() {
					DJIGimbalRotation mYaw;
				
					if (sumyaw < UavConstants.MaxYaw) {
						 mYaw = new DJIGimbalRotation(true,true, false, 50);
					} else {
						 mYaw = new DJIGimbalRotation(false,false, false, 0);
					}
					
					DJIGimbalRotation mYaw_stop = new DJIGimbalRotation(
							false, false, false, 0);

					while (bYawUp) {
						Log.e("111", "yawup");
						
						DJIDrone.getDjiGimbal().updateGimbalAttitude(null,null, mYaw);

						try {
							Thread.sleep(50);
							
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					DJIDrone.getDjiGimbal().updateGimbalAttitude(null,
							null, mYaw_stop);
				}
			}.start();

		} else if (event.getAction() == MotionEvent.ACTION_UP
				|| event.getAction() == MotionEvent.ACTION_OUTSIDE
				|| event.getAction() == MotionEvent.ACTION_CANCEL) {

			bYawUp = false;

		}
	}

	private void rollDown(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			bRollDown = true;

			new Thread() {
				public void run() {
					DJIGimbalRotation mRoll = new DJIGimbalRotation(true,
							true, false, 50);

					DJIGimbalRotation mRoll_stop = new DJIGimbalRotation(
							false, false, false, 0);

					while (bRollDown) {
						Log.e("111", "rolldown");

						sumroll = sumroll - 50;
						DJIDrone.getDjiGimbal().updateGimbalAttitude(null,
								mRoll, null);

						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					DJIDrone.getDjiGimbal().updateGimbalAttitude(null,
							mRoll_stop, null);
				}
			}.start();

		} else if (event.getAction() == MotionEvent.ACTION_UP
				|| event.getAction() == MotionEvent.ACTION_OUTSIDE
				|| event.getAction() == MotionEvent.ACTION_CANCEL) {

			bRollDown = false;

		}
	}

	private void rollUp(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			bRollUp = true;

			new Thread() {
				public void run() {
					DJIGimbalRotation mRoll = new DJIGimbalRotation(true,
							false, false, 50);

					DJIGimbalRotation mRoll_stop = new DJIGimbalRotation(
							false, false, false, 0);

					while (bRollUp) {
						Log.e("111", "rollup");

						DJIDrone.getDjiGimbal().updateGimbalAttitude(null,
								mRoll, null);
						sumroll = sumroll + 50;
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					DJIDrone.getDjiGimbal().updateGimbalAttitude(null,
							mRoll_stop, null);
				}
			}.start();

		} else if (event.getAction() == MotionEvent.ACTION_UP
				|| event.getAction() == MotionEvent.ACTION_OUTSIDE
				|| event.getAction() == MotionEvent.ACTION_CANCEL) {

			bRollUp = false;

		}
	}

	private void pitchDown(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			bPitchDown = true;

			new Thread() {
				public void run() {
					DJIGimbalRotation mPitch = new DJIGimbalRotation(true,
							false, false, 50);

					DJIGimbalRotation mPitch_stop = new DJIGimbalRotation(
							false, false, false, 0);

					while (bPitchDown) {
						Log.e("111", "pitchdown");

						DJIDrone.getDjiGimbal().updateGimbalAttitude(
								mPitch, null, null);
						sumpitch = sumpitch - 50;

						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					DJIDrone.getDjiGimbal().updateGimbalAttitude(
							mPitch_stop, null, null);
				}
			}.start();

		} else if (event.getAction() == MotionEvent.ACTION_UP
				|| event.getAction() == MotionEvent.ACTION_OUTSIDE
				|| event.getAction() == MotionEvent.ACTION_CANCEL) {

			bPitchDown = false;

		}
	}

	private void pitchUp(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			bPitchUp = true;

			new Thread() {
				public void run() {
					DJIGimbalRotation mPitch = new DJIGimbalRotation(true,
							true, false, 50);

					DJIGimbalRotation mPitch_stop = new DJIGimbalRotation(
							false, false, false, 0);

					while (bPitchUp) {
						Log.e("111", "pitchup");

						DJIDrone.getDjiGimbal().updateGimbalAttitude(
								mPitch, null, null);
						sumpitch = sumpitch + 50;

						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					DJIDrone.getDjiGimbal().updateGimbalAttitude(
							mPitch_stop, null, null);
				}
			}.start();

		} else if (event.getAction() == MotionEvent.ACTION_UP
				|| event.getAction() == MotionEvent.ACTION_OUTSIDE
				|| event.getAction() == MotionEvent.ACTION_CANCEL) {

			bPitchUp = false;

		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.pitchupbtn:

		case R.id.pitchdownbtn:

			break;
		case R.id.rollupbtn:

			break;
		case R.id.rolldownbtn:

			break;
		case R.id.yawupbtn:

			break;
		case R.id.yawdownbtn:

			break;
		case R.id.gimbalreset:
			DJIGimbalRotation mroll = new DJIGimbalRotation(true,
					false, true, 0);
			DJIGimbalRotation myaw = new DJIGimbalRotation(true,
					false, true, 0);
			DJIGimbalRotation mpitch = new DJIGimbalRotation(true,
					false, true, 0);
			DJIDrone.getDjiGimbal().updateGimbalAttitude(mpitch,
					mroll, myaw);
			sumyaw=0;
			Log.i("111", "reset");
		default:
			break;
		}

	}

}
