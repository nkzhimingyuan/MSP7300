package com.example.msp7300_client;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;

import com.example.msp7300_client.MySwitch.OnChangeAttemptListener;
import com.example.update.UpdateManager;


public class ServConf_Fragment_view extends Fragment implements OnChangeAttemptListener, OnCheckedChangeListener{
	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static final String ARG_SECTION_NUMBER = "section_number";
	SharedPreferences preference;
	SharedPreferences.Editor editor;
	

	private TextView ip_server_textview;

	
	

	public ServConf_Fragment_view(){
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.serverconf_view,
				container, false);
		preference = getActivity().getSharedPreferences("setting_server", Context.MODE_PRIVATE);
		editor = preference.edit();
		
		ip_server_textview = (TextView) rootView.findViewById(R.id.ip_serever_textview);
		ip_server_textview.setText(preference.getString("ip_server", "0.0.0.0"));

		
		
		Switch SendIEswitch =  (Switch) rootView.findViewById(R.id.switch_sendIE);
		SendIEswitch.setChecked(true);
		SendIEswitch.setOnCheckedChangeListener(this);
		
	
		
		return rootView;
	}
	@Override
	public void onChangeAttempted(boolean isChecked) {
		
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	
		if (isChecked){

			IEValueReport.sgInst().ContinueIEReport();//继续IE值上报
		}
		else {

			IEValueReport.sgInst().PauseIEReport();//暂停IE值上报
		}
		// TODO Auto-generated method stub
	}
}
