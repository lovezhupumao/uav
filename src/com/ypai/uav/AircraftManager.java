package com.ypai.uav;

import dji.sdk.api.DJIDrone;
import dji.sdk.api.DJIError;
import dji.sdk.api.Gimbal.DJIGimbalRotation;
import dji.sdk.api.GroundStation.DJIGroundStationTypeDef.GroundStationResult;
import dji.sdk.interfaces.DJIExecuteResultCallback;
import dji.sdk.interfaces.DJIGroundStationExecuteCallBack;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
/*
 * 该类的作用是控制无人机的姿态，如：上下、前进、后退、左右运动。
 */
public class AircraftManager implements OnTouchListener, OnClickListener {

	private static final String TAG = "AircraftManager";
	private static final int SHOWTOAST = 1;
	private boolean bGsYawLeft = false;
	private boolean bGsYawRight = false;
	private boolean bGsPitchPlus = false;
	private boolean bGsPitchMinus = false;
	private boolean bGsRollPlus = false;
	private boolean bGsRollMinus = false;
	private boolean bGsThrottlePlus = false;
	private boolean bGsThrottleMinus = false;
	private Handler handler;
	private Context context;

	AircraftManager(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.GsYawLeftButton:
			gsYawLeft_Method(event);
			break;
		case R.id.GsYawRightButton:
			gsYawRight_Method(event);
			break;
		case R.id.GsPitchPlusButton:
			gsPitchPlus_Method(event);
			break;
		case R.id.GsPitchMinusButton:
			gsPitchMinus_Method(event);
			break;
		case R.id.GsRollPlusButton:
			gsRollPlus_Method(event);
			break;
		case R.id.GsRollMinusButton:
			gsRollMinus_Method(event);
			break;
		case R.id.GsThrottlePlusButton:
			gsThrottlePlus_Method(event);
			break;
		case R.id.GsThrottleMinusButton:
			gsThrottleMinus_Method(event);
			break;
		default:
			break;
		}
		return false;
	}

	private void gsThrottleMinus_Method(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			bGsThrottleMinus = true;

			new Thread() {
				public void run() {

					while (bGsThrottleMinus) {
						Log.i(TAG, "GsThrottleMinusButton");

						DJIDrone.getDjiGroundStation().sendFlightControlData(0,
								0, 0, -1, new DJIExecuteResultCallback() {

									@Override
									public void onResult(DJIError mErr) {
										// TODO Auto-generated method stub
										Log.d(TAG,
												"gsThrottleMinus_Method errorCode = "
														+ mErr.errorCode);
										Log.d(TAG,
												"gsThrottleMinus_Method errorDescription = "
														+ mErr.errorDescription);
									}
								});
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}
			}.start();

		} else if (event.getAction() == MotionEvent.ACTION_UP
				|| event.getAction() == MotionEvent.ACTION_OUTSIDE
				|| event.getAction() == MotionEvent.ACTION_CANCEL) {

			bGsThrottleMinus = false;

		}
	}

	private void gsThrottlePlus_Method(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			bGsThrottlePlus = true;

			new Thread() {
				public void run() {

					while (bGsThrottlePlus) {
						Log.i(TAG, "GsThrottlePlusButton");

						DJIDrone.getDjiGroundStation().sendFlightControlData(0,
								0, 0, 1, new DJIExecuteResultCallback() {

									@Override
									public void onResult(DJIError mErr) {
										// TODO Auto-generated method stub
										Log.d(TAG,
												"gsThrottlePlus_Method errorCode = "
														+ mErr.errorCode);
										Log.d(TAG,
												"gsThrottlePlus_Method errorDescription = "
														+ mErr.errorDescription);
									}
								});
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}
			}.start();

		} else if (event.getAction() == MotionEvent.ACTION_UP
				|| event.getAction() == MotionEvent.ACTION_OUTSIDE
				|| event.getAction() == MotionEvent.ACTION_CANCEL) {

			bGsThrottlePlus = false;

		}
	}

	private void gsRollMinus_Method(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			bGsRollMinus = true;

			new Thread() {
				public void run() {

					while (bGsRollMinus) {
						Log.i(TAG, "GsRollMinusButton");

						DJIDrone.getDjiGroundStation().sendFlightControlData(0,
								-2, 0, 0, new DJIExecuteResultCallback() {

									@Override
									public void onResult(DJIError mErr) {
										// TODO Auto-generated method stub
										Log.d(TAG, "gsRollMinus errorCode = "
												+ mErr.errorCode);
										Log.d(TAG,
												"gsRollMinus errorDescription = "
														+ mErr.errorDescription);

									}
								});
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}
			}.start();

		} else if (event.getAction() == MotionEvent.ACTION_UP
				|| event.getAction() == MotionEvent.ACTION_OUTSIDE
				|| event.getAction() == MotionEvent.ACTION_CANCEL) {

			bGsRollMinus = false;

		}
	}

	private void gsRollPlus_Method(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			bGsRollPlus = true;

			new Thread() {
				public void run() {

					while (bGsRollPlus) {
						Log.i(TAG, "GsRollPlusButton");

						DJIDrone.getDjiGroundStation().sendFlightControlData(0,
								2, 0, 0, new DJIExecuteResultCallback() {

									@Override
									public void onResult(DJIError mErr) {
										// TODO Auto-generated method stub
										Log.d(TAG, "gsRollPlus errorCode = "
												+ mErr.errorCode);
										Log.d(TAG,
												"gsRollPlus errorDescription = "
														+ mErr.errorDescription);

									}
								});
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}
			}.start();

		} else if (event.getAction() == MotionEvent.ACTION_UP
				|| event.getAction() == MotionEvent.ACTION_OUTSIDE
				|| event.getAction() == MotionEvent.ACTION_CANCEL) {

			bGsRollPlus = false;

		}
	}

	private void gsPitchMinus_Method(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			bGsPitchMinus = true;

			new Thread() {
				public void run() {

					while (bGsPitchMinus) {
						Log.i(TAG, "GsPitchMinusButton");

						DJIDrone.getDjiGroundStation().sendFlightControlData(0,
								0, -2, 0, new DJIExecuteResultCallback() {

									@Override
									public void onResult(DJIError mErr) {
										// TODO Auto-generated method stub
										Log.d(TAG, "gsPitchMinus errorCode = "
												+ mErr.errorCode);
										Log.d(TAG,
												"gsPitchMinus errorDescription = "
														+ mErr.errorDescription);

									}
								});
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}
			}.start();

		} else if (event.getAction() == MotionEvent.ACTION_UP
				|| event.getAction() == MotionEvent.ACTION_OUTSIDE
				|| event.getAction() == MotionEvent.ACTION_CANCEL) {

			bGsPitchMinus = false;

		}
	}

	private void gsPitchPlus_Method(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			bGsPitchPlus = true;

			new Thread() {
				public void run() {

					while (bGsPitchPlus) {
						Log.i(TAG, "GsPitchPlusButton");

						DJIDrone.getDjiGroundStation().sendFlightControlData(0,0, 2, 0, new DJIExecuteResultCallback() {

									@Override
									public void onResult(DJIError mErr) {
										// TODO Auto-generated method stub
										Log.d(TAG, "gsPitchPlus errorCode = "
												+ mErr.errorCode);
										Log.d(TAG,
												"gsPitchPlus errorDescription = "
														+ mErr.errorDescription);

									}
								});
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}
			}.start();

		} else if (event.getAction() == MotionEvent.ACTION_UP
				|| event.getAction() == MotionEvent.ACTION_OUTSIDE
				|| event.getAction() == MotionEvent.ACTION_CANCEL) {

			bGsPitchPlus = false;

		}
	}

	private void gsYawRight_Method(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			bGsYawRight = true;

			new Thread() {
				public void run() {

					while (bGsYawRight) {
						Log.i(TAG, "GsYawRightButton");
						DJIDrone.getDjiGroundStation().sendFlightControlData(
								20, 0, 0, 0, new DJIExecuteResultCallback() {

									@Override
									public void onResult(DJIError mErr) {
										// TODO Auto-generated method stub
										Log.d(TAG, "gsYawRight errorCode = "
												+ mErr.errorCode);
										Log.d(TAG,
												"gsYawRight errorDescription = "
														+ mErr.errorDescription);

									}
								});
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}
			}.start();

		} else if (event.getAction() == MotionEvent.ACTION_UP
				|| event.getAction() == MotionEvent.ACTION_OUTSIDE
				|| event.getAction() == MotionEvent.ACTION_CANCEL) {

			bGsYawRight = false;

		}
	}

	private void gsYawLeft_Method(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			bGsYawLeft = true;

			new Thread() {
				public void run() {

					while (bGsYawLeft) {
						Log.i(TAG, "GsYawLeftButton");

						DJIDrone.getDjiGroundStation().sendFlightControlData(
								-20, 0, 0, 0, new DJIExecuteResultCallback() {

									@Override
									public void onResult(DJIError mErr) {
										// TODO Auto-generated method stub
										Log.d(TAG, "gsYawLeft errorCode = "
												+ mErr.errorCode);
										Log.d(TAG,
												"gsYawLeft errorDescription = "
														+ mErr.errorDescription);

									}
								});
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}
			}.start();

		} else if (event.getAction() == MotionEvent.ACTION_UP
				|| event.getAction() == MotionEvent.ACTION_OUTSIDE
				|| event.getAction() == MotionEvent.ACTION_CANCEL) {

			bGsYawLeft = false;

		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.OpenGsButton:
			openGs_Method();
			Log.i(TAG, "OpenGsButton");
			break;
		case R.id.TakeOffButton:
			takeOff_Method();
			Log.i(TAG, "TakeOffButton");
			break;
		case R.id.GohomeButton:
			gohome_Method();
			Log.i(TAG, "GohomeButton");
			break;
		case R.id.CloseGsButton:
			closeGS_Method();
			Log.i(TAG, "CloseGsButton");
			break;
		case R.id.Gs_TurnOn_Motor:
			gs_TurnOn_Motor();
			Log.i(TAG, "Gs_TurnOn_Motor");
			break;
		case R.id.Gs_TurnOff_Motor:
			gs_TurnOff_Motor();
			Log.i(TAG, "Gs_TurnOff_Motor");
			break;

		default:
			break;
		}

	}

	private void gohome_Method() {
		// TODO Auto-generated method stub
		DJIDrone.getDjiMainController().startGoHome(new DJIExecuteResultCallback() {
			
			@Override
			public void onResult(DJIError mErr) {
				// TODO Auto-generated method stub
				Log.d(TAG, "gohome_Method errorCode = "
						+ mErr.errorCode);
				Log.d(TAG, "gohome_Method errorDescription = "
						+ mErr.errorDescription);
				String result = "errorCode ="
						+ mErr.errorCode
						+ "\n"
						+ "errorDescription ="
						+ DJIError
								.getErrorDescriptionByErrcode(mErr.errorCode);
				handler.sendMessage(handler.obtainMessage(SHOWTOAST,
						result));
			}
		});
	}

	private void takeOff_Method() {
		// TODO Auto-generated method stub
		DJIDrone.getDjiMainController().startTakeoff(new DJIExecuteResultCallback() {
			
			@Override
			public void onResult(DJIError mErr) {
				// TODO Auto-generated method stub
				Log.d(TAG, "takeOff_Method errorCode = "
						+ mErr.errorCode);
				Log.d(TAG, "takeOff_Method errorDescription = "
						+ mErr.errorDescription);
				String result = "errorCode ="
						+ mErr.errorCode
						+ "\n"
						+ "errorDescription ="
						+ DJIError
								.getErrorDescriptionByErrcode(mErr.errorCode);
				handler.sendMessage(handler.obtainMessage(SHOWTOAST,
						result));
			}
		});
		
	}

	private void gs_TurnOn_Motor() {
		
		DJIDrone.getDjiMainController().turnOnMotor(
				new DJIExecuteResultCallback() {

					@Override
					public void onResult(DJIError mErr) {
						// TODO Auto-generated method stub
						Log.d(TAG, "Gs_TurnOn_Motor errorCode = "
								+ mErr.errorCode);
						Log.d(TAG, "Gs_TurnOn_Motor errorDescription = "
								+ mErr.errorDescription);
						String result = "errorCode ="
								+ mErr.errorCode
								+ "\n"
								+ "errorDescription ="
								+ DJIError
										.getErrorDescriptionByErrcode(mErr.errorCode);
						handler.sendMessage(handler.obtainMessage(SHOWTOAST,
								result));
					}
				});
	}

	private void gs_TurnOff_Motor() {
		
		DJIDrone.getDjiMainController().turnOffMotor(
				new DJIExecuteResultCallback() {

					@Override
					public void onResult(DJIError mErr) {
						// TODO Auto-generated method stub
						Log.d(TAG, "gs_TurnOff_Motor errorCode = "
								+ mErr.errorCode);
						Log.d(TAG, "gs_TurnOff_Motor errorDescription = "
								+ mErr.errorDescription);
						String result = "errorCode ="
								+ mErr.errorCode
								+ "\n"
								+ "errorDescription ="
								+ DJIError
										.getErrorDescriptionByErrcode(mErr.errorCode);
						handler.sendMessage(handler.obtainMessage(SHOWTOAST,
								result));
					}
				});
	}

	private void closeGS_Method() {
		DJIDrone.getDjiGroundStation().closeGroundStation(
				new DJIGroundStationExecuteCallBack() {

					@Override
					public void onResult(GroundStationResult result) {
						// TODO Auto-generated method stub
						String ResultsString = "return code ="
								+ result.toString();
						handler.sendMessage(handler.obtainMessage(SHOWTOAST,
								ResultsString));
					}

				});
	}

	private void openGs_Method() {
		// TODO Auto-generated method stub
		
		DJIDrone.getDjiGroundStation().openGroundStation(
				new DJIGroundStationExecuteCallBack() {

					@Override
					public void onResult(GroundStationResult result) {
						// TODO Auto-generated method stub
						String ResultsString = "return code ="
								+ result.toString();
						handler.sendMessage(handler.obtainMessage(SHOWTOAST,
								ResultsString));
					}

				});
	}

}
