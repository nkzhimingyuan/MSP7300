package com.example.Adapter;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import com.example.msp7300_client.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import com.example.Constantdata.ConstantData;
import com.example.Task.TaskInfo;

public class PhoneInfo_Fragment_adapter {

	
	private String strIMEI = "unkown";
	private String strIMSI = "unkown";
	private String strRSRP = "unkown";
	private String strRSRQ = "unkown";
	private String strSNR = "unkown";
	private String strRSSI = "unkown";
	private String strNetState = "unkown";
	private String strNetType = "unkown";
	private String strBatteryLevel = "unkown";
	private String strCoreIP = "unkown";
	private String strPLMN = "unkown";
	private String strCmdStat = "unkown";
	private String strTask = "unkown";
	
	private Context context;
	/**
     * @category ���캯��
     * @return null
     * @param  null
     * @author zy
     */	
 	public PhoneInfo_Fragment_adapter(Context context){
 		this.context =  context;
	}
	/**
     * @category ��ȡ��������
     * @return null
     * @param  null
     * @author zy
     */	
 	public String GetDLsp()
 	{
 		String strDLsp = InterfaceSPMonitor.SgInst().GetDLsp();
 		return strDLsp;
 	}
	/**
     * @category ��ȡ�ϴ�����
     * @return null
     * @param  null
     * @author zy
     */	
 	public String GetULsp()
 	{
 		String strULsp = InterfaceSPMonitor.SgInst().GetULsp();
 		return strULsp;
 	}
	/**
     * @category ��ȡ��ǰִ����������
     * @return null
     * @param  null
     * @author zy
     */	
 	public String GetCMDSTAT()
 	{
 		strCmdStat = TaskInfo.SgInst().GetCurrentCmd();
 		return strCmdStat;
 	}
	/**
     * @category ��ȡ��ǰִ����������
     * @return null
     * @param  null
     * @author zy
     */	
 	public String GetTASK()
 	{
 		strTask =  TaskInfo.SgInst().GetCurrentTask();
 		return strTask;
 	}


	/**
     * @category ��ȡ����ʹ�����
     * @return BatteryLevel
     * @param  null
     * @author zy
     */	
 	public String GetBattery()
 	{
 		strBatteryLevel = UESystemInfo.sgInst().GetBattery();
 		return strBatteryLevel;
 	}
	/**
     * @category ��ȡ�ն˷���IP 
     * @return CoreIP 
     * @param  null
     * @author zy
     */	
 	public String GetCoreIP()
 	{
 		strCoreIP = UESystemInfo.sgInst().GetCoreIP();
 		return strCoreIP;	
 	}
	/**
     * @category ��ȡ�ն�IMEI��
     * @return IMEI
     * @param  null
     * @author zy
     */	
 	public String GetIMEI()
 	{		
 		strIMEI = UESystemInfo.sgInst().GetIMEI();

 		return strIMEI;
 	}
	/**
     * @category ��ȡ�ն�IMSI��
     * @return IMSI
     * @param  null
     * @author zy
     */	
 	public String GetIMSI()
 	{
 		strIMSI = UESystemInfo.sgInst().GetIMSI();
 		return strIMSI;
 	}
	/**
     * @category ��ȡRSRPֵ
     * @return RSRP
     * @param  null
     * @author zy
     */	
 	public String GetRSRP()
 	{
 		strRSRP = UESystemInfo.sgInst().GetRSRP();
 		return strRSRP;
 	}
	/**
     * @category ��ȡ�ն�RSRQֵ
     * @return RSRQ
     * @param  null
     * @author zy
     */	
 	public String GetRSRQ()
 	{
 		strRSRQ = UESystemInfo.sgInst().GetRSRQ();
 		return strRSRQ;
 	}
	/**
     * @category ��ȡSNR
     * @return SNR
     * @param  null
     * @author zy
     */	
 	public String GetSNR()
 	{
 		strSNR = UESystemInfo.sgInst().GetSNR();
 		return strSNR;
 	}
	/**
     * @category ��ȡRSSIֵ
     * @return RSSI
     * @param  null
     * @author zy
     */	
 	public String GetRSSI()
 	{
 		strRSSI = UESystemInfo.sgInst().GetRSSI();
 		return strRSSI;
 	}
	/**
     * @category ��ȡ����״̬
     * @return ��ȡ����״̬
     * @param  null
     * @author zy
     */	
 	public String GetNetState()
 	{
 		strNetState = UESystemInfo.sgInst().GetNetState();
 		return strNetState;
 	}
	/**
     * @category ��ȡ��������
     * @return ��������
     * @param  null
     * @author zy
     */	
 	public String GetNetType()
 	{
 		strNetType = UESystemInfo.sgInst().GetNetType();
 		return strNetType;
 	}

}
