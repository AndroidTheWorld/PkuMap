package com.pkumap.util;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.pkumap.activity.MapActivity;
import com.pkumap.activity.MapView;
import com.pkumap.bean.Point;
import com.pkumap.bean.PointLonLat;
import com.pkumap.bean.Road;
import com.pkumap.bean.RoadNode;
import com.pkumap.manager.BuildingManager;
import com.pkumap.manager.PathPlanManager;
import com.pkumap.manager.PoiManager;

public class RoadPlan {
	/**
	 * 总共的点数
	 */
	private final int pCount = 1000;
	/**
	 * 初始化地图信息
	 */
	private final int INF = 999999;
	/**
	 * 路径规划中的邻接表
	 */
	private float[][] pathmap;
	private Dijkstra dijkstra;
	private ArrayList<RoadNode> roadNodes;
	private ArrayList<Road> roads;
	private PathPlanManager pathPlanManager;
	private PoiManager poiManager;
	private BuildingManager buildingManager;
	public MapView mapView;
	
	public RoadPlan(Context context){
		pathPlanManager=new PathPlanManager(context);
		poiManager=new PoiManager(context);
		buildingManager=new BuildingManager(context);
	}
	public RoadPlan(MapView mapView){
		this.mapView=mapView;
		this.pathPlanManager=mapView.pathPlanManager;
		this.poiManager=mapView.poiManager;
		this.buildingManager=mapView.buildingManager;
	}
	public RoadPlan(PathPlanManager pathPlanManager,PoiManager poiManager,BuildingManager buildingManager){
		this.pathPlanManager=pathPlanManager;
		this.poiManager=poiManager;
		this.buildingManager=buildingManager;
	}
	/**
	 * 初始化邻接表信息
	 * @return
	 */
	private void initPathInfo(String map_type){
		pathmap=new float[pCount][pCount];
		// 初始化地图为不可达
		for (int i = 0; i < pCount; i++){
			for (int j = 0; j < pCount; j++) {
				pathmap[i][j] = INF;
			}
		}
		//获取所有点和路的信息	
		roadNodes=pathPlanManager.getAllPointInfoByType(map_type);
		roads=pathPlanManager.getAllRoadInfoByType(map_type);
		//初始化pathmap
		for(int i=0;i<roads.size();i++){
			int begin=roads.get(i).getBegin();
			int end=roads.get(i).getEnd();
			float dis=roads.get(i).getDistance();
			pathmap[begin][end]=pathmap[end][begin]=dis;
		}
	}
	/**
	 * 根据起点和终点来获取路径上的点
	 * @param start
	 * @param end
	 * @return
	 */
	public ArrayList<RoadNode> getRoadNodes(String start,String end,String map_type){
		
		ArrayList<RoadNode> roadNodes;
		int startId,endId;
		
		if("3dmap".equals(map_type)){
			if("我的位置".equals(start)){
				startId=pathPlanManager.getPointIdFromCurGps(MapActivity.gpsLonLat, map_type);
			}else{
				startId=buildingManager.getPointIdByName(start);
			}
			endId=buildingManager.getPointIdByName(end);
		}else{
			if("我的位置".equals(start)){
				startId=pathPlanManager.getPointIdFromCurGps(MapActivity.gpsLonLat, map_type);
			}else{
				startId=poiManager.getPointIdByName(start);
			}
			endId=poiManager.getPointIdByName(end);
			
		}
		if(startId==-1){
			roadNodes=null;
			return roadNodes;
		}
		roadNodes=getRoadNodeInPath(startId, endId, map_type);
		return roadNodes;
	}
	/**
	 * 根据当前的GPs来获取附近的PointId
	 * @param gpsLonLat
	 * @return
	 */
	public int getPointIdFromCurGps(PointLonLat gpsLonLat){
		return pathPlanManager.getPointIdFromCurGps(gpsLonLat,mapView.map_type);
	}
	/**
	 * 根据当前的GPS获取附近的路口点
	 * @param curGpsLonLat
	 * @return
	 */
	public RoadNode getNearRoadNodeFromCurGps(PointLonLat curGpsLonLat){
		int pointId=getPointIdFromCurGps(curGpsLonLat);
		return pathPlanManager.getRoadNodeById(pointId, mapView.map_type);
	}
	/**
	 * 根据起点和终点的ID获取对应的路径上的点
	 * @param startId
	 * @param endId
	 * @return
	 */
	public ArrayList<RoadNode> getRoadNodeInPath(int startId,int endId,String map_type){
		
		initPathInfo(map_type);
		dijkstra=new Dijkstra(pathmap);
		
		ArrayList<RoadNode> roadNodes=new ArrayList<RoadNode>();
		
		ArrayList<Integer> pointids=dijkstra.getShortDistance(startId, endId);
		String ids="";
		for(int i=0;i<pointids.size();i++){
			ids+=pointids.get(i)+",";
			RoadNode roadNode=pathPlanManager.getRoadNodeById(pointids.get(i),map_type);
			roadNodes.add(roadNode);
		}
		Log.i("IDS", ids);
		return roadNodes;
	}
	public void close(){
		if(null!=pathPlanManager){
			pathPlanManager.close();
		}
		if(null!=poiManager){
			poiManager.close();
		}
		if(null!=buildingManager){
			buildingManager.close();
		}
		
	}
}
