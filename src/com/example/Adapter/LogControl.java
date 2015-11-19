package com.example.Adapter;

import android.app.Fragment;

import com.example.msp7300_client.Test_Fragment_view;

public class LogControl {
    public  Test_Fragment_view myTestView;
	public static LogControl sginst = null;
	/**
     * @category 单例模式
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
     * @category 发送日志,显示在Test_Fragment_view中的日志框中
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
     * @category 发送业务执行进度
     * @return null
     * @param  provalue:业务执行进度
     * @author zy
     */	
	public void sendproBar(int provalue){
		if(myTestView != null)
		{
			myTestView.setProBar(provalue);
		}

	}
	
}
