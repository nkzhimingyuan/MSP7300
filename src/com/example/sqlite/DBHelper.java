package com.example.sqlite;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	private Context mContext;

	public DBHelper(Context context, String databaseName,
			CursorFactory factory, int version) {
		super(context, databaseName, factory, version);
		mContext = context;
	}

	/**
	 * 鏁版嵁搴撶涓�鍒涘缓鏃惰皟鐢�
	 * */
	@Override
	public void onCreate(SQLiteDatabase db) {
		executeSchema(db, "msp7300.sql");
	}

	/**
	 * 鏁版嵁搴撳崌绾ф椂璋冪敤
	 * */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//鏁版嵁搴撲笉鍗囩骇
		if (newVersion <= oldVersion) {
			return;
		}
		Configuration.oldVersion = oldVersion;

		int changeCnt = newVersion - oldVersion;
		for (int i = 0; i < changeCnt; i++) {
			// 渚濇鎵цupdatei_i+1鏂囦欢      鐢�鏇存柊鍒� [1-2]锛�鏇存柊鍒� [2-3]
			String schemaName = "update" + (oldVersion + i) + "_"
					+ (oldVersion + i + 1) + ".sql";
			executeSchema(db, schemaName);
		}
	}

	/**
	 * 璇诲彇鏁版嵁搴撴枃浠讹紙.sql锛夛紝骞舵墽琛宻ql璇彞
	 * */
	private void executeSchema(SQLiteDatabase db, String schemaName) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(mContext.getAssets()
					.open(/*Configuration.DB_PATH + "/" + */schemaName)));
			String line;
			String buffer = "";
			while ((line = in.readLine()) != null) {
				buffer += line;
				if (line.trim().endsWith(";")) {
					db.execSQL(buffer.replace(";", ""));
					buffer = "";
				}
			}
		} catch (IOException e) {
			Log.e("db-error", e.toString());
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				Log.e("db-error", e.toString());
			}
		}
	}

}