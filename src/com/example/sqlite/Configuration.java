package com.example.sqlite;

import com.example.Config.FilePathConf;

/**
 * 配置信息
 * 
 * @author steven
 * 
 * http://www.juziku.com/sunlightcs/
 *
 */
public class Configuration {
	public static final String DB_PATH = FilePathConf.sgInst().Getmsp7300DataPathDir();
	//public static final String DB_NAME = "msp7300.db";
	public static final int DB_VERSION = 1;
	public static int oldVersion = -1;
	
}
