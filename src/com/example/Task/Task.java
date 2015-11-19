package com.example.Task;

import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.util.EncodingUtils;

import android.annotation.SuppressLint;
import android.app.DownloadManager.Request;

import com.example.Adapter.InterfaceSPMonitor;
import com.example.Adapter.LogControl;
import com.example.Adapter.UESystemInfo;
import com.example.Adapter.UESystemInfo.NetConnectReceiver;
import com.example.Config.FilePathConf;
import com.example.Constantdata.ConstantData;
import com.example.Log.Log;
import com.example.Log.LogLayout;

import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.NetworkInfo.State;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.StrictMode;
import android.telephony.TelephonyManager;

public class Task {	
	private String strTaskFile = null;
	private String strTaskfileName = null;
	private String strScriptInfo = null;
	private String[] strTasksplit = null;
	private Context mcontext = null;
	private DownloadManager myDownloadManager;
    private ArrayList<FTP> FtpTreadList = new ArrayList<FTP>();
    private ArrayList<FTP> FtpUTreadList = new ArrayList<FTP>();
    private DateFormat formatter = new SimpleDateFormat("_yy-MM-dd_HH-mm-ss"); 
    private boolean bTaskDLSuc =false;//task文件是否下载成功
    private DLManagerListen dlmanagerlis = null;//监控DLManager状态
	public Task(Context context){		
		mcontext = context;
		
 		IntentFilter intentFilterDL = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
 		dlmanagerlis = new DLManagerListen();
		context.registerReceiver(dlmanagerlis,intentFilterDL);
	}
    /**
     * @category 下载任务文件 
     * @return void
     * @param  null 
     * @author zy
     */	
	public boolean DownloadTask(String url,String File)
	{
		boolean bDLError = false;
		while(!bDLError)
		{
			try{
//				myDownloadManager = (DownloadManager) mcontext.getSystemService(Context.DOWNLOAD_SERVICE);
				
				if(url != null){
					bTaskDLSuc = false;
					LogControl.sgInst().sendlog("下载任务url: "+url+"\n");
					String strLocalTaskfile = FilePathConf.sgInst().GetScriptPathDir()+File;
					DeleteTaskFile(strLocalTaskfile);//若脚本已存在,则删除
//					Uri uri = Uri.parse(url);
//					DownloadManager.Request request = new Request(uri);
//					request.setDestinationInExternalPublicDir("/msp7300data/script/", File);
//					long reference = myDownloadManager.enqueue(request);
					DownLoadScript.sgInst().setParam(url, FilePathConf.sgInst().GetScriptPathDir(), File);
					DownLoadScript.sgInst().downloadspt();
					int nDlflag = DownLoadScript.sgInst().GetDLpro();
					bDLError = true;
					strTaskfileName = File;
					while(!bTaskDLSuc)//等待文件下载成功
					{
						Thread.sleep(1000);
						nDlflag = DownLoadScript.sgInst().GetDLpro();
						switch(nDlflag)
						{
						case -1:
							bDLError = false;	
							bTaskDLSuc = true;//跳出循环
							LogControl.sgInst().sendlog("正在准备重新下载脚本!\n");
							break;
						case 0:
							bTaskDLSuc = true;//跳出循环
							break;
						case 1:
							break;
						default:
							break;
						}						
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				LogControl.sgInst().sendlog("下载任务失败,原因为: "+e.toString()+"!\n");
				bDLError = false;
				
			}
			try{
				Thread.sleep(20000);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		LogControl.sgInst().sendlog("下载任务 "+File+" 文件成功!\n");
        return true;
	}
	/**
     * @category 监听任务文件是否下载完成
     * @return void
     * @param  null 
     * @author zy
     */	
	public class DLManagerListen extends BroadcastReceiver
	{
 		@Override
 		public void onReceive(Context context, Intent intent) {
 			// TODO Auto-generated method stub
  			String action = intent.getAction();
  			if(action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
  			{
  				bTaskDLSuc = true;
  			}
 		}
  	};
	/**
     * @category 删除文件
     * @return void
     * @param  null 
     * @author zy
     */	
	public void DeleteTaskFile(String FilePath)
	{
		try
		{
			File fTaskFile = new File(FilePath);//根据路径if(ULLogname == null)
			{
				
			}
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
		}

	}
    /**
     * @category 执行本地任务 
     * @return void
     * @param  null 
     * @author zy
     */	
	public void RunLocalScriptTask(String strScriptName)
	{
		strTaskfileName = strScriptName;
		StartTask();
	}
    /**
     * @category 开始任务 
     * @return void
     * @param  null 
     * @author zy
     */	
	public void StartTask()
	{
		  String time = formatter.format(new Date());
		  String strImei = UESystemInfo.sgInst().GetIMEI();
		  String strLogName = "UE"+strImei+"_"+time;
		  //String strLogName = "UE";
		  //String strLogName = "UE"+strImei;
		Log.getInstance().CreateNewLog(mcontext,strLogName);//创建日志数据库用于存储数据库记录文件
		
		strScriptInfo = ReadtxtInfo();
		TaskInfo.SgInst().SetCurrentTask(strTaskfileName);
		TaskInfo.SgInst().SetTaskONFlag(true);
		LogControl.sgInst().sendlog("开始执行任务 "+strTaskfileName+"!\n");
		if(strScriptInfo != null && strScriptInfo.contains(";"))
		{
			strTasksplit = strScriptInfo.split(";");
			String strCmd = null;
			int nCmdtype;
			int nLoopNum = GetLoopNum(strTasksplit[0]);
			LogControl.sgInst().sendproBar(0);
			for(int nloopCnt = 0;nloopCnt < nLoopNum;nloopCnt++)//总循环次数
			{
				
				for(int nCnt =1; nCnt< strTasksplit.length; nCnt++)//命令循环
				{
					strCmd = strTasksplit[nCnt];//获取命令
					nCmdtype = ParseCmdType(strCmd);//解析命令类型
					
					switch(nCmdtype)
					{
					    case ConstantData.DL_TASK:
					    	RunDLCmd(strCmd);
						    break;
					    case ConstantData.STOPDL_TASK:
					    	RunStopDLCmd();
						    break;
					    case ConstantData.UL_TASK:
					    	RunULCmd(strCmd);
						    break;
					    case ConstantData.STOPUL_TASK:
					    	RunStopULCmd();
						    break;
					    case ConstantData.CS_TASK:
					    	RunCSCmd(strCmd);
						    break;
					    case ConstantData.AIR_TASK:
					    	RunAirCmd(strCmd);
						    break;
					    case ConstantData.HTTP_TASK:
					    	RunHttpCmd(strCmd);
						    break;
					    case ConstantData.PING_TASK:
					    	RunPingCmd(strCmd);
						    break;
					    case ConstantData.DELAY_TASK:
					    	RunDelayCmd(strCmd);
						    break;
						default:
						    break;
					}
				}
				int probarvalue = (int)((nloopCnt+1)*100/nLoopNum);
				LogControl.sgInst().sendproBar(probarvalue);
			}	
			Log.getInstance().CloseDBConnect();
			TaskInfo.SgInst().SetTaskONFlag(true);
			LogControl.sgInst().sendproBar(100);
			TaskInfo.SgInst().SetCurrentCmd("NULL");
			TaskInfo.SgInst().SetCurrentTask("NULL");
		}
		else
		{
			TaskInfo.SgInst().SetCurrentTask("NULL");
			return;
		}
	}

    /**
     * @category 读取txt脚本中的信息 
     * @return txt信息
     * @param  null 
     * @author zy
     */	
	private String ReadtxtInfo()
	{
		// 读在/mnt/sdcard/目录下面的文件 
		
		String res = null;
		try{
			LogControl.sgInst().sendlog("开始读取任务文件信息!\n");
			String strLocalTaskfile = FilePathConf.sgInst().GetScriptPathDir()+strTaskfileName;
			LogControl.sgInst().sendlog("读取的本地文件路径为:"+strLocalTaskfile+"\n");
			FileInputStream fin =  new FileInputStream(strLocalTaskfile);
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			res = EncodingUtils.getString(buffer,"UTF-8");
			fin.close();
			LogControl.sgInst().sendlog("读取任务文件信息完成!\n");		
		}
        catch(Exception e)
        {
        	LogControl.sgInst().sendlog("读取任务文件出现异常!\n");
             e.printStackTrace();       	
        }
		//res = "Loop(1);DL(175.1.1.50,123,123,./download/7#1);Sleep(10);";
        return res; 
	}
	 /**
     * @category 解析命令类型
     * @return void
     * @param  null 
     * @author zy
     */	
	private int ParseCmdType(String strcmd)
	{
		if(strcmd.contains("DL"))
		{
			if(strcmd.contains("StopDL"))
			{
				return ConstantData.STOPDL_TASK;
			}
			else
			{
				return ConstantData.DL_TASK;
			}			
		}
		else if(strcmd.contains("UL"))
		{
			if(strcmd.contains("StopUL"))
			{
				return ConstantData.STOPUL_TASK;
			}
			else
			{
				return ConstantData.UL_TASK;
			}		
		}
		else if(strcmd.contains("Air"))
		{
			return ConstantData.AIR_TASK;
		}
		else if(strcmd.contains("Http"))
		{
			return ConstantData.HTTP_TASK;
		}
		else if(strcmd.contains("CS"))
		{
			return ConstantData.CS_TASK;
		}
		else if(strcmd.contains("Ping"))
		{
			return ConstantData.PING_TASK;
		}
		else if(strcmd.contains("Sleep"))
		{
			return ConstantData.DELAY_TASK;
		}
		else
		{
			return -1;
		}
	}
	
	/**
     * @category 解析循环次数
     * @return 循环次数
     * @param  null 
     * @author zy
     */	
	private int GetLoopNum(String strloopcmd)
	{
		try
		{	
			LogControl.sgInst().sendlog("正在获取循环次数!\n");
			String[] strsplit = strloopcmd.split("\\(|\\)");
			if(strsplit.length > 1)
			{
				LogControl.sgInst().sendlog("任务循环次数获取成功!循环次数为"+strsplit[1]+"\n");
				return Integer.parseInt(strsplit[1]);
			}
		}catch(Exception e)
		{
			LogControl.sgInst().sendlog("获取循环次数出现异常!\n");
			e.printStackTrace();
		}
		return 1;
	}
	/**
     * @category 运行中下载命令
     * @return null
     * @param  strcmd:命令名称 
     * @author zy
     */	
	private void RunDLCmd(String strCmd)
	{
		String[] strDLParam = strCmd.split("\\(|\\)|\\,|\\#");
		
		if(strDLParam.length < 6)
		{
			return;
		}
		else
		{
			TaskInfo.SgInst().SetCurrentCmd("Download");
			LogControl.sgInst().sendlog("开始进行下载业务!\n");
    	    String strFTPIP = strDLParam[1];
    	    String strFTPUser = strDLParam[2];
    	    String strFTPPwd = strDLParam[3];
    	    String strFileTmp = strDLParam[4];
    	    int nPos = strFileTmp.lastIndexOf("/");
    	    String strFTPFile = strFileTmp.substring(nPos+1);
    	    String strFTPRemotePath = strFileTmp.substring(0,nPos+1);   
    	    int nTreadCnt = Integer.parseInt(strDLParam[5]);
    	    LogControl.sgInst().sendlog("下载线程数:"+nTreadCnt+"下载用户名:"+strFTPUser+"下载密码:"+strFTPPwd+"下载文件:"+strFTPFile+"远程路径为:"+strFTPRemotePath+"\n");
    	    for(int n =0 ; n<nTreadCnt; n++)
    	    {
    	      DLThread FTPThread = new DLThread(strFTPIP,strFTPUser,strFTPPwd,strFTPFile,strFTPRemotePath);		  
    	      FTPThread.start();
    	    }
		}
	}
	/**
     * @category 运行中上传命令
     * @return null
     * @param  strcmd:命令名称 
     * @author zy
     */	
	private void RunULCmd(String strCmd)
	{
		String[] strDLParam = strCmd.split("\\(|\\)|\\,|\\#");
		
		if(strDLParam.length < 7)
		{
			return;
		}
		else
		{
			LogControl.sgInst().sendlog("开始执行上传业务!\n");
			TaskInfo.SgInst().SetCurrentCmd("Upload");
    	    String strFTPIP = strDLParam[1];
    	    String strFTPUser = strDLParam[2];
    	    String strFTPPwd = strDLParam[3];
    	    String strFTPFile = strDLParam[4]; 	  
    	    int nTreadCnt = Integer.parseInt(strDLParam[5]);
    	    String strFTPRemotePath =  strDLParam[6]; 
    	    for(int n =0 ; n<nTreadCnt; n++)
    	    {
    	      DLThread FTPThread = new DLThread(strFTPIP,strFTPUser,strFTPPwd,strFTPFile,strFTPRemotePath);		  
    	      FTPThread.start();
    	    }
		}
	}
	/**
     * @category 运行停止下载命令
     * @return null
     * @param  null
     * @author zy
     */	
	private void RunStopDLCmd()
	{
    	try
    	{    
    		LogControl.sgInst().sendlog("执行停止下载业务!\n");
			for(int i=0 ;i<FtpTreadList.size();i++)
			{
				((FTP) FtpTreadList.get(i)).SetFTPStop();
			}
			
			FtpTreadList.clear();
			TaskInfo.SgInst().SetCurrentCmd("StopDownload");
    	}
    	catch (Exception e)
    	{
    		LogControl.sgInst().sendlog("执行停止下载业务异常!\n");
    		e.printStackTrace();
    	}
	}
	/**
     * @category 运行停止上传命令
     * @return null
     * @param  null
     * @author zy
     */	
    public void RunStopULCmd()
    {

    	try
    	{      
    		LogControl.sgInst().sendlog("执行停止上传业务!\n");
			for(int i=0 ;i<FtpUTreadList.size();i++)
			{
				((FTP) FtpUTreadList.get(i)).SetFTPUStop();

			}
	
		    FtpUTreadList.clear();
		    TaskInfo.SgInst().SetCurrentCmd("StopUpload");//设置当前执行命令信息
    	}
    	catch (Exception e)
    	{
    		LogControl.sgInst().sendlog("执行停止业务异常!\n");
    		e.printStackTrace();
    	}

    }
	/**
     * @category 运行CS命令
     * @return null
     * @param  strCmd:命令参数
     * @author zy
     */	
    public void RunCSCmd(String strCmd)
    {
		String[] strDLParam = strCmd.split("\\(|\\)|\\,");
		
		if(strDLParam.length < 4)
		{
			return;
		}
		else
		{
			try{
				TaskInfo.SgInst().SetCurrentCmd("CS");//设置当前执行命令信息
	    	    String strTelCnt = strDLParam[1];
	    	    String strDialNum = strDLParam[2];
	    	    String strDialTime = strDLParam[3];
	    	    String strDialDelaytime = strDLParam[4]; 
	    	    LogControl.sgInst().sendlog("开始进行CS业务!拨打号码:"+strDialNum+"拨打时长:"+strDialTime+"拨打次数:"+strTelCnt+"\n");
	    	    CS.sgInst().StartCStest(mcontext,strDialNum,strTelCnt,strDialTime,strDialDelaytime);
			}
	        catch(Exception e)
	        {
	        	return;
	        } 
		}
    }
	/**
     * @category 运行Air命令
     * @return null
     * @param  strCmd:命令参数
     * @author zy
     */	
    public void RunAirCmd(String strCmd)
    {
		String[] strDLParam = strCmd.split("\\(|\\)|\\,");
		
		if(strDLParam.length < 3)
		{
			return;
		}
		else
		{
			try
			{
				TaskInfo.SgInst().SetCurrentCmd("Airtest");//设置当前执行命令信息
	    	    int nAirTestCnt = Integer.parseInt(strDLParam[2]);
	    	    int nAirTime = Integer.parseInt(strDLParam[1]);
	    	    LogControl.sgInst().sendlog("开始进行飞行模式测试!飞行时长:"+nAirTime+"飞行次数:"+nAirTestCnt+"\n");
	    	    Air.sgInst().StartAirtest(mcontext, nAirTestCnt, nAirTime);		
			}
            catch(Exception e)
            {
            	return;
            } 
		}
    }
	/**
     * @category 运行Http命令
     * @return null
     * @param  strCmd:命令参数
     * @author zy
     */	
    public void RunHttpCmd(String strCmd)
    {
	    String[] strDLParam = strCmd.split("\\(|\\)|\\,");
		boolean bResult = false;
   	    Date httpStartTime;//记录Http下载开始时间
	    Date httpEndTime;//记录Http下载结束时间
	    int nTimeoutSet = 0;
		if(strDLParam.length < 2)
		{
			return;
		}
		else
		{
			try
			{
        		Log.getInstance().InsertARecord(TaskInfo.SgInst().GetIMEI(), 
        				"Http Download",
        				"start",
        				TaskInfo.SgInst().GetCurrentRSRP(),
        				TaskInfo.SgInst().GetCurrentSNR(), 
        				"null",
        				"null",
        				"null");
        		
				TaskInfo.SgInst().SetCurrentCmd("Http");//设置当前执行命令信息
	    	    String  strUrl = strDLParam[1];
	    	    nTimeoutSet = Integer.parseInt(strDLParam[2]);
	    	    LogControl.sgInst().sendlog("开始进行Http业务!超时时间为:"+nTimeoutSet+"\n");
	    	    httpStartTime = new Date(System.currentTimeMillis());
	    	    Http http = new Http(strUrl,nTimeoutSet);
	    	    try
	    	    {
	    	    	bResult = http.DownLoad();
	    	    }
	    	    catch(Exception ex)
	    	    {
	        		Log.getInstance().InsertARecord(TaskInfo.SgInst().GetIMEI(), 
	        				"Http Download",
	        				"Exception",
	        				TaskInfo.SgInst().GetCurrentRSRP(),
	        				TaskInfo.SgInst().GetCurrentSNR(), 
	        				http.GetDLpercent(),
	        				"null",
	        				"null");
	        		
	    	    	ex.printStackTrace();
	    	    	return ;
	    	    }
	    	    String strHttpDelay = null;
            	if(bResult)
            	{
	            	httpEndTime = new Date(System.currentTimeMillis());         
		    	    long lRealtime = httpEndTime.getTime() - httpStartTime.getTime();	
		    	    strHttpDelay = lRealtime +"";
            	}
            	else
            	{
            		strHttpDelay = nTimeoutSet*1000+"";
		    		
            	}
            	String strPercent = http.GetDLpercent();
            	
        		Log.getInstance().InsertARecord(TaskInfo.SgInst().GetIMEI(), 
        				"Http Download",
        				"End",
        				TaskInfo.SgInst().GetCurrentRSRP(),
        				TaskInfo.SgInst().GetCurrentSNR(), 
        				strPercent,
        				strHttpDelay,
        				"null");
			}
            catch(Exception e)
            {
        		Log.getInstance().InsertARecord(TaskInfo.SgInst().GetIMEI(), 
        				"Http Download",
        				"Exception",
        				TaskInfo.SgInst().GetCurrentRSRP(),
        				TaskInfo.SgInst().GetCurrentSNR(), 
        				"null",
        				"null",
        				"null");
            	return;
            } 
		}
    }
	/**
     * @category 运行Ping命令
     * @return null
     * @param  strCmd:命令参数
     * @author zy
     */	
    public void RunPingCmd(String strCmd)
    {
    	String[] strDLParam = strCmd.split("\\(|\\)|\\,");
		boolean bwait4End = false;//是否等待命令执行完成
    	if(strCmd.contains("-t"))
    	{
    		bwait4End = true;//等待命令执行完成
    	}
    	
		if(strDLParam.length < 2)
		{
		LogControl.sgInst().sendlog("执行Ping业务失败!原因为命令参数有误.命令为:"+strCmd+"\n");
			return;
		}
		else
		{
			try
			{
        		Log.getInstance().InsertARecord(TaskInfo.SgInst().GetIMEI(), 
        				"Ping",
        				"start",
        				TaskInfo.SgInst().GetCurrentRSRP(),
        				TaskInfo.SgInst().GetCurrentSNR(), 
        				"null",
        				"null",
        				"null");
        		
				TaskInfo.SgInst().SetCurrentCmd("Ping");//设置当前执行命令信息
				if(bwait4End)//等待ping命令执行完成
				{
					 String strPingcmd = strDLParam[2];
					 LogControl.sgInst().sendlog("开始进行Ping业务!命令为:"+strPingcmd+"\n");
			    	 Ping ping = new Ping();//执行Ping命令
			    	 ping.StartPing(strPingcmd);
			    	 while(true)
			    	 {
			    		 if(ping.GetPingFinishstate())
			    		 {
			    			 break;
			    		 }
			    		 else
			    		 {
			    			 Thread.sleep(2000);
			    		 }
			    	 }
			    	 
				}
				else
				{
		    	    String strPingcmd = strDLParam[1];
		    	    LogControl.sgInst().sendlog("开始进行Ping业务!命令为:"+strPingcmd+"\n");
		    	    Ping ping = new Ping();//执行Ping命令
		    	    ping.StartPing(strPingcmd);
				}

			}
            catch(Exception e)
            {
        		Log.getInstance().InsertARecord(TaskInfo.SgInst().GetIMEI(), 
        				"Ping",
        				"Exception",
        				TaskInfo.SgInst().GetCurrentRSRP(),
        				TaskInfo.SgInst().GetCurrentSNR(), 
        				"null",
        				"null",
        				"null");
            	return;
            } 
		}
    }
	/**
     * @category 运行Http命令
     * @return null
     * @param  strCmd:命令参数
     * @author zy
     */	
    public void RunDelayCmd(String strCmd)
    {
       	String[] strDLParam = strCmd.split("\\(|\\)");
		
    		if(strDLParam.length < 2)
    		{
    			return;
    		}
    		else
    		{
    			try
    			{
    				TaskInfo.SgInst().SetCurrentCmd("Delay");//设置当前执行命令的信息
    	    	    int ntimeout = Integer.parseInt(strDLParam[1]);
    	    	    LogControl.sgInst().sendlog("开始执行时延命令!时长为:"+ntimeout+"s\n");
                    Thread.sleep(ntimeout*1000);
    			}
                catch(Exception e)
                {
                	return;
                } 
    		}
    }

	/**
     * @category 下载线程
     * @return null
     * @param   
     * @author zy
     */	
	 class DLThread extends Thread {   
		 
		 private String strFTPIP=null;
		 private String strFTPUser=null;
		 private String strFTPPwd=null;
		 private String strFTPFile=null;
		 private String strFTPRemotePath = null;
		 
		 
	        public DLThread(String strIP,String strUsr,String strPwd,String strFile,String strRemote) {	        	
	   		 strFTPIP   = strIP;
			 strFTPUser = strUsr;
			 strFTPPwd  = strPwd;
			 strFTPFile = strFile;
			 strFTPRemotePath = strRemote;
	            /*解决Android2.3以后版本网络连接错误*/
	            if (android.os.Build.VERSION.SDK_INT > 9) {
	                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	                StrictMode.setThreadPolicy(policy);
	            }
	        }
	        @Override
	        public void run() {
	                	try
	                	{           		
	                		Log.getInstance().InsertARecord(TaskInfo.SgInst().GetIMEI(), 
							                				"FTP Download",
							                				"start",
							                				TaskInfo.SgInst().GetCurrentRSRP(),
							                				TaskInfo.SgInst().GetCurrentSNR(), 
							                				"null",
							                				"null",
							                				"null");
	                		 LogControl.sgInst().sendlog("进入下载线程!IP:"+strFTPIP+"USer:"+strFTPUser+"Pwd:"+strFTPPwd+" RemotePath:"+strFTPRemotePath+"\n");
	                		FTP FTPClient  = new FTP(strFTPIP,strFTPUser,strFTPPwd);
	        			    FtpTreadList.add(FTPClient);//用于关闭停止下载线程
	        	            FTPClient.openConnect();        	            
	        	            LogControl.sgInst().sendlog("进入下载线程!连接服务器成功!\n");
	                        Result re = FTPClient.download(strFTPRemotePath, strFTPFile, Environment.getExternalStorageDirectory().getPath().toString());
	                        LogControl.sgInst().sendlog("download执行完成!\n");
	                        String time = null;
	                        if(re != null)
	                        {
	                        	time = re.getTime();//获取时延
	                        }
	                        else
	                        {
	                        	time = "unkown";                        	
	                        }
	                        String percent = FTPClient.getDownloadedpercent();//获取已下载的百分比
	                		Log.getInstance().InsertARecord(TaskInfo.SgInst().GetIMEI(), 
	                				"FTP Download",
	                				"End",
	                				TaskInfo.SgInst().GetCurrentRSRP(),
	                				TaskInfo.SgInst().GetCurrentSNR(), 
	                				percent,
	                				time,
	                				InterfaceSPMonitor.SgInst().GetDLsp());
	        	    		FTPClient.closeConnect();
	                	}
	                	catch (Exception e)
	                	{
	                		Log.getInstance().InsertARecord(TaskInfo.SgInst().GetIMEI(), 
	                				"FTP Download",
	                				"Exception",
	                				TaskInfo.SgInst().GetCurrentRSRP(),
	                				TaskInfo.SgInst().GetCurrentSNR(), 
	                				"null",
	                				"null",
	                				"null");
	                		LogControl.sgInst().sendlog("下载线程出现异常!请检查网络或参数!\n");
	                		e.printStackTrace();
	                	}
	               
	                

	            super.run();
	        }
	         	        
	        public void exit() {            
	           
	        }		 
	 }

		/**  
		 *   
		 * @Project: DounLoad  
		 * @Desciption: 上传线程
		 * @Author: zy
		 * @Date: 2015-08-21
		 */  
	 class ULThread extends Thread {
		 
		 private String strFTPIP=null;
		 private String strFTPUser=null;
		 private String strFTPPwd=null;
		 private String strFTPUFile=null;
		 private String strFTPURemotePath = null;
		 
	        public ULThread(String strIP,String strUsr,String strPwd,String strFile,String strRemote) {
		   		 strFTPIP   = strIP;
				 strFTPUser = strUsr;
				 strFTPPwd  = strPwd;
				 strFTPUFile = strFile;
				 strFTPURemotePath = strRemote;
	        	/*解决Android2.3以后版本网络连接错误*/
	            if (android.os.Build.VERSION.SDK_INT > 9) {
	                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	                StrictMode.setThreadPolicy(policy);
	            }
	        }
	        @Override
	        public void run() {
	                    try
	                	{     
	                		Log.getInstance().InsertARecord(TaskInfo.SgInst().GetIMEI(), 
	                				"FTP Upload",
	                				"start",
	                				TaskInfo.SgInst().GetCurrentRSRP(),
	                				TaskInfo.SgInst().GetCurrentSNR(), 
	                				"null",
	                				"null",
	                				"null");
	        			    FTP FTPClient  = new FTP(strFTPIP,strFTPUser,strFTPPwd);
	        			    FtpUTreadList.add(FTPClient);
	        	            FTPClient.openConnect();
        	    		    //要上传的本地文件
	        	    		String strLocalFile =  Environment.getExternalStorageDirectory().getPath().toString() + strFTPUFile;
	        	    		File hLocalFile = new File(strLocalFile);
	                        FTPClient.uploading(hLocalFile,strFTPURemotePath);//进行上传业务
	        	                         
	                		Log.getInstance().InsertARecord(TaskInfo.SgInst().GetIMEI(), 
	                				"FTP Upload",
	                				"End",
	                				TaskInfo.SgInst().GetCurrentRSRP(),
	                				TaskInfo.SgInst().GetCurrentSNR(), 
	                				"null",
	                				"null",
	                				InterfaceSPMonitor.SgInst().GetDLsp());
	                		
	        	    		FTPClient.closeConnect();//关闭上传连接

	                	}
	                	catch (Exception e)
	                	{
	                		Log.getInstance().InsertARecord(TaskInfo.SgInst().GetIMEI(), 
	                				"FTP Upload",
	                				"Exception",
	                				TaskInfo.SgInst().GetCurrentRSRP(),
	                				TaskInfo.SgInst().GetCurrentSNR(), 
	                				"null",
	                				"null",
	                				"null");
	                		LogControl.sgInst().sendlog("上传线程出现异常!请检查网络或参数!\n");
	                		e.printStackTrace();        	    		
	                	}

	            super.run();
	        }
	         	        
	        public void exit() {                       
	        }
		 
	 }
}
