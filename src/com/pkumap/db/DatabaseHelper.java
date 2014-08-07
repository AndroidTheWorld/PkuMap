package com.pkumap.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION=1;
	
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
