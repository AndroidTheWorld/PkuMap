package com.pkumap.db;

import java.util.ArrayList;
import java.util.HashMap;

import com.pkumap.bean.Poi;
import com.pkumap.bean.Point;
import com.pkumap.bean.PointLonLat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class PoiService {
	public DatabaseHelper helper;
	public SQLiteDatabase db;
	
	public PoiService(Context context){
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
	 * 根据名称返回POI
	 * @param name
	 * @return
	 */
	public Poi getPoiByName(String name){
		Poi poi=null;
		//暂时返回第一条数据
		String sql_select="select * from poi where name=? limit 0,1";
		String[] args=new String[]{name};
		Cursor cursor=db.rawQuery(sql_select, args);
		if(cursor.moveToNext()){
			poi=new Poi();
			poi.setId(cursor.getInt(cursor.getColumnIndex("id")));
			poi.setName(cursor.getString(cursor.getColumnIndex("name")));
			poi.setAddr(cursor.getString(cursor.getColumnIndex("addr")));
			poi.setDesc(cursor.getString(cursor.getColumnIndex("desc")));
			poi.setLayer(cursor.getInt(cursor.getColumnIndex("layer")));
			poi.setPointId(cursor.getInt(cursor.getColumnIndex("pointid")));
			poi.setWd(cursor.getString(cursor.getColumnIndex("wd")));
			
			Point center=new Point();
			center.setX(cursor.getFloat(cursor.getColumnIndex("center_x")));
			center.setY(cursor.getFloat(cursor.getColumnIndex("center_y")));
			poi.setCenter(center);
			
			PointLonLat gpsLonLat=new PointLonLat();
			gpsLonLat.setX(cursor.getDouble(cursor.getColumnIndex("gps_x")));
			gpsLonLat.setY(cursor.getDouble(cursor.getColumnIndex("gps_y")));
			poi.setGps(gpsLonLat);
		}
		cursor.close();
		return poi;
	}
	/**
	 * 根据名称获取PointId
	 * @param name
	 * @return
	 */
	public int getPointIdByName(String name){
		int pointid=-1;
		String select_sql="select pointid from poi where name=? limit 0,1";
		String[] args=new String[]{name};
		Cursor cursor=db.rawQuery(select_sql, args);
		if(cursor.moveToNext()){
			pointid=cursor.getInt(cursor.getColumnIndex("pointid"));
		}
		cursor.close();
		return pointid;
	}
	/**
	 * 根据PoiId来获取PointId
	 * @param poiId
	 * @return
	 */
	public int getPointIdByPoiId(int poiId){
		int pointid=-1;
		String select_sql="select pointid from poi where id=? limit 0,1";
		String[] args=new String[]{String.valueOf(poiId)};
		Cursor cursor=db.rawQuery(select_sql, args);
		if(cursor.moveToNext()){
			pointid=cursor.getInt(cursor.getColumnIndex("pointid"));
		}
		cursor.close();
		return pointid;
	}
	/**
	 * 获取全部POI的名称
	 * @return
	 */
	public ArrayList<String> getPoiNameFromDB(){
		ArrayList<String> poiNames=new ArrayList<String>(); 
		String sql_select="select distinct name from poi";
		Cursor cursor=db.rawQuery(sql_select, null);
		while(cursor.moveToNext()){
			poiNames.add(cursor.getString(0));
		}
		cursor.close();
		return poiNames;
	}
	/**
	 * 插入POI数据
	 */
	public void insertPoi(Poi poi){
		
		String sql_insert="insert into poi(id,name,addr,desc,layer,wd,center_x,center_y) values(?,?,?,?,?,?,?,?)";
		Object[] objects=new Object[]{poi.getId(),poi.getName(),poi.getAddr(),poi.getDesc(),poi.getLayer(),
				poi.getWd(),poi.getCenter().getX(),poi.getCenter().getY()};
		db.execSQL(sql_insert,objects);
	}
	/**
	 * 更新POI的表结构,添加PointID字段
	 */
	public void updatePoiTable(){
		String update_sql="ALTER TABLE poi ADD COLUMN pointid INT";
		db.execSQL(update_sql);
	}
	/**
	 * 更新POI数据，添加PointID字段的数据
	 */
	public void updatePoiAddPointId(int pointid,int id){
		String update_sql="update poi set pointid=? where id=?";
		String[] args=new String[]{String.valueOf(pointid),String.valueOf(id)};
		db.execSQL(update_sql, args);
	}
	/**
	 * 更新POI的表结构，添加GPS字段
	 */
	public void updatePoiAddGps(){
		String update_sql="ALTER TABLE poi ADD COLUMN gps_y REAL";
		db.execSQL(update_sql);
	}
	/**
	 * 根据id来获取POI
	 * @param id
	 * @return
	 */
	public Poi getPoiById(Integer id){
		Poi poi=null;
		String sql_select="select * from poi where id=?";
		String[] idArg=new String[]{id.toString()};
		Cursor cursor=db.rawQuery(sql_select, idArg);
		if(cursor.moveToNext()){
			poi=new Poi();
			poi.setId(cursor.getInt(cursor.getColumnIndex("id")));
			poi.setName(cursor.getString(cursor.getColumnIndex("name")));
			poi.setAddr(cursor.getString(cursor.getColumnIndex("addr")));
			poi.setDesc(cursor.getString(cursor.getColumnIndex("desc")));
			poi.setLayer(cursor.getInt(cursor.getColumnIndex("layer")));
			poi.setPointId(cursor.getInt(cursor.getColumnIndex("pointid")));
			poi.setWd(cursor.getString(cursor.getColumnIndex("wd")));
			
			Point center=new Point();
			center.setX(cursor.getFloat(cursor.getColumnIndex("center_x")));
			center.setY(cursor.getFloat(cursor.getColumnIndex("center_y")));
			poi.setCenter(center);
			
			PointLonLat gpsLonLat=new PointLonLat();
			gpsLonLat.setX(cursor.getDouble(cursor.getColumnIndex("gps_x")));
			gpsLonLat.setY(cursor.getDouble(cursor.getColumnIndex("gps_y")));
			poi.setGps(gpsLonLat);
		}
		cursor.close();
		return poi;
	}
	/**
	 * 获取所有的POI数据
	 */
	public ArrayList<Poi> getAllPoi(){
		ArrayList<Poi> pois=new ArrayList<Poi>();
		String select_sql="select * from poi";
		Cursor cursor=db.rawQuery(select_sql, null);
		while(cursor.moveToNext()){
			Poi poi=new Poi();
			poi.setId(cursor.getInt(cursor.getColumnIndex("id")));
			poi.setName(cursor.getString(cursor.getColumnIndex("name")));
			poi.setAddr(cursor.getString(cursor.getColumnIndex("addr")));
			poi.setDesc(cursor.getString(cursor.getColumnIndex("desc")));
			poi.setLayer(cursor.getInt(cursor.getColumnIndex("layer")));
			poi.setPointId(cursor.getInt(cursor.getColumnIndex("pointid")));
			poi.setWd(cursor.getString(cursor.getColumnIndex("wd")));
			
			Point center=new Point();
			center.setX(cursor.getFloat(cursor.getColumnIndex("center_x")));
			center.setY(cursor.getFloat(cursor.getColumnIndex("center_y")));
			poi.setCenter(center);
			
			PointLonLat gpsLonLat=new PointLonLat();
			gpsLonLat.setX(cursor.getDouble(cursor.getColumnIndex("gps_x")));
			gpsLonLat.setY(cursor.getDouble(cursor.getColumnIndex("gps_y")));
			poi.setGps(gpsLonLat);
			
			pois.add(poi);
		}
		cursor.close();
		return pois;
	}
	/**
	 * 获取所有POI的中心点的本地坐标
	 * @return
	 */
	public HashMap<Integer, PointLonLat> getCenterFromAllPoi(){
		HashMap<Integer, PointLonLat> lonlats=new HashMap<Integer, PointLonLat>();
		String select_sql="select id,center_x,center_y from poi";
		Cursor cursor=db.rawQuery(select_sql, null);
		while(cursor.moveToNext()){
			int poiId=cursor.getInt(cursor.getColumnIndex("id"));
			double center_x=cursor.getDouble(cursor.getColumnIndex("center_x"));
			double center_y=cursor.getDouble(cursor.getColumnIndex("center_y"));
			PointLonLat center=new PointLonLat(center_x, center_y);
			lonlats.put(poiId, center);
		}
		cursor.close();
		return lonlats;
	}
	/**
	 * 更新GPS坐标
	 * @param poiId
	 * @param gpsPoint
	 */
	public void updateGpsXYInPoi(int poiId,PointLonLat gpsPoint){
		double gps_x=gpsPoint.getX();
		double gps_y=gpsPoint.getY();
		String update_sql="update poi set gps_x=?,gps_y=? where id=?";
		String[] args=new String[]{String.valueOf(gps_x),String.valueOf(gps_y),String.valueOf(poiId)};
		
		db.execSQL(update_sql, args);
	}
	/**
	 * 根据中心点以及X,Y上的偏差获取POI
	 * @param center
	 * @param dx
	 * @param dy
	 * @return
	 */
	public Poi getPoiByBound(Point curPoint,float dx,float dy){
		Poi poi=null;
		float curX=curPoint.getX();
		float curY=curPoint.getY();
/*		String sql_select="select * from poi where (center_x between ? and ?) and (center_y between ? and ?)";
		String[] args=new String[]{String.valueOf(curX-dx),String.valueOf(curX+dx),
				String.valueOf(curY-dy),String.valueOf(curY+dy)};*/
		String sql_select="select * from poi where (center_x between "+String.valueOf(curX-dx)+" and "+
				String.valueOf(curX+dx)+") and (center_y between "+String.valueOf(curY-dy)+" and "+String.valueOf(curY+dy)+")";
//		Cursor cursor=db.rawQuery(sql_select, args);
		Log.i("SQL_SELECT","cur:"+curX+","+curY+","+sql_select);
		Cursor cursor=db.rawQuery(sql_select, null);
		if(cursor.moveToNext()){
			poi=new Poi();
			poi.setId(cursor.getInt(cursor.getColumnIndex("id")));
			poi.setName(cursor.getString(cursor.getColumnIndex("name")));
			poi.setAddr(cursor.getString(cursor.getColumnIndex("addr")));
			poi.setDesc(cursor.getString(cursor.getColumnIndex("desc")));
			poi.setLayer(cursor.getInt(cursor.getColumnIndex("layer")));
			poi.setPointId(cursor.getInt(cursor.getColumnIndex("pointid")));
			poi.setWd(cursor.getString(cursor.getColumnIndex("wd")));
			
			Point center=new Point();
			center.setX(cursor.getFloat(cursor.getColumnIndex("center_x")));
			center.setY(cursor.getFloat(cursor.getColumnIndex("center_y")));
			poi.setCenter(center);
			
			PointLonLat gpsLonLat=new PointLonLat();
			gpsLonLat.setX(cursor.getDouble(cursor.getColumnIndex("gps_x")));
			gpsLonLat.setY(cursor.getDouble(cursor.getColumnIndex("gps_y")));
			poi.setGps(gpsLonLat);
		}
		cursor.close();
		return poi;
	}
}
