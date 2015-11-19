package com.example.Task;

import java.io.DataOutputStream;

import android.content.Context;
import android.net.ConnectivityManager;

public class Air {

	private Context mcontext = null;
	public static Air air = null;
	
	public Air()
	{
		
	}
	
	public static Air sgInst()
	{
		if(air == null)
		{
			air = new Air();
		}
		return air;
	}
	/**
     * @category 打开飞行模式
     * @return null
     * @param  null
     * @author zy
     */	
	public void StartAirPlaneMode()
	{
		setAirPlaneMode();
	}
	/**
     * @category 关闭飞行模式
     * @return null
     * @param  null
     * @author zy
     */
	public void StopAirPlaneMode()
	{
		UnSetAirPlaneMode();
	}
	/**
     * @category 停止飞行模式测试
     * @return null
     * @param  null
     * @author zy
     */
	public void StopAirtest()
	{
		
	}
	/**
     * @category 开始飞行模式测试
     * @return null
     * @param  nAirTestCnt:测试次数
     * @param  nAirTime:飞行模式间隔
     * @author zy
     */
    public void StartAirtest(Context context,int nAirTestCnt,int nAirTime)
    {
    	int nSuccessCnt = 0;
    	int nAirCount = 0;
    	mcontext = context;
    	
		for(int i=0;i<nAirTestCnt;i++)
		{
			try
			{
            	int Count = i+1;
            	nAirCount += 1; //用于统计一次业务组合循环，发送ATP 生成报表
            	setAirPlaneMode();
            	Thread.sleep(nAirTime*1000);
            		                         		
            	UnSetAirPlaneMode();
            	Thread.sleep(nAirTime*1000);   
            		
				ConnectivityManager conMgr = (ConnectivityManager)mcontext.getSystemService(Context.CONNECTIVITY_SERVICE);
				android.net.NetworkInfo.State Mobile = conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
				if(Mobile.compareTo(android.net.NetworkInfo.State.CONNECTED) == 0)
				{
					nSuccessCnt += 1;				
				}
					
				float fRate = (float)nSuccessCnt/Count*100;			
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

		}
    }
	/**
     * @category 关闭飞行模式
     * @return null
     * @param  null
     * @author zy
     */
	private void UnSetAirPlaneMode()
	{
		try 
		{    
	        Process process =null; 
	        
	        process = Runtime.getRuntime().exec("su");	        
	        Thread.sleep(100);  
	        DataOutputStream os= new DataOutputStream(process.getOutputStream());

		    os.writeBytes("settings put global airplane_mode_on 0 \n");       
		    os.flush();
		    Thread.sleep(100);
		        
		    os.writeBytes("am broadcast -a android.intent.action.AIRPLANE_MODE --ez state false\n");        
		    os.flush();
		    Thread.sleep(100);		        
	       	                              
	        os.writeBytes("exit\n");        
	        os.flush();
	        os.close();
	        
		}	
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
	}
	/**
     * @category 打开飞行模式
     * @return null
     * @param  null
     * @author zy
     */
    private void setAirPlaneMode()
    {
		try 
		{    
	        Process process =null; 
	        
	        process = Runtime.getRuntime().exec("su");	        
	        Thread.sleep(100);  
	        DataOutputStream os= new DataOutputStream(process.getOutputStream());
	        
		    os.writeBytes("settings put global airplane_mode_on 1 \n");       
		    os.flush();
		    Thread.sleep(100);
		        
		    os.writeBytes("am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true\n");        
		    os.flush();
		    Thread.sleep(100);
	                
	        os.writeBytes("exit\n");        
	        os.flush();
	        os.close();
	        
		}
		
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    


    
}
