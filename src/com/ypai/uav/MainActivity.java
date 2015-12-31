package com.ypai.uav;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.log.uav.systemlog;
import com.service.uav.uav;

import dji.sdk.api.DJIDrone;
import dji.sdk.api.DJIError;
import dji.sdk.api.Battery.DJIBatteryProperty;
import dji.sdk.api.Camera.DJICameraFileNamePushInfo;
import dji.sdk.api.Camera.DJICameraPlaybackState;
import dji.sdk.api.Camera.DJICameraSDCardInfo;
import dji.sdk.api.Camera.DJICameraSystemState;
import dji.sdk.api.Camera.DJICameraDecodeTypeDef.DecoderType;
import dji.sdk.api.Camera.DJICameraSettingsTypeDef.CameraCaptureMode;
import dji.sdk.api.Camera.DJICameraSettingsTypeDef.CameraMode;
import dji.sdk.api.DJIDroneTypeDef.DJIDroneType;
import dji.sdk.api.Gimbal.DJIGimbalAttitude;
import dji.sdk.api.GroundStation.DJIGroundStationExecutionPushInfo;
import dji.sdk.api.GroundStation.DJIGroundStationFlyingInfo;
import dji.sdk.api.GroundStation.DJIGroundStationMissionPushInfo;
import dji.sdk.api.GroundStation.DJIGroundStationTask;
import dji.sdk.api.GroundStation.DJIGroundStationTypeDef.DJIGroundStationFinishAction;
import dji.sdk.api.GroundStation.DJIGroundStationTypeDef.DJIGroundStationMovingMode;
import dji.sdk.api.GroundStation.DJIGroundStationTypeDef.DJIGroundStationPathMode;
import dji.sdk.api.GroundStation.DJIGroundStationTypeDef.DJINavigationFlightControlHorizontalControlMode;
import dji.sdk.api.GroundStation.DJIGroundStationTypeDef.DJINavigationFlightControlVerticalControlMode;
import dji.sdk.api.GroundStation.DJIGroundStationTypeDef.DJINavigationFlightControlYawControlMode;
import dji.sdk.api.GroundStation.DJIGroundStationTypeDef.GroundStationOnWayPointAction;
import dji.sdk.api.GroundStation.DJIGroundStationTypeDef.GroundStationResult;
import dji.sdk.api.GroundStation.DJIGroundStationTypeDef.GroundStationWayPointExecutionState;
import dji.sdk.api.GroundStation.DJIGroundStationTypeDef.DJINavigationFlightControlCoordinateSystem;
import dji.sdk.api.GroundStation.DJIGroundStationWaypoint;
import dji.sdk.api.MainController.DJIDeformInfo;
import dji.sdk.api.MainController.DJIMainControllerSystemState;
import dji.sdk.api.MainController.DJIMainControllerTypeDef.DJIMcErrorType;
import dji.sdk.interfaces.DJIBatteryUpdateInfoCallBack;
import dji.sdk.interfaces.DJICameraFileNameInfoCallBack;
import dji.sdk.interfaces.DJICameraPlayBackStateCallBack;
import dji.sdk.interfaces.DJICameraSdCardInfoCallBack;
import dji.sdk.interfaces.DJICameraSystemStateCallBack;
import dji.sdk.interfaces.DJIDroneTypeChangedCallback;
import dji.sdk.interfaces.DJIExecuteResultCallback;
import dji.sdk.interfaces.DJIGeneralListener;
import dji.sdk.interfaces.DJIGimbalErrorCallBack;
import dji.sdk.interfaces.DJIGimbalUpdateAttitudeCallBack;
import dji.sdk.interfaces.DJIGroundStationExecuteCallBack;
import dji.sdk.interfaces.DJIGroundStationExecutionPushInfoCallBack;
import dji.sdk.interfaces.DJIGroundStationFlyingInfoCallBack;
import dji.sdk.interfaces.DJIGroundStationMissionPushInfoCallBack;
import dji.sdk.interfaces.DJIMainControllerExternalDeviceRecvDataCallBack;
import dji.sdk.interfaces.DJIMcDeformInfoCallBack;
import dji.sdk.interfaces.DJIMcuErrorCallBack;
import dji.sdk.interfaces.DJIMcuUpdateStateCallBack;
import dji.sdk.interfaces.DJIReceivedVideoDataCallBack;
import dji.sdk.widget.DjiGLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.R.string;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends DemoBaseActivity {
	private static final String TAG = "MainActivity";

	private static final int SHOWTOAST = 1;
	private static final int NAVI_MODE_ATTITUDE = 0;
	private static final int NAVI_MODE_WAYPOINT = 1;
	private static final int EXECUTION_STATUS_UPLOAD_FINISH = 0;
	private static final int EXECUTION_STATUS_FINISH = 1;
	private static final int EXECUTION_STATUS_REACH_POINT = 2;
	private final int SHOWDIALOG = 2;
	private Button mstartphotobtn;
	private Button mstopphotobth;
	private Button mstartvediobtn;
	private Button mstopvediobtn;
	private Button msetphotosizebtn;

	private Button mpitchupbtn;
	private Button mpitchdownbtn;
	private Button mrollupbtn;
	private Button mrolldownbtn;
	private Button myawupbtn;
	private Button myawdownbtn;
	private Button mgambilresetbtn;
	private Timer mTimer;
	private Timer mCheckYawTimer;
	private TextView mConnectStateTextView;
	private TextView mMainControllerStateTextView;
	private TextView  mBattaryTextView;
	private TextView mGimbalTextView;
	private DjiGLSurfaceView mDjiGLSurfaceView;
	private String McStateString = "";

	private Button mOpenGsButton;
	private Button mTakeOffButton;
	private Button mGohomeButton;
	private Button mCloseGsButton;
	private Button mTurnOnMotor;
	private Button mTurnOffMotor;
	private Button mfunnybtn;

	private Button mYawLeftBtn;
	private Button mYawRightBtn;
	private Button mPitchPlusBtn;
	private Button mPitchMinusBtn;
	private Button mRollPlusBtn;
	private Button mRollMinusBtn;
	private Button mThottlePlusBtn;
	private Button mThottleMinusBtn;
	private double homeLocationLatitude = -1;
	private double homeLocationLongitude = -1;
	private boolean getHomePointFlag = false;
	private DJIGroundStationTask mTask;
	private DJICameraFileNameInfoCallBack mCameraFileNameInfoCallBack = null;
	private DJICameraPlayBackStateCallBack mCameraPlayBackStateCallBack = null;
	private DJIGroundStationMissionPushInfoCallBack mGroundStationMissionPushInfoCallBack = null;
	private DJIGroundStationExecutionPushInfoCallBack mGroundStationExecutionPushInfoCallBack = null;
	private DJIGroundStationFlyingInfoCallBack mGroundStationFlyingInfoCallBack = null;
	private DJIMcuUpdateStateCallBack mMcuUpdateStateCallBack = null;
	private DJIBatteryUpdateInfoCallBack mBattryUpdateInfoCallBack = null;
    private DJIMcuErrorCallBack mMcuErrorCallBack = null;
    private DJIMainControllerExternalDeviceRecvDataCallBack mExtDevReceiveDataCallBack = null;
    private DJIGimbalErrorCallBack mGimbalErrorCallBack;
    private DJIGimbalUpdateAttitudeCallBack mGimbalUpdateAttitudeCallBack;
	
	
	protected String McErrorString="";
    protected String GimbalString="";
	protected String BatteryInfoString="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		systemlog.sys_Log_Output();
		Log.i(TAG,"onCreate");		
		//init_app();
		DJIDrone.getDjiCamera().setDecodeType(DecoderType.Software);
		findview();
		gimbalbtn();

		initAircraft();
		initCameraManager();
		
		mDjiGLSurfaceView.start();
		init_callback();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Log.d("TAG", "onResume");
		mDjiGLSurfaceView.resume();
		mTimer = new Timer();
		Task task = new Task();
		mTimer.schedule(task, 0, 500);

		DJIDrone.getDjiGimbal().startUpdateTimer(1000);
		DJIDrone.getDjiCamera().startUpdateTimer(1000);
		DJIDrone.getDjiMainController().startUpdateTimer(1000);
		DJIDrone.getDjiGroundStation().startUpdateTimer(1000);
		DJIDrone.getDjiBattery().startUpdateTimer(2000);
		//DJIDrone.getDjiMC().startUpdateTimer(1000);
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

		DJIDrone.getDjiGimbal().stopUpdateTimer();
		DJIDrone.getDjiCamera().stopUpdateTimer();
		DJIDrone.getDjiMainController().stopUpdateTimer();
		DJIDrone.getDjiGroundStation().stopUpdateTimer();
		DJIDrone.getDjiBattery().stopUpdateTimer();
		//DJIDrone.getDjiMC().stopUpdateTimer();
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		Log.d("TAG", "onStop");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.i(TAG, "onDestroy");
		if (DJIDrone.getDjiCamera() != null)
			DJIDrone.getDjiCamera().setReceivedVideoDataCallBack(null);

		mDjiGLSurfaceView.destroy();

		//onUnInitSDK();
		super.onDestroy();
		
	}

	private void init_app() {
		onInitSDK();

		new Thread() {
			public void run() {
				try {
					DJIDrone.checkPermission(getApplicationContext(),
							new DJIGeneralListener() {

								@Override
								public void onGetPermissionResult(int result) {
									// TODO Auto-generated method stub
									Log.e(TAG, "onGetPermissionResult = "
											+ result);
									Log.e(TAG,
											"onGetPermissionResultDescription = "
													+ DJIError
															.getCheckPermissionErrorDescription(result));
									if (result == 0) {
										handler.sendMessage(handler
												.obtainMessage(
														SHOWDIALOG,
														DJIError.getCheckPermissionErrorDescription(result)));
									} else {
										handler.sendMessage(handler
												.obtainMessage(
														SHOWDIALOG,
														getString(R.string.demo_activation_error)
																+ DJIError
																		.getCheckPermissionErrorDescription(result)
																+ "\n"
																+ getString(R.string.demo_activation_error_code)
																+ result));

									}
								}
							});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}

	private void onInitSDK() {
		DJIDrone.initWithType(this.getApplicationContext(),
				DJIDroneType.DJIDrone_M100);
		DJIDrone.connectToDrone();
	}

	private void onUnInitSDK() {
		DJIDrone.disconnectToDrone();
	}

	public void onReturn(View view) {
		Log.d("TAG", "onReturn");
		this.finish();
	}

	private void findview() {
		mpitchupbtn = (Button) findViewById(R.id.pitchupbtn);
		mpitchdownbtn = (Button) findViewById(R.id.pitchdownbtn);
		mrollupbtn = (Button) findViewById(R.id.rollupbtn);
		mrolldownbtn = (Button) findViewById(R.id.rolldownbtn);
		myawupbtn = (Button) findViewById(R.id.yawupbtn);
		myawdownbtn = (Button) findViewById(R.id.yawdownbtn);
		mgambilresetbtn = (Button) findViewById(R.id.gimbalreset);
		mConnectStateTextView = (TextView) findViewById(R.id.ConnectStateGimbalTextView);
		mDjiGLSurfaceView = (DjiGLSurfaceView) findViewById(R.id.DjiSurfaceView_gimbal);
		mMainControllerStateTextView = (TextView) findViewById(R.id.MainControllerStateTV);
		mGimbalTextView=(TextView)findViewById(R.id.gimbalstatutv);
		mBattaryTextView=(TextView)findViewById(R.id.BatteryInfoTV);

		mstartphotobtn = (Button) findViewById(R.id.startphotobtn);
		mstopphotobth = (Button) findViewById(R.id.stopphotobtn);
		mstartvediobtn = (Button) findViewById(R.id.startvediobtn);
		mstopvediobtn = (Button) findViewById(R.id.stopvediobtn);
		msetphotosizebtn = (Button) findViewById(R.id.setphotosizebtn);

		mOpenGsButton = (Button) findViewById(R.id.OpenGsButton);
		mTakeOffButton = (Button) findViewById(R.id.TakeOffButton);
		mGohomeButton = (Button) findViewById(R.id.GohomeButton);
		mCloseGsButton = (Button) findViewById(R.id.CloseGsButton);
		mTurnOnMotor = (Button) findViewById(R.id.Gs_TurnOn_Motor);
		mTurnOffMotor = (Button) findViewById(R.id.Gs_TurnOff_Motor);
		mfunnybtn = (Button) findViewById(R.id.FunnyBtn);

		mYawLeftBtn = (Button) findViewById(R.id.GsYawLeftButton);
		mYawRightBtn = (Button) findViewById(R.id.GsYawRightButton);
		mPitchPlusBtn = (Button) findViewById(R.id.GsPitchPlusButton);
		mPitchMinusBtn = (Button) findViewById(R.id.GsPitchMinusButton);
		mRollPlusBtn = (Button) findViewById(R.id.GsRollPlusButton);
		mRollMinusBtn = (Button) findViewById(R.id.GsRollMinusButton);
		mThottlePlusBtn = (Button) findViewById(R.id.GsThrottlePlusButton);
		mThottleMinusBtn = (Button) findViewById(R.id.GsThrottleMinusButton);

	}

	private void gimbalbtn() {
		GimbalManager mgimbal_ctrl = new GimbalManager();
		mpitchupbtn.setOnTouchListener(mgimbal_ctrl);
		mpitchdownbtn.setOnTouchListener(mgimbal_ctrl);
		mrollupbtn.setOnTouchListener(mgimbal_ctrl);
		mrolldownbtn.setOnTouchListener(mgimbal_ctrl);
		myawupbtn.setOnTouchListener(mgimbal_ctrl);
		myawdownbtn.setOnTouchListener(mgimbal_ctrl);
		mgambilresetbtn.setOnClickListener(mgimbal_ctrl);
	}

	private void initCameraManager() {
		Log.i("camerabtn", "11");
		CameraManager managercamera = new CameraManager(this, handler);
		mstartphotobtn.setOnClickListener(managercamera);
		mstopphotobth.setOnClickListener(managercamera);
		mstartvediobtn.setOnClickListener(managercamera);
		mstopvediobtn.setOnClickListener(managercamera);
		msetphotosizebtn.setOnClickListener(managercamera);

	}

	private void initAircraft() {
		DJIDrone.getDjiGroundStation()
				.setHorizontalControlCoordinateSystem(
						DJINavigationFlightControlCoordinateSystem.Navigation_Flight_Control_Coordinate_System_Body);
		DJIDrone.getDjiGroundStation()
				.setHorizontalControlMode(
						DJINavigationFlightControlHorizontalControlMode.Navigation_Flight_Control_Horizontal_Control_Velocity);
		DJIDrone.getDjiGroundStation()
				.setVerticalControlMode(
						DJINavigationFlightControlVerticalControlMode.Navigation_Flight_Control_Vertical_Control_Velocity);
		DJIDrone.getDjiGroundStation()
				.setYawControlMode(
						DJINavigationFlightControlYawControlMode.Navigation_Flight_Control_Yaw_Control_Palstance);

		AircraftManager aircraftManager = new AircraftManager(this, handler);

		mTakeOffButton.setOnClickListener(aircraftManager);
		mGohomeButton.setOnClickListener(aircraftManager);
		mTurnOnMotor.setOnClickListener(aircraftManager);
		mTurnOffMotor.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//init_app();
				//init_callback();
				Log.i(TAG, "Init_CallBack");
			}
		});

		mPitchPlusBtn.setOnTouchListener(aircraftManager);
		mPitchMinusBtn.setOnTouchListener(aircraftManager);
		mRollPlusBtn.setOnTouchListener(aircraftManager);
		mRollMinusBtn.setOnTouchListener(aircraftManager);
		mThottlePlusBtn.setOnTouchListener(aircraftManager);
		mThottleMinusBtn.setOnTouchListener(aircraftManager);
		mYawLeftBtn.setOnTouchListener(aircraftManager);
		mYawRightBtn.setOnTouchListener(aircraftManager);
		Groundstation groundstation = new Groundstation();
		mfunnybtn.setOnClickListener(groundstation);
		mOpenGsButton.setOnClickListener(groundstation);
		mCloseGsButton.setOnClickListener(groundstation);
	}

	private void init_callback() {

		init_Gimbal_CallBack();
		init_Camera_CallBack();
		init_GStation_CallBack();
		init_MainControl_CallBack();
		init_Battary_CallBack();
		Log.d(TAG, "over");
	}

	private void init_Battary_CallBack() {
		Log.i(TAG, "init_Battary_CallBack");
		// TODO Auto-generated method stub
		mBattryUpdateInfoCallBack=new DJIBatteryUpdateInfoCallBack() {
			
			@Override
			public void onResult(DJIBatteryProperty state) {
				// TODO Auto-generated method stub
				    StringBuffer sb = new StringBuffer();
	                sb.append(getString(R.string.battery_info)).append("\n");
	                sb.append("designedVolume=").append(state.designedVolume).append("\n");
	                sb.append("fullChargeVolume=").append(state.fullChargeVolume).append("\n");        
	                sb.append("currentElectricity=").append(state.currentElectricity).append("\n");
	                sb.append("currentVoltage=").append(state.currentVoltage).append("\n");        
	                sb.append("currentCurrent=").append(state.currentCurrent).append("\n");
	                sb.append("remainLifePercent=").append(state.remainLifePercent).append("\n");
	                sb.append("remainPowerPercent=").append(state.remainPowerPercent).append("\n");
	                sb.append("batteryTemperature=").append(state.batteryTemperature).append("\n");
	                sb.append("dischargeCount=").append(state.dischargeCount).append("\n");
	                
	                BatteryInfoString = sb.toString();
	                MainActivity.this.runOnUiThread(new Runnable(){

	                    @Override
	                    public void run() 
	                    {   
	                        mBattaryTextView.setText(BatteryInfoString);
	                       
	                    }
	                });
			}
		};
		DJIDrone.getDjiBattery().setBatteryUpdateInfoCallBack(mBattryUpdateInfoCallBack);
		
	}

	private void init_MainControl_CallBack() {
		Log.i(TAG, "init_MainControl_CallBack");
		mMcuUpdateStateCallBack = new DJIMcuUpdateStateCallBack() {

			@Override
			public void onResult(DJIMainControllerSystemState state) {
				// TODO Auto-generated method stub
				homeLocationLatitude = state.droneLocationLatitude;
				homeLocationLongitude = state.droneLocationLongitude;

				if (homeLocationLatitude != -1 && homeLocationLongitude != -1
						&& homeLocationLatitude != 0
						&& homeLocationLongitude != 0) {
					getHomePointFlag = true;
				} else {
					getHomePointFlag = false;
				}
				StringBuffer sb = new StringBuffer();   
                sb.append(getString(R.string.main_controller_state)).append("\n");
                sb.append("MCU Version=").append(DJIDrone.getDjiMainController().getMcuVersion()).append("\n");
                sb.append("satelliteCount=").append(state.satelliteCount).append("\n");
                sb.append("homeLocationLatitude=").append(state.homeLocationLatitude).append("\n");
                sb.append("homeLocationLongitude=").append(state.homeLocationLongitude).append("\n");
                sb.append("droneLocationLatitude=").append(state.droneLocationLatitude).append("\n");
                sb.append("droneLocationLongitude=").append(state.droneLocationLongitude).append("\n");
                sb.append("velocityX=").append(state.velocityX).append("\n");
                sb.append("velocityY=").append(state.velocityY).append("\n");
                sb.append("velocityZ=").append(state.velocityZ).append("\n");
                sb.append("speed=").append(state.speed).append("\n");      
                sb.append("altitude=").append(state.altitude).append("\n");
                sb.append("pitch=").append(state.pitch).append("\n");
                sb.append("roll=").append(state.roll).append("\n");
                sb.append("yaw=").append(state.yaw).append("\n");
                sb.append("remainPower=").append(state.remainPower).append("\n");
                sb.append("remainFlyTime=").append(state.remainFlyTime).append("\n");
                sb.append("powerLevel=").append(state.powerLevel).append("\n");
                sb.append("isFlying=").append(state.isFlying).append("\n");
                sb.append("noFlyStatus=").append(state.noFlyStatus).append("\n");
                sb.append("noFlyZoneCenterLatitude=").append(state.noFlyZoneCenterLatitude).append("\n");
                sb.append("noFlyZoneCenterLongitude=").append(state.noFlyZoneCenterLongitude).append("\n");
                sb.append("noFlyZoneRadius=").append(state.noFlyZoneRadius);
                
                if (DJIDrone.getDroneType() != DJIDroneType.DJIDrone_Vision) {
                    sb.append("\n").append("remoteControllerState=").append(state.remoteControllerState).append("\n");
                    sb.append("flightControlState=").append(state.fightControlState.name()).append("\n");
                    sb.append("IOCType=").append(state.IOCType).append("\n");
                    sb.append("lastFlightControlCommend=").append(state.lastFilghtControlCommend.toString()).append("\n");
                    sb.append("IOCState=").append(state.IOCState).append("\n");
                    sb.append("isUltrasonicWaveWork=").append(state.isUltrasonicWaveWork).append("\n");
                    sb.append("goHomeState=").append(state.goHomeStatus.toString()).append("\n");
                    sb.append("imuPreheated=").append(state.imuPreheated).append("\n");
                    sb.append("isCompassErrorStatus=").append(state.isCompassErrorStatus).append("\n");
                    sb.append("compassCalibrationStatus=").append(state.CompassCalibrationStatus.name()).append("\n");
                    sb.append("isCompassCalibrationOpen=").append(state.isCompassCalibrationOpen).append("\n");
                    sb.append("isNavigationModeEnabled=").append(state.isNavigationModeEnabled).append("\n");
                }

                McStateString = sb.toString();
  
                MainActivity.this.runOnUiThread(new Runnable(){

                    @Override
                    public void run() 
                    {   
                        mMainControllerStateTextView.setText(McStateString);
                    }
                });

			}

		};

		DJIDrone.getDjiMainController().setMcuUpdateStateCallBack(mMcuUpdateStateCallBack);
		DJIDrone.getDjiMainController().setMcuErrorCallBack(new DJIMcuErrorCallBack() {

			@Override
			public void onError(DJIMcErrorType error) {
				// TODO Auto-generated method stub

			}
		});
		

	}

	private void init_GStation_CallBack() {
		Log.i(TAG, "init_GStation_CallBack");
		mGroundStationMissionPushInfoCallBack = new DJIGroundStationMissionPushInfoCallBack() {

			@Override
			public void onResult(DJIGroundStationMissionPushInfo info) {
				// TODO Auto-generated method stub
				StringBuffer sb = new StringBuffer();
				switch (info.missionType.value()) {
				case NAVI_MODE_WAYPOINT: {
					sb.append("Mission Type : " + info.missionType.toString())
							.append("\n");
					sb.append(
							"Mission Target Index : "
									+ info.targetWayPointIndex).append("\n");
					sb.append(
							"Mission Current Status : "
									+ GroundStationWayPointExecutionState.find(
											info.currState).toString()).append(
							"\n");
					// sb.append("Mission : " +
					// info.targetWayPointIndex).append("\n");
					break;
				}

				case NAVI_MODE_ATTITUDE: {
					sb.append("Mission Type : " + info.missionType.toString())
							.append("\n");
					sb.append("Mission Reserve : " + info.reserved)
							.append("\n");
					break;
				}

				default:
					sb.append("Worng Selection").append("\n");
				}

				// GsInfoString = sb.toString();

				MainActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						// mGroundStationTextView.setText(GsInfoString);
					}

				});
			}

		};

		DJIDrone.getDjiGroundStation().setGroundStationMissionPushInfoCallBack(
				mGroundStationMissionPushInfoCallBack);

		mGroundStationExecutionPushInfoCallBack = new DJIGroundStationExecutionPushInfoCallBack() {

			@Override
			public void onResult(DJIGroundStationExecutionPushInfo info) {
				// TODO Auto-generated method stub
				StringBuffer sb = new StringBuffer();
				switch (info.eventType.value()) {
				case EXECUTION_STATUS_UPLOAD_FINISH: {
					sb.append("Execution Type : " + info.eventType.toString())
							.append("\n");
					sb.append(
							"Validation : "
									+ (info.isMissionValid ? "true" : "false"))
							.append("\n");
					sb.append("Estimated run time : " + info.estimateRunTime)
							.append("\n");
					break;
				}

				case EXECUTION_STATUS_FINISH: {
					sb.append("Execution Type : " + info.eventType.toString())
							.append("\n");
					sb.append("Repeat : " + Integer.toString(info.isRepeat))
							.append("\n");
					// sb.append("Reserve: " +
					// GroundStationWayPointExecutionState.find(info.reserved).toString()).append("\n");
					break;
				}

				case EXECUTION_STATUS_REACH_POINT: {
					sb.append("Execution Type : " + info.eventType.toString())
							.append("\n");
					sb.append("WayPoint index : " + info.wayPointIndex).append(
							"\n");
					sb.append(
							"Current State : "
									+ GroundStationWayPointExecutionState.find(
											info.currentState).toString())
							.append("\n");
					sb.append("Reserve : " + info.reserved).append("\n");
					break;
				}
				}

				// GsInfoString1 = sb.toString();

				MainActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						// mGroundStationTextView1.setText(GsInfoString1);
					}

				});
			}

		};

		DJIDrone.getDjiGroundStation()
				.setGroundStationExecutionPushInfoCallBack(
						mGroundStationExecutionPushInfoCallBack);

		mTask = new DJIGroundStationTask();
		DJIDrone.getDjiGroundStation().setGroundStationFlyingInfoCallBack(
				new DJIGroundStationFlyingInfoCallBack() {

					@Override
					public void onResult(DJIGroundStationFlyingInfo mInfo) {
						StringBuffer sb = new StringBuffer();

						sb.append("Altitude = ").append(mInfo.altitude)
								.append("\n");
						sb.append("Way Point Index = ")
								.append(mInfo.targetWaypointIndex).append("\n");
						sb.append("Aircraft's Pitch = ").append(mInfo.pitch)
								.append("\n");
						sb.append("Aircraft's Roll = ").append(mInfo.roll)
								.append("\n");
						sb.append("Aircraft's Yaw = ").append(mInfo.yaw)
								.append("\n");
						sb.append("Aircraft's velocityX = ")
								.append(mInfo.velocityX).append("\n");
						sb.append("Aircraft's veloctiyY = ")
								.append(mInfo.velocityY).append("\n");
						sb.append("Aircraft's veloctiyZ = ")
								.append(mInfo.velocityZ).append("\n");

						// handler.sendMessage(handler.obtainMessage(SHOWTOAST,
						// sb.toString()));
					}
				});
	}

	private void init_Camera_CallBack() {
		Log.i(TAG, "init_Camera_CallBack");
		DJIDrone.getDjiCamera().setReceivedVideoDataCallBack(
				new DJIReceivedVideoDataCallBack() {

					@Override
					public void onResult(byte[] videoBuffer, int size) {
						// TODO Auto-generated method stub
						mDjiGLSurfaceView.setDataToDecoder(videoBuffer, size);
					}

				});
		DJIDrone.getDjiCamera().setDjiCameraSystemStateCallBack(
				new DJICameraSystemStateCallBack() {

					@Override
					public void onResult(DJICameraSystemState state) {
						// TODO Auto-generated method stub
						if (state.isTakingContinusPhoto) {
							handler.sendMessage(handler.obtainMessage(
									SHOWTOAST, "isTakingContinuousPhoto"));
						}
					}
				});

		mCameraFileNameInfoCallBack = new DJICameraFileNameInfoCallBack() {

			@Override
			public void onResult(final DJICameraFileNamePushInfo mInfo) {
				// TODO Auto-generated method stub
				Log.d(TAG, "camera file info type = " + mInfo.type.toString());
				Log.d(TAG, "camera file info filePath = " + mInfo.filePath);
				Log.d(TAG, "camera file info fileName = " + mInfo.fileName);
			}
		};

		DJIDrone.getDjiCamera().setDjiCameraFileNameInfoCallBack(
				mCameraFileNameInfoCallBack);
		mCameraPlayBackStateCallBack = new DJICameraPlayBackStateCallBack() {

			@Override
			public void onResult(DJICameraPlaybackState mState) {
				// TODO Auto-generated method stub

				getStateText(mState);

				MainActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// mCameraPlaybackStateTV.setText(mPlayBackStateString);
					}
				});

				// mPlayBackThumbnailNum = mState.numbersOfThumbnail;
				// mPlayBackMediaFileNum = mState.numbersOfMediaFiles;
				// mPlayBackCurrentSelectIndex =
				// mState.currentSelectedFileIndex;
			}

			private String getStateText(DJICameraPlaybackState mState) {
				StringBuffer sb = new StringBuffer();

				sb.append("playbackMode=")
						.append(mState.playbackMode.toString()).append("\n");
				sb.append("mediaFileType=")
						.append(mState.mediaFileType.toString()).append("\n");
				sb.append("numbersOfThumbnail=")
						.append(mState.numbersOfThumbnail).append("\n");
				sb.append("numbersOfMediaFiles=")
						.append(mState.numbersOfMediaFiles).append("\n");
				sb.append("currentSelectedFileIndex=")
						.append(mState.currentSelectedFileIndex).append("\n");
				sb.append("videoDuration=").append(mState.videoDuration)
						.append("\n");
				sb.append("videoPlayProgress=")
						.append(mState.videoPlayProgress).append("\n");
				sb.append("videoPlayPosition=")
						.append(mState.videoPlayPosition).append("\n");
				sb.append("numbersOfSelected=")
						.append(mState.numbersOfSelected).append("\n");
				sb.append("numbersOfPhotos=").append(mState.numbersOfPhotos)
						.append("\n");
				sb.append("numbersOfVideos=").append(mState.numbersOfVideos)
						.append("\n");
				sb.append("zoomScale=").append(mState.zoomScale).append("\n");
				sb.append("photoWidth=").append(mState.photoWidth).append("\n");
				sb.append("photoHeight=").append(mState.photoHeight)
						.append("\n");
				sb.append("photoCenterCoordinateX=")
						.append(mState.photoCenterCoordinateX).append("\n");
				sb.append("photoCenterCoordinateY=")
						.append(mState.photoCenterCoordinateY).append("\n");
				sb.append("fileDeleteStatus=")
						.append(mState.fileDeleteStatus.toString())
						.append("\n");
				sb.append("isAllFilesInPageSelected=")
						.append(mState.isAllFilesInPageSelected).append("\n");
				sb.append("isSelectedFileValid=")
						.append(mState.isSelectedFileValid).append("\n");
				sb.append("isFileDownloaded=").append(mState.isFileDownloaded);

				return sb.toString();
			}

		};
		DJIDrone.getDjiCamera().setDJICameraPlayBackStateCallBack(
				mCameraPlayBackStateCallBack);
		DJIDrone.getDjiCamera().setDjiCameraSdcardInfoCallBack(
				new DJICameraSdCardInfoCallBack() {

					@Override
					public void onResult(DJICameraSDCardInfo mInfo) {
						// TODO Auto-generated method stub
						/*
						 * if(mSdcardRemainSize != mInfo.remainSize){
						 * mSdcardRemainSize = mInfo.remainSize;
						 * 
						 * //String result = "sdcard remain size =" +
						 * mSdcardRemainSize ;
						 * //handler.sendMessage(handler.obtainMessage
						 * (SHOWTOAST, result)); }
						 */
					}
				});
	}

	private void init_Gimbal_CallBack() {
		Log.i(TAG, "init_Gimbal_CallBack");
		mGimbalUpdateAttitudeCallBack=new DJIGimbalUpdateAttitudeCallBack() {

			@Override
			public void onResult(DJIGimbalAttitude attitude) {
				// TODO Auto-generated method stub
				// Log.d(TAG, attitude.toString());

				StringBuffer sb = new StringBuffer();
				sb.append(getString(R.string.gimbal_attitude)).append(
						"\n");
				sb.append("pitch=").append(attitude.pitch).append("\n");
				sb.append("roll=").append(attitude.roll).append("\n");
				sb.append("yaw=").append(attitude.yaw).append("\n");
				sb.append("yawAngle=")
						.append(DJIDrone.getDjiGimbal().getYawAngle())
						.append("\n");
				sb.append("roll adjust=").append(attitude.rollAdjust)
						.append("\n");
				sb.toString();
				GimbalString=sb.toString();
				MainActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// mGimbalAttitudeBtn.setText(AttiudeString);
						mGimbalTextView.setText(GimbalString);
					}
				});

			}

		};
	/*	mGimbalErrorCallBack=new DJIGimbalErrorCallBack() {

			@Override
			public void onError(int error) {
				// TODO Auto-generated method stub
				// Log.d(TAG, "Gimbal error = "+error);
				String result = "errorCode ="
						+ error
						+ "\n";
						
				handler.sendMessage(handler.obtainMessage(SHOWTOAST,
						result));
			}

		};
		DJIDrone.getDjiGimbal().setGimbalErrorCallBack(mGimbalErrorCallBack);*/
		DJIDrone.getDjiGimbal().setGimbalUpdateAttitudeCallBack(mGimbalUpdateAttitudeCallBack);
	}

	class Task extends TimerTask {
		// int times = 1;

		@Override
		public void run() {
			// Log.d(TAG ,"==========>Task Run In!");
			checkConnectState();
		}

	};

	private void checkConnectState() {

		MainActivity.this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (DJIDrone.getDjiCamera() != null) {
					boolean bConnectState = DJIDrone.getDjiCamera()
							.getCameraConnectIsOk();
					if (bConnectState) {
						mConnectStateTextView
								.setText(R.string.camera_connection_ok);
					} else {
						mConnectStateTextView
								.setText(R.string.camera_connection_break);
					}
				}
			}
		});

	}

	private Handler handler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case SHOWDIALOG:
				showMessage(getString(R.string.demo_activation_message_title),
						(String) msg.obj);
				Log.d("", "nonono");
				break;
			case SHOWTOAST:
				setResultToToast((String) msg.obj);
				break;

			default:
				break;
			}
			return false;
		}
	});

	public void showMessage(String title, String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title).setMessage(msg).setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void setResultToToast(String result) {
		Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
	}

	private int checkYawTimes = 0;

	class CheckYawTask extends TimerTask {

		@Override
		public void run() {
			if (checkYawTimes >= 12) {
				if (mCheckYawTimer != null) {
					Log.d(TAG, "==========>mCheckYawTimer cancel!");
					mCheckYawTimer.cancel();
					mCheckYawTimer.purge();
					mCheckYawTimer = null;

					new Thread() {
						public void run() {

							DJIDrone.getDjiGroundStation().setAircraftYawSpeed(
									0, new DJIGroundStationExecuteCallBack() {

										@Override
										public void onResult(
												GroundStationResult result) {
											// TODO Auto-generated method stub

										}

									});
						}
					}.start();
				}
				return;
			}

			checkYawTimes++;
			Log.d(TAG, "==========>mCheckYawTimer checkYawTimes="
					+ checkYawTimes);

			new Thread() {
				public void run() {

					DJIDrone.getDjiGroundStation().setAircraftYawSpeed(300,
							new DJIGroundStationExecuteCallBack() {

								@Override
								public void onResult(GroundStationResult result) {
									// TODO Auto-generated method stub

								}

							});

					DJIDrone.getDjiCamera().startTakePhoto(
							new DJIExecuteResultCallback() {

								@Override
								public void onResult(DJIError mErr) {
									// TODO Auto-generated method stub
									
									Log.d(TAG, "Start Takephoto errorCode = "
											+ mErr.errorCode);
									Log.d(TAG,
											"Start Takephoto errorDescription = "
													+ mErr.errorDescription);
									String result = "errorCode ="
											+ mErr.errorCode
											+ "\n"
											+ "errorDescription ="
											+ DJIError
													.getErrorDescriptionByErrcode(mErr.errorCode);
									handler.sendMessage(handler.obtainMessage(
											SHOWTOAST, result));
								}

							});
				}
			}.start();

		}

	};

	private class Groundstation implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.OpenGsButton:
				Log.i("M100", "OpenGsButton");
				if (!checkGetHomePoint())return;
				
				DJIDrone.getDjiGroundStation().openGroundStation(new DJIGroundStationExecuteCallBack() {

							@Override
							public void onResult(GroundStationResult result) {
								// TODO Auto-generated method stub
								String ResultsString = "return code ="+ result.toString();
								handler.sendMessage(handler.obtainMessage(SHOWTOAST, ResultsString));
							}

						});
				break;
			case R.id.CloseGsButton:
				Log.i("M100", "CloseGsButton");
				 if(!checkGetHomePoint()) return;
	                
	                DJIDrone.getDjiGroundStation().closeGroundStation(new DJIGroundStationExecuteCallBack(){

	                    @Override
	                    public void onResult(GroundStationResult result) {
	                        // TODO Auto-generated method stub
	                        String ResultsString = "return code =" + result.toString();
	                        handler.sendMessage(handler.obtainMessage(SHOWTOAST, ResultsString));
	                    }

	                });  
				break;
			case R.id.FunnyBtn:
				if (!checkGetHomePoint())return;
				if (DJIDrone.getDroneType() == DJIDroneType.DJIDrone_Vision) {
					// north
					DJIGroundStationWaypoint mWayPoint1 = new DJIGroundStationWaypoint(homeLocationLatitude + 0.0000899322,homeLocationLongitude);
					mWayPoint1.altitude = 15f;
					mWayPoint1.speed = 2; // slow 2
					mWayPoint1.heading = 0;
					mWayPoint1.maxReachTime = 999;
					mWayPoint1.stayTime = 10;
					mWayPoint1.turnMode = 1;
                    mTask.addWaypoint(mWayPoint1);
					// east
					DJIGroundStationWaypoint mWayPoint2 = new DJIGroundStationWaypoint(homeLocationLatitude,homeLocationLongitude + 0.0000899322);
					mWayPoint2.altitude = 15f;
					mWayPoint2.speed = 2; // slow 2
					mWayPoint2.heading = 80;
					mWayPoint2.maxReachTime = 999;
					mWayPoint2.stayTime = 20;
					mWayPoint2.turnMode = 1;

					mTask.addWaypoint(mWayPoint2);

					// south
					DJIGroundStationWaypoint mWayPoint3 = new DJIGroundStationWaypoint(homeLocationLatitude - 0.0000899322,homeLocationLongitude);
					mWayPoint3.altitude = 15f;
					mWayPoint3.speed = 2; // slow 2
					mWayPoint3.heading = -60;
					mWayPoint3.maxReachTime = 999;
					mWayPoint3.stayTime = 25;
					mWayPoint3.turnMode = 1;
					
					mTask.addWaypoint(mWayPoint3);
					DJIGroundStationWaypoint mWayPoint4 = new DJIGroundStationWaypoint(homeLocationLatitude ,homeLocationLongitude- 0.0000899322);
					mWayPoint3.altitude = 15f;
					mWayPoint3.speed = 2; // slow 2
					mWayPoint3.heading = -60;
					mWayPoint3.maxReachTime = 999;
					mWayPoint3.stayTime = 25;
					mWayPoint3.turnMode = 1;
					mTask.addWaypoint(mWayPoint4);
					mTask.wayPointCount = mTask.getAllWaypoint().size();
				} else {
					// north
					DJIGroundStationWaypoint mWayPoint1 = new DJIGroundStationWaypoint(homeLocationLatitude + 0.0000899322,homeLocationLongitude);
					mWayPoint1.action.actionRepeat = 1;
					mWayPoint1.altitude = 15f;
					mWayPoint1.heading = 0;
					mWayPoint1.actionTimeout = 10;
					mWayPoint1.turnMode = 1;
					mWayPoint1.dampingDistance = 1.5f;
					mWayPoint1.hasAction = true;

					mWayPoint1.addAction(GroundStationOnWayPointAction.Way_Point_Action_Simple_Shot,1);
					mWayPoint1.addAction(GroundStationOnWayPointAction.Way_Point_Action_Video_Start,0);
					mWayPoint1.addAction(GroundStationOnWayPointAction.Way_Point_Action_Video_Stop,	0);
					mTask.addWaypoint(mWayPoint1);

					// east
					DJIGroundStationWaypoint mWayPoint2 = new DJIGroundStationWaypoint(homeLocationLatitude,homeLocationLongitude + 0.0000899322);
					mWayPoint2.action.actionRepeat = 1;
					mWayPoint2.altitude = 20f;
					mWayPoint2.heading = 80;
					mWayPoint2.actionTimeout = 20;
					mWayPoint2.turnMode = 1;
					mWayPoint2.dampingDistance = 2.5f;
					mWayPoint2.hasAction = true;

					mWayPoint2.addAction(GroundStationOnWayPointAction.Way_Point_Action_Craft_Yaw,-130);

					mTask.addWaypoint(mWayPoint2);

					// south
					DJIGroundStationWaypoint mWayPoint3 = new DJIGroundStationWaypoint(homeLocationLatitude - 0.0000899322,homeLocationLongitude);
					mWayPoint3.action.actionRepeat = 1;
					mWayPoint3.altitude = 30f;
					mWayPoint3.heading = -60;
					mWayPoint3.actionTimeout = 25;
					mWayPoint3.turnMode = 1;
					mWayPoint3.dampingDistance = 1.0f;
					mWayPoint3.hasAction = true;

					mWayPoint3.addAction(GroundStationOnWayPointAction.Way_Point_Action_Gimbal_Pitch,-89);
					mTask.addWaypoint(mWayPoint3);

					mTask.finishAction = DJIGroundStationFinishAction.Go_Home;
					mTask.movingMode = DJIGroundStationMovingMode.GSHeadingUsingWaypointHeading;
					mTask.pathMode = DJIGroundStationPathMode.Point_To_Point;
					mTask.wayPointCount = mTask.getAllWaypoint().size();
				}
				DJIDrone.getDjiGroundStation().uploadGroundStationTask(mTask, new DJIGroundStationExecuteCallBack(){

                    @Override
                    public void onResult(GroundStationResult result) {
                        // TODO Auto-generated method stub
                        String ResultsString = "return code =" + result.toString();
                        handler.sendMessage(handler.obtainMessage(SHOWTOAST, ResultsString));
                    }
                    
                });    
				 DJIDrone.getDjiGroundStation().startGroundStationTask(new DJIGroundStationExecuteCallBack(){

	                    @Override
	                    public void onResult(GroundStationResult result) {
	                        // TODO Auto-generated method stub
	                        String ResultsString = "return code =" + result.toString();
	                        handler.sendMessage(handler.obtainMessage(SHOWTOAST, ResultsString));
	                    }
	                });      
				break;

			default:
				break;
			}
		}

	}

	private boolean checkGetHomePoint() {
		// TODO Auto-generated method stub
		if (!getHomePointFlag) {
			setResultToToast(getString(R.string.gs_not_get_home_point));
		}
		return getHomePointFlag;
	}

	private String getErrorDescriptionByErrorCode(DJIMcErrorType errCode){
        String result = "";
        
        if(errCode == DJIMcErrorType.Mc_No_Error ){
            result = getString(R.string.MCU_NO_ERROR);
        }
        else if(errCode == DJIMcErrorType.Mc_Config_Error ){
            result = getString(R.string.MCU_CONFIG_ERROR);
        }
        else if(errCode == DJIMcErrorType.Mc_SerialNum_Error ){
            result = getString(R.string.MCU_SERIALNUM_ERROR);
        }
        else if(errCode == DJIMcErrorType.Mc_Imu_Error ){
            result = getString(R.string.MCU_IMU_ERROR);
        }
        else if(errCode == DJIMcErrorType.Mc_X1_Error ){
            result = getString(R.string.MCU_X1_ERROR);
        }       
        else if(errCode == DJIMcErrorType.Mc_X2_Error ){
            result = getString(R.string.MCU_X2_ERROR);
        }  
        else if(errCode == DJIMcErrorType.Mc_Pmu_Error ){
            result = getString(R.string.MCU_PMU_ERROR);
        }  
        else if(errCode == DJIMcErrorType.Mc_Transmitter_Error ){
            result = getString(R.string.MCU_TRANSMITTER_ERROR);
        }
        else if(errCode == DJIMcErrorType.Mc_Sensor_Error ){
            result = getString(R.string.MCU_SENSOR_ERROR);
        }        
        else if(errCode == DJIMcErrorType.Mc_Compass_Error ){
            result = getString(R.string.MCU_COMPASS_ERROR);
        }  
        else if(errCode == DJIMcErrorType.Mc_Imu_Calibration_Error ){
            result = getString(R.string.MCU_IMU_CALIBRATION_ERROR);
        }         
        else if(errCode == DJIMcErrorType.Mc_Compass_Calibration_Error ){
            result = getString(R.string.MCU_COMPASS_CALIBRATION_ERROR);
        }   
        else if(errCode == DJIMcErrorType.Mc_Transmitter_Calibration_Error ){
            result = getString(R.string.MCU_TRANSMITTER_CALIBRATION_ERROR);
        }           
        else if(errCode == DJIMcErrorType.Mc_Invalid_Battery_Error ){
            result = getString(R.string.MCU_INVALID_BATTERY_ERROR);
        }    
        else if(errCode == DJIMcErrorType.Mc_Invalid_Battery_Communication_Error ){
            result = getString(R.string.MCU_INVALID_BATTERY_COMMUNICATION_ERROR);
        }    
        else if(errCode == DJIMcErrorType.Mc_Unknown_Error ){
            result = getString(R.string.MCU_UNKOWN_ERROR);
        }          
        return result;
    }

}
