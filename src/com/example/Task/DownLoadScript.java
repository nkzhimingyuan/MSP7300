package com.example.Task;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.example.Adapter.LogControl;
import com.example.msp7300_client.R;

public class DownLoadScript {

	private Context mContext;

	// 杩斿洖鐨勫畨瑁呭寘url
	private String sptUrl = "http://172.16.20.213/7300/UsrUI/data/apk/MSP7300_client.apk";

	private Dialog noticeDialog;

	private Dialog downloadDialog;
	/* 涓嬭浇鍖呭畨瑁呰矾寰�*/
	private  String savePath = "/sdcard/updatedemo/";

	private  String saveFileName = savePath
			+ "UpdateDemoRelease.apk";

	private Thread downLoadThread;

	public static DownLoadScript DLsptclass = null;
	
	private boolean interceptFlag = false;
	
	private boolean bfinish = false;
	
	private boolean bError = false;
	/**
	 * 鍗曚緥
	 * 
	 * @param url
	 */
	public static DownLoadScript sgInst()
	{
		if(DLsptclass == null)
		{
			DLsptclass = new DownLoadScript();
		}
		
		return DLsptclass;
	}
	/**
	 * 璁剧疆鍙傛暟
	 * 
	 * @param url
	 */
	public void setParam(String url,String localPath,String strFileName)
	{
		savePath = localPath;
		saveFileName = localPath + strFileName;
		sptUrl = url;
	}
	/**
	 * 鑾峰彇涓嬭浇鎴愬姛澶辫触鎯呭喌
	 * 
	 * @param url
	 */
	public int GetDLpro()
	{
		if(bfinish)
		{
			return 0;
		}
		if(bError)
		{
			return -1;
		}
		return 1;
	}
	/**
	 * 涓嬭浇鑴氭湰
	 * 
	 * @param url
	 */
	private Runnable mdownsptRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				URL url = new URL(sptUrl);

				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();
				BufferedInputStream bin = new BufferedInputStream(conn.getInputStream());
				File file = new File(savePath);
				if (!file.exists()) {
					file.mkdir();
				}
				String apkFile = saveFileName;
				File ApkFile = new File(apkFile);
				//FileOutputStream fos = new FileOutputStream(ApkFile);
				OutputStream fos = new FileOutputStream(ApkFile);
				int count = 0;
				byte buf[] = new byte[1024];

				do {
					int numread = bin.read(buf);
					count += numread;
					if (numread <= 0) {
						// 下载完成通知安装						
						break;
					}
					fos.write(buf, 0, numread);
				} while (!interceptFlag);// 鐐瑰嚮鍙栨秷灏卞仠姝笅杞�

				fos.close();
				bin.close();
				bfinish = true;						
			} catch (Exception e) {
				bError = true;
				LogControl.sgInst().sendlog("下载任务失败,原因:"+e.toString()+"!\n");
				e.printStackTrace();
			} 
//			catch (IOException e) {
//				bError = true;
//				LogControl.sgInst().sendlog("下载任务失败,原因:"+e.toString()+"!\n");
//				e.printStackTrace();
//			}

		}
	};

	/**
	 * 涓嬭浇鑴氭湰
	 * 
	 * @param url
	 */
	public void downloadspt() {
		bfinish = false;		
		bError = false;
		downLoadThread = new Thread(mdownsptRunnable);
		downLoadThread.start();
	}

}
