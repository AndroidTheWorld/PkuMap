package com.pkumap.db;

import java.util.ArrayList;

import org.json.JSONArray;

import com.pkumap.bean.Building;
import com.pkumap.bean.Point;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BuildingService {
	public DatabaseHelper helper;
	public SQLiteDatabase db;
	
	public BuildingService(Context context){
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
	 * 创建BuildingVector表
	 */
	public void createBuildingVector(){
		StringBuffer sb=new StringBuffer();
		sb.append("CREATE TABLE buildingvector (");
		sb.append("id           INT,");
		sb.append("name         VARCHAR,");
		sb.append("introduction TEXT,");
		sb.append("category     VARCHAR,");
		sb.append("pointid      INT,");
		sb.append("center_x     REAL,");
		sb.append("center_y     REAL,");
		sb.append("type         VARCHAR,");
		sb.append("coordinates  TEXT)");
		
		db.execSQL(sb.toString());
	}
	/**
	 * 向Building表中添加数据
	 */
	public void insertBuildingData(Building building,String coordinates){
		String insert_sql="insert into buildingvector values(?,?,?,?,?,?,?,?,?)";
/*		ArrayList<Point> pointList=building.getCoordinates();
		JSONArray pointArray=new JSONArray(pointList);
		String coordinate=pointArray.toString();*/
/*		StringBuffer sb=new StringBuffer();
		sb.append("[");
		for(int i=0;i<pointList.size();i++){
			Point point=pointList.get(i);
			sb.append("[");
			sb.append(point.getX()+",");
			sb.append(point.getY()+"]");
			
		}*/
		String[] args=new String[]{String.valueOf(building.getId()),building.getName(),building.getIntroduction(),
				building.getCategory(),String .valueOf(building.getPointid()),String.valueOf(building.getCenter().getX()),
				String.valueOf(building.getCenter().getY()),building.getType(),coordinates};
		db.execSQL(insert_sql,args);
	}
	/**
	 * 获取3d地图中所有的POI名称
	 */
	public ArrayList<String> getAllBuildingNames(){
		ArrayList<String> buildingNames=new ArrayList<String>();
		
		String select_sql="select name from buildingvector";
		Cursor cursor=db.rawQuery(select_sql, null);
		while(cursor.moveToNext()){
			buildingNames.add(cursor.getString(cursor.getColumnIndex("name")));
		}
		cursor.close();
		return buildingNames;
	}
	/**
	 * 根据名称获取Building在路劲规划上的PointID
	 * @param name
	 * @return
	 */
	public int getPointIdByName(String name){
		int pointid=-1;
		String select_sql="select pointid from buildingvector where name=?";
		String[] args=new String[]{name};
		Cursor cursor=db.rawQuery(select_sql, args);
		if(cursor.moveToNext()){
			pointid=cursor.getInt(cursor.getColumnIndex("pointid"));
		}
		cursor.close();
		return pointid;
	}
	/**
	 * 根据范围和X,Y方向上的偏移量来获取Building
	 * @param center
	 * @return
	 */
	public Building getBuildingByBound(Point curPoint,float dx,float dy){
		Building building=null;
		float center_x=curPoint.getX();
		float center_y=curPoint.getY();
		String select_sql="select * from buildingvector where (center_x between "+String.valueOf(center_x-dx)+
				" and "+String.valueOf(center_x+dx)+") and (center_y between "+String.valueOf(center_y-dy)+
				" and "+String.valueOf(center_y+dy)+")";
		Cursor cursor=db.rawQuery(select_sql, null);
		if(cursor.moveToNext()){
			building=new Building();
			building.setId(cursor.getInt(cursor.getColumnIndex("id")));
			building.setName(cursor.getString(cursor.getColumnIndex("name")));
			building.setIntroduction(cursor.getString(cursor.getColumnIndex("introduction")));
			building.setCategory(cursor.getString(cursor.getColumnIndex("category")));
			building.setPointid(cursor.getInt(cursor.getColumnIndex("pointid")));
			building.setType(cursor.getString(cursor.getColumnIndex("type")));
			
			Point center=new Point();
			center.setX(cursor.getFloat(cursor.getColumnIndex("center_x")));
			center.setY(cursor.getFloat(cursor.getColumnIndex("center_y")));
			
			building.setCenter(center);
			try{
				JSONArray arrayCoordinates=new JSONArray(cursor.getString(cursor.getColumnIndex("coordinates")));
				ArrayList<Point> coordinates=new ArrayList<Point>();
				for(int i=0;i<arrayCoordinates.length();i++){
					Point point=new Point();
					
					JSONArray ps=arrayCoordinates.getJSONArray(i);
					point.setX((float)ps.getDouble(0));
					point.setY((float)ps.getDouble(1));
					coordinates.add(point);
				}
				building.setCoordinates(coordinates);
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		cursor.close();
		return building;
	}
	/**
	 * 根据名称获取Building
	 * @return
	 */
	public Building getBuildingByName(String name){
		Building building=new Building();
		
		String select_sql="select * from buildingvector where name=?";
		String[] args=new String[]{name};
		Cursor cursor=db.rawQuery(select_sql, args);
		if(cursor.moveToNext()){
			building.setId(cursor.getInt(cursor.getColumnIndex("id")));
			building.setName(cursor.getString(cursor.getColumnIndex("name")));
			building.setIntroduction(cursor.getString(cursor.getColumnIndex("introduction")));
			building.setCategory(cursor.getString(cursor.getColumnIndex("category")));
			building.setPointid(cursor.getInt(cursor.getColumnIndex("pointid")));
			building.setType(cursor.getString(cursor.getColumnIndex("type")));
			
			Point center=new Point();
			center.setX(cursor.getFloat(cursor.getColumnIndex("center_x")));
			center.setY(cursor.getFloat(cursor.getColumnIndex("center_y")));
			
			building.setCenter(center);
			try{
				JSONArray arrayCoordinates=new JSONArray(cursor.getString(cursor.getColumnIndex("coordinates")));
				ArrayList<Point> coordinates=new ArrayList<Point>();
				for(int i=0;i<arrayCoordinates.length();i++){
					Point point=new Point();
					
					JSONArray ps=arrayCoordinates.getJSONArray(i);
					point.setX((float)ps.getDouble(0));
					point.setY((float)ps.getDouble(1));
					coordinates.add(point);
				}
				building.setCoordinates(coordinates);
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		cursor.close();
		return building;
 	}
}
