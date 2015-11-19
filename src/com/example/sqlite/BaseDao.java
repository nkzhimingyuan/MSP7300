package com.example.sqlite;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.example.Adapter.LogControl;
import com.example.Config.FilePathConf;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 鍩虹DAO锛屾瘡涓狣AO閮借缁ф壙姝ょ被
 * 
 * @author steven
 * 
 * http://www.juziku.com/sunlightcs/
 *
 */
public class BaseDao<T> {

	private Context mContext;
	private SQLiteDatabase db;

	public Context getmContext() {
		return mContext;
	}

	public BaseDao(Context context,String DBName) {
		this.mContext = context;
		DBHelper dbHelper = new DBHelper(mContext,DBName/* Configuration.DB_NAME */,
				null, Configuration.DB_VERSION);
		//db = dbHelper.getWritableDatabase();
		try
		{
			db = SQLiteDatabase.openOrCreateDatabase(FilePathConf.sgInst().GetDatabasePathDir()+DBName, null);
		
		}
		catch(Exception ex)
		{
			LogControl.sgInst().sendlog("Android创建数据库为本地数据库!未存储在SD卡中!\n");
			db = dbHelper.getWritableDatabase();
		}
		
	}

	/**
	 * 澧炲姞銆佸垹闄ゃ�淇敼琛ㄦ椂锛岃皟鐢ㄦ鏂规硶
	 * @param sql  DDL璇彞
	 * @throws SQLException
	 */
	public void execute(String sql){
		db.execSQL(sql);
	}

	/**
	 * 鍒犻櫎琛ㄤ腑鐨勮褰�
	 * @param table   琛ㄥ悕
	 * @param whereClause 鍒犻櫎鏉′欢   濡傦細( id>? and time>?)
	 * @param whereArgs   鏉′欢閲岀殑鍙傛暟    鐢ㄦ潵鏇挎崲"?"    绗�涓弬鏁帮紝浠ｈ〃绗�涓棶鍙凤紱绗�涓弬鏁帮紝浠ｈ〃绗�涓棶鍙凤紱渚濇绫绘帹......
	 * @return  杩斿洖鍒犻櫎鐨勬潯鏁�
	 */
	public int delete(String table, String whereClause, String[] whereArgs) {
		return db.delete(table, whereClause, whereArgs);
	}
	
	/**
	 * 鎻掑叆鏁版嵁
	 * @param table 琛ㄥ悕
	 * @param values    ContentValues瀵硅薄
	 * @return          杩斿洖褰撳墠琛孖D鍊硷紝濡傛灉澶辫触杩斿洖-1
	 */
	public long insert(String table,	ContentValues values){
		return this.insert(table, null, values);
	}
			
	/**
	 * 鎻掑叆鏁版嵁
	 * @param table 琛ㄥ悕
	 * @param values    ContentValues瀵硅薄
	 * @param nullColumnHack   绌哄垪
	 * @return          杩斿洖褰撳墠琛孖D鍊硷紝濡傛灉澶辫触杩斿洖-1
	 */
	public long insert(String table, String nullColumnHack,
			ContentValues values) throws SQLException {
		return db.insertOrThrow(table, nullColumnHack, values);
	}

	/**
	 * 淇敼鏁版嵁
	 * @param table         琛ㄥ悕 
	 * @param values        ContentValues瀵硅薄          琛ㄧず瑕佷慨鏀圭殑鍒楋紝濡傦細 name="steven" 鍗�values.put("name", "steven");
	 * @param whereClause   淇敼鏉′欢   濡傦細( id=?)
	 * @param whereArgs     鏉′欢閲岀殑鍙傛暟    鐢ㄦ潵鏇挎崲"?"    绗�涓弬鏁帮紝浠ｈ〃绗�涓棶鍙凤紱绗�涓弬鏁帮紝浠ｈ〃绗�涓棶鍙凤紱渚濇绫绘帹......
	 * @return              杩斿洖淇敼鐨勬潯鏁�
	 */
	public int update(String table, ContentValues values,
			String whereClause, String[] whereArgs) {
		return db.update(table, values, whereClause, whereArgs);
	}

	

	/**
	 * 鏌ヨ鏁版嵁
	 * @param table             琛ㄥ悕 
	 * @param columns           瑕佹煡璇㈢殑鍒楀悕 
	 * @param selection         鏌ヨ鏉′欢    濡傦細( id=?)
	 * @param selectionArgs     鏉′欢閲岀殑鍙傛暟锛岀敤鏉ユ浛鎹�?"
	 * @return                  杩斿洖Cursor
	 */
	public Cursor query(String table, String[] columns, String selection,
			String[] selectionArgs) {
		return db.query(table, columns, selection, selectionArgs, null, null,
				null);
	}
	
	
	/**
	 * 鏌ヨ鏁版嵁
	 * @param table             琛ㄥ悕 
	 * @param columns           瑕佹煡璇㈢殑鍒楀悕 
	 * @param selection         鏌ヨ鏉′欢    濡傦細( id=?)
	 * @param selectionArgs     鏉′欢閲岀殑鍙傛暟锛岀敤鏉ユ浛鎹�?"
	 * @param orderBy           鎺掑簭              濡傦細id desc 
	 * @return                  杩斿洖Cursor
	 */
	public Cursor query(String table, String[] columns, String selection,
			String[] selectionArgs, String orderBy) {
		return db.query(table, columns, selection, selectionArgs, null, null,
				orderBy);
	}
	
	/**
	 * 鏌ヨ鏁版嵁
	 * @param distinct          姣忚鏄敮涓�    true:琛ㄧず鍞竴       false:琛ㄧず涓嶅敮涓�
	 * @param table             琛ㄥ悕 
	 * @param columns           瑕佹煡璇㈢殑鍒楀悕 
	 * @param selection         鏌ヨ鏉′欢    濡傦細( id=?)
	 * @param selectionArgs     鏉′欢閲岀殑鍙傛暟锛岀敤鏉ユ浛鎹�?"
	 * @param orderBy           鎺掑簭              濡傦細id desc 
	 * @param limit             鏌ヨ鐨勬潯鏁�             濡傦細10
	 * @return                  杩斿洖Cursor
	 */
	public Cursor query(boolean distinct, String table, String[] columns, String selection,
			String[] selectionArgs, String orderBy, String limit){
		return db.query(distinct, table, columns, selection, selectionArgs, null, null, orderBy, limit);
	}
	
	
	
	
	/**
	 * 鏌ヨ鏌愪釜瀛楁  
	 * @param classz            瀛楄妭鐮�     濡傦細String.class
	 * @param table             琛ㄥ悕 
	 * @param columns           瑕佹煡璇㈢殑鍒楀悕 
	 * @param selection         鏌ヨ鏉′欢    濡傦細( id=?)
	 * @param selectionArgs     鏉′欢閲岀殑鍙傛暟锛岀敤鏉ユ浛鎹�?"
	 * @return                  杩斿洖Object
	 */
	public Object queryField(Class<?> classz, String table, String[] columns, String selection,
			String[] selectionArgs){
		Object o = null;
		//鏌ヨ鍗曟潯璁板綍
		Cursor c = db.query(table, columns, selection, selectionArgs, null, null, null, "1");
		if(c.moveToFirst()){
			try {
				if(classz == Integer.TYPE) {   //int
					o = c.getInt(0);
				}else if(classz == String.class){   //String
					o = c.getString(0);
				}else if(classz == Long.TYPE){   //long
					o = c.getLong(0);
				}else if(classz == Float.TYPE){   //float
					o = c.getFloat(0);
				}else if(classz == Double.TYPE){   //double
					o = c.getDouble(0);
				}else if(classz == Short.TYPE){   //short
					o = c.getShort(0);
				}
			} catch (Exception e) {
				Log.e("queryField", e.toString());
			}
		}
		c.close();
		return o;
	}
	
	
	/**
	 * 鏌ヨ鏁版嵁    鍗曚釜瀵硅薄
	 * @param classz            瀛楄妭鐮�     濡傦細String.class
	 * @param table             琛ㄥ悕 
	 * @param columns           瑕佹煡璇㈢殑鍒楀悕 
	 * @param selection         鏌ヨ鏉′欢    濡傦細( id=?)
	 * @param selectionArgs     鏉′欢閲岀殑鍙傛暟锛岀敤鏉ユ浛鎹�?"
	 * @return                  杩斿洖Object
	 */
	@SuppressWarnings("unchecked")
	public T queryObject(Class<?> classz, String table, String[] columns, String selection,
			String[] selectionArgs){
		
		//鏌ヨ鍗曟潯璁板綍
		Cursor c = db.query(table, columns, selection, selectionArgs, null, null, null, "1");
		T t = null;
		if(c.moveToFirst()){
			try{
				//鐢熸垚鏂扮殑瀹炰緥
				t = (T) classz.newInstance();
				
				//鎶婂垪鐨勫�锛岃浆鎹㈡垚瀵硅薄閲屽睘鎬х殑鍊�
				columnToField(t, c);
				
			}catch(Exception e){
			//	Log.e("newInstance error", "鐢熸垚鏂板疄渚嬫椂鍑洪敊 锛� + e.toString());
			}
		}
		c.close();
		return t;
	}
	
	
	/**
	 * 鏌ヨ鏁版嵁    甯﹀垎椤靛姛鑳�
	 * @param classz            瀛楄妭鐮�     濡傦細String.class
	 * @param table             琛ㄥ悕 
	 * @param columns           瑕佹煡璇㈢殑鍒楀悕 
	 * @param selection         鏌ヨ鏉′欢    濡傦細( id=?)
	 * @param selectionArgs     鏉′欢閲岀殑鍙傛暟锛岀敤鏉ユ浛鎹�?"
	 * @param orderBy           鎺掑簭              濡傦細id desc 
	 * @param pageNo            椤电爜              涓嶅垎椤垫椂锛屼负null
	 * @param pageSize          姣忛〉鐨勪釜鏁�       涓嶅垎椤垫椂锛屼负null
	 * @return                  杩斿洖List
	 */
	@SuppressWarnings("unchecked")
	public List<T> queryList(Class<?> classz, String table, String[] columns, String selection,
			String[] selectionArgs, String orderBy, Integer pageNo, Integer pageSize){
		
		//鍒嗛〉
		if(!(pageNo == null || pageSize ==null)){
			//鍒嗛〉鐨勮捣濮嬩綅缃�
			int begin = (pageNo -1)*pageSize;
			orderBy = orderBy + " limit " + begin + ", " + pageSize;
		}
		
		
		//鏌ヨ鏁版嵁
		Cursor c = db.query(table, columns, selection, selectionArgs, null, null, orderBy);
		
		List<T> list = new ArrayList<T>();
		T t = null;
		while (c.moveToNext()) {
			try{
				//鐢熸垚鏂扮殑瀹炰緥
				t = (T) classz.newInstance();
			}catch(Exception e){
			//	Log.e("newInstance error", "鐢熸垚鏂板疄渚嬫椂鍑洪敊 锛� + e.toString());
			}
			
			//鎶婂垪鐨勫�锛岃浆鎹㈡垚瀵硅薄閲屽睘鎬х殑鍊�
			columnToField(t, c);
			
			list.add(t);
		}
		c.close();
		
		return list;
	}
	
	
	/**
	 * 鎶婂垪鐨勫�锛岃浆鎹㈡垚瀵硅薄閲屽睘鎬х殑鍊�
	 */
	private void columnToField(T t, Cursor c){
		//鑾峰彇T閲岀殑鎵�湁灞炴�
		Field[] f = t.getClass().getDeclaredFields();
		for (int i = 0; i < f.length; i++) {
			
			int columnIndex = c.getColumnIndex(f[i].getName());
			//濡傛灉涓�1锛岃〃绀轰笉瀛樺湪姝ゅ垪
			if(columnIndex == -1){
				continue;
			}
			
			Class<?>  classz = f[i].getType();
			//璁剧疆鎴愬彲璁块棶锛屽惁鍒欎笉鑳絪et鍊�
			f[i].setAccessible(true);
			
			try {
				if(classz == Integer.TYPE) {   //int
					f[i].set(t, c.getInt(columnIndex));
				}else if(classz == String.class){   //String
					f[i].set(t, c.getString(columnIndex));
				}else if(classz == Long.TYPE){   //long
					f[i].set(t, c.getLong(columnIndex));
				}else if(classz == byte[].class){   //byte
					f[i].set(t, c.getBlob(columnIndex));
				}else if(classz == Float.TYPE){   //float
					f[i].set(t, c.getFloat(columnIndex));
				}else if(classz == Double.TYPE){   //double
					f[i].set(t, c.getDouble(columnIndex));
				}else if(classz == Short.TYPE){   //short
					f[i].set(t, c.getShort(columnIndex));
				}
			} catch (Exception e) {
		//		Log.e("column to field error", "瀛楁杞崲鎴愬璞℃椂鍑洪敊 锛� + e.toString());
			}
		}
	}


	/**
	 * 寮�浜嬪姟
	 */
	protected void beginTransaction() {
		db.beginTransaction();
	}

	/**
	 * 鎻愪氦浜嬪姟鍙婄粨鏉熶簨鍔�
	 */
	protected void commit() {
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	/**
	 * 鍥炴粴浜嬪姟
	 */
	protected void rollback() {
		db.endTransaction();
	}

	/**
	 * 鍏抽棴杩炴帴
	 */
	public void close() {
		if (db != null && db.isOpen())
			db.close();
	}
}
