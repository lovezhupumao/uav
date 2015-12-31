package com.ypai.uav;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.ypai.sqlwaypoint.WayPointDbHelper;
import com.ypai.sqlwaypoint.WayPointMethod;

import dji.sdk.api.DJIDrone;
import dji.sdk.api.Battery.DJIBatteryProperty;
import dji.sdk.api.Camera.DJICameraDecodeTypeDef.DecoderType;
import dji.sdk.api.GroundStation.DJIGroundStationTask;
import dji.sdk.api.GroundStation.DJIGroundStationWaypoint;
import dji.sdk.api.GroundStation.DJIGroundStationTypeDef.DJIGroundStationFinishAction;
import dji.sdk.api.GroundStation.DJIGroundStationTypeDef.DJIGroundStationMovingMode;
import dji.sdk.api.GroundStation.DJIGroundStationTypeDef.DJIGroundStationPathMode;
import dji.sdk.api.GroundStation.DJIGroundStationTypeDef.GroundStationOnWayPointAction;
import dji.sdk.api.GroundStation.DJIGroundStationTypeDef.GroundStationResult;
import dji.sdk.api.MainController.DJIMainControllerSystemState;
import dji.sdk.interfaces.DJIBatteryUpdateInfoCallBack;
import dji.sdk.interfaces.DJIGroundStationExecuteCallBack;
import dji.sdk.interfaces.DJIGroundStationExecutionPushInfoCallBack;
import dji.sdk.interfaces.DJIGroundStationFlyingInfoCallBack;
import dji.sdk.interfaces.DJIGroundStationMissionPushInfoCallBack;
import dji.sdk.interfaces.DJIMcuUpdateStateCallBack;
import dji.sdk.interfaces.DJIReceivedVideoDataCallBack;
import dji.sdk.widget.DjiGLSurfaceView;
import android.app.Activity;
import android.app.ApplicationErrorReport.BatteryInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class LearnActivity extends DemoBaseActivity implements OnClickListener {
	private final int SHOWTOAST = 1;
	private double homeLocationLatitude = -1;
	private double homeLocationLongitude = -1;
	private double matti=-1;
	private double mlati=-1;
	private double mlongi=-1;
	private boolean getHomePointFlag = false;
	private Button mButtonOpenGS;
	private Button mButtonCloseGS;
	private Button mButtonRecord;
	private Button mButtonStopRecord;
	private Button mButtonRemove;
	private Button mButtonUpload;
	private Button mButtonBegin;
	private Button mButtonPause;
	private Button mButtonResume;
	private Button mButtonGoHome;
	private Button mButtondefault;
	private TextView mTextViewinfo;
	private ListView mylist;
	private DJIGroundStationTask mTask;
	private Timer mTimer;
	private Timer mRecordTimer = null;
	private SimpleAdapter sa;
	protected String mMCStringinfo = "";
	private TextView mConnectStateTextView;
	private DjiGLSurfaceView mDjiGLSurfaceView;
	private DJIMainControllerSystemState mMCstate = null;
	private DJIReceivedVideoDataCallBack mReceivedVideoDataCallBack = null;
	private DJIGroundStationFlyingInfoCallBack mGroundStationFlyingInfoCallBack = null;
	private DJIGroundStationMissionPushInfoCallBack mGroundStationMissionPushInfoCallBack = null;
	private DJIGroundStationExecutionPushInfoCallBack mGroundStationExecutionPushInfoCallBack = null;
	private DJIMcuUpdateStateCallBack mMcuUpdateStateCallBack = null;

	private WayPointDbHelper mDbHelper;

	class Task extends TimerTask {
		// int times = 1;

		@Override
		public void run() {
			// Log.d(TAG ,"==========>Task Run In!");
			checkConnectState();
		}

	};

	class RecordTask extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(matti!=mMCstate.altitude||mlati!=mMCstate.droneLocationLatitude||mlongi!=mMCstate.droneLocationLongitude){
				WayPointMethod.addwaypoint(mDbHelper, mMCstate.altitude,mMCstate.droneLocationLatitude,mMCstate.droneLocationLongitude);
				matti=mMCstate.altitude;
				mlati=mMCstate.droneLocationLatitude;
				mlongi=mMCstate.droneLocationLongitude;
				Cursor c = WayPointMethod.selectwaypoint(mDbHelper);
				ArrayList<Map<String, Object>> ss = new ArrayList<Map<String, Object>>();
				for (int i = 0; i < c.getCount(); i++) {
					Map<String, Object> mm = new HashMap<String, Object>();
					c.moveToPosition(i);
					mm.put("num", Integer.toString(i+1));
					mm.put("altitude", c.getString(1));
					mm.put("latitude", c.getString(2));
					mm.put("longitude", c.getString(3));
					ss.add(mm);
				}
				c.moveToPosition(0);

				sa = new SimpleAdapter(LearnActivity.this, ss,
						R.layout.waypointview, new String[] { "num", "altitude",
								"latitude", "longitude" }, new int[] {
								R.id.waypointnum, R.id.waypoint_altitude,
								R.id.waypoint_latitude, R.id.waypoint_longitude });
				LearnActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mylist.setAdapter(sa);
					}
				});
			}
			

		}

	}

	private void checkConnectState() {

		LearnActivity.this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (DJIDrone.getDjiCamera() != null) {
					boolean bConnectState = DJIDrone.getDjiCamera()
							.getCameraConnectIsOk();
					if (bConnectState) {
						mConnectStateTextView
								.setText("Connected"+"("+Integer.toString(mBattery) +"%)");
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
			case SHOWTOAST:
				setResultToToast((String) msg.obj);
				break;
			default:
				break;
			}
			return false;
		}
	});
	protected int mBattery=0;

	private void setResultToToast(String result) {
		Toast.makeText(LearnActivity.this, result, Toast.LENGTH_SHORT).show();
	}

	private boolean checkGetHomePoint() {
		if (!getHomePointFlag) {
			setResultToToast(getString(R.string.gs_not_get_home_point));
		}
		return getHomePointFlag;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_learn);
		DJIDrone.getDjiCamera().setDecodeType(DecoderType.Software);
		mDbHelper = new WayPointDbHelper(this);
		mDjiGLSurfaceView = (DjiGLSurfaceView) findViewById(R.id.learn_DjiSurfaceView);
		mButtonOpenGS = (Button) findViewById(R.id.learn_OpenGsButton);
		mButtonRecord = (Button) findViewById(R.id.learn_RecordWaypointButton);
		mButtonStopRecord = (Button) findViewById(R.id.learn_StopRecord);
		mButtonRemove = (Button) findViewById(R.id.learn_RemoveWaypointButton);
		mButtonUpload = (Button) findViewById(R.id.learn_UploadWaypointButton);
		mButtonBegin = (Button) findViewById(R.id.learn_TakeOffButton);
		mButtonPause = (Button) findViewById(R.id.learn_PauseButton);
		mButtonResume = (Button) findViewById(R.id.learn_ResumeButton);
		mButtonGoHome = (Button) findViewById(R.id.learn_GohomeButton);
		mButtonCloseGS = (Button) findViewById(R.id.learn_CloseGsButton);
		mButtondefault=(Button)findViewById(R.id.learn_default);
		mTextViewinfo = (TextView) findViewById(R.id.learn_GroundStationInfoTV);
		mConnectStateTextView = (TextView) findViewById(R.id.learn_ConnectStateGsTextView);
		mylist = (ListView) findViewById(R.id.learn_listview);
		mButtonOpenGS.setOnClickListener(this);
		mButtonRecord.setOnClickListener(this);
		mButtonStopRecord.setOnClickListener(this);
		mButtonRemove.setOnClickListener(this);
		mButtonUpload.setOnClickListener(this);
		mButtonBegin.setOnClickListener(this);
		mButtonPause.setOnClickListener(this);
		mButtonResume.setOnClickListener(this);
		mButtonGoHome.setOnClickListener(this);
		mButtonCloseGS.setOnClickListener(this);
		mButtondefault.setOnClickListener(this);
		mDjiGLSurfaceView.start();
		mylist.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				TextView latitudetv=(TextView)view.findViewById(R.id.waypoint_latitude);
				TextView longitudetv=(TextView)view.findViewById(R.id.waypoint_longitude);
				TextView altitudetv=(TextView)view.findViewById(R.id.waypoint_altitude);
				TextView numtx=(TextView)findViewById(R.id.waypointnum);
				
				WayPointMethod.deletewaypoint(mDbHelper, Double.parseDouble(altitudetv.getText().toString()),Double.parseDouble(latitudetv.getText().toString()),Double.parseDouble(longitudetv.getText().toString()));
				showWayPoint();
				return false;
			}
		});
		mReceivedVideoDataCallBack = new DJIReceivedVideoDataCallBack() {

			@Override
			public void onResult(byte[] videoBuffer, int size) {
				// TODO Auto-generated method stub
				mDjiGLSurfaceView.setDataToDecoder(videoBuffer, size);
			}

		};

		DJIDrone.getDjiCamera().setReceivedVideoDataCallBack(
				mReceivedVideoDataCallBack);
		mMcuUpdateStateCallBack = new DJIMcuUpdateStateCallBack() {

			@Override
			public void onResult(DJIMainControllerSystemState state) {
				// TODO Auto-generated method stub
				mMCstate = state;
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
				sb.append(getString(R.string.main_controller_state)).append(
						"\n");
				sb.append("MCU Version=")
						.append(DJIDrone.getDjiMainController().getMcuVersion())
						.append("\n");
				sb.append("satelliteCount=").append(state.satelliteCount)
						.append("\n");
				sb.append("homeLocationLatitude=")
						.append(state.homeLocationLatitude).append("\n");
				sb.append("homeLocationLongitude=")
						.append(state.homeLocationLongitude).append("\n");
				sb.append("droneLocationLatitude=")
						.append(state.droneLocationLatitude).append("\n");
				sb.append("droneLocationLongitude=")
						.append(state.droneLocationLongitude).append("\n");
				sb.append("velocityX=").append(state.velocityX).append("\n");
				sb.append("velocityY=").append(state.velocityY).append("\n");
				sb.append("velocityZ=").append(state.velocityZ).append("\n");
				sb.append("speed=").append(state.speed).append("\n");
				sb.append("altitude=").append(state.altitude).append("\n");
				sb.append("pitch=").append(state.pitch).append("\n");
				sb.append("roll=").append(state.roll).append("\n");
				sb.append("yaw=").append(state.yaw).append("\n");
				sb.append("remainPower=").append(state.remainPower)
						.append("\n");
				sb.append("remainFlyTime=").append(state.remainFlyTime)
						.append("\n");
				sb.append("powerLevel=").append(state.powerLevel).append("\n");
				sb.append("isFlying=").append(state.isFlying).append("\n");
				sb.append("noFlyStatus=").append(state.noFlyStatus)
						.append("\n");
				sb.append("noFlyZoneCenterLatitude=")
						.append(state.noFlyZoneCenterLatitude).append("\n");
				sb.append("noFlyZoneCenterLongitude=")
						.append(state.noFlyZoneCenterLongitude).append("\n");
				sb.append("noFlyZoneRadius=").append(state.noFlyZoneRadius);
				mMCStringinfo = sb.toString();

				LearnActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mTextViewinfo.setText(mMCStringinfo);
					}
				});
			}

		};
		DJIDrone.getDjiMC().setMcuUpdateStateCallBack(mMcuUpdateStateCallBack);
		DJIDrone.getDjiBattery().setBatteryUpdateInfoCallBack(new DJIBatteryUpdateInfoCallBack() {
			
			@Override
			public void onResult(DJIBatteryProperty arg0) {
				// TODO Auto-generated method stub
				mBattery=arg0.remainPowerPercent;
			}
		});
		showWayPoint();
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
		DJIDrone.getDjiBattery().startUpdateTimer(2000);
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
		DJIDrone.getDjiBattery().stopUpdateTimer();
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

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		menu.setHeaderTitle("²Ù×÷");
		menu.add(1, 111, 0, "É¾³ý");
		
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		switch (item.getItemId()) {
		case 111:
			WayPointMethod.deletewaypoint(mDbHelper);
			showWayPoint();
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	public void onReturn(View view) {
		this.finish();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.learn_OpenGsButton:
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
		case R.id.learn_RecordWaypointButton:
			if (!checkGetHomePoint()) {
				return;
			}
			mRecordTimer = new Timer();
			RecordTask mRecordTask = new RecordTask();
			mRecordTimer.schedule(mRecordTask, 0, 1000);
			break;
		case R.id.learn_StopRecord:
			if (!checkGetHomePoint())
				return;
			if (mRecordTimer != null) {
				mRecordTimer.cancel();
				mRecordTimer.purge();
				mRecordTimer = null;
			}

			break;
		case R.id.learn_RemoveWaypointButton:
			WayPointMethod.deletewaypoint(mDbHelper);
			showWayPoint();
			break;
		case R.id.learn_UploadWaypointButton:
			mTask.RemoveAllWaypoint();
			Cursor upCursor = WayPointMethod.selectwaypoint(mDbHelper);
			for (int i = 0; i < upCursor.getCount(); i++) {
				upCursor.moveToPosition(i);
				DJIGroundStationWaypoint mWayPoint1 = new DJIGroundStationWaypoint(
						Double.parseDouble(upCursor.getString(2)),
						Double.parseDouble(upCursor.getString(3)));
				mWayPoint1.action.actionRepeat = 1;
				mWayPoint1.altitude = (Float.parseFloat(upCursor.getString(1)));
				// mWayPoint1.heading = 0;
				mWayPoint1.actionTimeout = 10;
				mWayPoint1.turnMode = 1;
				mWayPoint1.dampingDistance = 1.5f;
				mWayPoint1.hasAction = false;

				mTask.addWaypoint(mWayPoint1);
			}
			mTask.finishAction = DJIGroundStationFinishAction.None;
			mTask.movingMode = DJIGroundStationMovingMode.GSHeadingTowardNextWaypoint;
			mTask.pathMode = DJIGroundStationPathMode.Point_To_Point;
			mTask.wayPointCount = mTask.getAllWaypoint().size();
			DJIDrone.getDjiGroundStation().uploadGroundStationTask(mTask,
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
		case R.id.learn_TakeOffButton:
			DJIDrone.getDjiGroundStation().startGroundStationTask(
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
		case R.id.learn_PauseButton:
			if (!checkGetHomePoint())
				return;
			DJIDrone.getDjiGroundStation().pauseGroundStationTask(
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
		case R.id.learn_ResumeButton:
			if (!checkGetHomePoint())
				return;

			DJIDrone.getDjiGroundStation().continueGroundStationTask(
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
		case R.id.learn_GohomeButton:
			if (!checkGetHomePoint())
				return;
			DJIDrone.getDjiGroundStation().goHome(
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
		case R.id.learn_CloseGsButton:
			if (!checkGetHomePoint())
				return;

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
			break;
		case R.id.learn_default:
			mTask.RemoveAllWaypoint();
			if (!checkGetHomePoint()) return;
			new Thread(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					DJIGroundStationWaypoint mWayPoint1 = new DJIGroundStationWaypoint(homeLocationLatitude+0.0000899322, homeLocationLongitude);
		            mWayPoint1.action.actionRepeat = 1;
		            mWayPoint1.altitude = 50f;
		            mWayPoint1.heading = 0;
		            mWayPoint1.actionTimeout = 10;       
		            mWayPoint1.turnMode = 1;
		            mWayPoint1.dampingDistance = 1.5f;
		            mWayPoint1.hasAction = false;
		            
		            mTask.addWaypoint(mWayPoint1);
		            
		            //east
		            DJIGroundStationWaypoint mWayPoint2 = new DJIGroundStationWaypoint(homeLocationLatitude, homeLocationLongitude+0.0000899322);
		            mWayPoint2.action.actionRepeat = 1;
		            mWayPoint2.altitude = 50f;
		            mWayPoint2.heading = 80;
		            mWayPoint2.actionTimeout = 20;  
		            mWayPoint2.turnMode = 1;
		            mWayPoint2.dampingDistance = 2.5f;
		            mWayPoint2.hasAction = false;
		            mTask.addWaypoint(mWayPoint2);

		            //south
		            DJIGroundStationWaypoint mWayPoint3 = new DJIGroundStationWaypoint(homeLocationLatitude-0.0000899322, homeLocationLongitude);
		            mWayPoint3.action.actionRepeat = 1;
		            mWayPoint3.altitude = 50f;
		            mWayPoint3.heading = -60;
		            mWayPoint3.actionTimeout = 25;     
		            mWayPoint3.turnMode = 1;
		            mWayPoint3.dampingDistance = 1.0f;
		            mWayPoint3.hasAction = false;
		            mTask.addWaypoint(mWayPoint3);
		            DJIGroundStationWaypoint mWayPoint4 = new DJIGroundStationWaypoint(homeLocationLatitude, homeLocationLongitude-0.0000899322);
		            mWayPoint4.action.actionRepeat = 1;
		            mWayPoint4.altitude = 50f;
		            mWayPoint4.heading = -60;
		            mWayPoint4.actionTimeout = 25;     
		            mWayPoint4.turnMode = 1;
		            mWayPoint4.dampingDistance = 1.0f;
		            mWayPoint4.hasAction = false;
		            mTask.addWaypoint(mWayPoint4);
		            mTask.finishAction = DJIGroundStationFinishAction.None;
		            mTask.movingMode = DJIGroundStationMovingMode.GSHeadingUsingInitialDirection;
		            mTask.pathMode = DJIGroundStationPathMode.Point_To_Point;
		            mTask.wayPointCount = mTask.getAllWaypoint().size();
		            	
		           
		            
		            DJIDrone.getDjiGroundStation().uploadGroundStationTask(mTask, new DJIGroundStationExecuteCallBack(){

		                @Override
		                public void onResult(GroundStationResult result) {
		                    // TODO Auto-generated method stub
		                    String ResultsString = "return code =" + result.toString();
		                    handler.sendMessage(handler.obtainMessage(SHOWTOAST, ResultsString));
		                }
		                
		            });  
		            try {
						Thread.sleep(1000);
					} catch (Exception e) {
						// TODO: handle exception
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
				
			}.start();
			break;
		default:
			break;
		}
	}

	private void showWayPoint() {
		Cursor c = WayPointMethod.selectwaypoint(mDbHelper);
		ArrayList<Map<String, Object>> ss = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < c.getCount(); i++) {
			Map<String, Object> mm = new HashMap<String, Object>();
			c.moveToPosition(i);
			mm.put("num", Integer.toString(i+1));
			mm.put("altitude", c.getString(1));
			mm.put("latitude", c.getString(2));
			mm.put("longitude", c.getString(3));
			ss.add(mm);
		}
		c.moveToPosition(0);

		sa = new SimpleAdapter(LearnActivity.this, ss, R.layout.waypointview,
				new String[] { "num", "altitude", "latitude", "longitude" },
				new int[] { R.id.waypointnum, R.id.waypoint_altitude,
						R.id.waypoint_latitude, R.id.waypoint_longitude });
		mylist.setAdapter(sa);
	}
}
