package com.example.Task;

import java.io.BufferedReader;     
import java.io.IOException;   
import java.io.InputStream;   
import java.io.InputStreamReader;   
import java.net.HttpURLConnection;   
import java.net.MalformedURLException;   
import java.net.URL;   
import java.util.ArrayList;

public class Http {

	private String strIPAddr;
	private String strPort;
	private String strPath;
	public  String strUrl; 
	public  int    ntimeout;
	private byte buffer[] = new byte[1024*4];
	private int hasread = 0;
	private int size = 1;
	public Http(String url,int timeout)
	{
		this.strUrl = url;
		this.ntimeout = timeout;
	}
	/**  
	 *   
	 * @Project: DounLoad  
	 * @Desciption: 下载
	 * @Author: YuFei  
	 * @Date: 2015-07-01
	 */  
	public boolean DownLoad()
	{
		boolean SuccessFlg = false;
		String urlStr = this.strUrl;   
        try {   
            /*  
             * ͨURL HttpURLConnection           
             * <uses-permission android:name="android.permission.INTERNET" />  
             */  
            URL url=new URL(urlStr);   
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            conn.setRequestProperty("Accept-Encoding", "identity");
            
            conn.setUseCaches(false);
 	        conn.setConnectTimeout(ntimeout*1000);//设置连接超时时间
 	        conn.setReadTimeout(ntimeout*1000);//设置下载超时时间
            conn.connect();
	        int len = 0;
	        hasread = 0;
            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
            	size = conn.getContentLength();//获取下载数据大小
            }
            else
            {
            	return false;
            }

            //inputStream
            InputStream input=conn.getInputStream(); 
            //读取数据
            while((len = input.read(buffer))!=-1)
            {   
            	hasread += len;            
            	//bufferList.add(buffer);
            }  
            input.close();
      
            if(hasread<size)//下载是否完成
            {
            	SuccessFlg = false;
            }
            else
            {
            	SuccessFlg = true;
            }
            
        } 
        catch (MalformedURLException e) 
        {   
            e.printStackTrace();  
            SuccessFlg = false;
        }
        catch (IOException e)
        {   
            e.printStackTrace(); 
            SuccessFlg = false;
        }
		return SuccessFlg;
	}
	/**  
	 *   
	 * @Project: DownLoadtxt  
	 * @Desciption:获取下载进度
	 * @Author: 
	 * @Date: 2015-07-01
	 */  
	public String GetDLpercent()
	{
		int npercent = (int)(hasread*100)/size;
		return npercent+"%";
	}
	/**  
	 *   
	 * @Project: DownLoadtxt  
	 * @Desciption: ֻhttp下载 txt文件
	 * @Author: YuFei  
	 * @Date: 2015-07-01
	 */  
	public boolean DownLoadtxt()
	{
		String urlStr = this.strUrl;
		boolean SuccessFlg = false;
	    try 
	    {   
	        /*  
	         * URL HttpURLConnection  
	         * AndroidMainfest.xml
	         * <uses-permission android:name="android.permission.INTERNET" />  
	         */  
	        URL url=new URL(urlStr);   
	        HttpURLConnection conn=(HttpURLConnection)url.openConnection(); 
	        
	     
	        conn.setUseCaches(false);
	        conn.setConnectTimeout(ntimeout*1000);
	        conn.setReadTimeout(ntimeout*1000);

	        InputStream input=conn.getInputStream();   
	        BufferedReader in=new BufferedReader(new InputStreamReader(input));   
	        String line=null;   
	        StringBuffer sb=new StringBuffer();   
	        while((line=in.readLine())!=null)
	        {   
	        	
	            sb.append(line);   
	        }   
	      
	        if(sb.length() > 0)
	        {
	        	SuccessFlg = true;
	        }
	        else
	        {
	        	SuccessFlg = false;
	        }
	        
	    } 
	    catch (IOException e) 
	    {   
	        e.printStackTrace(); 
	        SuccessFlg = false;
	    }  
	    return SuccessFlg;
	}
	
	/**  
	 *   
	 * @Project: DounLoadData  
	 * @Desciption: 下载除txt外数据
	 * @Author: YuFei  
	 * @Date: 2015-07-01
	 */  
	public boolean DounLoadData()
	{
		boolean SuccessFlg = false;
		String urlStr = this.strUrl;   
        try {   
            /*  
             *URL HttpURLConnection  
             * AndroidMainfest.xml
             * <uses-permission android:name="android.permission.INTERNET" />  
             */  
            URL url=new URL(urlStr);   
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
       
            conn.setUseCaches(false);
	        //conn.setConnectTimeout(ntimeout*1000);
	        conn.setReadTimeout(ntimeout*1000);
       
            InputStream input=conn.getInputStream(); 
 
            byte[] buffer=new byte[4*1024];
            ArrayList<byte[]> bufferList = new ArrayList<byte[]>();
            while(input.read(buffer)!=-1)
            {   
            	bufferList.add(buffer);
            }  
            if(bufferList.size()>0)
            {
            	SuccessFlg = true;
            }
            else
            {
            	SuccessFlg = false;
            }
            
        } 
        catch (MalformedURLException e) 
        {   
            e.printStackTrace();  
            SuccessFlg = false;
        }
        catch (IOException e)
        {   
            e.printStackTrace(); 
            SuccessFlg = false;
        }
		return SuccessFlg;
	}
	
}
