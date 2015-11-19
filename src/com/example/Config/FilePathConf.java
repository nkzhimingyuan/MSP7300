package com.example.Config;

import java.io.File;

import android.os.Environment;

public class FilePathConf {

	public static FilePathConf filepathconf = null;
	private String strScriptPath = null;
	private String strDBPath = null;
	private String strmsp7300Path = null;
    /**
     * @category 构造函数
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
     * @category 单例
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
     * @category 初始化存储文件路径
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
     * @category 获取msp7300数据存储路径
     * @return msp7300数据存储路径
     * @param  null 
     * @author zy
     */	
	public String Getmsp7300DataPathDir()
	{
		return strmsp7300Path;
	}
    /**
     * @category 获取msp7300脚本文件存储路径
     * @return msp7300脚本文件存储路径
     * @param  null 
     * @author zy
     */	
	public String GetScriptPathDir()
	{
		return strScriptPath;
	}
    /**
     * @category 获取msp7300数据库文件存储路径
     * @return msp7300数据库文件存储路径
     * @param  null 
     * @author zy
     */	
	public String GetDatabasePathDir()
	{
		return strDBPath;
	}
}
