package com.pkumap.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.pkumap.bean.Poi;
import com.pkumap.bean.Point;
import com.pkumap.bean.Road;
import com.pkumap.bean.RoadNode;
import com.pkumap.db.PathPlanService;
import com.pkumap.db.PoiService;

import android.content.Context;


public class PoiManager {
	private PoiService poiService;
	private PathPlanService pathPlanService;
	private Context context;
	public PoiManager(Context context) {
		this.context=context;
		if(poiService==null){
			poiService=new PoiService(context);
		}
		if(pathPlanService==null){
			pathPlanService=new PathPlanService(context);
		}
		
	}
	/**
	 * 根据名称返回POI
	 * @param name
	 * @return
	 */
	public Poi getPoiByName(String name){
		return poiService.getPoiByName(name);
	}
	/**
	 * 获取POI的名称
	 * @return
	 */
	public ArrayList<String> getPoiNameFromDB(){
		return poiService.getPoiNameFromDB();
	}
	/**
	 * 根据POI点的范围来获取POI
	 * @param center
	 * @param dx
	 * @param dy
	 * @return
	 */
	public Poi getPoiFromBound(Point center){
		float dx=10f,dy=10f;//暂时这样写死，之后进行测试
		return poiService.getPoiByBound(center, dx, dy);
	}
	/**
	 * 根据ID来获取POI
	 * @param id
	 * @return
	 */
	public Poi getPoiById(Integer id){
		return poiService.getPoiById(id);
	}
	/**
	 * 更新表结构
	 */
	public void updatePoiTable(){
		poiService.updatePoiTable();
	}
	/**
	 * 更新poi数据，添加pointid字段
	 */
	public void updatePoiAddPointID(){
		try{
				
				InputStream input=context.getResources().getAssets().open("layer3.txt");
				
				byte[] data=readInputStream(input);
				String dataStr=new String(data,"UTF-8");
				JSONObject jsonPOI=new JSONObject(dataStr);
				JSONArray arrayPOI=jsonPOI.getJSONArray("features");
				
				for(int i=0;i<arrayPOI.length();i++){
					JSONObject poiObj=arrayPOI.getJSONObject(i);
					JSONObject properties=poiObj.getJSONObject("properties");
					int pointid=properties.getInt("pointid");
					int id=properties.getInt("id");
					
					poiService.updatePoiAddPointId(pointid, id);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		
		
	}
	/**
	 * 导入poi数据
	 */
	public  void importPoiFromMongo(){
		String path="http://192.168.0.5:8083/pkumap/poi/list?layer=3";
		try{
		/*	URL url=new URL(path);
			HttpURLConnection urlConn=(HttpURLConnection) url.openConnection();
			urlConn.setConnectTimeout(5*1000);
			urlConn.connect();*/
			
			InputStream input=context.getResources().getAssets().open("layer3.txt");
			
			byte[] data=readInputStream(input);
			String dataStr=new String(data,"UTF-8");
			JSONObject jsonPOI=new JSONObject(dataStr);
			JSONArray arrayPOI=jsonPOI.getJSONArray("features");
			
			for(int i=0;i<arrayPOI.length();i++){
				JSONObject poiObj=arrayPOI.getJSONObject(i);
				JSONObject properties=poiObj.getJSONObject("properties");
				
				Poi poi=new Poi();
				poi.setId(properties.getInt("id"));
				poi.setLayer(properties.getInt("layer"));
				
				if(properties.has("name")){
					poi.setName(properties.getString("name"));
				}else{
					poi.setName("无");
				}
				
				if(properties.has("addr")){
					poi.setAddr(properties.getString("addr"));
				}else{
					poi.setAddr("无");
				}
				
				poi.setDesc(properties.getString("desc"));
				if(properties.has("wd")){
					poi.setWd(properties.getString("wd"));
				}else{
					poi.setWd("无");
				}
				
				JSONObject icon=properties.getJSONObject("icon");
				
				
				Point center=new Point();
				center.setX((float)icon.getDouble("x"));
				center.setY((float)icon.getDouble("y"));
				poi.setCenter(center);
				
				poiService.insertPoi(poi);
			}
			
			
	/*		if(urlConn.getResponseCode()==200){
				byte[] data=readInputStream(urlConn.getInputStream());
				String dataStr=new String(data,"UTF-8");
				JSONObject jsonPOI=new JSONObject(dataStr);
				JSONArray arrayPOI=jsonPOI.getJSONArray("features");
				DBManager dbManager=new DBManager(eventActivity);
				
				for(int i=0;i<arrayPOI.length();i++){
					JSONObject poiObj=arrayPOI.getJSONObject(i);
					JSONObject properties=poiObj.getJSONObject("properties");
					
					Poi poi=new Poi();
					poi.setId(properties.getInt("id"));
					poi.setLayer(properties.getInt("layer"));
					poi.setName(properties.getString("name"));
					poi.setAddr(properties.getString("addr"));
					poi.setDesc(properties.getString("desc"));
					poi.setWd(properties.getString("wd"));
					
					JSONObject icon=properties.getJSONObject("icon");
					Point center=new Point();
					center.setX((float)icon.getDouble("x"));
					center.setY((float)icon.getDouble("y"));
					poi.setCenter(center);
					
					dbManager.insert(poi);
				}
				
			}*/
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	/**
	 * 导入路径规划的路的信息
	 */
	public void importRoadInfo(){
			try{
				
				InputStream input=context.getResources().getAssets().open("3dpath.txt");
				
				byte[] data=readInputStream(input);
				String dataStr=new String(data,"UTF-8");
				JSONObject jsonPOI=new JSONObject(dataStr);
				JSONArray arrayPOI=jsonPOI.getJSONArray("road");
				
				for(int i=0;i<arrayPOI.length();i++){
					JSONObject roadObj=arrayPOI.getJSONObject(i);
					
					Road road=new Road();
					road.setBegin(roadObj.getInt("begin"));
					road.setEnd(roadObj.getInt("end"));
					road.setDistance((float)roadObj.getDouble("distance"));
					road.setType(roadObj.getString("type"));
					
					pathPlanService.insertRoadInfo(road);
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
	}
	/**
	 * 导入路径规划的点的信息
	 */
	public void importRoadNodeInfo(){
			try{
			
			InputStream input=context.getResources().getAssets().open("3dpoint.txt");
			
			byte[] data=readInputStream(input);
			String dataStr=new String(data,"UTF-8");
			JSONObject jsonPOI=new JSONObject(dataStr);
			JSONArray arrayPOI=jsonPOI.getJSONArray("point");
			
			for(int i=0;i<arrayPOI.length();i++){
				JSONObject roadNodeObj=arrayPOI.getJSONObject(i);
				
				RoadNode roadNode=new RoadNode();
				roadNode.setId(roadNodeObj.getInt("id"));
				roadNode.setX((float)roadNodeObj.getDouble("x"));
				roadNode.setY((float)roadNodeObj.getDouble("y"));
				roadNode.setType(roadNodeObj.getString("type"));
				
				pathPlanService.insertRoadNodeInfo(roadNode);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 关闭数据库
	 */
	public void  close(){
		if(poiService.helper!=null){
			poiService.helper.close();
		}
		if(poiService.db!=null){
			poiService.db.close();
		}
	}
	public byte[] readInputStream(InputStream input) throws IOException {
	    ByteArrayOutputStream output = new ByteArrayOutputStream();
	    byte[] buffer = new byte[1024];
	    int n = 0;
	    while (-1 != (n = input.read(buffer))) {
	        output.write(buffer, 0, n);
	    }
	    return output.toByteArray();
	}
}
