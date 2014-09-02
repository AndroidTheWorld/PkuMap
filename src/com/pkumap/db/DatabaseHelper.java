package com.pkumap.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION=1;
	private static final String DATABASE_PATH="/data/data/com.zdx.pkumap/databases/";
	private static final String DATABASE_NAME="PkuMap.db";
	
	public DatabaseHelper(Context context,String name,CursorFactory factory,
			int version,DatabaseErrorHandler errorHandler){
		super(context, name, factory, version, errorHandler);
	}
	public DatabaseHelper(Context context,String name,CursorFactory factory,int version){
		super(context, name, factory, version);
	}
	public DatabaseHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		writeDBFileToDataBase(context);
	}
	/**
	 * 将PkuMap.db写入到默认文件夹下
	 * @param context
	 */
	private void writeDBFileToDataBase(Context context){
		if ((new File(DATABASE_PATH + DATABASE_NAME)).exists() == false) {
			File f = new File(DATABASE_PATH);
			if (!f.exists()) {
	            f.mkdir();
	        }
			try{
				InputStream is = context.getAssets().open(DATABASE_NAME);
				OutputStream os = new FileOutputStream(DATABASE_PATH + DATABASE_NAME);
				
				byte[] buffer = new byte[1024];
	            int length;
	            while ((length = is.read(buffer)) > 0) {
	                 os.write(buffer, 0, length);
	            }
	            
	            // 关闭文件流
                os.flush();
                os.close();
                is.close();
			}catch (Exception e) {
                e.printStackTrace();
            }
		}
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		StringBuffer sBuffer=new StringBuffer();

		sBuffer.append("CREATE TABLE poi (");
		sBuffer.append("id      INT     UNIQUE,");
		sBuffer.append("name    VARCHAR,");
		sBuffer.append("addr VARCHAR,");
		sBuffer.append("desc  VARCHAR,");
		sBuffer.append("layer   INT,");
		sBuffer.append("wd   VARCHAR,");
		sBuffer.append("center_x REAL,");
		sBuffer.append("center_y REAL)");
		
		db.execSQL(sBuffer.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		//把数据库数据给删掉了
/*		db.execSQL("DROP TABLE IF EXISTS poi");
		onCreate(db);*/
	}
	
}
