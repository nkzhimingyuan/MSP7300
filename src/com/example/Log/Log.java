package com.example.Log;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.Adapter.LogControl;
import com.example.Config.FilePathConf;

import android.content.ContentValues;
import android.content.Context;
import android.telephony.TelephonyManager;

public class Log {

	public static Log log = null;
	private Usrsqlite sql=null;
	private DateFormat formatter = new SimpleDateFormat("yy-MM-dd*HH:mm:ss"); 
	private String DBName = null;
	private String ULLogname = null;
	public Log()
	{
		
	}
	/**
	 * 鍗曚緥
	 * */
	public static Log getInstance()
	{
		if(log == null)
		{
			log = new Log();
			return log;
		}
		else
		{
			return log;
		}
	}
	/**
     * @category 获取当前日志名称
     * @return null
     * @param  context:Context  
     * * @author zy
     */	
	public String GetCurrentLogName()
	{
		return DBName;
	}
	/**
     * @category 创建日志文件
     * @return null
     * @param  context:Context
     * @param  strDBName:鏁版嵁搴撳悕绉�     * @author zy
     */	
	public void CreateNewLog(Context context,String strDBName)
	{		
		DBName = strDBName;
		
		sql = new Usrsqlite(context,strDBName);
		try
		{
			String strCreateTable = "CREATE TABLE IF NOT EXISTS msp7300 (Avgsp VARCHAR(30),time VARCHAR(30)," +
					"RSRP VARCHAR(80)," +
					"SNR VARCHAR(10)," +
					"Delay VARCHAR(40)," +
					"event VARCHAR(14)," +
					"task VARCHAR(14)," +
					"ueid VARCHAR(30)," +
					"SucRate VARCHAR(20))";
			sql.execute(strCreateTable);
			LogControl.sgInst().sendlog("创建记录日志数据库表成功!\n");
		}
		catch(Exception ex)
		{
			LogControl.sgInst().sendlog("创建记录日志数据库表失败!\n");
		}

	}
	/**
     * @category 删除文件
     * @return void
     * @param  null 
     * @author zy
     */	
	public boolean DeleteLogFile()
	{
		try
		{
			String FilePath = FilePathConf.sgInst().GetDatabasePathDir()+DBName;
			File fTaskFile = new File(FilePath);//根据路径创建文件
		    if(fTaskFile.exists())//判断是否存在
		    {
		    	if(fTaskFile.isFile())//判断是否为文件
		    	{
		    		fTaskFile.delete();//删除文件
		    	}
		    }
			FilePath = FilePathConf.sgInst().GetDatabasePathDir()+DBName+"-journal";
			fTaskFile = new File(FilePath);//根据路径创建文件
		    if(fTaskFile.exists())//判断是否存在
		    {
		    	if(fTaskFile.isFile())//判断是否为文件
		    	{
		    		fTaskFile.delete();//删除文件
		    	}
		    }
		   
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
        return true;
	}
	/**
     * @category 创建日志文件
     * @return null
     * @param  context:Context
     * @param  strDBName:鏁版嵁搴撳悕绉�     * @author zy
     */	
	public void InsertARecord(String strUeid,String strtask,String strevent,String strRSRP,
			String strSNR,String strSucRate,String strDelay,String strAvgsp)
	{
            //long timestamp = System.currentTimeMillis();  
            //String time = formatter.format(new Date());   
            long ltime = new Date().getTime();  
            ltime = ltime/1000;
          
		    LogLayout loginfo = new LogLayout();
		   
			loginfo.setId(strUeid);
			loginfo.settask(strtask);
			loginfo.setevent(strevent);
			loginfo.settime(ltime+"");
			loginfo.setRSRP(strRSRP);
			loginfo.setSNR(strSNR);
			loginfo.setSucRate(strSucRate);
			loginfo.setDelay(strDelay);
			loginfo.setAvgsp(strAvgsp);
			
			write(loginfo);
	}
	/**
     * @category 写入数据库中
     * @return null
     * @param  loginfo:鏃ュ織淇℃伅
     * @author zy
     */	
	public void write(LogLayout loginfo)
	{		
		ContentValues values = new ContentValues();

		values.put("ueid", loginfo.getueId());
		values.put("task",loginfo.gettask() );
		values.put("event", loginfo.getevent());
		values.put("time", loginfo.gettime());
		values.put("RSRP", loginfo.getRSRP());
		values.put("SNR", loginfo.getSNR());
		values.put("SucRate",loginfo.getSucRate() );
		values.put("Delay", loginfo.getDelay());
		values.put("Avgsp", loginfo.getAvgsp());
		try
		{
			if(sql != null)
			{
				sql.insert("msp7300", values);	
				
				//sql.close();
			}
		}
		catch(Exception ex)
		{
			LogControl.sgInst().sendlog("写入数据库错误!"+ex.toString()+"\n");
			ex.printStackTrace();
		}
	}
	/**
     * @category 关闭数据库
     * @return null
     * @param  
     * @author zy
     */	
	public void CloseDBConnect()
	{
		if(sql != null)
		{			
			sql.close();
			sql = null;
		}
	}
	/**
     * @category 上传日志至服务器
     * @return null
     * @param  
     * @author zy
     */	
	public boolean UpLoadLog2Server()
	{
		LogControl.sgInst().sendlog("上传日志名称为:"+DBName+"\n");
		return UploadLog2Server.sgInst().UploadLog(DBName);
	}
}
