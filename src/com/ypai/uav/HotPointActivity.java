package com.ypai.uav;

import java.util.Timer;
import java.util.TimerTask;

import dji.sdk.api.DJIDrone;
import dji.sdk.api.DJIError;
import dji.sdk.api.Battery.DJIBatteryProperty;
import dji.sdk.api.Camera.DJICameraDecodeTypeDef.DecoderType;
import dji.sdk.api.Camera.DJICameraSettingsTypeDef.CameraCaptureMode;
import dji.sdk.api.Gimbal.DJIGimbalAttitude;
import dji.sdk.api.Gimbal.DJIGimbalRotation;
import dji.sdk.api.GroundStation.DJIGroundStationMissionPushInfo;
import dji.sdk.api.GroundStation.DJIHotPointInitializationInfo;
import dji.sdk.api.GroundStation.DJIGroundStationTypeDef.GroundStationHotPointInterestDirection;
import dji.sdk.api.GroundStation.DJIGroundStationTypeDef.GroundStationHotPointNavigationMode;
import dji.sdk.api.GroundStation.DJIGroundStationTypeDef.GroundStationHotPointSurroundDirection;
import dji.sdk.api.GroundStation.DJIGroundStationTypeDef.GroundStationResult;
import dji.sdk.api.MainController.DJIMainControllerSystemState;
import dji.sdk.interfaces.DJIBatteryUpdateInfoCallBack;
import dji.sdk.interfaces.DJIExecuteResultCallback;
import dji.sdk.interfaces.DJIGimbalUpdateAttitudeCallBack;
import dji.sdk.interfaces.DJIGroundStationExecuteCallBack;
import dji.sdk.interfaces.DJIGroundStationMissionPushInfoCallBack;
import dji.sdk.interfaces.DJIMcuUpdateStateCallBack;
import dji.sdk.interfaces.DJIReceivedVideoDataCallBack;
import dji.sdk.widget.DjiGLSurfaceView;
import android.R.integer;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class HotPointActivity extends DemoBaseActivity implements
		OnClickListener {

	private static final String TAG = "GsProtocolFollowMeDemoActivity";

	protected static final int SHOWTOAST = 1;
	private SharedPreferences sp;
	private Button mOpenGroundStation;
	private Button mStartHotPoint;
	private Button mPauseHotPoint;
	private Button mResumeHotPoint;
	private Button mCancelHotPoint;
	private Button mCloseGroundStation;
	private Button mGoHome;
	private TextView mConnectStateTextView;
	private TextView mHotPointTextView;
	private Timer mTimer;
	private Timer mtakephotoTimer= null;
	private double latitude;
	private double longitude;
	private View view_para;
	private AlertDialog.Builder mBuilder;
	private boolean getHomePiontFlag = false;
	private double angle=0;
	private int		mpicturenum=0;
	private DjiGLSurfaceView mDjiGLSurfaceView;
	private DJIReceivedVideoDataCallBack mReceivedVideoDataCallBack = null;

	private String HpInfoString = "";

	DJIGroundStationMissionPushInfoCallBack mGroundStationMissionPushInfoCallBack;

	private DJIBatteryProperty battary=null;

	class Task extends TimerTask {
		// int times = 1;

		@Override
		public void run() {
			// Log.d(TAG ,"==========>Task Run In!");
			checkConnectState();
		}

	};
	class TakePhotoTask extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(mpicturenum<36)
			{
				mpicturenum=mpicturenum+1;
				DJIDrone.getDjiCamera().startTakePhoto(CameraCaptureMode.Camera_Single_Capture, new DJIExecuteResultCallback() {
					
					@Override
					public void onResult(DJIError mErr) {
						// TODO Auto-generated method stub
						String result = "errorCode ="+ mErr.errorCode+ "\n"	+ "errorDescription ="+ DJIError
										.getErrorDescriptionByErrcode(mErr.errorCode);
						handler.sendMessage(handler	.obtainMessage(	SHOWTOAST,	result));
					}
				});
			}
			else{
				stopTakePhotoMission();
			}
			
		}
		
	}
	private void checkConnectState() {

		HotPointActivity.this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (DJIDrone.getDjiCamera() != null) {
					boolean bConnectState = DJIDrone.getDjiCamera()
							.getCameraConnectIsOk();
					if (bConnectState) {
						mConnectStateTextView
								.setText("Connected"+"("+Integer.toString(battary.remainPowerPercent) +"%)");
					} else {
						mConnectStateTextView
								.setText(R.string.camera_connection_break);
					}
				}
			}
		});

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hotpoint);
		DJIDrone.getDjiCamera().setDecodeType(DecoderType.Software);
		mDjiGLSurfaceView = (DjiGLSurfaceView) findViewById(R.id.DjiSurfaceView_gs);
		sp=getSharedPreferences("hotpointpara", 0);
		mOpenGroundStation = (Button) findViewById(R.id.OpenGsButton);
		mStartHotPoint = (Button) findViewById(R.id.StartHotPointBtn);
		mPauseHotPoint = (Button) findViewById(R.id.PauseHotPoint);
		mResumeHotPoint = (Button) findViewById(R.id.ResumeHotPoint);
		mCancelHotPoint = (Button) findViewById(R.id.CancelHotPoint);
		mCloseGroundStation = (Button) findViewById(R.id.CloseGroundStation);
		mGoHome=(Button)findViewById(R.id.HotPointGohome);
		mHotPointTextView = (TextView) findViewById(R.id.HotPointInfoTV);

		mConnectStateTextView = (TextView) findViewById(R.id.ConnectStateGsTextView);

		mOpenGroundStation.setOnClickListener(this);
		mStartHotPoint.setOnClickListener(this);
		mPauseHotPoint.setOnClickListener(this);
		mResumeHotPoint.setOnClickListener(this);
		mCancelHotPoint.setOnClickListener(this);
		mCloseGroundStation.setOnClickListener(this);
		mGoHome.setOnClickListener(this);
		mDjiGLSurfaceView.start();

		mReceivedVideoDataCallBack = new DJIReceivedVideoDataCallBack() {

			@Override
			public void onResult(byte[] videoBuffer, int size) {
				// TODO Auto-generated method stub
				mDjiGLSurfaceView.setDataToDecoder(videoBuffer, size);
			}

		};

		mBuilder = new AlertDialog.Builder(this);
		DJIDrone.getDjiCamera().setReceivedVideoDataCallBack(
				mReceivedVideoDataCallBack);

		DJIDrone.getDjiMC().setMcuUpdateStateCallBack(
				new DJIMcuUpdateStateCallBack() {

					@Override
					public void onResult(DJIMainControllerSystemState state) {
						// TODO Auto-generated method stub
						latitude = state.droneLocationLatitude;
						longitude = state.droneLocationLongitude;

						if (latitude != -1 && longitude != -1 && latitude != 0
								&& longitude != 0) {
							getHomePiontFlag = true;
						} else {
							getHomePiontFlag = false;
						}

					}

				});

		mGroundStationMissionPushInfoCallBack = new DJIGroundStationMissionPushInfoCallBack() {

			@Override
			public void onResult(DJIGroundStationMissionPushInfo info) {
				// TODO Auto-generated method stub
				StringBuffer sb = new StringBuffer();
				switch (info.missionType) {
				case Navi_Mode_Hotpoint: {
					sb.append("Mission Type : " + info.missionType.toString())
							.append("\n");
					sb.append("Mission Status : " + info.hotPointMissionStatus)
							.append("\n");
					sb.append(
							"Hot Point Radius : " + info.hotPointRadius / 100d)
							.append("\n");
					sb.append("Hot Point Reason : " + info.hotPointReason)
							.append("\n");
					break;
				}

				case Navi_Mode_Attitude: {
					sb.append("Mission Type : " + info.missionType.toString())
							.append("\n");
					sb.append("Mission Reserve : " + info.reserved)
							.append("\n");
					break;
				}

				default:
					sb.append("Wrong Selection").append("\n");
				}

				HpInfoString = sb.toString();

				HotPointActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						mHotPointTextView.setText(HpInfoString);
					}

				});
			}

		};

		DJIDrone.getDjiGroundStation().setGroundStationMissionPushInfoCallBack(
				mGroundStationMissionPushInfoCallBack);
		DJIDrone.getDjiBattery().setBatteryUpdateInfoCallBack(new DJIBatteryUpdateInfoCallBack() {
			
			@Override
			public void onResult(DJIBatteryProperty arg0) {
				// TODO Auto-generated method stub
				battary=arg0;
			}
		});
		DJIDrone.getDjiGimbal().setGimbalUpdateAttitudeCallBack(new DJIGimbalUpdateAttitudeCallBack() {
			
			@Override
			public void onResult(DJIGimbalAttitude arg0) {
				// TODO Auto-generated method stub
				angle=arg0.pitch;
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		mDjiGLSurfaceView.resume();

		mTimer = new Timer();
		Task task = new Task();
		mTimer.schedule(task, 0, 500);

		DJIDrone.getDjiMC().startUpdateTimer(1000);
		DJIDrone.getDjiBattery().startUpdateTimer(2000);
		 DJIDrone.getDjiGimbal().startUpdateTimer(1000);
		 DJIDrone.getDjiCamera().startUpdateTimer(1000);
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mDjiGLSurfaceView.pause();

		if (mTimer != null) {
			mTimer.cancel();
			mTimer.purge();
			mTimer = null;
		}
		DJIDrone.getDjiMC().stopUpdateTimer();
		DJIDrone.getDjiBattery().stopUpdateTimer();
		DJIDrone.getDjiGimbal().stopUpdateTimer();
		DJIDrone.getDjiCamera().stopUpdateTimer();
		super.onPause();
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (DJIDrone.getDjiCamera() != null) {
			DJIDrone.getDjiCamera().setReceivedVideoDataCallBack(null);
		}
		mDjiGLSurfaceView.destroy();
		super.onDestroy();
	}

	private Handler handler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case SHOWTOAST:
				setResultToToast((String) msg.obj);
				break;

			default:
				break;
			}
			return false;
		}
	});

	public void onReturn(View view) {
		Log.d(TAG, "onReturn");
		this.finish();
	}

	private void setResultToToast(String result) {
		Toast.makeText(HotPointActivity.this, result, Toast.LENGTH_SHORT)
				.show();
	}

	private boolean checkGetHomePoint() {
		if (!getHomePiontFlag) {
			setResultToToast(getString(R.string.gs_not_get_home_point));
		}
		return getHomePiontFlag;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.OpenGsButton: {

			if (!checkGetHomePoint())
				return;

			DJIDrone.getDjiGroundStation().openGroundStation(
					new DJIGroundStationExecuteCallBack() {

						@Override
						public void onResult(GroundStationResult result) {
							// TODO Auto-generated method stub
							String ResultsString = "return code ="
									+ result.toString();
							handler.sendMessage(handler.obtainMessage(
									SHOWTOAST, ResultsString));
						}

					});

			break;
		}

		case R.id.StartHotPointBtn: {
			if(!checkGetHomePoint())return;
			// Get the layout inflater
			initHotPointPara();
			DJIGimbalRotation mroll = new DJIGimbalRotation(true,
					false, true, 0);
			DJIGimbalRotation myaw = new DJIGimbalRotation(true,
					false, true, 0);
			DJIGimbalRotation mpitch = new DJIGimbalRotation(true,
					false, true, 0);
			DJIDrone.getDjiGimbal().updateGimbalAttitude(mpitch,
					mroll, myaw);
			mBuilder.setTitle("兴趣点参数设置")
					.setView(view_para)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									// TODO Auto-generated method stub
									
											// TODO Auto-generated method stub
									initHotPointAngle();		
									DJIHotPointInitializationInfo info = new DJIHotPointInitializationInfo();
									EditText maltitude = (EditText) view_para.findViewById(R.id.hotpoint_altitude);
									EditText mradius = (EditText) view_para.findViewById(R.id.hotpoint_radius);
									EditText mvelocity = (EditText) view_para.findViewById(R.id.hotpoint_velocity);
									Spinner msurroundDirection = (Spinner) view_para.findViewById(R.id.hotpoint_surroundDirection);
									Spinner minterestDirection = (Spinner) view_para.findViewById(R.id.hotpoint_interestDirection);
									Spinner mnavigationMode = (Spinner) view_para.findViewById(R.id.hotpoint_navigationMode);
									SharedPreferences.Editor editor =sp.edit();
									editor.putFloat("altitude",Float.parseFloat(maltitude.getText().toString()) );
									editor.putFloat("radius",Float.parseFloat(mradius.getText().toString()) );
									editor.putInt("velocity", Integer.parseInt(mvelocity.getText().toString()));
									editor.putInt("surroundDirection",msurroundDirection.getSelectedItemPosition());
									editor.putInt("interestDirection",minterestDirection.getSelectedItemPosition());
									editor.putInt("navigationMode",mnavigationMode.getSelectedItemPosition());
									editor.commit();
									switch (msurroundDirection.getSelectedItemPosition()) {
									case 0:
										info.surroundDirection = GroundStationHotPointSurroundDirection.Anit_Clockwise;
										break;
									case 1:
										info.surroundDirection = GroundStationHotPointSurroundDirection.Clockwise;
										break;
									default:
										break;
									}
									switch (minterestDirection
											.getSelectedItemPosition()) {
									case 0:
										info.interestDirection = GroundStationHotPointInterestDirection.East;
										break;
									case 1:
										info.interestDirection = GroundStationHotPointInterestDirection.South;
										break;
									case 2:
										info.interestDirection = GroundStationHotPointInterestDirection.West;
										break;
									case 3:
										info.interestDirection = GroundStationHotPointInterestDirection.North;
										break;
									case 4 :
										info.interestDirection = GroundStationHotPointInterestDirection.Nearest;
										break;
									default:
										break;
									}
									switch (mnavigationMode
											.getSelectedItemPosition()) {
									case 0:
										info.navigationMode = GroundStationHotPointNavigationMode.SurroundHeadingAlongTheCircleLookingForward;
										break;
									case 1:
										info.navigationMode = GroundStationHotPointNavigationMode.SurroundHeadingTowardHotPoint;
										break;
									case 2:
										info.navigationMode = GroundStationHotPointNavigationMode.SurroundHeadingBackwardHotPoint;
										break;
									case 3:
										info.navigationMode = GroundStationHotPointNavigationMode.SurroundHeadingControlByRemoteController;
										break;
									case 4 :
										info.navigationMode = GroundStationHotPointNavigationMode.SurroundHeadingAlongTheCircleLookingBackward;
										break;
									default:
										break;
									}
									info.latitude = latitude;
									info.longitude = longitude;
									info.altitude = Double.parseDouble(maltitude.getText().toString());
									info.radius = Double.parseDouble(mradius.getText().toString());
									info.velocity = Integer.parseInt(mvelocity.getText().toString());								
									DJIDrone.getDjiGroundStation()
											.startHotPoint(info,new DJIGroundStationExecuteCallBack() {

														@Override
														public void onResult(
																GroundStationResult result) {
															// TODO
															// Auto-generated
															// method stub
															String ResultsString = "return code ="+ result.toString();
															handler.sendMessage(handler.obtainMessage(SHOWTOAST,ResultsString));
														}

													});
									startTakePhotoMission();
									
									dialog.dismiss();
								}

								

								private void initHotPointAngle() {
									new Thread() {
										public void run() {
											DJIGimbalRotation mPitch = new DJIGimbalRotation(true,
													false, false, 50);

											DJIGimbalRotation mPitch_stop = new DJIGimbalRotation(
													false, false, false, 0);

											while (angle>-45) {
												

												DJIDrone.getDjiGimbal().updateGimbalAttitude(
														mPitch, null, null);
												

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
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							}).show();

			break;

		}

		case R.id.PauseHotPoint: {
			if(!checkGetHomePoint())return;
			DJIDrone.getDjiGroundStation().pauseHotPoint(
					new DJIGroundStationExecuteCallBack() {

						@Override
						public void onResult(GroundStationResult result) {
							// TODO Auto-generated method stub
							String ResultsString = "return code ="
									+ result.toString();
							handler.sendMessage(handler.obtainMessage(
									SHOWTOAST, ResultsString));
						}

					});
			stopTakePhotoMission();
			break;
		}

		case R.id.ResumeHotPoint: {
			if(!checkGetHomePoint())return;
			DJIDrone.getDjiGroundStation().resumeHotPoint(
					new DJIGroundStationExecuteCallBack() {

						@Override
						public void onResult(GroundStationResult result) {
							// TODO Auto-generated method stub
							String ResultsString = "return code ="
									+ result.toString();
							handler.sendMessage(handler.obtainMessage(
									SHOWTOAST, ResultsString));
						}

					});
			startTakePhotoMission();
			break;
		}

		case R.id.CancelHotPoint: {
			if(!checkGetHomePoint())return;
			DJIDrone.getDjiGroundStation().cancelHotPoint(
					new DJIGroundStationExecuteCallBack() {

						@Override
						public void onResult(GroundStationResult result) {
							// TODO Auto-generated method stub
							String ResultsString = "return code ="
									+ result.toString();
							handler.sendMessage(handler.obtainMessage(
									SHOWTOAST, ResultsString));
						}

					});
			stopTakePhotoMission();
			break;
		}

		case R.id.CloseGroundStation: {
			if(!checkGetHomePoint())return;
			DJIDrone.getDjiGroundStation().closeGroundStation(
					new DJIGroundStationExecuteCallBack() {

						@Override
						public void onResult(GroundStationResult result) {
							// TODO Auto-generated method stub
							String ResultsString = "return code ="
									+ result.toString();
							handler.sendMessage(handler.obtainMessage(
									SHOWTOAST, ResultsString));
						}

					});
			stopTakePhotoMission();
			break;
		}
		case R.id.HotPointGohome:
			DJIDrone.getDjiMC().startGoHome(new DJIExecuteResultCallback() {
				
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
			break;

		default: {
			break;
		}
		}
	}

	private void stopTakePhotoMission() {
		if (mtakephotoTimer != null) {
			mtakephotoTimer.cancel();
			mtakephotoTimer.purge();
			mtakephotoTimer = null;
		}
	}

	private void initHotPointPara() {
		LayoutInflater inflater = this.getLayoutInflater();
		view_para = inflater.inflate(R.layout.dialog_hotpoint_para, null);
		EditText maltitude = (EditText) view_para.findViewById(R.id.hotpoint_altitude);
		EditText mradius = (EditText) view_para.findViewById(R.id.hotpoint_radius);
		EditText mvelocity = (EditText) view_para.findViewById(R.id.hotpoint_velocity);
		Spinner msurroundDirection = (Spinner) view_para.findViewById(R.id.hotpoint_surroundDirection);
		Spinner minterestDirection = (Spinner) view_para.findViewById(R.id.hotpoint_interestDirection);
		Spinner mnavigationMode = (Spinner) view_para.findViewById(R.id.hotpoint_navigationMode);
		maltitude.setText(sp.getFloat("altitude", 30)+"");
		mradius.setText(sp.getFloat("radius", 10)+"");
		mvelocity.setText(sp.getInt("velocity", 5)+"");
		msurroundDirection.setSelection(sp.getInt("surroundDirection", 0));
		minterestDirection.setSelection(sp.getInt("interestDirection", 0));
		mnavigationMode.setSelection(sp.getInt("navigationMode", 0));
	}
	private void startTakePhotoMission() {
		mtakephotoTimer=new Timer();
		TakePhotoTask mTakePhotoTask=new TakePhotoTask();
		mtakephotoTimer.schedule(mTakePhotoTask, 5000,3000);
	}
}
