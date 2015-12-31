package com.ypai.uav;

import java.util.Timer;
import java.util.TimerTask;

import dji.sdk.api.DJIDrone;
import dji.sdk.api.DJIDroneTypeDef.DJIDroneType;
import dji.sdk.interfaces.DJIDroneTypeChangedCallback;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class EnterActivity extends DemoBaseActivity {

	private static final String TAG = "EnterActivity";
	private Button mEnterbtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enteruav);
		mEnterbtn=(Button)findViewById(R.id.enterbtn);
		mEnterbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = null;

		        intent = new Intent(EnterActivity.this, Select_PageActivity.class);
		        EnterActivity.this.startActivity(intent);
			}
		});
		 DJIDrone.initAPPManager(this.getApplicationContext(), new DJIDroneTypeChangedCallback() {

	            @Override
	            public void onResult(DJIDroneType type)
	            {
	                               
	            }
	            
	        });
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Process.killProcess(Process.myPid());
	}
	 private static boolean first = false;
	   private Timer ExitTimer = new Timer();
	       
	   class ExitCleanTask extends TimerTask{

	           @Override
	           public void run() {
	               
	               Log.e("ExitCleanTask", "Run in!!!! ");
	               first = false;
	           }
	   }   
	    @Override
	    public boolean onKeyDown(int keyCode, KeyEvent event) {
	        // TODO Auto-generated method stub
	        if (keyCode == KeyEvent.KEYCODE_BACK) {
	            Log.d(TAG,"onKeyDown KEYCODE_BACK");
	            
	            if (first) {
	                first = false;
	                finish();
	            } 
	            else 
	            {
	                first = true;
	                Toast.makeText(EnterActivity.this, getText(R.string.press_again_exit), Toast.LENGTH_SHORT).show();
	                ExitTimer.schedule(new ExitCleanTask(), 2000);
	            }
	            
	            //finish();
	            return true;
	        }
	        return super.onKeyDown(keyCode, event);
	    }
}
