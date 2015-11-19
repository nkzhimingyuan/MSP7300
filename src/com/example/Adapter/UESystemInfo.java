package com.example.Adapter;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import com.example.Constantdata.ConstantData;
import com.example.Task.TaskInfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

public class UESystemInfo {
     public static UESystemInfo uesysinfo = null;
 	private BatteryListen batteryListen=null;
 	private NetConnectReceiver NetstateListen = null;
     private Context mcontext = null;
     private String  strCoreIP = null;
	 private String  strRSRP = "unkown";
	 private String  strRSRQ="unkown";
	 private String  strSNR ="unkown";
	 private String  strRSSI ="unkown";
	 private String strNetState = "unkown";
	 private String strNetType = "unkown";
	 private String strIMEI = null;
	 private String strIMSI = null;
	 private String strPLMN = null;
	 private String strBatterryLevel = null;
	 private String strUEName = null;
		/**
	     * @category 构造函数
	     * @return null
	     * @param  null
	     * @author zy
	     */	
     public UESystemInfo()
     {
    	 
     }
 	/**
      * @category 单例模式
      * @return 对象
      * @param  null
      * @author zy
      */	
     public static UESystemInfo sgInst()
     {
    	 if(uesysinfo == null)
    	 {
    		 uesysinfo = new UESystemInfo();
    	 }
    	 return uesysinfo;
     }
 	/**
      * @category 开始监控系统信息
      * @return null
      * @param  context
      * @author zy
      */	
     public void UESystemInfoGet_Start(Context context)
     {
    	 mcontext =context;
    	 
  		strIMEI = ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
  		TaskInfo.SgInst().SetIMEI(strIMEI);
  		
 		strIMSI = ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getSubscriberId();
 		if(strIMSI==null)
 		{
 			strIMSI = "Unkown";
 		}
 		//telephonelistener
  		MyPhoneStateListener phoneLis = new MyPhoneStateListener();
  		TelephonyManager telMag = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
 		telMag.listen(phoneLis, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
 		
 		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
 		 batteryListen = new BatteryListen();
 		context.registerReceiver(batteryListen,intentFilter);
 				
 		IntentFilter intentFilterNet = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
 		NetstateListen = new NetConnectReceiver();
		context.registerReceiver(NetstateListen,intentFilterNet);
		
     }
  	/**
      * @category 设置UE别名
      * @return UE别名
      * @param  null
      * @author zy
      */	
  	public void SetUENAME(String strName)
  	{
  		strUEName = strName;
  	}
 	/**
      * @category 获取UE别名
      * @return UE别名
      * @param  null
      * @author zy
      */	
  	public String GetUENAME()
  	{
  		return strUEName;
  	}
 	/**
      * @category 获取终端PLMN
      * @return PLMN
      * @param  null
      * @author zy
      */	
  	public String GetPLMN()
  	{
  		if(strIMSI.length() >5)
  		{
  			strPLMN = strIMSI.substring(0, 5);
  		}
  		else
  		{
  			strPLMN = "unkown";
  		}
  		return strPLMN;
  	}
 	/**
      * @category 获取CoreIP
      * @return CoreIP
      * @param  null
      * @author zy
      */	
     public String  GetCoreIP()
     {
    	 GetIPInfo();
    	 return strCoreIP;
     }
 	/**
      * @category 获取RSRP
      * @return RSRP
      * @param  null
      * @author zy
      */	
     public String  GetRSRP()
     {
    	 return strRSRP;
     }
 	/**
      * @category 获取RSRQ
      * @return RSRQ
      * @param  null
      * @author zy
      */	
     public String  GetRSRQ()
     {
    	 return strRSRQ;
     }
 	/**
      * @category 获取SNR
      * @return SNR
      * @param  null
      * @author zy
      */	
     public String  GetSNR()
     {
    	 return strSNR;
     }
 	/**
      * @category 获取RSSI
      * @return RSSI
      * @param  null
      * @author zy
      */	
     public String  GetRSSI()
     {
    	 return strRSSI;
     }
 	/**
      * @category 获取网络状态
      * @return 获取网络状态
      * @param  null
      * @author zy
      */	
     public String  GetNetState()
     {
    	 return strNetState;
     }
 	/**
      * @category 获取网络类型
      * @return 获取网络类型
      * @param  null
      * @author zy
      */	
     public String  GetNetType()
     {
    	 return strNetType;
     }
 	/**
      * @category 获取IMEI
      * @return IMEI
      * @param  null
      * @author zy
      */	
     public String  GetIMEI()
     {
    	 return strIMEI;
     }
 	/**
      * @category 获取IMSI
      * @return IMSI
      * @param  null
      * @author zy
      */	
     public String  GetIMSI()
     {
    	 return strIMSI;
     }
 	/**
      * @category 获取网络状态
      * @return 获取网络状态
      * @param  null
      * @author zy
      */	
     public String GetBattery()
     {
    	 return strBatterryLevel;
     }
     /**
      * @category 获取IP信息
      * @return null
      * @param  null
      * @author zy
      */	
     public void GetIPInfo()
     {
 		try 
 		{
 	       for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
 	            {
 	               NetworkInterface intf = en.nextElement();
 	               for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
 	               {
 	                   InetAddress inetAddress = enumIpAddr.nextElement();
 	                   if (!inetAddress.isLoopbackAddress())
 	                   {
 	                        String strinet =  inetAddress.getHostAddress().toString();	                        
 	                        	                		
 	                        if(strinet.contains("."))
 	                        {
// 	                        	if(strinet.contains("192.168."))
// 	                        	{	                        
// 	                                continue;
// 	                        	}
 	                            strCoreIP = strinet;                         
 	                        }
 	                   }
 	               }
 	           }
 		}		
     	catch (Exception e)
     	{
     		e.printStackTrace();
     	}
     }
     

 	/**
      * @category 接收网络状态广播
      * @return null
      * @param  null
      * @author zy
      */	
  	public class NetConnectReceiver extends BroadcastReceiver
  	{
 		@Override
 		public void onReceive(Context context, Intent intent) {
 			// TODO Auto-generated method stub
  			String action = intent.getAction();
  			if(action.equals(ConnectivityManager.CONNECTIVITY_ACTION))
  			{
  				ConnectivityManager conMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
 				State Mobile = conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();	
 				strNetState = Mobile.toString();
 				
 				int nNetWorkType = ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkType();
 				ParseNetType(nNetWorkType);
  			}
 		}
  	};
 	/**
      * @category 解析网络类型
      * @return null
      * @param  网络类型ID
      * @author zy
      */	
  	private void ParseNetType(int nType)
  	{
  		switch(nType)
  		{
  		case ConstantData.NETWORK_TYPE_UNKNOWN:
 		{
 			strNetType = "Unknown";
 			break;
 		}
 	case ConstantData.NETWORK_TYPE_GPRS:
 		{
 			strNetType = "GPRS";
 			break;
 		}
 	case ConstantData.NETWORK_TYPE_EDGE:
 		{
 			strNetType = "EDGE";
 			break;
 		}
 	case ConstantData.NETWORK_TYPE_CDMA:
 		{
 			strNetType = "CDMA";
 			break;
 		}
 	case ConstantData.NETWORK_TYPE_EVDO_0:
 		{
 			strNetType = "EVDO_0";
 			break;
 		}
 	case ConstantData.NETWORK_TYPE_EVDO_A:
 		{
 			strNetType = "EVDO_A";
 			break;
 		}
 	case ConstantData.NETWORK_TYPE_EVDO_B:
 		{
 			strNetType = "EVDO_B";
 			break;
 		}
 	case ConstantData.NETWORK_TYPE_HSDPA:
 		{
 			strNetType = "HSDPA";
 			break;
 		}
 	case ConstantData.NETWORK_TYPE_UMTS:
 		{
 			strNetType = "UMTS";
 			break;
 		}
 	case ConstantData.NETWORK_TYPE_LTE:
 		{
 			strNetType = "LTE";
 			break;
 		}
 	case ConstantData.NETWORK_TYPE_IDEN:
 		{
 			strNetType = "IDEN";
 			break;
 		}
 	case ConstantData.NETWORK_TYPE_HSUPA:
 		{
 			strNetType = "HSUPA";
 			break;
 		}
 	case ConstantData.NETWORK_TYPE_1xRTT:
 		{
 			strNetType = "1xRTT";
 			break;
 		}
 	case ConstantData.NETWORK_TYPE_HSPA:
 		{
 			strNetType = "HSPA";
 			break;
 		}
 	case ConstantData.NETWORK_TYPE_EHRPD:
 		{
 			strNetType = "EHRPD";
 			break;
 		}
 	case ConstantData.NETWORK_TYPE_HSPAP:
 		{
 			strNetType = "HSPAP";
 			break;
 		}
 	default:
 		{
 			strNetType = "Unknown";
 			break;
 		}
  		}
  	}
 	/**
      * @category 监听手机网络状态变化,获取网络参数
      * @return null
      * @param  null
      * @author zy
      */	
 	 private class MyPhoneStateListener extends PhoneStateListener {
 		 private int lastStrength = 0;

 		 public void onSignalStrengthsChanged(SignalStrength signalStrength) 
 		 { 		
   		 strRSRP = "unkown";
 		 strRSRQ="unkown";
 		 strSNR ="unkown";
 		 strRSSI ="unkown";
 		  super.onSignalStrengthsChanged(signalStrength);
            try {	   		   
 		     String strSigInfo = signalStrength.toString();
 		     String[] strInfoList = strSigInfo.split(" ");
 		     if(strInfoList.length >12)
 		     {
 		    	 strRSSI = strInfoList[8];
 				 strRSRP = strInfoList[9];
 				 strRSRQ = strInfoList[10];
 				 strSNR  = strInfoList[11];		
 				 TaskInfo.SgInst().SetCurrentRSRP(strRSRP);
 				 TaskInfo.SgInst().SetCurrentSNR(strSNR);
 		     }	    
            }
            catch(Exception e)
            {
         	   e.printStackTrace();
            }
 		 }
 	 }
 	/**
      * @category 手机电量监控
      * @return 手机电量监控
      * @param  null
      * @author zy
      */	
 	public class BatteryListen extends BroadcastReceiver{
 		
 		@Override
 		public void onReceive(Context context, Intent intent) 
 		{
 			
 			if(Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction()))
 			{
 				//鑾峰彇褰撳墠鐢甸噺
 				int level = intent.getIntExtra("level", 0);
 				//鐢甸噺鐨勬�鍒诲害
 				int scale = intent.getIntExtra("scale", 100);
 				//杞垚鐧惧垎姣�			
 				strBatterryLevel = (level*100)/scale+"";
 			}
 		}				
 	}

}
