package com.example.Task;

public class TaskInfo {
	
   public static TaskInfo taskinfo = null;
   private String strCurrentCmd = null;
   private String strCurrentTask = null;
   private String strRSRP = null;
   private String strSNR = null;
   private String strIMEI = null;
   private boolean bTaskON = false;
	/**
    * @category ���캯��
    * @return null
    * @param  null
    * @author zy
    */
   public TaskInfo()
   {
	 
   }
	/**
    * @category ����
    * @return null
    * @param  null
    * @author zy
    */
   public static TaskInfo SgInst()
   {
	   if(taskinfo == null)
	   {
		   taskinfo = new TaskInfo();
	   }
	   return taskinfo;
   }
	/**
    * @category ��������ִ��״̬
    * @return null
    * @param  bTaskflag:�����Ƿ���ִ��
    * @author zy
    */
   public void SetTaskONFlag(boolean bTaskflag)
   {
	   bTaskON = bTaskflag;
   }
	/**
    * @category ��ȡ�����Ƿ���ִ��
    * @return null
    * @param  null
    * @author zy
    */
   public boolean GetTaskONFlag()
   {
	   return bTaskON;
   }
	/**
    * @category ����IMEI
    * @return null
    * @param  null
    * @author zy
    */
   public void SetIMEI(String strimei)
   {
	   strIMEI = strimei;
   }
	/**
    * @category ��ȡIMEI
    * @return null
    * @param  null
    * @author zy
    */
   public String GetIMEI()
   {
	   return strIMEI;
   }
	/**
    * @category ���õ�ǰ����ִ������
    * @return null
    * @param  null
    * @author zy
    */
   public void SetCurrentRSRP(String strrsrp)
   {
	   strRSRP = strrsrp;
   }
	/**
    * @category ��ȡ��ǰ����ִ�е�����
    * @return null
    * @param  null
    * @author zy
    */
   public String GetCurrentRSRP()
   {
	   return strRSRP;
   }
	/**
    * @category ���õ�ǰ����ִ������
    * @return null
    * @param  null
    * @author zy
    */
   public void SetCurrentSNR(String strsnr)
   {
	   strSNR = strsnr;
   }
	/**
    * @category ��ȡ��ǰ����ִ�е�����
    * @return null
    * @param  null
    * @author zy
    */
   public String GetCurrentSNR()
   {
	   return strSNR;
   }
	/**
    * @category ���õ�ǰ����ִ������
    * @return null
    * @param  null
    * @author zy
    */
   public void SetCurrentCmd(String strcmd)
   {
	   strCurrentCmd = strcmd;
   }
	/**
    * @category ��ȡ��ǰ����ִ�е�����
    * @return null
    * @param  null
    * @author zy
    */
   public String GetCurrentCmd()
   {
	   return strCurrentCmd;
   }
	/**
    * @category ���õ�ǰִ�е���������
    * @return null
    * @param  null
    * @author zy
    */
   public void SetCurrentTask(String strtask)
   {
	   strCurrentTask = strtask;
   }
	/**
    * @category ��ȡ��ǰִ�е���������
    * @return null
    * @param  null
    * @author zy
    */
   public String GetCurrentTask()
   {
	   return strCurrentTask;
   }
}
