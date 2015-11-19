package com.example.msp7300_client;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.Constantdata.ConstantData;
import com.example.Task.Task;
import com.example.Adapter.PhoneInfo_Fragment_adapter;
/**
 * A dummy fragment representing a section of the app, but that simply
 * displays dummy text.
 */
@SuppressLint("ValidFragment")
public class PhoneInfo_Fragment_view extends Fragment{

	 private TextView IMEI_textview = null;
	 private TextView IMSI_textview = null;
	 private TextView CoreIP_textview = null;
	 private TextView RSRP_textview = null;
	 private TextView RSRQ_textview = null;
	 private TextView SNR_textview = null;
	 private TextView RSSI_textview = null;
	 private TextView NetStat_textview = null;
	 private TextView NetType_textview = null;
	 private TextView DLSp_textview = null;
	 private TextView ULSp_textview = null;
	 private TextView Task_textview = null;
	 private TextView Cmd_textview  = null;
	 private Context context;
	 private PhoneInfo_Fragment_adapter phoneinfo_adapter;
	 private boolean bRefreshthreadRun = false;
		/**
	     * @category 新建Handeler
	     * @return null
	     * @param  null
	     * @author zy
	     */	
	   Handler myHandler = new Handler() 
	     {	 		
			@Override 
	 		public void handleMessage(Message msg)
	 		{	
	 			super.handleMessage(msg);
	 			//Excute cmd
	 			ExcuteMsg(msg);
	 		}
	 		
	     };
	 	/**
	      * @category 构造函数
	      * @return null
	      * @param  context:Context 
	      * @author zy
	      */	
	 	public PhoneInfo_Fragment_view(Context context){
	 		this.context = context;
	 		 phoneinfo_adapter = new PhoneInfo_Fragment_adapter(context);
		}
	 	
		/**
	     * @category 执行消息内容
	     * @return null
	     * @param  msg:消息 
	     * @author zy
	     */	 
	     public void ExcuteMsg(Message msg)
	     {
	    	 
	    	 if(msg.arg1 != 98)
	    	 {
	    		 return;
	    	 }
	    	 switch(msg.what)
	    	 {
	    	 case ConstantData.RSRP: 
	    		 RSRP_textview.setText(msg.obj.toString());
	    		 break;
	    	 case ConstantData.SNR: 
	    		 SNR_textview.setText(msg.obj.toString());
	    		 break;
	    	 case ConstantData.RSSI:
	    		 RSSI_textview.setText(msg.obj.toString());
	    		 break;
	    	 case ConstantData.RSRQ: 
	    		 RSRQ_textview.setText(msg.obj.toString());
	    		 break;
	    	 case ConstantData.NET_STAT: 
	    		 NetStat_textview.setText(msg.obj.toString());
	    		 break;
	    	 case ConstantData.NET_TYPE: 
	    		 NetType_textview.setText(msg.obj.toString());
	    		 break;
	    	 case ConstantData.DL_SP: 
	    		 DLSp_textview.setText(msg.obj.toString());
	    		 break;
	    	 case ConstantData.UL_SP: 
	    		 ULSp_textview.setText(msg.obj.toString());
	    		 break;
	    	 case ConstantData.TASK: 
	    		 Task_textview.setText(msg.obj.toString());
	    		 break;
	    	 case ConstantData.CMD_STAT: 
	    		 Cmd_textview.setText(msg.obj.toString());
	    		 break;
	    	 case ConstantData.COREIP:
	    		 CoreIP_textview.setText(msg.obj.toString());
	    		 break;
	    	 default:
	    	     break;
	    	 }

	     }
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	public static final String ARG_SECTION_NUMBER = "section_number";

	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.phoneinfo_view,
				container, false);
		
		 IMEI_textview = (TextView)rootView.findViewById(R.id.IMEI_Value);
	     IMSI_textview =  (TextView)rootView.findViewById(R.id.IMSI_Value);
	     CoreIP_textview = (TextView)rootView.findViewById(R.id.CoreIP_Value); 
		 RSRP_textview =  (TextView)rootView.findViewById(R.id.RSRP_Value);
		 RSRQ_textview =  (TextView)rootView.findViewById(R.id.RSRQ_value);
		 SNR_textview =  (TextView)rootView.findViewById(R.id.SNR_Value);
		 RSSI_textview =  (TextView)rootView.findViewById(R.id.RSSI_Value);
		 NetStat_textview =  (TextView)rootView.findViewById(R.id.NetWorkStat_Value);
		 NetType_textview =  (TextView)rootView.findViewById(R.id.NetWorkType_Value);
		 DLSp_textview =  (TextView)rootView.findViewById(R.id.DLSp_Value);
		 ULSp_textview =  (TextView)rootView.findViewById(R.id.ULSp_Value);
		 Task_textview =  (TextView)rootView.findViewById(R.id.Task_Value);
		 Cmd_textview =  (TextView)rootView.findViewById(R.id.Cmd_Value);
		 
		 IMEI_textview.setText(phoneinfo_adapter.GetIMEI());
		 IMSI_textview.setText(phoneinfo_adapter.GetIMSI());
     	try
     	{
     		bRefreshthreadRun= true;
     		RefreshViewThread refreshThread= new RefreshViewThread();
     		refreshThread.start();
     		
     	}
     	catch (Exception e)
     	{
     		e.printStackTrace();
     	}
		// TextView dummyTextView = (TextView) rootView
		// .findViewById(R.id.section_label);
		// dummyTextView.setText(Integer.toString(getArguments().getInt(
		// ARG_SECTION_NUMBER)));
		return rootView;
	}
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
	}
	/**
     * @category 刷新界面线程
     * @return null
     * @param  null
     * @author zy
     */	 
	 class RefreshViewThread extends Thread {
		    private int nRefreshIPCnt = 0;
		 	public RefreshViewThread() {
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
                 while(bRefreshthreadRun)
                 {
                	 
                	if(nRefreshIPCnt == 0)//由于CoreIP不会经常变动 界面刷新20次 刷新1次CoreIP
                	{
                    	Message msg = myHandler.obtainMessage();
                    	msg.arg1 = 98;
                 		msg.what = ConstantData.COREIP;            	
                 		msg.obj = phoneinfo_adapter.GetCoreIP();
                 		myHandler.sendMessage(msg);	  
                 		
                 		try
                 		{
                 			Thread.sleep(200);
                 		}
                 		catch(Exception e)
                 		{
                 			e.printStackTrace();
                 		} 
                 		nRefreshIPCnt = 20;
                	}
                	nRefreshIPCnt--;
                	Message msg = myHandler.obtainMessage();
               		msg.arg1 = 98;
            		msg.what = ConstantData.RSRP;            	
            		msg.obj = phoneinfo_adapter.GetRSRP();
            		myHandler.sendMessage(msg);	  
            		
            		try
            		{
            			Thread.sleep(200);
            		}
            		catch(Exception e)
            		{
            			e.printStackTrace();
            		}
            		msg = myHandler.obtainMessage();
            		msg.arg1 = 98;
            		msg.what = ConstantData.SNR;            	
            		msg.obj = phoneinfo_adapter.GetSNR();
            		myHandler.sendMessage(msg);	  
            		
            		try
            		{
            			Thread.sleep(200);
            		}
            		catch(Exception e)
            		{
            			e.printStackTrace();
            		}
            		msg = myHandler.obtainMessage();
            		msg.arg1 = 98;
            		msg.what = ConstantData.RSRQ;            	
            		msg.obj = phoneinfo_adapter.GetRSRQ();
            		myHandler.sendMessage(msg);	  
            		
            		try
            		{
            			Thread.sleep(200);
            		}
            		catch(Exception e)
            		{
            			e.printStackTrace();
            		}
            		msg = myHandler.obtainMessage();
            		msg.arg1 = 98;
            		msg.what = ConstantData.RSSI;            	
            		msg.obj = phoneinfo_adapter.GetRSSI();
            		myHandler.sendMessage(msg);	  
            		
            		try
            		{
            			Thread.sleep(200);
            		}
            		catch(Exception e)
            		{
            			e.printStackTrace();
            		}
               		msg = myHandler.obtainMessage();
               		msg.arg1 = 98;
            		msg.what = ConstantData.NET_STAT;            	
            		msg.obj = phoneinfo_adapter.GetNetState();
            		myHandler.sendMessage(msg);	  
            		
            		try
            		{
            			Thread.sleep(200);
            		}
            		catch(Exception e)
            		{
            			e.printStackTrace();
            		}
               		msg = myHandler.obtainMessage();
               		msg.arg1 = 98;
            		msg.what = ConstantData.NET_TYPE;            	
            		msg.obj = phoneinfo_adapter.GetNetType();
            		myHandler.sendMessage(msg);	  
            		
            		try
            		{
            			Thread.sleep(200);
            		}
            		catch(Exception e)
            		{
            			e.printStackTrace();
            		}
               		msg = myHandler.obtainMessage();
               		msg.arg1 = 98;
            		msg.what = ConstantData.DL_SP;            	
            		msg.obj = phoneinfo_adapter.GetDLsp();
            		myHandler.sendMessage(msg);	  
            		
            		try
            		{
            			Thread.sleep(100);
            		}
            		catch(Exception e)
            		{
            			e.printStackTrace();
            		}
               		msg = myHandler.obtainMessage();
               		msg.arg1 = 98;
            		msg.what = ConstantData.UL_SP;            	
            		msg.obj = phoneinfo_adapter.GetULsp();
            		myHandler.sendMessage(msg);	  
            		
            		try
            		{
            			Thread.sleep(100);
            		}
            		catch(Exception e)
            		{
            			e.printStackTrace();
            		}
            		
            		String Taskname = phoneinfo_adapter.GetTASK();
            		if(Taskname == null)
            		{
            			Taskname = "null";
            		}
               		msg = myHandler.obtainMessage();
               		msg.arg1 = 98;
            		msg.what = ConstantData.TASK;            	
            		msg.obj = Taskname;
            		myHandler.sendMessage(msg);	  
            		
            		try
            		{
            			Thread.sleep(100);
            		}
            		catch(Exception e)
            		{
            			e.printStackTrace();
            		}
            		
            		String Cmdname = phoneinfo_adapter.GetCMDSTAT();
            		if(Cmdname == null)
            		{
            			Cmdname = "null";
            		}
               		msg = myHandler.obtainMessage();
               		msg.arg1 = 98;
            		msg.what = ConstantData.CMD_STAT;            	
            		msg.obj = Cmdname;
            		myHandler.sendMessage(msg);	  
            		
            		try
            		{
            			Thread.sleep(100);
            		}
            		catch(Exception e)
            		{
            			e.printStackTrace();
            		}
                 }
	    	}
	        public void exit() {            
	        }
	 }

}
