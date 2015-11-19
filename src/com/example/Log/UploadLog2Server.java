package com.example.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import android.os.Environment;

import com.example.Adapter.InterfaceSPMonitor;
import com.example.Adapter.LogControl;
import com.example.Config.FilePathConf;
import com.example.Config.LogFTPConf;
import com.example.Task.FTP;
import com.example.Task.Result;
import com.example.Task.TaskInfo;
import com.example.Task.Util;

public class UploadLog2Server {
	private String hostName;
    private String userName;
    private String UploadFileName=null;
    private String password;

    private FTPClient ftpClient;
    private List<FTPFile> list;
    public static final String REMOTE_PATH = "\\";

    private String currentPath = "";
    
    private boolean bkeepFTP = true;
    private boolean bkeepFTPU = true;

    private long ldownloaded = 0;
    private long lFilesize  = 0;
    /**
     */
    private double response;
    private String strFTPNetInterface;
    static UploadLog2Server uploadlog = null;
    
    /**
     * @param host hostName 
     * @param user userName 
     * @param pass password 
     */
    public UploadLog2Server() {
        userName = LogFTPConf.FTP_USER;
        password = LogFTPConf.FTP_PWD;
        
        this.ftpClient = new FTPClient();
        this.list = new ArrayList<FTPFile>();
    }
    /**
     * @category:单例
     * @param null
     * @return null
     * @author zy
     */
    public static UploadLog2Server sgInst()
    {
    	if(uploadlog == null)
    	{
    		uploadlog = new UploadLog2Server();
    	}
    	return uploadlog;
    }
    /**
     * @param host hostName 
     * @param user userName 
     * @return 
     */   
    public void SetFTPUStop()
    {
    	bkeepFTPU = false;
    }
    /**
     * @category 上传日志
     * @param strLogName 日志名称 
     * @return 成功/失败
     * @author zy
     */   
    public boolean UploadLog(String strLogName)
    {
        try
    	{     
            openConnect();
		    //要上传的本地文件
    		String strLocalFile =  FilePathConf.sgInst().GetDatabasePathDir() + strLogName;
    		File hLocalFile = new File(strLocalFile);
            uploading(hLocalFile,LogFTPConf.Get_FTP_RemotePath());//进行上传业务
                             		
    		//closeConnect();//关闭上传连接
    		return true;

    	}
    	catch (Exception e)
    	{
    		LogControl.sgInst().sendlog("上传日志线程出现异常!请检查网络或参数!失败原因:"+e.toString()+"\n");
    		e.printStackTrace(); 
    		return false;
    	}
    }
    /**
     * @throws IOException 
     */
    public void openConnect() throws IOException {
    	hostName = LogFTPConf.Get_FTP_ADDR();
    	GetIPInfo();
    	String ipAddress = strFTPNetInterface;
        ftpClient.setControlEncoding("UTF-8");
        int reply; 
        //test 0629
        ftpClient.setActiveExternalIPAddress(ipAddress);
        ftpClient.connect(hostName);
        ftpClient.setBufferSize(1024*64);//婢х偛銇囩紓鎾冲暱閸栫尨绱濋幓鎰扮彯娑撳娴囬柅鐔哄芳
        reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftpClient.disconnect();
            throw new IOException("connect fail: " + reply);
        }
        ftpClient.login(userName, password);
        reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftpClient.disconnect();
            throw new IOException("connect fail: " + reply);
        } else {
            FTPClientConfig config = new FTPClientConfig(ftpClient.getSystemType().split(" ")[0]);
            config.setServerLanguageCode("zh");
            ftpClient.configure(config);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
            System.out.println("login");
        }
    }
    /**
     * @category 关闭连接
     * @param  null
     * @return null
     * @author zy
     */  
    public void closeConnect() throws IOException {
        if (ftpClient != null) {
            ftpClient.logout();
            ftpClient.disconnect();
            System.out.println("logout");
        }
    }

    /**
     * @param remotePath FTP閺堝秴濮熼崳銊ㄧ熅瀵帮拷
     * @return FTPFile
     * @throws IOException 
     */
    public List<FTPFile> listFiles(String remotePath) throws IOException {
        FTPFile[] files = ftpClient.listFiles(remotePath);
        for (FTPFile file : files) {
            list.add(file);
        }
        return list;
    }

    /**
     * @param localFile 閺堫剙婀撮弬鍥︽
     * @param remotePath FTP鐠侯垰绶�     * @return Result
     * @throws IOException 
     */
    public Result uploading(File localFile, String remotePath) throws IOException {
        boolean flag = true;
        Result result = null;
        currentPath = remotePath;
        response = 0;
        ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileTransferMode(org.apache.commons.net.ftp.FTP.STREAM_TRANSFER_MODE);
       // ftpClient.changeWorkingDirectory(REMOTE_PATH);
        ftpClient.changeWorkingDirectory(remotePath);
        Date startTime = new Date();
        if (localFile.isDirectory()) {
            flag = uploadingMany(localFile);
        } else {
            flag = uploadingSingle(localFile);
        }
        Date endTime = new Date();
        result = new Result(flag, Util.getFormatTime(endTime.getTime() - startTime.getTime()), Util.getFormatSize(response));
        return result;
    }

    /**
     * @param localFile 閺堫剙婀撮弬鍥︽
     * @return true
     * @throws IOException 
     */
    private boolean uploadingSingle(File localFile) throws IOException {
        boolean flag = true;
        InputStream inputStream = new FileInputStream(localFile);
        response += (double) inputStream.available() / 1;		
		try
		{
             flag = ftpClient.storeFile(localFile.getName(), inputStream);

			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
       
        inputStream.close();
        return flag;
    }

    /**
     * @param localFile 閺堫剙婀撮弬鍥︽
     * @return true
     * @throws IOException 
     */
    private boolean uploadingMany(File localFile) throws IOException {
        boolean flag = true;     
        if (!currentPath.equals(REMOTE_PATH)) {
            currentPath = currentPath + REMOTE_PATH + localFile.getName();
        } else {
            currentPath = currentPath + localFile.getName();
        }
        ftpClient.makeDirectory(currentPath);
        ftpClient.changeWorkingDirectory(currentPath);
        File[] files = localFile.listFiles();
        for (File file : files) {
            if (file.isHidden()) {
                continue;
            }
            if (file.isDirectory()) {
                flag = uploadingMany(file);
            } else {
                flag = uploadingSingle(file);
            }
        }
        return flag;
    }
    
    /********************************************************************************************
     * GetIPInfo
     ********************************************************************************************/     
    /** GetIPInfo */
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
//	                        	if(strinet.contains("192.168."))
//	                        	{	                        
//	                                continue;
//	                        	}
	                            strFTPNetInterface = strinet;                         
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
}
