package com.example.Constantdata;

public class ConstantData {
     //NetType define
	public static final int NETWORK_TYPE_UNKNOWN        =0;
	public static final int NETWORK_TYPE_GPRS           =     1;
	public static final int NETWORK_TYPE_EDGE           =     2; 
	public static final int NETWORK_TYPE_CDMA            =    4; 
	public static final int NETWORK_TYPE_EVDO_0          =    5;
	public static final int NETWORK_TYPE_EVDO_A          =    6;
	public static final int NETWORK_TYPE_EVDO_B          =   12;
	public static final int NETWORK_TYPE_HSDPA           =    8;
	public static final int NETWORK_TYPE_UMTS            =    3;
	public static final int NETWORK_TYPE_LTE             =   13;
	public static final int NETWORK_TYPE_IDEN            =   11;
	public static final int NETWORK_TYPE_HSUPA           =    9; 
	public static final int NETWORK_TYPE_1xRTT           =    7;
	public static final int NETWORK_TYPE_HSPA             =  10;
	public static final int NETWORK_TYPE_EHRPD            =  14;
	public static final int NETWORK_TYPE_HSPAP            =  15;
	//Phone_Info Msg define
	public static final int RSRP_MSG  = 0;
	public static final int RSRQ_MSG  = 0;
	public static final int SNR_MSG  = 0;
	public static final int RSSI_MSG  = 0;
	public static final int NETSTATE_MSG  = 0;
	
	//IE define
	public static final int  UE_ID       =    0;
	public static final int  BATTERY_LEV   =  1;
	public static final int  CORE_IP      =   2;
	public static final int  DL_SP       =    3;
	public static final int  IMSI        =    4;
	public static final int  MSP_VERSION  =   5;
	public static final int  NET_STAT      =  6;
	public static final int NET_TYPE      =  7;
	public static final int PLMN         =   8;
	public static final int CMD_STAT     =   9;
	public static final int RSRP          =  10;
	public static final int SNR           =  11;
	public static final int RSSI          =  12;
	public static final int RSRQ          =  13;
	public static final int UE_NAME       =  14;
	public static final int UL_SP         =  15;
	public static final int TASK          =  16;
	public static final int COREIP        =  17;
	//Task typeID
	public static final int DL_TASK  = 0;
	public static final int UL_TASK  = 1;
	public static final int STOPDL_TASK  = 2;
	public static final int STOPUL_TASK  = 3;
	public static final int HTTP_TASK = 4;
	public static final int CS_TASK = 5;
	public static final int AIR_TASK = 6;
	public static final int PING_TASK = 7;
	public static final int DELAY_TASK = 8;
	
	public static final int PC_BROADCAST=0x0001;//下行广播消息
	public static final int UE_REGISTER_REQ=0x0002;//上行注册请求
	public static final int PC_REGISTER_RSP=0x0003;//注册应答
	public static final int UE_HEARTBEAT=0x0004;//心跳消息
	public static final int PC_COMMAND=0x0005;//业务命令
	public static final int UE_COMMAND_RSP=0x0006;//命令回应
	public static final int PC_HEARTBEAT_RSP=0x0007;//心跳回应
	public static final int UE_REPORT=0x0008;//
	public static final int UE_IEREPORT=0x0009;//
	public static final int UE_STATEREPORT=0x000A;//
	public static final int PC_REFRESHUESTATE=0x000B;//
	public static final int PC_NOTICE=0x000C;//
	public static final int UE_SERVCOUNTREPORT = 0x000D;//上报业务统计用于生成报表
	public static final int UE_TRAFFICREPORT = 0x000E;//上报业务统计用于生成报表
	public static final int PC_COMMAND_RSP = 0x000F;//命令结果下发后收到的确认
	public static final int UE_PS_REPORT = 0x0010;//PS结果下发
	public static final int PC_EXCUTE_TASK = 0x0010;
	public static final int UE_UPLOADLOG_REQ = 0x0011;
	public static final int PC_UPLOADLOG_PERMISSION = 0x0012;
	//constant
	public static final int IMEI_LEN = 15;
	public static final int MAXMSGLEN = 512;
	//UDP Socket 
    static final String SERVER_ADDR = "192.168.1.101";
    static final int SERVER_PORT = 4567;    
    public static final int CLIENT_PORT = 4568;  
//	public static final int RSRP_MSG  = 0;
//	public static final int RSRP_MSG  = 0;
//	public static final int RSRP_MSG  = 0;
}
