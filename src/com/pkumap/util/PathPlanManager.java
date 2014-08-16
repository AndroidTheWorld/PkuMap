package com.pkumap.util;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import android.R.integer;
import android.content.Context;
import android.util.Log;

import com.pkumap.bean.PointLonLat;
import com.pkumap.bean.Road;
import com.pkumap.bean.RoadNode;
import com.pkumap.db.PathPlanService;

public class PathPlanManager {
	private PathPlanService pathPlanService;
	private Context context;
	public PathPlanManager(Context context){
		this.context=context;
		pathPlanService=new PathPlanService(context);
	}
	/**
	 * 根据地图类型获取路径规划所有点的信息
	 */
	public ArrayList<RoadNode> getAllPointInfoByType(String type){
		return pathPlanService.getAllPointInfoByType(type);
	}
	/**
	 * 根据地图类型获取路径规划所有路的信息
	 * @param type
	 * @return
	 */
	public ArrayList<Road> getAllRoadInfoByType(String type){
		return pathPlanService.getAllRoadInfoByType(type);
	}
	/**
	 * 更新表的结构，添加Gps的相应信息
	 */
	public void updateRoadNodeTableAddGps(){
		pathPlanService.updateRoadNodeTableAddGps();
	}
	/**
	 * 更新GPS坐标
	 * @param poiId
	 * @param gpsPoint
	 */
	public void updateGpsXYInRoadNode(int nodeId,PointLonLat gpsPoint,String type){
		pathPlanService.updateGpsXYInRoadNode(nodeId, gpsPoint,type);
	}
	/**
	 * 将2维上的本地坐标转化为Gps坐标，并保存到数据库中
	 */
	public void ConvertGpsFromLocalAndUpdateRoadNode(){
		PoiManager poiManager=new PoiManager(context);
		String type="2dmap";//暂时只更新2维上的点的坐标
//		ArrayList<RoadNode> roadNodes=pathPlanService.getAllPointInfoByType(type);
		
//		HashMap<Integer, PointLonLat> localRoadNode=SaveRoadNodeToHashMap(roadNodes);
//		poiManager.WriteHashMapToFile(localRoadNode, "/mnt/sdcard/convertLonLat/localRoadNode.txt", "localRoadNode");
		
//		HashMap<Integer, PointLonLat> localRoadNode=poiManager.ReadHashMapFromFile("/mnt/sdcard/convertLonLat/localRoadNode.txt", "localRoadNode");
		
//		HashMap<Integer, PointLonLat> mercatorRoadNode=poiManager.ConvertLocalCoordToMercator(localRoadNode);
//		poiManager.WriteHashMapToFile(mercatorRoadNode, "/mnt/sdcard/convertLonLat/mercatorRoadNode.txt", "mercatorRoadNode");
		
//		HashMap<Integer, PointLonLat> mercatorRoadNode=poiManager.ReadHashMapFromFile("/mnt/sdcard/convertLonLat/mercatorRoadNode.txt", "mercatorRoadNode");
		
//		HashMap<Integer, PointLonLat> lonlatRoadNode=poiManager.ConvertMercatorToLonLat(mercatorRoadNode);
//		poiManager.WriteHashMapToFile(lonlatRoadNode, "/mnt/sdcard/convertLonLat/lonlatRoadNode.txt", "lonlatRoadNode");
		
//		HashMap<Integer, PointLonLat> lonlatRoadNode=poiManager.ReadHashMapFromFile("/mnt/sdcard/convertLonLat/lonlatRoadNode.txt", "lonlatRoadNode");
		
//		HashMap<Integer, PointLonLat> gpsRoadNode=poiManager.ConvertLonLatToGPS(lonlatRoadNode);
//		poiManager.WriteHashMapToFile(gpsRoadNode, "/mnt/sdcard/convertLonLat/gpsRoadNode.txt", "gpsRoadNode");
		
		HashMap<Integer, PointLonLat> gpsRoadNode=poiManager.ReadHashMapFromFile("/mnt/sdcard/convertLonLat/gpsRoadNode.txt", "gpsRoadNode");
		
		updateGpsXYInRoadNode(gpsRoadNode);
		
	}
	/**
	 * 更新Gps的坐标数据
	 * @param gpsRoadNode
	 */
	public void updateGpsXYInRoadNode(HashMap<Integer, PointLonLat> gpsRoadNode){
		Iterator<Integer> iterator=gpsRoadNode.keySet().iterator();
		while(iterator.hasNext()){
			int nodeId=(Integer) iterator.next();
			PointLonLat gpsPoint=gpsRoadNode.get(nodeId);
			updateGpsXYInPoi(nodeId, gpsPoint);
		}
	}
	/**
	 * 更新RoadNode中的gps坐标
	 * @param nodeId
	 * @param gpspoint
	 */
	public void updateGpsXYInPoi(int nodeId,PointLonLat gpsRoadNode){
		//做一个简单的微调,在经度范围上加上0.0001，在维度范围上减去0.0002
		String type="2dmap";
		double gps_x=gpsRoadNode.getX();
		double gps_y=gpsRoadNode.getY();
		gps_x=gps_x+0.0001;
		gps_y=gps_y-0.0002;
		Log.i("GPS_UPDATE", "poiId:"+nodeId+",gps_x:"+gps_x+",gps_y:"+gps_y);
		PointLonLat newGpsPoint=new PointLonLat(gps_x, gps_y);
		pathPlanService.updateGpsXYInRoadNode(nodeId, newGpsPoint,type);
	}
	/**
	 * 将Arraylist转化为HashMap
	 * @param roadNodes
	 * @return
	 */
	public HashMap<Integer, PointLonLat> SaveRoadNodeToHashMap(ArrayList<RoadNode> roadNodes){
		HashMap<Integer, PointLonLat> localRoadNodeMap=new HashMap<Integer, PointLonLat>();
		for(int i=0;i<roadNodes.size();i++){
			RoadNode roadNode=roadNodes.get(i);
			int nodeId=roadNode.getId();
			double x=roadNode.getX();
			double y=roadNode.getY();
			PointLonLat roadNodePoint=new PointLonLat(x, y);
			
			localRoadNodeMap.put(nodeId, roadNodePoint);
		}
		return localRoadNodeMap;
	}
	/**
	 * 根据Id获取RoadNode
	 * @param id
	 * @return
	 */
	public RoadNode getRoadNodeById(int id,String map_type){
		return pathPlanService.getRoadNodeById(id,map_type);
	}
	/**
	 * 根据当前的Gps坐标获取附近的PointId(路口)
	 * @param gpsLonLat
	 * @return
	 */
	public int getPointIdFromCurGps(PointLonLat gpsLonLat){
		//暂时设置在一个0.0004*0.0004范围内搜索一个路口的PointId
		double dx=0.0002;
		double dy=0.0002;
		return pathPlanService.getPointIdFromGps(gpsLonLat, dx, dy);
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
	public byte[] readInputStream(InputStream input) throws IOException {
	    ByteArrayOutputStream output = new ByteArrayOutputStream();
	    byte[] buffer = new byte[1024];
	    int n = 0;
	    while (-1 != (n = input.read(buffer))) {
	        output.write(buffer, 0, n);
	    }
	    return output.toByteArray();
	}
	/**
	 * 关闭数据库
	 */
	public void  close(){
		if(pathPlanService.helper!=null){
			pathPlanService.helper.close();
		}
		if(pathPlanService.db!=null){
			pathPlanService.db.close();
		}
	}
}
