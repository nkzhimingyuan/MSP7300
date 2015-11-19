package com.example.Adapter;

import com.example.Constantdata.ConstantData;

import android.net.TrafficStats;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;

public class InterfaceSPMonitor {
	
	private double dDLsp=0;//��¼��������
	private double dULsp=0;//��¼�ϴ�����
	// ��������
	long mobile_down_rate;
	
	//�ϴ�����
	long mobile_up_rate;
	
	// ��ʼ��������
	long mobile_down_firstrate=0;
	//��ʼ�ϴ�����
	long mobile_up_firstrate=0;
	
	static int nlastPeriod = 10;
	public int nReportPSPeriod = 1000;//ˢ��ʱ��
	public static InterfaceSPMonitor spmonitor = null;//����
	private RefreshThread refrshthread = null;
	/**
     * @category ���캯��
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
     * @category ��ȡ��������
     * @return null
     * @param  null
     * @author zy
     */	
	public String GetDLsp()
	{
		return String.format("%.2f", dDLsp);		
	}
	/**
     * @category ��ȡ�ϴ�����
     * @return null
     * @param  null
     * @author zy
     */	
	public String GetULsp()
	{
		return String.format("%.2f", dULsp);	
	}
	/**
     * @category ����
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
     * @category ˢ�����������߳�
     * @return null
     * @param  null
     * @author zy
     */	
	 class RefreshThread extends Thread {
		    private boolean bExit =false;
		 	public RefreshThread() {
	            /*解决Android2.3以后版本网络连接错误*/
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
	           			LogControl.sgInst().sendlog("ˢ�����������̳߳����쳣���˳�!\n");
	           			e.printStackTrace();
	           		}
     
                }
	    	}
	                
	        public void exit() {  
	        	bExit = true;
	        }
	 }
		/**
	     * @category ˢ�»�ȡ��������
	     * @return null
	     * @param  null
	     * @author zy
	     */	
	public void refresh() {
	  
		try
		{

			// ÿ�λ�ȡ����ֵ	
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
			
			double dULsptmp=mobile_up_rate*8/nCurrentPeriod;//traffic test�ϴ�ʵʱ����
			dULsp = dULsptmp/1024/1024;
			 
			mobile_down_rate=bytestmp-mobile_down_firstrate;	
			mobile_down_firstrate=bytestmp;
				
			double dDLSptmp=mobile_down_rate*8/nCurrentPeriod;//traffic test����ʵʱ����
			dDLsp = dDLSptmp/1024/1024;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}	
}
