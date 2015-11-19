package com.example.Log;

import android.content.Context;

import com.example.sqlite.BaseDao;

public class Usrsqlite extends BaseDao<LogLayout>{

	public Usrsqlite(Context context,String strDBName) {
		super(context,strDBName);
	}
}
