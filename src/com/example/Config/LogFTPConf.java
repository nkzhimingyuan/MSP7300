package com.example.Config;

import android.content.Context;
import android.content.SharedPreferences;

public class LogFTPConf {
	 public static final String FTP_USER = "123";
	 public static final String FTP_PWD = "123";
	 private static  String FTP_RemotePath = "";
	 
     private static String FTP_ADDR = "192.168.1.1";
	 public static String Get_FTP_ADDR() 
	 {
		return FTP_ADDR;
	 }
	 
	 public static void Set_FTP_ADDR(String strServIP) 
	 {
		 FTP_ADDR = strServIP;
	 }
	 
	 public static String Get_FTP_RemotePath() 
	 {
		return FTP_RemotePath;
	 }
	 
	 public static void Set_FTP_RemotePath(String strUsrID) 
	 {
		 FTP_RemotePath = "/" +strUsrID+"/";
	 }
}
