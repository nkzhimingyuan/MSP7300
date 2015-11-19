package com.example.msp7300_client;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.example.Adapter.LogControl;
import com.example.Adapter.UESystemInfo;
import com.example.Config.FilePathConf;
import com.example.Constantdata.ConstantData;
import com.example.Log.Log;
import com.example.Task.Task;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class Test_Fragment_view extends Fragment{

	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private Context mContext = null;
	public static final String ARG_SECTION_NUMBER = "section_number";
	private DateFormat formatter = new SimpleDateFormat("yy-MM-dd**HH:mm:ss");   
	private TextView LogTextView;//日志显示View
	private ProgressBar probar;
	private Button   btnDLtask;//下载Button
	private Button   btnRuntask;//下载Button
	private String logInfo = "";//记录日志信息
	private String[] strLocalScriptlist = null;//本地脚本列表
	private String SelectTaskScript = null;
	     /**
	      * @category 消息处理
	      * @return null
	      * @param  null 
	      * @author zy
	      */	
	private Handler myHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what)
			{
			case 1:
	        	 //add time stamp				
	             long timestamp = System.currentTimeMillis();  
	             String time = formatter.format(new Date());  
	        	 String strInfotmp = "["+time+"]"+msg.obj.toString();	        	
	        	 if(logInfo.length() > 1024*5)
	        	 {
	        		 logInfo = "";
	        	 }
	        	 //set Etextinfo
	        	 //ETextInfo.setText(strInfotmp); 
	     		logInfo += strInfotmp; 
	        	 //strInfo Max Length = 100K

				LogTextView.setText(logInfo);
				break;
			case 2:
				String msgobj =  msg.obj.toString();
				int provalue = Integer.parseInt(msgobj);
				if(provalue<0)
				{
					provalue = 0;
				}
				if(provalue>100)
				{
					provalue = 100;
				}
				probar.setProgress(provalue);
	        	 //add time stamp
			    break;
			default:
			    break;
			}
		}
	};

	/**
	  * @category 构造函数
      * @return txt信息
      * @param  null 
      * @author zy
     */	
	public Test_Fragment_view(Context context) {
		mContext = context;
	}
	

	/**
	 * @category 创建view
	 * @return txt信息
	 * @param  null 
	 * @author zy
	*/	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.test_view, container,
				false);
		
		LogTextView = (TextView) rootView.findViewById(R.id.Log_info_textview);
		probar = (ProgressBar)rootView.findViewById(R.id.progressbar_updown);
		
	   //下载脚本按钮
		btnDLtask = (Button) rootView.findViewById(R.id.test_download);
		btnDLtask.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {				
				getFileDir(FilePathConf.sgInst().GetScriptPathDir());//获取脚本目录下现存脚本
				new AlertDialog.Builder(mContext, 0)
				.setTitle("请选择脚本!")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setSingleChoiceItems(strLocalScriptlist, 0, new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						SelectTaskScript = strLocalScriptlist[which];//获取选择的脚本
						dialog.dismiss();
					}
				}
				)
				.setNegativeButton("取消",null)
				.show();			
			}
		});	
		   //执行脚本按钮
		btnRuntask = (Button) rootView.findViewById(R.id.test_start);
		btnRuntask.setOnClickListener(new OnClickListener() {		
					@Override
					public void onClick(View v) {				
					if(SelectTaskScript == null)
					{
						new AlertDialog.Builder(mContext, 0)
						.setTitle("提示!")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setMessage("请选择加载本地脚本后再运行本地测试!")
						.setPositiveButton("确定", null)
						.show();	
					}
					else
					{
						new AlertDialog.Builder(mContext, 0)
						.setTitle("提示!")
						.setMessage("是否执行脚本:"+SelectTaskScript+" ?测试日志将自动上传服务器.")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {					
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
							
							}
						 }
						)
						.setNegativeButton("取消",null)
						.show();	
					}
				}
				});	
		return rootView;
	}
	/**
	 * @category 获取路径下的脚本文件列表
	 * @return void
	 * @param  filepath:脚本路径 
	 * @author zy
	*/	
	private void getFileDir(String filePath) { 
		try{		
			    
				File f = new File(filePath); 
				File[] files = f.listFiles();// 列出所有文件  
				// 如果不是根目录,则列出返回根目录和上一目录选项  
					// 将所有文件存入list中 
				 if(files != null){ 

					 int count = files.length;// 文件个数  
					 strLocalScriptlist = new String[count];
					 for (int i = 0; i < count; i++) { 
						 File file = files[i]; 
						 strLocalScriptlist[i] = file.getName();//写入脚本列表
					     }
				 }
		}
		 catch(Exception ex)
		 {
		     ex.printStackTrace();
		 }
		}
	/**  
	 *   
	 * @Project: 执行任务线程 
	 * @Desciption: 执行任务线程 
	 * @Author: zy
	 * @Date: 2015-08-21
	 */  
  	 class ExcuteTaskThread extends Thread {
  		    private boolean bReceiveLogperm = false;
  		    boolean mExit = false;	   		 
  	        public ExcuteTaskThread() {
  	           
  	            if (android.os.Build.VERSION.SDK_INT > 9) {
  	                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
  	                StrictMode.setThreadPolicy(policy);
  	            }
  	        }
  	        
  	        @Override
  	        public void run() {
  	                while(mExit == false) 
  	                {  
  	                	try
  	                	{      	 	  
  	    	    	    	Task task = new Task(mContext);
  	    	    	    	//task.DownloadTask(Taskurl,TaskFile);
  	    	    	    	task.StartTask();
  	    	 		    	RequestUploadLog2Server();
  	    	    	    	break;
  	                	}
  	                	catch(Exception e)
  	                	{
  	                		e.printStackTrace();
  	                	}
  	                	         
  	                } 
  	            super.run();
  	            
  	        }
  	         
  	     /**
  	      * @category 请求上传日志至 服务器
  	      * @return txt信息
  	      * @param  null 
  	      * @author zy
  	      */	
  	 	private void RequestUploadLog2Server()
  	 	{
  	 		while(!bReceiveLogperm)
  	 		{
  	 		    try 
  	 		    {
	   	 		    ServCommuUDP.sgInst().SendMsg2Server(ConstantData.UE_UPLOADLOG_REQ,Log.getInstance().GetCurrentLogName());
	   	 		    LogControl.sgInst().sendlog("向服务器请求上传测试日志文件!\n");
  	 		    }
  	 		    catch(Exception ex)
  	 		    {
  	 		    	LogControl.sgInst().sendlog("向服务器请求上传测试日志文件失败,请检查网络!\n");
  	 		    	ex.printStackTrace();
  	 		    }

  	 		    try 
  	 		    {
  	 		    	Thread.sleep(10000);
  	 		    }
  	 		    catch(Exception ex)
  	 		    {
  	 		    	ex.printStackTrace();
  	 		    }
  	 		}
  	    	
  	 	}
  	 	
  	        public void exit() {            
  	            mExit = true;
  	        }  
  	        
  	        public void ExitLogRequest()
  	        {
  	        	bReceiveLogperm = true;
  	        }
  	 } 
	/**
	 * @category 发送日志显示在窗口
	 * @return txt信息
	 * @param  null 
	 * @author zy
	*/	
	public void sendLog(String log){

		Message msg = new Message();
		msg.obj = log;
		msg.what = 1;
		myHandler.sendMessage(msg);

	}
	/**
	 * @category 设置业务完成进度条
	 * @return null
	 * @param  proValue:进度条值 
	 * @author zy
	*/	
	public void setProBar(int proValue){
		Message msg = new Message();
		msg.obj = proValue+"";
		msg.what = 2;
		myHandler.sendMessage(msg);

	}
	
}
