package com.example.Adapter;

import android.app.Fragment;

import com.example.msp7300_client.Test_Fragment_view;

public class LogControl {
    public  Test_Fragment_view myTestView;
	public static LogControl sginst = null;
	/**
     * @category ����ģʽ
     * @return null
     * @param  null
     * @author zy
     */	
	public static LogControl sgInst()
	{
		if(sginst==null)
		{
			sginst = new LogControl();
		}
		return sginst;
	}
	/**
     * @category ������־,��ʾ��Test_Fragment_view�е���־����
     * @return null
     * @param  null
     * @author zy
     */	
	public void sendlog(String log){
		if(myTestView != null)
		{
			myTestView.sendLog(log);
		}

	}
	/**
     * @category ����ҵ��ִ�н���
     * @return null
     * @param  provalue:ҵ��ִ�н���
     * @author zy
     */	
	public void sendproBar(int provalue){
		if(myTestView != null)
		{
			myTestView.setProBar(provalue);
		}

	}
	
}
