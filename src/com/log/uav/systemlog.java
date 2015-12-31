package com.log.uav;

import java.io.File;
import java.io.IOException;

import android.os.Environment;

public class systemlog {

	public static void sys_Log_Output() {
		
		try {
		    File filename = new File(Environment.getExternalStorageDirectory()+"/uavlogfile.txt"); 
		    if (filename.exists()&&filename.isFile()) {
					filename.delete();
				
			}
		    filename.createNewFile(); 
		    String cmd = "logcat -d -f "+filename.getAbsolutePath();
		    Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	}
}
