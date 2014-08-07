package com.pkumap.db;

import com.pkumap.bean.Road;
import com.pkumap.bean.RoadNode;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class PathPlanService {
	public DatabaseHelper helper;
	public SQLiteDatabase db;
	
	public PathPlanService(Context context){
		if(helper!=null){
			helper.close();
		}
		this.helper=new DatabaseHelper(context);
		if(db!=null){
			db.close();
		}
		this.db=helper.getWritableDatabase();
	}
	/**
	 * 创建Road的表
	 */
	public void createRoadTable(){
		StringBuffer sBuffer=new StringBuffer();
		sBuffer.append("CREATE TABLE Road (");
		sBuffer.append("[begin]  INT,");
		sBuffer.append("[end]    INT,");
		sBuffer.append("distance REAL,");
		sBuffer.append("type     VARCHAR)");
		db.execSQL(sBuffer.toString());
	}
	/**
	 * 向Road中写入数据
	 */
	public void insertRoadInfo(Road road){
		String insert_sql="insert into Road values(?,?,?,?)";
		String[] args=new String[]{String.valueOf(road.getBegin()),String.valueOf(road.getEnd())
				,String.valueOf(road.getDistance()),road.getType()};
		db.execSQL(insert_sql, args);
	}
	/**
	 * 创建RoadNode的表
	 */
	public void createRodeNodeTable(){
		StringBuffer sBuffer=new StringBuffer();
		sBuffer.append("CREATE TABLE RoadNode (");
		sBuffer.append("id  INT,");
		sBuffer.append("x   REAL,");
		sBuffer.append("y   REAL,");
		sBuffer.append("type     VARCHAR)");
		db.execSQL(sBuffer.toString());
	}
	/**
	 * 向RoadNode写入数据
	 * @param roadNode
	 */
	public void insertRoadNodeInfo(RoadNode roadNode){
		String insert_sql="insert into RoadNode values(?,?,?,?)";
		String[] args=new String[]{String.valueOf(roadNode.getId()),String.valueOf(roadNode.getX())
				,String.valueOf(roadNode.getY()),roadNode.getType()};
		db.execSQL(insert_sql, args);
	}
}
