package com.ypai.uav;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dji.sdk.api.DJIDrone;
import dji.sdk.api.DJIError;
import dji.sdk.api.DJIDroneTypeDef.DJIDroneType;
import dji.sdk.interfaces.DJIGeneralListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class Select_PageActivity extends DemoBaseActivity {
	private static final String TAG = "MainActivity";
	private int type = 0;
	private String[] list_str_title = { "基本功能", "热点环绕","位移控制","模仿飞行" };
	private String[] list_str_desec = { "云台、相机、飞行等控制", "M100热点环绕" ,"控制飞机前后左右运动一段距离","先用遥控器飞行记录轨迹，下次按照此轨迹飞行"};
	private final int SHOWDIALOG = 2;

	private Handler handler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case SHOWDIALOG:
				showMessage(getString(R.string.demo_activation_message_title),
						(String) msg.obj);
				Log.d("", "nonono");
				break;

			default:
				break;
			}
			return false;
		}
	});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_page);
		ArrayList<Map<String, Object>> arrayList = getarraylist();
		SimpleAdapter sampAdapter = new SimpleAdapter(this, arrayList,
				R.layout.demo_info_item, new String[] { "title", "desec" },
				new int[] { R.id.title, R.id.desc });
		ListView mListView = (ListView) findViewById(R.id.select_page_listView);
		mListView.setAdapter(sampAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = null;
				switch (position) {
				case 0:
					intent = new Intent(Select_PageActivity.this,MainActivity.class);
					break;
				case 1:
					intent=new Intent(Select_PageActivity.this,HotPointActivity.class);
					break;
				case 2:
					intent=new Intent(Select_PageActivity.this,PosActivity.class);
					break;
				case 3: 
					intent=new Intent(Select_PageActivity.this,LearnActivity.class);
					break;
				default:
					break;
				}
				Select_PageActivity.this.startActivity(intent);
			}

		});
		init_app();
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

	private ArrayList<Map<String, Object>> getarraylist() {
		// TODO Auto-generated method stub
		ArrayList<Map<String, Object>> arrayList = new ArrayList<Map<String, Object>>();

		int i;
		for (i = 0; i < list_str_title.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("title", list_str_title[i]);
			map.put("desec", list_str_desec[i]);
			arrayList.add(map);
		}
		return arrayList;
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
		onUnInitSDK();
	}

	public void onReturn(View view) {
		Log.d("TAG", "onReturn");
		this.finish();
	}

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
}
