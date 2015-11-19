package com.example.msp7300_client;

import android.os.Looper;
import android.os.StrictMode;

import com.example.Adapter.InterfaceSPMonitor;
import com.example.Adapter.UESystemInfo;
import com.example.Constantdata.ConstantData;
import com.example.Task.Task;
import com.example.Task.TaskInfo;

public class IEValueReport {
	private boolean bReport = true;
	private SendIEThread sendie = null;
	private boolean bReportThreadExit = false;
	public static IEValueReport iereport = null;
	/**
     * @category 单例
     * @return null
     * @param  null
     * @author zy
     */	
	public static IEValueReport sgInst()
	{
		if(iereport == null)
		{
			iereport = new IEValueReport();
		}
		return iereport;
	}
	/**
     * @category 开始发送IE 
     * @return null
     * @param  null
     * @author zy
     */	
	public void StartIEReport()
	{
      	try
     	{
      		bReport= true; 
      		bReportThreadExit = true;
     	    sendie = new SendIEThread();
     		sendie.start();
     	}
     	catch (Exception e)
     	{
     		e.printStackTrace();
     	}
	}
	/**
     * @category 停止IE上报
     * @return null
     * @param  null
     * @author zy
     */	
	public void StopIEReport()
	{
		if(sendie != null)
		{
			sendie.exit();
		}
	}
	/**
     * @category 暂停IE上报
     * @return null
     * @param  null
     * @author zy
     */	
	public void PauseIEReport()
	{
		bReport = false;
	}
	/**
     * @category 继续IE上报
     * @return null
     * @param  null
     * @author zy
     */	
	public void ContinueIEReport()
	{
		bReport = true;
	}
	/**
     * @category 发送IE值线程
     * @return null
     * @param  null
     * @author zy
     */	
 class SendIEThread extends Thread {
	
	 	public SendIEThread() {
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
             while(bReportThreadExit)
             {
            	 if(bReport)
            	 {
                 	String strMsg = null;
                	strMsg = MsgPacket();
                	ServCommuUDP.sgInst().SendMsg2Server(ConstantData.UE_TRAFFICREPORT,strMsg);
            	 }
        		
        		try
        		{
        			Thread.sleep(5000);
        		}
        		catch(Exception e)
        		{
        			e.printStackTrace();
        		}

             }
    	}
    	/**
         * @category IE内容组包
         * @return 组包结果
         * @param  null
         * @author zy
         */	
        private String MsgPacket()
        {
              String strMsg = null;
              strMsg = ConstantData.BATTERY_LEV +":"+UESystemInfo.sgInst().GetBattery()+";";
              strMsg += ConstantData.CORE_IP +":"+UESystemInfo.sgInst().GetCoreIP()+";";
              strMsg += ConstantData.DL_SP +":"+InterfaceSPMonitor.SgInst().GetDLsp()+";";
              strMsg += ConstantData.IMSI +":"+UESystemInfo.sgInst().GetIMSI()+";";
              strMsg += ConstantData.MSP_VERSION +":"+UESystemInfo.sgInst().GetRSRP()+";";
              strMsg += ConstantData.NET_STAT +":"+UESystemInfo.sgInst().GetNetState()+";";
              strMsg += ConstantData.NET_TYPE +":"+UESystemInfo.sgInst().GetNetType()+";";
              strMsg += ConstantData.PLMN +":"+UESystemInfo.sgInst().GetPLMN()+";";
              strMsg += ConstantData.CMD_STAT +":"+TaskInfo.SgInst().GetCurrentCmd()+";";
              strMsg += ConstantData.RSRP +":"+UESystemInfo.sgInst().GetRSRP()+";";
              strMsg += ConstantData.SNR +":"+UESystemInfo.sgInst().GetSNR()+";";
              strMsg += ConstantData.RSSI +":"+UESystemInfo.sgInst().GetRSSI()+";";
              strMsg += ConstantData.RSRQ +":"+UESystemInfo.sgInst().GetRSRQ()+";";
              strMsg += ConstantData.UE_NAME +":"+UESystemInfo.sgInst().GetUENAME()+";";
              strMsg += ConstantData.UL_SP +":"+InterfaceSPMonitor.SgInst().GetULsp()+";";
              strMsg += ConstantData.TASK +":"+TaskInfo.SgInst().GetCurrentTask()+";";
              
              return strMsg;
        }
        
        public void exit() {  
        	bReportThreadExit  = false;
        }
 }
}
