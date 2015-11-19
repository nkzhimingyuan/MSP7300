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
    * @category 构造函数
    * @return null
    * @param  null
    * @author zy
    */
   public TaskInfo()
   {
	 
   }
	/**
    * @category 单例
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
    * @category 设置任务执行状态
    * @return null
    * @param  bTaskflag:任务是否在执行
    * @author zy
    */
   public void SetTaskONFlag(boolean bTaskflag)
   {
	   bTaskON = bTaskflag;
   }
	/**
    * @category 获取任务是否在执行
    * @return null
    * @param  null
    * @author zy
    */
   public boolean GetTaskONFlag()
   {
	   return bTaskON;
   }
	/**
    * @category 设置IMEI
    * @return null
    * @param  null
    * @author zy
    */
   public void SetIMEI(String strimei)
   {
	   strIMEI = strimei;
   }
	/**
    * @category 获取IMEI
    * @return null
    * @param  null
    * @author zy
    */
   public String GetIMEI()
   {
	   return strIMEI;
   }
	/**
    * @category 设置当前正在执行命令
    * @return null
    * @param  null
    * @author zy
    */
   public void SetCurrentRSRP(String strrsrp)
   {
	   strRSRP = strrsrp;
   }
	/**
    * @category 获取当前正在执行的命令
    * @return null
    * @param  null
    * @author zy
    */
   public String GetCurrentRSRP()
   {
	   return strRSRP;
   }
	/**
    * @category 设置当前正在执行命令
    * @return null
    * @param  null
    * @author zy
    */
   public void SetCurrentSNR(String strsnr)
   {
	   strSNR = strsnr;
   }
	/**
    * @category 获取当前正在执行的命令
    * @return null
    * @param  null
    * @author zy
    */
   public String GetCurrentSNR()
   {
	   return strSNR;
   }
	/**
    * @category 设置当前正在执行命令
    * @return null
    * @param  null
    * @author zy
    */
   public void SetCurrentCmd(String strcmd)
   {
	   strCurrentCmd = strcmd;
   }
	/**
    * @category 获取当前正在执行的命令
    * @return null
    * @param  null
    * @author zy
    */
   public String GetCurrentCmd()
   {
	   return strCurrentCmd;
   }
	/**
    * @category 设置当前执行的任务名称
    * @return null
    * @param  null
    * @author zy
    */
   public void SetCurrentTask(String strtask)
   {
	   strCurrentTask = strtask;
   }
	/**
    * @category 获取当前执行的任务名称
    * @return null
    * @param  null
    * @author zy
    */
   public String GetCurrentTask()
   {
	   return strCurrentTask;
   }
}
