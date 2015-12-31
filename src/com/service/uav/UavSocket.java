package com.service.uav;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.TaskStackBuilder;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class UavSocket {

	private String ip;
	private int port;
	private Socket socket = null;
	private boolean flag;
	private ReceiveThread receiveThread = null;
	private String sendstr = null;
	private Handler mhandler;

	UavSocket(String ipstr, String portstr) {
		try {
			this.ip = ipstr;
			this.port = Integer.parseInt(portstr);

		} catch (Exception e) {
			// TODO: handle exception
			Log.e("111", "空");
		}

	}

	public void connect() {

		try {
			new Thread(connectThread).start();

		} catch (Exception e) {
			// TODO: handle exception
			flag = false;
		}

	}

	public boolean connect_statue() {

		return flag;
	}

	public boolean data_Send(String string) {

		try {
			sendstr = string;
		} catch (Exception e) {
			// TODO: handle exception
			Log.i("111", "string is null");
		}
		boolean sflag = false;

		try {

			new Thread(sendThread).start();

		} catch (Exception e) {
			// TODO: handle exception
			sflag = false;
		}

		return sflag;

	}

	public void data_receive(Handler handler) {

		mhandler = handler;
		if (socket.isConnected()) {
			receiveThread = new ReceiveThread(socket);
			receiveThread.start();
		} else {
			Log.i("111", "unconnected");
		}
	}

	public void close() {
		if(receiveThread != null){
			receiveThread.interrupt();
		}
		if(!socket.isClosed()){
			try {
				socket.shutdownInput();
				socket.shutdownOutput();
				socket.close();
				flag=false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	Runnable connectThread = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				// 初始化Scoket，连接到服务器

				socket = new Socket(ip, port);
				if (socket.isConnected()) {
					flag = true;
					System.out.println("----connected success----");
				} else {
					flag = false;

				}
				// 启动接收线程

			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				flag = false;
				e.printStackTrace();
				System.out.println("UnknownHostException-->" + e.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				flag = false;
				System.out.println("IOException" + e.toString());
			} catch (Exception e) {
				// TODO: handle exception
				flag = false;
			}
		}
	};
	// 发送消息的接口
	Runnable sendThread = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			byte[] sendBuffer = null;
			OutputStream outStream = null;
			try {
				sendBuffer = sendstr.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				outStream = socket.getOutputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				outStream.write(sendBuffer);
				Log.i("111", "write success");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.i("111", "write failed");
				e.printStackTrace();
			}
		}
	};

	// 接收线程
	private class ReceiveThread extends Thread {
		private InputStream inStream = null;

		private byte[] buffer;
		private String str = null;

		ReceiveThread(Socket socket) {
			try {
				inStream = socket.getInputStream();
				Log.i("111", "2");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.i("111", "e");
			}
		}

		@Override
		public void run() {
			while (flag) {
				buffer = new byte[512];
				try {
					inStream.read(buffer);
					Log.i("111", "3");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.i("111", "e2");
					e.printStackTrace();
				}
				try {
					str = new String(buffer, "UTF-8").trim();
					Log.i("111", str);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					Log.i("111", "e3");
					e.printStackTrace();
				}
				Message msg = new Message();
				msg.obj = str;
				mhandler.sendMessage(msg);
				
			}
		}
	}

}
