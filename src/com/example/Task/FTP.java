package com.example.Task;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import 	java.util.ArrayList;
import  java.util.Date;
import java.util.Enumeration;
import  java.util.List;

import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;



import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.example.Adapter.LogControl;

import android.R.integer;

/*
 * FTP 
 * author liuxuhua
 * 閸掆晝鏁�apache 閻ㄥ垻ommon.net缁绨辩亸浣筋棅FTP,鐎圭偟骞囬張宥呭閸ｃ劎娈戦惂璇插弳'閺傚洣娆㈡稉瀣祰\閺傚洣娆㈡稉濠佺炊缁涘濮涢懗锟� * 閹绘劒绶甸幒銉ュ經娓氭稑顦婚柈銊ㄧ殶閻拷
 */

public class FTP {
    private String hostName;
    private String userName;

    private String password;

    private FTPClient ftpClient;
    private List<FTPFile> list;
    public static final String REMOTE_PATH = "";

    private String currentPath = "";
    
    private boolean bkeepFTP = true;
    private boolean bkeepFTPU = true;

    private long ldownloaded = 0;
    private long lFilesize  = 0;
    /**
     */
    private double response;
    private String strFTPNetInterface;
    
    
    /**
     * @param host hostName 
     * @param user userName 
     * @param pass password 
     */
    public FTP(String host, String user, String pass) {
        this.hostName = host;
        this.userName = user;
        this.password = pass;
        this.ftpClient = new FTPClient();
        this.list = new ArrayList<FTPFile>();
    }
    
    /**
     * @param host hostName 
     * @param user userName 
     * @param pass password 
     * @return 
     */
    public void SetFTPStop()
    {
    		bkeepFTP = false;
    }
    
    public void SetFTPUStop()
    {
    		bkeepFTPU = false;
    }

    /**
     * @throws IOException 
     */
    public void openConnect() throws IOException {
    	GetIPInfo();
    	String ipAddress = strFTPNetInterface;
    	LogControl.sgInst().sendlog("ftp连接使用的本地IP:"+ipAddress+"!\n");
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
     * @param remotePath FTP閺堝秴濮熼崳銊ㄧ熅瀵帮拷
     * @param fileName 娑撳娴囬弬鍥︽
     * @param localPath 閺堫剙婀寸捄顖氱窞
     * @return Result
     * @throws IOException 
     */
    public Result download(String remotePath, String fileName, String localPath) throws IOException {
        boolean flag = true;
        Result result = null;
        currentPath = remotePath;
        response = 0;
        if(ftpClient.changeWorkingDirectory(remotePath))
        {
        	String strreply = ftpClient.getReplyString();
        	LogControl.sgInst().sendlog("changeWorkingDirectory成功!reply:"+strreply+"\n");
        }
        else
        {
        	String strreply = ftpClient.getReplyString();
        	LogControl.sgInst().sendlog("changeWorkingDirectory失败!reply:"+strreply+"\n");
        }
        FTPFile[] ftpFiles = ftpClient.listFiles();
//        LogControl.sgInst().sendlog("进入下载download函数!ftpfiles:+"+ftpFiles+" remotepath:"+remotePath+"\n");
        for (FTPFile ftpFile : ftpFiles) {
//        	LogControl.sgInst().sendlog("for (FTPFile ftpFile : ftpFiles)FileName:"+fileName+" FTPFile:"+ftpFile+"\n");
            if (ftpFile.getName().equals(fileName)) {
            	 LogControl.sgInst().sendlog("ftpFile.getName().equals(fileName)\n");
                File file = new File(localPath + "/" + fileName);
                Date startTime = new Date();
                if (ftpFile.isDirectory()) {
                    flag = downloadMany(file);
                } else {
                	LogControl.sgInst().sendlog("进入下载downloadsingle函数!\n");
                    flag = downloadSingle(file, ftpFile);
                }
                Date endTime = new Date();
                result = new Result(flag, Util.getFormatTime(endTime.getTime() - startTime.getTime()), Util.getFormatSize(response));
            }
        }
        return result;
    }
    
    /**
     * @param remotePath FTP閺堝秴濮熼崳銊ㄧ熅瀵帮拷
     * @param fileName 娑撳娴囬弬鍥︽
     * @param localPath 閺堫剙婀寸捄顖氱窞
     * @return Result
     * @throws IOException 
     */
    public Result downloaduntilend(String remotePath, String fileName, String localPath) throws IOException {
        boolean flag = true;
        Result result = null;
        currentPath = remotePath;
        response = 0;
        ftpClient.changeWorkingDirectory(remotePath);
        FTPFile[] ftpFiles = ftpClient.listFiles();
        for (FTPFile ftpFile : ftpFiles) {
            if (ftpFile.getName().equals(fileName)) {
                System.out.println("download...");
                File file = new File(localPath + "/" + fileName);
                Date startTime = new Date();
                if (ftpFile.isDirectory()) {
                    flag = downloadMany(file);
                } else {
                    flag = downloadSingleuntilend(file, ftpFile);
                }
                Date endTime = new Date();
                result = new Result(flag, Util.getFormatTime(endTime.getTime() - startTime.getTime()), Util.getFormatSize(response));
            }
        }
        return result;
    } 
    /**
     * @param remotePath FTP閺堝秴濮熼崳銊ㄧ熅瀵帮拷
     * @param fileName 娑撳娴囬弬鍥︽
     * @param localPath 閺堫剙婀寸捄顖氱窞
     * @return Result
     * @throws IOException 
     */
    public Result downloadAPK(String remotePath, String fileName, String localPath) throws IOException {
        boolean flag = true;
        Result result = null;
        currentPath = remotePath;
        response = 0;
        //ftpClient.changeWorkingDirectory(remotePath);
        FTPFile[] ftpFiles = ftpClient.listFiles();
        for (FTPFile ftpFile : ftpFiles) {
            if (ftpFile.getName().equals(fileName)) {
                System.out.println("download...");
                File file = new File(localPath + "/" + fileName);
                Date startTime = new Date();
                if (ftpFile.isDirectory()) {
                    //flag = downloadMany(file);
                } else {
                    flag = downloadSingleAPK(file, ftpFile);
                    break;
                }
                Date endTime = new Date();
                result = new Result(flag, Util.getFormatTime(endTime.getTime() - startTime.getTime()), Util.getFormatSize(response));
            }
        }
        return result;
    }
    /**
     * @param localFile 閺堫剙婀撮弬鍥︽
     * @param ftpFile FTP閺傚洣娆�     * @return true
     * @throws IOException 
     */
    private boolean downloadSingleuntilend(File localFile, FTPFile ftpFile) throws IOException {
        boolean flag = true;
        try {
            //test 0620
            //OutputStream outputStream = new FileOutputStream(localFile);
            //ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            //test 0620e
        	InputStream mInputStream = null;
			mInputStream = ftpClient.retrieveFileStream(localFile.getName());
			int c = 0; 
			int nExcept = 0;
			byte []bytes = new byte[1024*64];
			ldownloaded = 0;
			lFilesize = ftpFile.getSize();
			//test 0731
			/*
			while ((c = mInputStream.read(bytes)) != -1 && bkeepFTP) {
	
				
			}*/
			
			
			while (bkeepFTP) {
				
				//test 0805
				
				try
				{
					
					c = mInputStream.read(bytes);
					ldownloaded += c;
					if(ldownloaded>lFilesize)
					{
						break;
					}
				}
				catch(Exception e)
				{

					nExcept++;
					e.printStackTrace();
					if(nExcept == 5)
					{
						break;
					}
				}
	

			}
			mInputStream.close();
			response += ftpFile.getSize();
            //flag = ftpClient.retrieveFile(localFile.getName(), outputStream);
            //outputStream.close();
            
		} catch (Exception e) {
			// TODO: handle exception
		}

        return flag;
    }
    /**
     * @param localFile 閺堫剙婀撮弬鍥︽
     * @param ftpFile FTP閺傚洣娆�     * @return true
     * @throws IOException 
     */
    public String getDownloadedpercent()
    {
        double dSuccessPercent = 0.0;
        String strPercent = "0";
        if(lFilesize > 0)
        {
        	dSuccessPercent = (double)ldownloaded / (double)lFilesize;
        	strPercent = Integer.toString((int)(dSuccessPercent * 100)) + "%";
        }
        else
        {
        	strPercent = "0%";
        }
    	return strPercent;
    }
    /**
     * @param localFile 閺堫剙婀撮弬鍥︽
     * @param ftpFile FTP閺傚洣娆�     * @return true
     * @throws IOException 
     */
    private boolean downloadSingle(File localFile, FTPFile ftpFile) throws IOException {
        boolean flag = true;
        try {
            //test 0620
            //OutputStream outputStream = new FileOutputStream(localFile);
            //ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            //test 0620e
        	InputStream mInputStream = null;
			mInputStream = ftpClient.retrieveFileStream(localFile.getName());
			int c = 0; 
			byte []bytes = new byte[1024*64];
			ldownloaded = 0;
			lFilesize = ftpFile.getSize();
			LogControl.sgInst().sendlog("下载线程进入downloadsingle函数!\n");
			//test 0731
			/*
			while ((c = mInputStream.read(bytes)) != -1 && bkeepFTP) {
	
				
			}*/
			
			
			while (bkeepFTP) {
				
				//test 0805
				
				try
				{
					c = mInputStream.read(bytes);
					ldownloaded += c;
				}
				catch(Exception e)
				{
					LogControl.sgInst().sendlog("下载线程read异常!\n");
					e.printStackTrace();
				}
	

			}
			mInputStream.close();
            response += ftpFile.getSize();
            LogControl.sgInst().sendlog("下载线程结束,response:"+response+"\n");
            //flag = ftpClient.retrieveFile(localFile.getName(), outputStream);
            //outputStream.close();
            
		} catch (Exception e) {
			LogControl.sgInst().sendlog("下载线程异常!原因:"+e.toString()+"\n");
			// TODO: handle exception
		}

        return flag;
    }
    
    /**
     * @param localFile 閺堫剙婀撮弬鍥︽
     * @param ftpFile FTP閺傚洣娆�     * @return true
     * @throws IOException 
     */
    private boolean downloadSingleAPK(File localFile, FTPFile ftpFile) throws IOException {
        boolean flag = true;
        OutputStream outputStream = new FileOutputStream(localFile);
        response += ftpFile.getSize();
        flag = ftpClient.retrieveFile(localFile.getName(), outputStream);
        outputStream.close();
        return flag;
    }

    /**
     * @param localFile 閺堫剙婀撮弬鍥︽
     * @return true
     * @throws IOException 
     */
    private boolean downloadMany(File localFile) throws IOException {
        boolean flag = true;
        if (!currentPath.equals(REMOTE_PATH)) {
            currentPath = currentPath + REMOTE_PATH + localFile.getName();
        } else {
            currentPath = currentPath + localFile.getName();
        }
        localFile.mkdir();
        ftpClient.changeWorkingDirectory(currentPath);
        FTPFile[] ftpFiles = ftpClient.listFiles();
        for (FTPFile ftpFile : ftpFiles) {
            File file = new File(localFile.getPath() + "/" + ftpFile.getName());
            if (ftpFile.isDirectory()) {
                flag = downloadMany(file);
            } else {
                flag = downloadSingle(file, ftpFile);
            }
        }
        return flag;
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
        ftpClient.changeWorkingDirectory(REMOTE_PATH);
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
     * @param remotePath FTP鐠侯垰绶�     * @return Result
     * @throws IOException 
     */
//    public Result uploadingUntilEnd(File localFile, String remotePath) throws IOException {
//        boolean flag = true;
//        Result result = null;
//        currentPath = remotePath;
//        response = 0;
//        ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
//        ftpClient.enterLocalPassiveMode();
//        ftpClient.setFileTransferMode(org.apache.commons.net.ftp.FTP.STREAM_TRANSFER_MODE);
//        ftpClient.changeWorkingDirectory(REMOTE_PATH);
//        Date startTime = new Date();
//        if (localFile.isDirectory()) {
//            flag = uploadingMany(localFile);
//        } else {
//            flag = uploadingSingleUtilEnd(localFile);
//        }
//        Date endTime = new Date();
//        result = new Result(flag, Util.getFormatTime(endTime.getTime() - startTime.getTime()), Util.getFormatSize(response));
//        return result;
//    }
    /**
     * @param localFile 閺堫剙婀撮弬鍥︽
     * @return true
     * @throws IOException 
     */
//    private boolean uploadingSingleUtilEnd(File localFile) throws IOException {
//        boolean flag = true;
//        InputStream inputStream = new FileInputStream(localFile);
//		
//		long ldownloaded = 0;
//		long lFilesize = inputStream.available();
//        response += (double) inputStream.available() / 1;
//		while (bkeepFTPU) {
//
//			
//			
//			while (bkeepFTP) {
//				
//				//test 0805
//				
//				try
//				{					
//					c = mInputStream.read(bytes);
//
//			try
//			{
//				flag = ftpClient.storeFile(localFile.getName(), inputStream);
//				
//				ldownloaded += inputStream.;
//				if(ldownloaded>lFilesize)
//				{
//					break;
//				}
//			}
//			catch(Exception e)
//			{
//				e.printStackTrace();
//			}
//			
//		}
//        
//        inputStream.close();
//        return flag;
//    }
    /**
     * @param localFile 閺堫剙婀撮弬鍥︽
     * @return true
     * @throws IOException 
     */
    private boolean uploadingSingle(File localFile) throws IOException {
        boolean flag = true;
        InputStream inputStream = new FileInputStream(localFile);
        response += (double) inputStream.available() / 1;
		while (bkeepFTPU) {
			
			
			try
			{
				flag = ftpClient.storeFile(localFile.getName(), inputStream);
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
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
	                        	if(strinet.contains("192.168."))
	                        	{	                        
	                                continue;
	                        	}
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