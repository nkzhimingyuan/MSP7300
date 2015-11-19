package com.example.msp7300_client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.Adapter.LogControl;
import com.example.Log.Log;
import com.example.Constantdata.ConstantData;
import com.example.Task.Task;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import android.widget.Toast;

public class ServCommuUDP {
	
	private DatagramSocket clientsocket = null;   //udp socket
	private DataReceiverThread mDataRecvThread = null;  //thread for socket receive
    private static int nIsRegister = 0;       // indicate UE Registered or not 
    private static int nHeartRSP = 3;         // Heart Response from server count
    private Context mcontext = null;
    private HeartBeatThread mHeartBeatThread = null;
    private MonitorHeartThread mMonitorHeartThread=null;
    private  byte[] lock = new byte[0]; 
    private  byte[] lockheart = new byte[0]; 
    private String Serverip=null;
    private int ServerAgentport=7081;
    private String strIMEI = null;
    private RigesterThread trigster = null;
    private String Taskurl = null;
    private String TaskFile = null;
    private SharedPreferences preference;
    private SharedPreferences userPreference;
    public static ServCommuUDP servudp = null;
    private boolean bUEOnline =  false;
    private ExcuteTaskThread excuteScript = null;
    private UploadLogThread uploadlog;//上传日志文件
    private boolean bUpLogfinish = true;//上传日志线程是否在运行
    RequestQueue mQueue;
	// 用户登录请求
	StringRequest loginStringRequest;
 // 每隔20s，心跳请求
 	JsonObjectRequest commendJsonObjectRequest;
 	
	public void ServCommuUDPRegister(Context context)
	{
		mcontext = context;
		mQueue = Volley.newRequestQueue(mcontext);   
	}
	
	public static ServCommuUDP sgInst()
	{
		if(servudp == null)
		{
			servudp = new ServCommuUDP();
			//LogControl.sgInst().sendlog("创建UDP连接成功!\n");
		}
		return servudp;
	}
	
	public void StartUDPSocket()
	{
		InitThread mInitThread = new InitThread(); 
		mInitThread.start();
		trigster = new RigesterThread();
		trigster.start();
	}

	 /********************************************************************************************
	  * init wifi\UDP\service
	  ********************************************************************************************/	
	 class InitThread extends Thread {
		 
		    boolean mExit = false;
	        
	        public InitThread() {
	           
	            if (android.os.Build.VERSION.SDK_INT > 9) {
	                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	                StrictMode.setThreadPolicy(policy);
	            }
	        }
	        @Override
	        public void run() {        	
	        	{
	                //start  Socket for Communicate with ATP 
		    		try 
		    		{
		    			System.out.println("start");
		    			strIMEI = ((TelephonyManager)mcontext.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		    			clientsocket  = new DatagramSocket(ConstantData.CLIENT_PORT);
		    			
		    			//no bind for broadcast udp receive
		                //SocketAddress localAddr = new InetSocketAddress(strWlanNetInterface,ConstantData.CLIENT_PORT);		
		    			//clientsocket.bind(localAddr);
		    			
		        		System.out.println("end");
		        		mDataRecvThread = new DataReceiverThread();
		                mDataRecvThread.start();
		    		}
		    		
		        	catch (Exception e)
		        	{
		        		e.printStackTrace();
		        	}       		
	        	}

	            super.run();
	        }
	         	        
	        public void exit() {            
	            mExit = true;
	        }
	 }
	 
	    /********************************************************************************************
	     * Udp msg receiver class
	     ********************************************************************************************/		
		
	 class DataReceiverThread extends Thread {
			 
		        boolean mExit = false;
		        
		        public DataReceiverThread() {
		           
		            if (android.os.Build.VERSION.SDK_INT > 9) {
		                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		                StrictMode.setThreadPolicy(policy);
		            }
		        }
		                
		        @Override
		        public void run() 
		        {
		            try 
		            {
		                byte []buffer = new byte[1024];
	                
		                while(mExit == false) { 	                	
		                    DatagramPacket dataPacket = new DatagramPacket(buffer, buffer.length);  
		                    clientsocket.receive(dataPacket); 
		               
		                    byte []data = dataPacket.getData();
		                    int dataLen = dataPacket.getLength(); 
		                    Serverip=dataPacket.getAddress().getHostAddress();
		                    int  nServerport=dataPacket.getPort();
		                    
			                //System.out.println("recv from 閺堝秴濮熺粩鐤楶 : "+Serverip);    
			                //System.out.println("recv from 閺堝秴濮熺粩鐥痮rt : "+nServerport);    

		                    byte []tempData = new byte[dataLen];
		                    System.arraycopy(data, 0, tempData, 0, dataLen);
		                    /*
		                     parseData(tempData);//濞戝牊浼呯憴锝嗙�閸戣姤鏆�閺嶈宓侀柅姘繆閸楀繗顔呯憴锕�絺娑撳秴鎮撻惃鍕З娴ｏ拷閺堫剛鈻兼惔蹇涙付鐟曚礁鐤勯悳锟紺S 閸滃S娑撴艾濮�		                      
		                     * */
		                    ParserAndroidMsg(tempData,Serverip,nServerport);
		                }
		                
		            }
		            catch (Exception e) {
		                e.printStackTrace();               
		            }

		            
		            super.run();
		        }
		         	        
		        public void exit() {            
		            mExit = true;
		        }
		 }
		 
	        /********************************************************************************************
	         * ParserAndroidMsg
	         ********************************************************************************************/	        
	        public void ParserAndroidMsg(byte []chMsgData,String strIPDst,int nPORTDst)
	    	{
	    	    byte []temp = new byte[2];
	    	    System.arraycopy(chMsgData, 0, temp, 0, 2);
	    	    short u16MsgID = ByteAndShort.byteArray2U16(temp);
	    	   
	    	  switch(u16MsgID)
	    	  {	    	        
	    	     case ConstantData.PC_BROADCAST:        		
	    	    	 break;	    	    	
	    	    case ConstantData.PC_REGISTER_RSP:
	    	    	ServerAgentport = nPORTDst;
	    	    	LogControl.sgInst().sendlog("收到服务器的注册响应消息!终端注册服务器成功!\n");
	    	    	bUEOnline = true;
	    			trigster.exit();
	         		synchronized(lock)
	         		{
	         			nIsRegister =1;
	         		}
					try
					{
	            		synchronized(lockheart)
	            		{
							nHeartRSP = 3;
	            		}

	            		if(mHeartBeatThread == null)
	            		{
	            			mHeartBeatThread = new HeartBeatThread();       //thread for heartbeat send
	            			mHeartBeatThread.start();
	            		}
					    if(mMonitorHeartThread == null)
					    {
						    mMonitorHeartThread= new MonitorHeartThread(); 
			    	    	
			    	    	mMonitorHeartThread.start();
					    }

					} 
					catch(Exception e)
					{
						e.printStackTrace();
					}
					break;
	    	    case ConstantData.PC_HEARTBEAT_RSP: 
	    	    	LogControl.sgInst().sendlog("收到服务器心跳回应!\n");
	    	    	synchronized(lockheart)
            		{
	    	    	nHeartRSP = 3;
            		}
	    	    	break;
	    	    case ConstantData.PC_COMMAND:
//	    	    	break;	
//	    	    case ConstantData.PC_COMMAND_RSP:	    	    	
//	    	    	break;
//	    	    case ConstantData.PC_EXCUTE_TASK:
	                //parser command para		
    	    	    byte []templen = new byte[2];
    	    	    System.arraycopy(chMsgData, 2, templen, 0, 2);
    	    	    short u16MsgLen = ByteAndShort.byteArray2U16(templen);
    	    	    byte [] command = new byte[u16MsgLen];
    	    	    System.arraycopy(chMsgData, 8, command, 0, u16MsgLen);
    	    	    String strCommand = new String(command);    	    	    
    	    	    String []strArrCommand =  strCommand.split("begintime:");
    	    	   
    	    	    if(strArrCommand.length < 1)       
		            {
    	    	    	return;
		            }
    	    	    else
    	    	    {
    	    	    	    TaskFile = strArrCommand[0];
    	    	    		
    	    	    	    
    	    	    		excuteScript = new  ExcuteTaskThread();
    	    	    		excuteScript.start();
    	    	    }
                    break;
	    	    case ConstantData.PC_UPLOADLOG_PERMISSION:
	    	    	LogControl.sgInst().sendlog("收到服务器允许上传日志命令,准备上传日志!\n");
	    	    	if(excuteScript != null)
	    	    	{
	    	    		excuteScript.ExitLogRequest();
	    	    	}
	    	    	if(bUpLogfinish)//如果上传线程不在运行 则开始上传
	    	    	{
		    	    	uploadlog = new UploadLogThread();
		    	    	uploadlog.start();
	    	    	}

	    	    	break;
                default:
                    break;
 			   };  	
 		
	    	}
		   	 /********************************************************************************************
		   	  * Register thread
		   	  ********************************************************************************************/	
		   	/*
		   	 * Monitor HeartThread
		   	 */	
		   	 class UploadLogThread extends Thread {		   		
		   		    boolean mExit = false;	   		 
		   	        public UploadLogThread() {
		   	           
		   	            if (android.os.Build.VERSION.SDK_INT > 9) {
		   	                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		   	                StrictMode.setThreadPolicy(policy);
		   	            }
		   	        }
		   	        
		   	        @Override
		   	        public void run() {
		   	        	bUpLogfinish = false;
		   	        while(!mExit)
		   	        {
		   	        	try{
			   	        	if(Log.getInstance().UpLoadLog2Server())
			   	        	{
			   	        		LogControl.sgInst().sendlog("上传日志至服务器成功!\n");
			   	        		if(Log.getInstance().DeleteLogFile())
			   	        		{
			   	        			LogControl.sgInst().sendlog("删除终端日志文件成功!\n");
			   	        		}
			   	        		String DBName = Log.getInstance().GetCurrentLogName();
								String ip_server = preference.getString("ip_server", "0.0.0.0");
								String userString = userPreference.getString("username", "zzbarry");
								userString = userString.trim();
								String url_logString = "http://"
										+ ip_server
										+ "/7300/UsrUI/controller/UELogToDB.php?DBname=" + DBName + "&userid="
										+ userString;

								LogControl.sgInst().sendlog(
										"向服务器请求文件汇总,Url:" + url_logString + "!\n");

								JsonObjectRequest commendJsonObjectRequest = new JsonObjectRequest(
										url_logString, null, new Response.Listener<JSONObject>() {
											@Override
											public void onResponse(JSONObject response) {

												try {
													
													if(response.getString("res").equals("true")){
														LogControl.sgInst().sendlog("向服务器请求文件汇总结果:完成" + "!\n");
													}
													else {
														LogControl.sgInst().sendlog("向服务器请求文件汇总结果:失败" + "!\n");
													}
													
												} catch (JSONException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
											}
										}, new Response.ErrorListener() {
											@Override
											public void onErrorResponse(VolleyError error) {

												LogControl.sgInst().sendlog(
														"向服务器请求文件汇总出现异常!" + error.getMessage()
																+ "\n");
											}
										});
								bUpLogfinish = true;
								mQueue.add(commendJsonObjectRequest);
			   	        		return;
			   	        	}

		   	        	}
		   	        	catch(Exception ex)
		   	        	{
		   	        		bUpLogfinish = false;
		   	        		LogControl.sgInst().sendlog("尝试上传日志至服务器失败!正在准备重新上传!\n");
		   	        		ex.printStackTrace();		   	        		
		   	        	}
		   	        	try{
		   	        		Thread.sleep(30000);
		   	        	}
		   	        	catch(Exception ex)
		   	        	{
		   	        	}
		   	        	
		   	        }
		            super.run();	   	            
	   	        }
	   	 	
	   	        public void exit() { 
	   	        	bUpLogfinish = true;
	   	            mExit = true;
	   	        }  
	   	        
	   	 } 
		   	 /********************************************************************************************
		   	  * Register thread
		   	  ********************************************************************************************/	
		   	/*
		   	 * Monitor HeartThread
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
			   	 		   	 		preference = mcontext.getSharedPreferences("setting_server", Context.MODE_PRIVATE);		   			
			   			   			String strServerIP = preference.getString("ip_server", "192.168.1.3");
			   			   	 	    userPreference = mcontext.getSharedPreferences("user", Context.MODE_PRIVATE);
			   			   		    String strUserID =  userPreference.getString("username", "zzbarry"); 
			   			   		    Taskurl = "http://"+strServerIP+"/7300/UsrUI/data/case/"+ strUserID+"/"+TaskFile;
		   	    	    	    	Task task = new Task(mcontext);
		   	    	    	    	task.DownloadTask(Taskurl,TaskFile);
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
		   	 		    	userPreference = mcontext.getSharedPreferences("user", Context.MODE_PRIVATE);
		   	 		    	String strUserID =  userPreference.getString("username", "zzbarry");
		   	 		    	strUserID = strUserID.trim();
			   	 		    SendMsg2Server(ConstantData.UE_UPLOADLOG_REQ, strUserID + ";" + Log.getInstance().GetCurrentLogName());
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
		   	 /********************************************************************************************
		   	  * Register thread
		   	  ********************************************************************************************/	
		   	/*
		   	 * Monitor HeartThread
		   	 */	
		   	 class RigesterThread extends Thread {
		   		 
		   		    boolean mExit = false;
		   		   
		   	        public RigesterThread() {
		   	           
		   	            if (android.os.Build.VERSION.SDK_INT > 9) {
		   	                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		   	                StrictMode.setThreadPolicy(policy);
		   	            }
		   	        }
		   	        
		   	        @Override
		   	        public void run() {
		    	           

		   			int nPORTDst = 7081; 
		   	                while(mExit == false) 
		   	                {  
		   	                	try
		   	                	{     		

		   	 		   	 		preference = mcontext.getSharedPreferences("setting_server", Context.MODE_PRIVATE);		   			

		   			   			String strServerIP = preference.getString("ip_server", "192.168.1.3");	   	                		
		   		    	    	    byte []tempsend = new byte[ConstantData.MAXMSGLEN];
		   		    	    	    short MsgID =(short) ConstantData.UE_REGISTER_REQ;
		   		    	    	   SharedPreferences userPreference = mcontext.getSharedPreferences("user", Context.MODE_PRIVATE);
		   		    	    	    String userid = userPreference.getString("username", "zzbarry");
		   		                    String strContent = strIMEI+"@"+userid+"@";	                    
		   		                    AndroidMsg.AndroidMsgPack(MsgID, strContent, tempsend);

		   		                    InetAddress Serveripaddr =null;
		   							try 
		   							{
		   								Serveripaddr = InetAddress.getByName(strServerIP);
		   							} 
		   							catch (Exception e) 
		   							{
		   								e.printStackTrace();
		   							}
		   				            //send UE_REGISTER_REQ
		   		                    DatagramPacket senddata=new DatagramPacket(tempsend,ConstantData.MAXMSGLEN,Serveripaddr,nPORTDst);
		   		                    try 
		   		                    {
		   								clientsocket.send(senddata);
		   								LogControl.sgInst().sendlog("向服务器发送注册消息!\n");
		   							}
		   		                    catch (Exception e) 
		   		                    {
		   								e.printStackTrace();
		   							}
		   		                    try 
		   		                    {
		   								Thread.sleep(10000);
		   							}
		   		                    catch (InterruptedException e)
		   		                    {
		   								e.printStackTrace();
		   							}  
		   	                	}
		   	                	catch(Exception e)
		   	                	{
		   	                		e.printStackTrace();
		   	                	}
		   	                	         
		   	                } 
		   	            super.run();
		   	            
		   	        }
		   	         	        
		   	        public void exit() {            
		   	            mExit = true;
		   	        }   	    
		   	 } 
	   	 /********************************************************************************************
	   	  * Monitor Heart 10s
	   	  ********************************************************************************************/	
	   	/*
	   	 * Monitor HeartThread
	   	 */	
	   	 class MonitorHeartThread extends Thread {
	   		 
	   		    boolean mExit = false;
	   	        
	   	        public MonitorHeartThread() {
	   	        
	   	            if (android.os.Build.VERSION.SDK_INT > 9) {
	   	                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	   	                StrictMode.setThreadPolicy(policy);
	   	            }
	   	        }
	   	        
	   	        @Override
	   	        public void run() {
	    	           
	   	                while(true && mExit == false) 
	   	                {  
	   	                	try
	   	                	{     		
	   		                    try 
	   		                    {
	   								Thread.sleep(10000);
	   							}
	   		                    catch (InterruptedException e)
	   		                    {
	   								e.printStackTrace();
	   							}  
	                   	    	
	   	                		synchronized(lockheart)
	   	                		{
	   		                	    nHeartRSP--;
	   	                		}
	                   	    		   	                	    
	   	                	    if(nHeartRSP <0)
	   	                	    {
	   	                	    	//ReStartSocket();
	   	                	    	
	   	                			if(mHeartBeatThread != null)
	   	                			{
	   	                				LogControl.sgInst().sendlog("终端与服务器断开链接,将重新发起注册消息!\n");
	   	                				mHeartBeatThread.exit();
	   	                				bUEOnline = false;
	   	                				Thread.sleep(6000);
	   	                				trigster = new RigesterThread();
	   	                				trigster.start();
	   	                				mHeartBeatThread = null;
	   	                			}
	   	                			
	   		                		synchronized(lock)
	   		                		{
	   		                			nIsRegister = 0;
	   		                		}
	   		                		
	   		                		mExit = true;
	   	                	    	
	   	                	    }
	                       		
	   	                	}
	   	                	catch(Exception e)
	   	                	{
	   	                		e.printStackTrace();
	   	                	}
	   	                	         
	   	                } 
	   	            super.run();
	   	            mMonitorHeartThread=null;
	   	        }
	   	         	        
	   	        public void exit() {            
	   	            mExit = true;
	   	        }   	    
	   	 }
	     /********************************************************************************************
	      * heartbeat 10s
	      ********************************************************************************************/	
	 	/*
	 	 * HeartBeatThread  send Heartbeat to server
	 	 */
	 	class HeartBeatThread extends Thread {
	 		
	         boolean mExit = false;
	         
	         public HeartBeatThread() {
	            
	             if (android.os.Build.VERSION.SDK_INT > 9) {
	                 StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	                 StrictMode.setThreadPolicy(policy);
	             }
	         }
	         
	         @Override
	         public void run() 
	         {        	
	             while(mExit == false)
	             {   
	             	//android msg packet
	 	    	    byte []tempsend = new byte[ConstantData.MAXMSGLEN];
	 	    	    short MsgID =(short) ConstantData.UE_HEARTBEAT;
	 	            AndroidMsg.AndroidMsgPack(MsgID, strIMEI, tempsend);
	 	    	    
	 	            //Service ip
	 	            InetAddress Serveripaddr =null;
	 				try
	 				{
	 				    Serveripaddr = InetAddress.getByName(Serverip);
	 				}
	 				catch (Exception e) 
	 				{
	 					e.printStackTrace();
	 				}
	 				if(bUEOnline)
	 				{
	 					 DatagramPacket senddata=new DatagramPacket(tempsend,ConstantData.MAXMSGLEN,Serveripaddr,ServerAgentport);
	 					
	 					 LogControl.sgInst().sendlog("向服务器发送心跳包!IP:"+Serveripaddr+" Port:"+ServerAgentport+"\n");
	 	 	            try
		 	            {
		 				    clientsocket.send(senddata);
		 				} 
		 	            catch (Exception e)
		 	            {
		 	            	LogControl.sgInst().sendlog("向服务器发送心跳包失败!IP:"+Serveripaddr+" Port:"+ServerAgentport+"\n");
		 					e.printStackTrace();
		 	            }
	 				}
	 	           
	 	                    
	 	            try
	 	            {
	 					Thread.sleep(10000);
	 				}
	 	            catch (InterruptedException e)
	 	            {
	 				    e.printStackTrace();
	 				}
	                
	             } 
	             super.run();
	         }
	          	        
	         public void exit() {            
	             mExit = true;
	         }		
	 	}
	 	
	        public static class AndroidMsg {

	        	static int AndroidMsgPack(short u16MsgID, String strMsgContent, byte[] MsgSend)
	        	{
	        	    byte [] MsgTemp = new byte[2];
	        	    MsgTemp = ByteAndShort.U162ByteArray(u16MsgID);
	        	    System.arraycopy(MsgTemp, 0, MsgSend, 0, 2);
	        	    
	                byte []tempContent = strMsgContent.getBytes();
	                int nContentLen = strMsgContent.length();
	        	    MsgTemp = ByteAndShort.U162ByteArray((short)nContentLen);
	        	    System.arraycopy(MsgTemp, 0, MsgSend, 2, 2);
	        	    System.arraycopy(tempContent, 0, MsgSend, 8, nContentLen);
	        	    
	        		return 0;
	        	}
	        	
	        }
  	
	    public boolean SendMsg2Server(int MsgFlag,String strMsgContent)
	    {
	    	if(!bUEOnline)
	    	{
	    		LogControl.sgInst().sendlog("UE与服务器断开连接!数据包发送失败!\n");
	    		return false;
	    	}
	    	strMsgContent = strIMEI+"@"+strMsgContent;
	    	//android msg packet
	    	    byte []tempsend = new byte[ConstantData.MAXMSGLEN];
	    	    short MsgID =(short) MsgFlag;
	            AndroidMsg.AndroidMsgPack(MsgID, strMsgContent, tempsend);
	    	    
	            //Service ip
	            InetAddress Serveripaddr =null;
				try
				{
				    Serveripaddr = InetAddress.getByName(Serverip);
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				
	            DatagramPacket senddata=new DatagramPacket(tempsend,ConstantData.MAXMSGLEN,Serveripaddr,ServerAgentport);
	            
	            try
	            {
				    clientsocket.send(senddata);
				} 
	            catch (Exception e)
	            {
					e.printStackTrace();
					LogControl.sgInst().sendlog("向服务器发送数据包失败!\n");
					return false;
	            }
                return true;
	    }
}
