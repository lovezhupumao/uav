package com.log.uav;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.R.string;
import android.os.Environment;
import android.util.Log;


public class LogFile {

	public static void writelog(String str) {
		final String PATH =Environment.getExternalStorageDirectory()  
                .getAbsolutePath(); 
		final String FILENAME = "/zhupumao.txt";
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			File path = new File(PATH);
			// �ļ�
			File f = new File(PATH + FILENAME);
			// Ŀ¼
			if (!f.exists()) { 
		        try { 
		            f.createNewFile(); 
		        }catch (IOException e){ 
		            e.printStackTrace(); 
		      } 
		   } 
			try {  
				
			    //�ڶ�������������˵�Ƿ���append��ʽ�������  
			    BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));  
			    Date now = new Date(); 
			    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//���Է�����޸����ڸ�ʽ
                String hehe = dateFormat.format( now ); 
			    bw.write(hehe+":"+str+"\n");  
			    bw.flush();  
			    
			    Log.i("info", "success") ;
			} catch (Exception e) {  
			    e.printStackTrace();  
			} 
		}
		else {
			Log.w("WARN", "NO VALID");
		}

	}
	
}