package com.example.Adapter;

import com.example.Constantdata.ConstantData;

import android.net.TrafficStats;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;

public class InterfaceSPMonitor {
	
	private double dDLsp=0;//记录下载速率
	private double dULsp=0;//记录上传速率
	// 下载速率
	long mobile_down_rate;
	
	//上传速率
	long mobile_up_rate;
	
	// 起始下载速率
	long mobile_down_firstrate=0;
	//起始上传速率
	long mobile_up_firstrate=0;
	
	static int nlastPeriod = 10;
	public int nReportPSPeriod = 1000;//刷新时延
	public static InterfaceSPMonitor spmonitor = null;//单例
	private RefreshThread refrshthread = null;
	/**
     * @category 构造函数
     * @return null
     * @param  null
     * @author zy
     */	
	public InterfaceSPMonitor()
	{
		refrshthread = new RefreshThread();
		refrshthread.start();
	}
	/**
     * @category 获取下载速率
     * @return null
     * @param  null
     * @author zy
     */	
	public String GetDLsp()
	{
		return String.format("%.2f", dDLsp);		
	}
	/**
     * @category 获取上传速率
     * @return null
     * @param  null
     * @author zy
     */	
	public String GetULsp()
	{
		return String.format("%.2f", dULsp);	
	}
	/**
     * @category 单例
     * @return null
     * @param  null
     * @author zy
     */	
	public static InterfaceSPMonitor SgInst()
	{
		if(spmonitor == null)
		{
			spmonitor = new InterfaceSPMonitor();
		}
		return spmonitor;
	}
	
	/**
     * @category 刷新网卡速率线程
     * @return null
     * @param  null
     * @author zy
     */	
	 class RefreshThread extends Thread {
		    private boolean bExit =false;
		 	public RefreshThread() {
	            /*瑙ｅ喅Android2.3浠ュ悗鐗堟湰缃戠粶杩炴帴閿欒*/
	            if (android.os.Build.VERSION.SDK_INT > 9) {
	                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	                StrictMode.setThreadPolicy(policy);
	            }
	        }
	    	
	        @Override
	    	public void run()
	    	{
	        	 Looper.prepare();
                while(!bExit)
                {
                	refresh();
	           		try
	           		{
	           			Thread.sleep(nReportPSPeriod);
	           		}
	           		catch(Exception e)
	           		{
	           			LogControl.sgInst().sendlog("刷新网卡速率线程出现异常并退出!\n");
	           			e.printStackTrace();
	           		}
     
                }
	    	}
	                
	        public void exit() {  
	        	bExit = true;
	        }
	 }
		/**
	     * @category 刷新获取网卡速率
	     * @return null
	     * @param  null
	     * @author zy
	     */	
	public void refresh() {
	  
		try
		{

			// 每次获取流量值	
			long bytestmp = TrafficStats.getMobileRxBytes();
		
			mobile_up_rate=TrafficStats.getMobileTxBytes()-mobile_up_firstrate;
			mobile_up_firstrate=TrafficStats.getMobileTxBytes();

			int nCurrentPeriod = (int)(nReportPSPeriod/1000);
			
			//for Period change ,but mobile_down_rate&mobile_up_rate not change for new period
			if(nCurrentPeriod != nlastPeriod)
			{
			   int nTmp = nCurrentPeriod;
			   nCurrentPeriod = nlastPeriod;
			   nlastPeriod = nTmp;
			}
			
			double dULsptmp=mobile_up_rate*8/nCurrentPeriod;//traffic test上传实时速率
			dULsp = dULsptmp/1024/1024;
			 
			mobile_down_rate=bytestmp-mobile_down_firstrate;	
			mobile_down_firstrate=bytestmp;
				
			double dDLSptmp=mobile_down_rate*8/nCurrentPeriod;//traffic test下载实时速率
			dDLsp = dDLSptmp/1024/1024;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}	
}
