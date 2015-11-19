package com.example.Task;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.example.Log.Log;

public class Ping {

	private Process Pingprocess = null;  
	
	private boolean bpingfinish = false;
	/**
     * @category ping缁撴灉鑾峰彇
     * @author zy
     */	
	 class InputStreamRunnablePing implements Runnable
	 {
		 BufferedReader bReader = null;
		 String type = null;
		 public InputStreamRunnablePing(InputStream is, String _type)
		 {
			 try
			 {
				 bReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(is),"UTF-8"));
				 type = _type;
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		 }
		 public void run()
		 {
			 String line;
			 boolean bSend = false;
			 String avg = null;
			 String max = null;
			 int nSucRate = 0;
			 try
			 {
				 while((line = bReader.readLine()) != null)
				 {
					    if(line.contains("unreachable") || line.contains("100% packet loss"))
					    {

					    	Log.getInstance().InsertARecord(TaskInfo.SgInst().GetIMEI(), 
			        				"Ping",
			        				"End",
			        				TaskInfo.SgInst().GetCurrentRSRP(),
			        				TaskInfo.SgInst().GetCurrentSNR(), 
			        				line,
			        				"null",
			        				"null");
			        		bpingfinish = true;
					    }
					    if(line.contains("avg"))
					    {
					    	String[] strtmp = line.split("\\/");
					    	if(strtmp.length > 6)
					    	{
					    		avg = strtmp[4];
					    		max = strtmp[5];
					    	}
					    	bSend = true;
					    }
					    if(line.contains("transmitted") ||  line.contains("ping:"))
					    {
					    	
					    	String[] strsplit = line.split("\\,|\\%");
					    	if(strsplit.length > 4)
					    	{
					    		String FailRate = strsplit[2];
					    		nSucRate = 100 -  (int)Float.parseFloat(FailRate);
					    		bSend = true;
					    	}			    						    					        	
					    }
				 }
				 if(bSend)
				 {
				    	Log.getInstance().InsertARecord(TaskInfo.SgInst().GetIMEI(), 
		        				"Ping",
		        				"End",
		        				TaskInfo.SgInst().GetCurrentRSRP(),
		        				TaskInfo.SgInst().GetCurrentSNR(), 
		        				nSucRate+"",
		        				avg,
		        				max);
		        		bpingfinish = true;
				 }
			 }
			 catch(Exception e)
			 {
				 bpingfinish = true;
				 e.printStackTrace();
			 }
		 }
	 }
		/**
	     * @category 获取Ping命令是否执行完成
	     * @return null
	     * @param null
	     * @author zy
	     */	
	 public boolean  GetPingFinishstate()
	 {
		 return bpingfinish;
	 }
		/**
	     * @category 寮�ping涓氬姟
	     * @return null
	     * @param  strPingcmd:ping鍛戒护
	     * @author zy
	     */	
	public void StartPing(String strPingcmd)
	{	  
			try 
			{   
				bpingfinish = false;
	    		Pingprocess = Runtime.getRuntime().exec(strPingcmd);	        	        
		        DataOutputStream os= new DataOutputStream(Pingprocess.getOutputStream());
		        Thread.sleep(100);           
//		        os.writeBytes(strPingcmd + "\n");        
//		        os.flush();
//		        Thread.sleep(1000);
	          
		        os.writeBytes("exit\n");        
		        os.flush();
		                                      
		        Thread Terro = new Thread(new InputStreamRunnablePing(Pingprocess.getErrorStream(),"ErrorStream"));
		        Terro.start();
		               
		        Thread Tinput = new Thread(new InputStreamRunnablePing(Pingprocess.getInputStream(),"InputStream"));
		        Tinput.start();
			}
			
	    	catch (Exception e)
	    	{
	    		e.printStackTrace();
	    	}	    
	}
	/**
     * @category 鍋滄ping涓氬姟
     * @return null
     * @param  strPingcmd:ping鍛戒护
     * @author zy
     */
	public void StopPing()
	{    
			try 
			{   
				if(null == Pingprocess)
				{
					return;
				}
				else
				{
					Pingprocess.destroy();
				}
			}
			
	    	catch (Exception e)
	    	{
	    		e.printStackTrace();    		
	    	}
	    
	}
}
