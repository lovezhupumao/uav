package com.ypai.uav;

import java.util.Timer;
import java.util.TimerTask;

import com.google.myjson.annotations.Since;
import com.ypai.uav.R.id;

import dji.sdk.api.DJIDrone;
import dji.sdk.api.DJIError;
import dji.sdk.api.Camera.DJICameraDecodeTypeDef.DecoderType;
import dji.sdk.api.DJIDroneTypeDef.DJIDroneType;
import dji.sdk.api.GroundStation.DJIGroundStationFlyingInfo;
import dji.sdk.api.GroundStation.DJIGroundStationMissionPushInfo;
import dji.sdk.api.GroundStation.DJIGroundStationTask;
import dji.sdk.api.GroundStation.DJIGroundStationWaypoint;
import dji.sdk.api.GroundStation.DJIGroundStationTypeDef.DJIGroundStationFinishAction;
import dji.sdk.api.GroundStation.DJIGroundStationTypeDef.DJIGroundStationMovingMode;
import dji.sdk.api.GroundStation.DJIGroundStationTypeDef.DJIGroundStationPathMode;
import dji.sdk.api.GroundStation.DJIGroundStationTypeDef.DJINavigationFlightControlCoordinateSystem;
import dji.sdk.api.GroundStation.DJIGroundStationTypeDef.DJINavigationFlightControlHorizontalControlMode;
import dji.sdk.api.GroundStation.DJIGroundStationTypeDef.DJINavigationFlightControlVerticalControlMode;
import dji.sdk.api.GroundStation.DJIGroundStationTypeDef.DJINavigationFlightControlYawControlMode;
import dji.sdk.api.GroundStation.DJIGroundStationTypeDef.GroundStationOnWayPointAction;
import dji.sdk.api.GroundStation.DJIGroundStationTypeDef.GroundStationResult;
import dji.sdk.api.MainController.DJIMainControllerSystemState;
import dji.sdk.interfaces.DJIExecuteResultCallback;
import dji.sdk.interfaces.DJIGroundStationExecuteCallBack;
import dji.sdk.interfaces.DJIGroundStationFlyingInfoCallBack;
import dji.sdk.interfaces.DJIGroundStationMissionPushInfoCallBack;
import dji.sdk.interfaces.DJIMcuUpdateStateCallBack;
import dji.sdk.interfaces.DJIReceivedVideoDataCallBack;
import dji.sdk.widget.DjiGLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PosActivity extends DemoBaseActivity implements OnClickListener {

	private final int SHOWTOAST = 1;
	private EditText mEditTextValue;
	private Button mButtonForward;
	private Button mButtonBackoff;
	private Button mButtonLeft;
	private Button mButtonRight;
	private Button mButtonOpenGS;
	private Button mButtonCloseGS;
	private Button mButtonUpLoad;
	private Button mButtonTakeoff;
	private TextView mConnectStateTextView;
	private TextView mGpsinfo;
	private TextView mMCinfo;
	private DjiGLSurfaceView mDjiGLSurfaceView;

	private DJIReceivedVideoDataCallBack mReceivedVideoDataCallBack = null;
	private Timer mTimer;
	private DJIMainControllerSystemState mMCstate=null;
	private DJIGroundStationTask mTask;
	private double homeLocationLatitude=-1;
	private double homeLocationLongitude=-1;
	private boolean getHomePointFlag=false;
	private String McStateString="";
	private String GSFlyStateString;
	private double posx;
	private double posy;
	private double lati;
	private double longi;
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
	class Task extends TimerTask {
		// int times = 1;

		@Override
		public void run() {
			// Log.d(TAG ,"==========>Task Run In!");
			checkConnectState();
		}

	};

	private void checkConnectState() {

		PosActivity.this.runOnUiThread(new Runnable() {

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pos);
		DJIDrone.getDjiCamera().setDecodeType(DecoderType.Software);
		mButtonForward=(Button)findViewById(R.id.pos_forward);
		mButtonBackoff=(Button)findViewById(R.id.pos_backoff);
		mButtonLeft=(Button)findViewById(R.id.pos_left);
		mButtonRight=(Button)findViewById(R.id.pos_right);
		mButtonOpenGS=(Button)findViewById(R.id.Pos_OpenGsButton);
		mButtonCloseGS=(Button)findViewById(R.id.Pos_CloseGroundStation);
		mButtonUpLoad=(Button)findViewById(R.id.Pos_UpDownWaypoint);
		mButtonTakeoff=(Button)findViewById(R.id.Pos_Begin);
		mEditTextValue=(EditText)findViewById(R.id.pos_value);
		mGpsinfo=(TextView)findViewById(R.id.Pos_GPSinfo);
		mMCinfo=(TextView)findViewById(R.id.Pos_MCinfo);
		mConnectStateTextView = (TextView) findViewById(R.id.ConnectStatePosTextView);
		mDjiGLSurfaceView = (DjiGLSurfaceView) findViewById(R.id.DjiSurfaceView_Pos);

		mDjiGLSurfaceView.start();
		mButtonForward.setOnClickListener(this);
		mButtonBackoff.setOnClickListener(this);
		mButtonLeft.setOnClickListener(this);
		mButtonRight.setOnClickListener(this);
		mButtonOpenGS.setOnClickListener(this);
		mButtonCloseGS.setOnClickListener(this);
		mButtonUpLoad.setOnClickListener(this);
		mButtonTakeoff.setOnClickListener(this);
		mReceivedVideoDataCallBack = new DJIReceivedVideoDataCallBack() {

			@Override
			public void onResult(byte[] videoBuffer, int size) {
				// TODO Auto-generated method stub
				mDjiGLSurfaceView.setDataToDecoder(videoBuffer, size);
			}

		};
		DJIDrone.getDjiCamera().setReceivedVideoDataCallBack(mReceivedVideoDataCallBack);
		DJIDrone.getDjiGroundStation().setGroundStationFlyingInfoCallBack(new DJIGroundStationFlyingInfoCallBack() {
			
			@Override
			public void onResult(DJIGroundStationFlyingInfo mInfo) {
				// TODO Auto-generated method stub
				    StringBuffer sb = new StringBuffer();
					sb.append("Aircraft state").append("\n");
	                sb.append("Altitude = ").append(mInfo.altitude).append("\n");
	                sb.append("Way Point Index = ").append(mInfo.targetWaypointIndex).append("\n");
	                sb.append("Aircraft's Pitch = ").append(mInfo.pitch).append("\n");
	                sb.append("Aircraft's Roll = ").append(mInfo.roll).append("\n");
	                sb.append("Aircraft's Yaw = ").append(mInfo.yaw).append("\n");
	                sb.append("Aircraft's velocityX = ").append(mInfo.velocityX).append("\n");
	                sb.append("Aircraft's veloctiyY = ").append(mInfo.velocityY).append("\n");
	                sb.append("Aircraft's veloctiyZ = ").append(mInfo.velocityZ).append("\n");
	                GSFlyStateString = sb.toString();
	                
	                PosActivity.this.runOnUiThread(new Runnable(){

	                    @Override
	                    public void run() 
	                    {   
	                       mMCinfo.setText(GSFlyStateString);
	                    }
	                });
			}
		});
		DJIDrone.getDjiMC().setMcuUpdateStateCallBack(new DJIMcuUpdateStateCallBack() {
			
			@Override
			public void onResult(DJIMainControllerSystemState state) {
				// TODO Auto-generated method stub
				mMCstate=state;
				homeLocationLatitude = state.droneLocationLatitude;
                homeLocationLongitude = state.droneLocationLongitude;
                if(homeLocationLatitude != -1 && homeLocationLongitude != -1 && homeLocationLatitude != 0 && homeLocationLongitude != 0){
                    getHomePointFlag = true;
                }
                else{
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
                McStateString = sb.toString();
                
                PosActivity.this.runOnUiThread(new Runnable(){

                    @Override
                    public void run() 
                    {   
                       mGpsinfo .setText(McStateString);
                    }
                });
			}
			
		});
		DJIDrone.getDjiGroundStation().setGroundStationMissionPushInfoCallBack(new DJIGroundStationMissionPushInfoCallBack() {
			
			@Override
			public void onResult(DJIGroundStationMissionPushInfo arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		mTask=new DJIGroundStationTask();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		mDjiGLSurfaceView.resume();

		mTimer = new Timer();
		Task task = new Task();
		mTimer.schedule(task, 0, 500);

		DJIDrone.getDjiMC().startUpdateTimer(1000);
		DJIDrone.getDjiGroundStation().startUpdateTimer(1000);
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
		DJIDrone.getDjiGroundStation().stopUpdateTimer();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (DJIDrone.getDjiCamera() != null)
			DJIDrone.getDjiCamera().setReceivedVideoDataCallBack(null);
		mDjiGLSurfaceView.destroy();
		super.onDestroy();
	}

	public void onReturn(View view) {
		this.finish();
	}
	private boolean checkGetHomePoint() {
		// TODO Auto-generated method stub
		if (!getHomePointFlag) {
			setResultToToast(getString(R.string.gs_not_get_home_point));
		}
		return getHomePointFlag;
	}
	private void setResultToToast(String result) {
		Toast.makeText(PosActivity.this, result, Toast.LENGTH_SHORT).show();
	}

	private double posTolat(double pos)
	{
		double temp;
		temp=pos*0.00000899322;
		return temp;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.Pos_OpenGsButton:
			if(!checkGetHomePoint()) return;
        	DJIDrone.getDjiGroundStation().openGroundStation(new DJIGroundStationExecuteCallBack(){

                @Override
                public void onResult(GroundStationResult result) {
                    // TODO Auto-generated method stub
                    String ResultsString = "return code =" + result.toString();
                    handler.sendMessage(handler.obtainMessage(SHOWTOAST, ResultsString));
                }
                
            }); 
			break;
		case R.id.Pos_CloseGroundStation:
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
		case R.id.pos_forward:

			posx = 10 * Math.cos(mMCstate.yaw * Math.PI / 180);
			posy = 10 * Math.sin(mMCstate.yaw * Math.PI / 180);
			double forwardlati = posx * 0.00000899322;
			double forwardlongi = posy * 0.00000899322;

			addWaypoint_Method(forwardlati, forwardlongi);

			break;
		case R.id.pos_backoff:
			mTask.RemoveAllWaypoint();
			posx = -10 * Math.cos(mMCstate.yaw * Math.PI / 180);
			posy = -10 * Math.sin(mMCstate.yaw * Math.PI / 180);
			double backofflati = posx * 0.00000899322;
			double backofflongi = posy * 0.00000899322;
			addWaypoint_Method(backofflati, backofflongi);
			break;
		case R.id.pos_left:
			mTask.RemoveAllWaypoint();
			posx = 10 * Math.sin(mMCstate.yaw * Math.PI / 180);
			posy = -10 * Math.cos(mMCstate.yaw * Math.PI / 180);
			double leftlati = posx * 0.00000899322;
			double leftlongi = posy * 0.00000899322;
			addWaypoint_Method(leftlati, leftlongi);
			break;
		case R.id.pos_right:
			mTask.RemoveAllWaypoint();
			posx = -10 * Math.sin(mMCstate.yaw * Math.PI / 180);
			posy = 10 * Math.cos(mMCstate.yaw * Math.PI / 180);
			double rightlati = posx * 0.00000899322;
			double rightlongi = posy * 0.00000899322;
			addWaypoint_Method(rightlati, rightlongi);
			break;
		case R.id.Pos_UpDownWaypoint:
			DJIDrone.getDjiGroundStation().uploadGroundStationTask(mTask,new DJIGroundStationExecuteCallBack() {
				@Override
				public void onResult(GroundStationResult result) {
					// TODO Auto-generated method stub
					String ResultsString = "return code ="+ result.toString();
					handler.sendMessage(handler.obtainMessage(SHOWTOAST, ResultsString));
				}

			});  
			break;
			case R.id.Pos_Begin:
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

	private void addWaypoint_Method(double lati, double longi) {
		mTask.RemoveAllWaypoint();
		DJIGroundStationWaypoint mWayPoint1 = new DJIGroundStationWaypoint(homeLocationLatitude + lati/2, homeLocationLongitude+ longi/2);
		mWayPoint1.action.actionRepeat = 1;
		mWayPoint1.altitude = 15f;
		// mWayPoint1.heading = 0;
		mWayPoint1.actionTimeout = 10;
		mWayPoint1.turnMode = 1;
		mWayPoint1.dampingDistance = 1.5f;
		mWayPoint1.hasAction = false;

		mTask.addWaypoint(mWayPoint1);

		// east
		DJIGroundStationWaypoint mWayPoint2 = new DJIGroundStationWaypoint(homeLocationLatitude+lati, homeLocationLongitude + longi);
		mWayPoint2.action.actionRepeat = 1;
		mWayPoint2.altitude = 15f;
		// mWayPoint2.heading = 0;
		mWayPoint2.actionTimeout = 20;
		mWayPoint2.turnMode = 1;
		mWayPoint2.dampingDistance = 2.5f;
		mWayPoint2.hasAction = false;

		mTask.addWaypoint(mWayPoint2);

		mTask.finishAction = DJIGroundStationFinishAction.None;
		mTask.movingMode = DJIGroundStationMovingMode.GSHeadingUsingInitialDirection;
		mTask.pathMode = DJIGroundStationPathMode.Point_To_Point;
		mTask.wayPointCount = mTask.getAllWaypoint().size();
		DJIDrone.getDjiGroundStation().uploadGroundStationTask(mTask,new DJIGroundStationExecuteCallBack() {
			@Override
			public void onResult(GroundStationResult result) {
				// TODO Auto-generated method stub
				String ResultsString = "return code ="+ result.toString();
				handler.sendMessage(handler.obtainMessage(SHOWTOAST, ResultsString));
			}

		});  
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DJIDrone.getDjiGroundStation().startGroundStationTask(new DJIGroundStationExecuteCallBack(){

            @Override
            public void onResult(GroundStationResult result) {
                // TODO Auto-generated method stub
                String ResultsString = "return code =" + result.toString();
                handler.sendMessage(handler.obtainMessage(SHOWTOAST, ResultsString));
            }
        });   
	}

}
