package com.example.msp7300_client;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.Adapter.LogControl;
import com.example.Adapter.UESystemInfo;
import com.example.Config.FilePathConf;
import com.example.Config.LogFTPConf;
import com.example.update.UpdateManager;

@SuppressLint("NewApi") public class MSP7300Client_Activity extends FragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	private Context mcontext = null;
	
	SharedPreferences preference;
	SharedPreferences.Editor editor;
	
	SharedPreferences userPreference;
	SharedPreferences.Editor uderEditor;
	ActionBar actionBar;
	RequestQueue mQueue;
	
	private String strServerIP = null;
	// 用户登录请求
	StringRequest loginStringRequest;
	
	// 每隔20s，心跳请求
	JsonObjectRequest commendJsonObjectRequest;
	
	// 
	Handler handler;
	
	String urlCommend = null;
	
	// 使用DownloadManager 来处理程序的下载流程
	DownloadManager myDownloadManager;
	
	private String logInfo = "";
	
	//
	private TextView logInfoTextView;
	
    private Fragment testFrag;
    
    private UpdateManager myUpdateManager;
    
    // 判断同步时间是不是打开
    private Boolean TimeBoolean;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_msp7300_client);
		
		// 判断时间同步是否开启，如果没有打开，跳转到设置界面去打开
		TimeBoolean = Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME, 0) > 0;
		if(!TimeBoolean){
			startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
		}
		
		myUpdateManager = new UpdateManager(this);
		
		//初始化preference 来实现存储
		preference = getSharedPreferences("setting_server", Context.MODE_PRIVATE);
		editor = preference.edit();	
		userPreference = getSharedPreferences("user", Context.MODE_PRIVATE);
				
		//初始化Download Manager
		myDownloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
		
		// 初始化网络传输队列
		mQueue = Volley.newRequestQueue(this);       
		
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg){

				// 处理检测更新
				 if(msg.what == 0x1233){
					//Toast.makeText(getApplicationContext(), "准备更新", Toast.LENGTH_SHORT).show();
					//myUpdateManager.checkUpdateInfo();
				}
			}
		};
		
		new Timer().schedule(new TimerTask()
		{
			@Override
			public void run(){
				initCommendJsonObjectRequest();
				mQueue.add(commendJsonObjectRequest);
			}
		}, 0 , 10000);
		// 处理更新程序
		new Timer().schedule(new TimerTask()
		{
			@Override
			public void run(){
				handler.sendEmptyMessage(0x1233);
			}
		}, 3600000 , 3600000);
		
		// Set up the action bar.
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setTitle(userPreference.getString("username", "未登录"));

		mcontext = this;
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setIcon(mSectionsPagerAdapter.getTabIcon(i))
					.setTabListener(this));
		}


		
		strServerIP = preference.getString("ip_server", "0.0.0.0");
		LogFTPConf.Set_FTP_ADDR(strServerIP);
		myUpdateManager.Set_serverip(strServerIP);
		String username = userPreference.getString("username", "zzbarry");
		
		username = username.trim();
		LogFTPConf.Set_FTP_RemotePath(username);//设置终端日志上报至服务器的目录(每个用户上传至本用户名对应下的文件夹)
		
		FilePathConf.sgInst().InitFilePath();
		//开启与服务器通信UDP
		ServCommuUDP.sgInst().ServCommuUDPRegister(this);
		ServCommuUDP.sgInst().StartUDPSocket();
		//开始终端信息
		UESystemInfo.sgInst().UESystemInfoGet_Start(this);
		UESystemInfo.sgInst().SetUENAME(username);
		//开始IE值上报
		IEValueReport.sgInst().StartIEReport();
		//////////////////////zy test //////////////////////////// 
//		Task task = new Task(this);
//	    task.StartTask();
	}
	
	@Override
	protected void onResume(){
		super.onResume(); 
		//logInfoTextView = (TextView)findViewById(R.id.Log_info_textview);
	}
	
	// 初始化登录请求
	public void initloginStringRequest(String username, String password){
        String ip_server = preference.getString("ip_server", "0.0.0.0");
        loginStringRequest = new StringRequest("http://" + ip_server + "/msp7300/login.php?username=" + username + "password=" + password,  
                new Response.Listener<String>() {  
                    @Override  
                    public void onResponse(String response) {  
                        //Log.d("TAG", response);
                    	Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                    }  
                }, new Response.ErrorListener() {  
                    @Override  
                    public void onErrorResponse(VolleyError error) {  
                    	Toast.makeText(getApplicationContext(), "请求出现错误", Toast.LENGTH_SHORT).show();
                    }  
                });  
	}
	
	// 每隔20s 心跳请求
	public void initCommendJsonObjectRequest(){
        String ip_server = preference.getString("ip_server", "0.0.0.0");
        commendJsonObjectRequest = new JsonObjectRequest("http://"+ip_server+"/7300/UeCmd.php?ueid=123456789&usrid=zhangyi", null,  
                new Response.Listener<JSONObject>() {  
                    @Override  
                    public void onResponse(JSONObject response) {  
                    	try {
							String msgid = response.getString("msgid");
							String msgcontent = response.getString("msgcontent");
							urlCommend = msgcontent;
							handler.sendEmptyMessage(0x1234);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                    	 
                    }  
                }, new Response.ErrorListener() {  
                    @Override  
                    public void onErrorResponse(VolleyError error) {  
                    	Toast.makeText(getApplicationContext(), "请求出现错误", Toast.LENGTH_SHORT).show(); 
                    }  
                });  
	}
	


	// 发送log
	public void sendLog(String log){
		logInfo = logInfo + log;
		logInfoTextView = (TextView)findViewById(R.id.Log_info_textview);
		logInfoTextView.setText(logInfo);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.msp7300_client_, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
		case R.id.action_login:
			
			LayoutInflater inflaterLogin = getLayoutInflater();
			final View layoutLogin = inflaterLogin.inflate(R.layout.login,(ViewGroup) findViewById(R.id.login_dialog));
			Builder dialogLogin = new AlertDialog.Builder(this).setTitle("用户登录").setView(layoutLogin).setPositiveButton("登录", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// 得到用户名和密码
					EditText userEditText = (EditText) layoutLogin.findViewById(R.id.login_username);
					String username = userEditText.getText().toString();
					username = username.trim();
					EditText passwordEditText = (EditText) layoutLogin.findViewById(R.id.login_password);
					String password = passwordEditText.getText().toString();
					
					Editor userEditor = userPreference.edit();
					userEditor.putString("username", username);
					userEditor.commit();
					
					actionBar.setTitle(username);
					UESystemInfo.sgInst().SetUENAME(username);
					initloginStringRequest(username, password);
					mQueue.add(loginStringRequest);
					LogFTPConf.Set_FTP_RemotePath(username);
				}
			});
			dialogLogin.show();
			
			
			return true;
		case R.id.action_server:
			// show出设置服务器ip的dialog
			// 创建一个Dialog，来配置服务器的ip 地址
			LayoutInflater inflater = getLayoutInflater();
			final View layout = inflater.inflate(R.layout.server_setting,(ViewGroup) findViewById(R.id.server_setting_dialog));
			Builder dialogServerSettring = new AlertDialog.Builder(this).setTitle("配置服务器").setView(layout).setPositiveButton("设置", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					EditText editText = (EditText) layout.findViewById(R.id.ip_server);
					String ip_server = editText.getText().toString();
					editor.putString("ip_server", ip_server);	
					editor.commit();
										
					TextView ip_server_textview = (TextView) findViewById(R.id.ip_serever_textview);
					ip_server_textview.setText(preference.getString("ip_server", "0.0.0.0"));
					strServerIP = preference.getString("ip_server", "0.0.0.0");
					LogFTPConf.Set_FTP_ADDR(strServerIP);
					myUpdateManager.Set_serverip(strServerIP);
				}
			});
			dialogServerSettring.show();
			
			return true;     
		case R.id.action_settings:
			/*Intent intent = new Intent();
			intent.setClass(MSP7300Client_Activity.this, SettingActivity.class);
			startActivity(intent);*/
			
			
			myUpdateManager.checkUpdateInfo();

			
			return true;
		default:
			return super.onOptionsItemSelected(item);
				
		}
		
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = null;
			switch (position) {
			case 0:
				return fragment = new ServConf_Fragment_view();
			case 1:						
				fragment = new Test_Fragment_view(mcontext);
				testFrag = fragment;	
				LogControl.sgInst().myTestView = (Test_Fragment_view)testFrag;
				return fragment;
			case 2:
				return fragment = new PhoneInfo_Fragment_view(mcontext);
			}

			return null;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);

			}
			return null;
		}
		

		public Drawable getTabIcon(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getResources().getDrawable(R.drawable.serv);
			case 1:
				return getResources().getDrawable(R.drawable.test);
			case 2:
				return getResources().getDrawable(R.drawable.phoneinfo);

			}
			return null;
		}
	}

}
