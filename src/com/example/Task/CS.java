package com.example.Task;

import com.android.volley.Request.Method;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import com.android.internal.telephony.ITelephony;

public class CS {

	private Context mcontext = null;
	private ITelephony  iTelephony;
    private TelephonyManager manager;  
    
    public static CS cs = null;
    
    public CS()
    {}
    
	public static CS sgInst()
	{
		if(cs == null)
		{
			cs = new CS();
		}
		return cs;
	}
	
	public void StartCStest(Context context,String strTelNumber,String strTelCnt,String strTelTime,String strDelayTime)
	{
		mcontext = context;
	    int nTelCnt = Integer.parseInt(strTelCnt); 
	    int nTelTime = Integer.parseInt(strTelTime); 
	    int nDelayTime = Integer.parseInt(strDelayTime); 
	    for(int i=0 ;i< nTelCnt;i++)
	    {
    		phoner();           		
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + strTelNumber));
            //通知activtity处理传入的call服务
            mcontext.startActivity(intent);  
           
            try 
            {
            	Thread.sleep(nTelTime*1000);
    		} 
            catch (InterruptedException e1)
            {
    			e1.printStackTrace();
    		}
            
    		try
    		{
    			iTelephony.endCall();
    		}
    		catch (Exception e) 
    		{
    			e.printStackTrace();
    		}
            try
            {
            	Thread.sleep(nDelayTime*1000);
     		} 
            catch (InterruptedException e1)
            {
     			e1.printStackTrace();
     		}
	    }
	}
	
    public void phoner()
    {
    	manager = (TelephonyManager)mcontext.getSystemService(Context.TELEPHONY_SERVICE);
        Class <TelephonyManager> c = TelephonyManager.class;  
        java.lang.reflect.Method getITelephonyMethod = null;
        
        try
        {  
            getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[])null);  
            getITelephonyMethod.setAccessible(true);  
            iTelephony = (ITelephony) getITelephonyMethod.invoke(manager, (Object[])null);  
        } 
        catch (IllegalArgumentException e) 
        {  
            e.printStackTrace();  
        } 
        catch (Exception e) 
        {  
            e.printStackTrace();  
        } 
    }
}
