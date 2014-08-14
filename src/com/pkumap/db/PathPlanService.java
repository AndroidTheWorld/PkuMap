package com.pkumap.db;

import java.util.ArrayList;

import com.pkumap.bean.Road;
import com.pkumap.bean.RoadNode;

import android.content.Context;
import android.database.Cursor;
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
	 * 根据Id获取RoadNode
	 * @param id
	 * @return
	 */
	public RoadNode getRoadNodeById(int id,String map_type){
		RoadNode roadNode=null;
		String select_sql="select * from RoadNode where id=? and type=?";
		String[] args=new String[]{String.valueOf(id),map_type};
		Cursor cursor=db.rawQuery(select_sql, args);
		if(cursor.moveToNext()){
			roadNode=new RoadNode();
			roadNode.setId(cursor.getInt(cursor.getColumnIndex("id")));
			roadNode.setX(cursor.getFloat(cursor.getColumnIndex("x")));
			roadNode.setY(cursor.getFloat(cursor.getColumnIndex("y")));
			roadNode.setType(cursor.getString(cursor.getColumnIndex("type")));
		}
		cursor.close();
		return roadNode;
	}
	/**
	 * 根据地图类型获取路径规划所有点的信息
	 */
	public ArrayList<RoadNode> getAllPointInfoByType(String type){
		ArrayList<RoadNode> roadNodes=new ArrayList<RoadNode>();
		String select_sql="select * from RoadNode where type=?";
		String[] args=new String[]{type};
		Cursor cursor=db.rawQuery(select_sql, args);
		roadNodes.add(new RoadNode(0,0,0,type));
		while(cursor.moveToNext()){
			RoadNode roadNode=new RoadNode();
			roadNode.setId(cursor.getInt(cursor.getColumnIndex("id")));
			roadNode.setX(cursor.getFloat(cursor.getColumnIndex("x")));
			roadNode.setY(cursor.getFloat(cursor.getColumnIndex("y")));
			roadNode.setType(type);
			
			roadNodes.add(roadNode);
		}
		cursor.close();
		return roadNodes;
	}
	/**
	 * 根据地图类型获取路径规划所有路的信息
	 * @param type
	 * @return
	 */
	public ArrayList<Road> getAllRoadInfoByType(String type){
		ArrayList<Road> roads=new ArrayList<Road>();
		String select_sql="select * from Road where type=?";
		String[] args=new String[]{type};
		Cursor cursor=db.rawQuery(select_sql, args);
		while(cursor.moveToNext()){
			Road road=new Road();
			road.setBegin(cursor.getInt(cursor.getColumnIndex("begin")));
			road.setEnd(cursor.getInt(cursor.getColumnIndex("end")));
			road.setDistance(cursor.getFloat(cursor.getColumnIndex("distance")));
			road.setType(type);
			
			roads.add(road);
		}
		cursor.close();
		return roads;
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
