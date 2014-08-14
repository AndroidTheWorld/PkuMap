package com.pkumap.util;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

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
	 * 根据Id获取RoadNode
	 * @param id
	 * @return
	 */
	public RoadNode getRoadNodeById(int id,String map_type){
		return pathPlanService.getRoadNodeById(id,map_type);
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
