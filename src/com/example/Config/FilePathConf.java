package com.example.Config;

import java.io.File;

import android.os.Environment;

public class FilePathConf {

	public static FilePathConf filepathconf = null;
	private String strScriptPath = null;
	private String strDBPath = null;
	private String strmsp7300Path = null;
    /**
     * @category ���캯��
     * @return void
     * @param  null 
     * @author zy
     */	
	public FilePathConf()
	{
		strmsp7300Path = Environment.getExternalStorageDirectory().getPath().toString()+"/msp7300data/";
		strScriptPath = Environment.getExternalStorageDirectory().getPath().toString()+"/msp7300data/script/";
		strDBPath = Environment.getExternalStorageDirectory().getPath().toString()+"/msp7300data/database/";
	}
    /**
     * @category ����
     * @return void
     * @param  null 
     * @author zy
     */	
	public static FilePathConf sgInst()
	{
	    	if(filepathconf == null)
	    	{
	    		filepathconf = new FilePathConf();
	    	}
	    	return filepathconf;
	}
    /**
     * @category ��ʼ���洢�ļ�·��
     * @return void
     * @param  null 
     * @author zy
     */	
	public void InitFilePath()
	{
		
		File file = new File(strmsp7300Path);
		if(!file.exists())
		{
			file.mkdir();
		}
	
		file = new File(strScriptPath);
		if(!file.exists())
		{
			file.mkdir();
		}
		
		file = new File(strDBPath);
		if(!file.exists())
		{
			file.mkdir();
		}
	}
    /**
     * @category ��ȡmsp7300���ݴ洢·��
     * @return msp7300���ݴ洢·��
     * @param  null 
     * @author zy
     */	
	public String Getmsp7300DataPathDir()
	{
		return strmsp7300Path;
	}
    /**
     * @category ��ȡmsp7300�ű��ļ��洢·��
     * @return msp7300�ű��ļ��洢·��
     * @param  null 
     * @author zy
     */	
	public String GetScriptPathDir()
	{
		return strScriptPath;
	}
    /**
     * @category ��ȡmsp7300���ݿ��ļ��洢·��
     * @return msp7300���ݿ��ļ��洢·��
     * @param  null 
     * @author zy
     */	
	public String GetDatabasePathDir()
	{
		return strDBPath;
	}
}
