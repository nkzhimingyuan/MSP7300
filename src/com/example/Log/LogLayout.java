package com.example.Log;

public class LogLayout {

	private String ueid;
	private String task;
	private String event;
	private String time;
	private String RSRP;
	private String SNR;
	private String SucRate;
	private String Delay;
	private String Avgsp;
	
	public String getueId() {
		return ueid;
	}
	public void setId(String id) {
		this.ueid = id;
	}
	public String gettask() {
		return task;
	}
	public void settask(String id) {
		this.task = id;
	}
	public String getevent() {
		return event;
	}
	public void setevent(String id) {
		this.event = id;
	}
	public String gettime() {
		return time;
	}
	public void settime(String id) {
		this.time = id;
	}
	public String getRSRP() {
		return RSRP;
	}
	public void setRSRP(String id) {
		this.RSRP = id;
	}
	public String getSNR() {
		return SNR;
	}
	public void setSNR(String id) {
		this.SNR = id;
	}
	public String getSucRate() {
		return SucRate;
	}
	public void setSucRate(String id) {
		this.SucRate = id;
	}
	public String getDelay() {
		return Delay;
	}
	public void setDelay(String id) {
		this.Delay = id;
	}
	public String getAvgsp() {
		return Avgsp;
	}
	public void setAvgsp(String id) {
		this.Avgsp = id;
	}
	
}
