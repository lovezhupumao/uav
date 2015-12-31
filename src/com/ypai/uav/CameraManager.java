package com.ypai.uav;

import dji.sdk.api.DJIDrone;
import dji.sdk.api.DJIError;
import dji.sdk.api.Camera.DJICameraSettingsTypeDef.CameraCaptureMode;
import dji.sdk.api.Camera.DJICameraSettingsTypeDef.CameraPhotoSizeType;
import dji.sdk.interfaces.DJIExecuteResultCallback;
import android.R.integer;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
/*
 * 该类的作用是控制相机，如拍照、录像。
 */
public class CameraManager implements OnClickListener {

	private Context mcontext;
	private CameraCaptureMode mode;
	private Handler handler;
	private static final String TAG = "MainActivity";

	private static final int SHOWTOAST = 1;
	CameraManager(Context context, Handler handler) {
		mcontext = context;
		this.handler = handler;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		mode = CameraCaptureMode.Camera_Single_Capture;
		switch (v.getId()) {
		case R.id.startphotobtn:
			startCapturePhoto();
			break;

		case R.id.stopphotobtn:
			stopCapturePhoto();
			break;

		case R.id.startvediobtn:
			startVideo();
			break;

		case R.id.stopvediobtn:
			stopVideo();
			break;
		case R.id.setphotosizebtn:
			setPhotoSize();
			break;
		default:
			break;
		}
	}

	private void setPhotoSize() {
		
		new AlertDialog.Builder(mcontext).setTitle("选择照片尺寸").setItems(UavConstants.PhotoSize,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						DJIDrone.getDjiCamera().setCameraPhotoSize(CameraPhotoSizeType.values()[which],new DJIExecuteResultCallback(){

		                    @Override
		                    public void onResult(DJIError mErr)
		                    {
		                        // TODO Auto-generated method stub
		                        Log.d(TAG, "setCameraPhotoSize errorCode = "+ mErr.errorCode);
		                        Log.d(TAG, "setCameraPhotoSize errorDescription = "+ mErr.errorDescription);
		                        String result = "errorCode =" + mErr.errorCode + "\n"+"errorDescription =" + DJIError.getErrorDescriptionByErrcode(mErr.errorCode);
		                        handler.sendMessage(handler.obtainMessage(SHOWTOAST, result));
		                                               
		                    }
		                    
		                });
						dialog.dismiss();
					}

				}).show();
	}

	private void stopVideo() {
		DJIDrone.getDjiCamera().stopRecord(new DJIExecuteResultCallback() {

			@Override
			public void onResult(DJIError mErr) {
				// TODO Auto-generated method stub
				Log.d(TAG, "Stop Recording errorCode = " + mErr.errorCode);
				Log.d(TAG, "Stop Recording errorDescription = "
						+ mErr.errorDescription);
				String result = "errorCode ="
						+ mErr.errorCode
						+ "\n"
						+ "errorDescription ="
						+ DJIError.getErrorDescriptionByErrcode(mErr.errorCode);
				handler.sendMessage(handler
						.obtainMessage(SHOWTOAST, result));

			}

		});
	}

	private void startVideo() {
		DJIDrone.getDjiCamera().startRecord(new DJIExecuteResultCallback() {

			@Override
			public void onResult(DJIError mErr) {
				// TODO Auto-generated method stub
				Log.d(TAG, "Start Recording errorCode = " + mErr.errorCode);
				Log.d(TAG, "Start Recording errorDescription = "
						+ mErr.errorDescription);
				String result = "errorCode ="
						+ mErr.errorCode
						+ "\n"
						+ "errorDescription ="
						+ DJIError
								.getErrorDescriptionByErrcode(mErr.errorCode);
				handler.sendMessage(handler
						.obtainMessage(SHOWTOAST, result));

			}

		});
	}

	private void stopCapturePhoto() {
		DJIDrone.getDjiCamera().stopTakePhoto(
				new DJIExecuteResultCallback() {

					@Override
					public void onResult(DJIError mErr) {
						// TODO Auto-generated method stub
						Log.d(TAG, "Stop Takephoto errorCode = "
								+ mErr.errorCode);
						Log.d(TAG, "Stop Takephoto errorDescription = "
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

	private void startCapturePhoto() {
		new AlertDialog.Builder(mcontext)
				.setTitle("选择拍照模式")
				.setSingleChoiceItems(
						new String[] { "single", "burst", "time" }, 0,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
								int which) {
								switch (which) {
								case 0:
									mode = CameraCaptureMode.Camera_Single_Capture;
									break;
								case 1:
									mode = CameraCaptureMode.Camera_Multi_Capture;
									break;
								case 2:
									mode = CameraCaptureMode.Camera_Continous_Capture;
									break;
								default:
									break;
								}
								Log.i("", mode.toString());
								DJIDrone.getDjiCamera()
										.startTakePhoto(
												mode,
												new DJIExecuteResultCallback() {

													@Override
													public void onResult(DJIError mErr) {
														// TODO
														// Auto-generated
														// method stub
														Log.d(TAG,
																"Set Action errorCode = "
																		+ mErr.errorCode);
														Log.d(TAG,
																"Set Action errorDescription = "
																		+ mErr.errorDescription);
														String result = "errorCode ="
																+ mErr.errorCode
																+ "\n"
																+ "errorDescription ="
																+ DJIError
																		.getErrorDescriptionByErrcode(mErr.errorCode);
														handler.sendMessage(handler
																.obtainMessage(
																		SHOWTOAST,
																		result));
													}

												});
								dialog.dismiss();

							}
						}).show();
	}

}
