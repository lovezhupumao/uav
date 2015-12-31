package com.service.uav;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.util.Log;
import dji.sdk.api.DJIDrone;
import dji.sdk.api.DJIError;
import dji.sdk.api.GroundStation.DJIGroundStationTypeDef.GroundStationResult;
import dji.sdk.interfaces.DJIExecuteResultCallback;
import dji.sdk.interfaces.DJIGroundStationExecuteCallBack;

public class CheckYawTask extends TimerTask {

	private static int checkYawTimes = 0;
	private static final String TAG = "CheckYawTask";
	 private final int SHOWTOAST = 1;
	 private Timer mCheckYawTimer;
	 private Handler handler;
	 CheckYawTask(Handler handler,Timer mCheckYawTimer){
		 this.handler=handler;
		 this.mCheckYawTimer=mCheckYawTimer;
	 }
	@Override
	public void run() {
		// TODO Auto-generated method stub
		 if(checkYawTimes >= 12){
             if(mCheckYawTimer != null){
                 Log.d(TAG ,"==========>mCheckYawTimer cancel!");
                 mCheckYawTimer.cancel();
                 mCheckYawTimer.purge();
                 mCheckYawTimer = null;
                 
                 new Thread()
                 {
                     public void run()
                     {
                             
                         DJIDrone.getDjiGroundStation().setAircraftYawSpeed(0, new DJIGroundStationExecuteCallBack(){

                             @Override
                             public void onResult(GroundStationResult result) {
                                 // TODO Auto-generated method stub
                                 
                             }
                             
                         });
                     }
                 }.start();
             }
             return;
         }
         
         checkYawTimes++;
         Log.d(TAG ,"==========>mCheckYawTimer checkYawTimes="+checkYawTimes);
         
         new Thread()
         {
             public void run()
             {
                     
                 DJIDrone.getDjiGroundStation().setAircraftYawSpeed(300, new DJIGroundStationExecuteCallBack(){

                     @Override
                     public void onResult(GroundStationResult result) {
                         // TODO Auto-generated method stub
                         
                     }
                     
                 });
                 
                 DJIDrone.getDjiCamera().startTakePhoto(new DJIExecuteResultCallback(){

                     @Override
                     public void onResult(DJIError mErr)
                     {
                         // TODO Auto-generated method stub

                         Log.d(TAG, "Start Takephoto errorCode = "+ mErr.errorCode);
                         Log.d(TAG, "Start Takephoto errorDescription = "+ mErr.errorDescription);
                         String result = "errorCode =" + mErr.errorCode + "\n"+"errorDescription =" + DJIError.getErrorDescriptionByErrcode(mErr.errorCode);
                         handler.sendMessage(handler.obtainMessage(SHOWTOAST, result));
                                                
                     }
                     
                 });
             }
         }.start();
	}

}
