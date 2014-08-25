package com.pkumap.manager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.pkumap.bean.Building;
import com.pkumap.bean.Poi;
import com.pkumap.bean.Point;
import com.pkumap.db.BuildingService;

import android.content.Context;

public class BuildingManager {
	private BuildingService buildingService;
	private Context context;
	public BuildingManager(Context context) {
		this.context=context;
		if(buildingService==null){
			buildingService=new BuildingService(context);
		}
	}
	
	/**
	 * 创建Building表
	 */
	public void createBuildingTable(){
		buildingService.createBuildingVector();
	}
	/**
	 * 获取3d地图中所有的POI名称
	 */
	public ArrayList<String> getAllBuildingNames(){
		return buildingService.getAllBuildingNames();
	}
	/**
	 * 根据范围和X,Y方向上的偏移量来获取Building
	 * @param center
	 * @return
	 */
	public Building getBuildingByBound(Point curPoint){
		float dx=30f,dy=30f;  //暂时这样吧
		return buildingService.getBuildingByBound(curPoint, dx, dy);
	}
	/**
	 * 根据名称获取该Building在路径规则中的PointID
	 * @param name
	 * @return
	 */
	public int getPointIdByName(String name){
		return buildingService.getPointIdByName(name);
	}
	/**
	 * 根据名称获取Building
	 * @return
	 */
	public Building getBuildingByName(String name){
		return buildingService.getBuildingByName(name);
	}
	/**
	 * 导入building数据
	 */
	public void importBuildingData(){
		try{
			InputStream input=context.getResources().getAssets().open("building.txt");
			
			byte[] data=readInputStream(input);
			String dataStr=new String(data,"UTF-8");
			JSONObject jsonBuilding=new JSONObject(dataStr);
			JSONArray arrayBuilding=jsonBuilding.getJSONArray("features");
			
			for(int i=0;i<arrayBuilding.length();i++){
				JSONObject buildingObj=arrayBuilding.getJSONObject(i);
				JSONObject properties=buildingObj.getJSONObject("properties");
				
				Building building=new Building();
				building.setId(properties.getInt("id"));
				
				if(properties.has("name")){
					building.setName(properties.getString("name"));
				}else{
					building.setName("无");
				}
				
				if(properties.has("introduction")){
					building.setIntroduction(properties.getString("introduction"));
				}else{
					building.setIntroduction("无");
				}
				building.setCategory("building");
				building.setType("3dmap");
				building.setPointid(properties.getInt("pointid"));
				
				Point center=new Point();
				center.setX((float)properties.getDouble("centerX"));
				center.setY((float)properties.getDouble("centerY"));
				building.setCenter(center);
				
				JSONObject geometry=buildingObj.getJSONObject("geometry");
				JSONArray coordinates=geometry.getJSONArray("coordinates");
				
				String coordinateStr=coordinates.get(0).toString();
				buildingService.insertBuildingData(building,coordinateStr);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
		/**
		 * 将InputStream 转化为Byte[]
		 * @param input
		 * @return
		 * @throws IOException
		 */
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
		if(buildingService.helper!=null){
			buildingService.helper.close();
		}
		if(buildingService.db!=null){
			buildingService.db.close();
		}
	}
}
