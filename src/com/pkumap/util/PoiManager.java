package com.pkumap.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pkumap.bean.Poi;
import com.pkumap.bean.Point;
import com.pkumap.bean.PointLonLat;
import com.pkumap.db.PoiService;

import android.content.Context;
import android.util.Log;


public class PoiManager {
	private PoiService poiService;
	private Context context;
	public PoiManager(Context context) {
		this.context=context;
		if(poiService==null){
			poiService=new PoiService(context);
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
	 * 根据名称获取PointId
	 * @param name
	 * @return
	 */
	public int getPointIdByName(String name){
		return poiService.getPointIdByName(name);
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
	 * 更新表结构，添加GPS字段
	 */
	public void updatePoiAddGps(){
		poiService.updatePoiAddGps();
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
	 * 将本地坐标转化为Gps并更新到POI中
	 */
	public void ConvertToGpsAndUpdateGpsInPoi(){
		HashMap<Integer, PointLonLat> gpsLonLat=ConvertLocalCoordToGPS();
		updateGpsInPoi(gpsLonLat);
	}
	/**
	 * 将本地的坐标转换为真实的GPS坐标
	 * @param local_x
	 * @param local_y
	 */
	public HashMap<Integer, PointLonLat> ConvertLocalCoordToGPS(){
		HashMap<Integer, PointLonLat> localPoints=poiService.getCenterFromAllPoi();
//		WriteHashMapToFile(localPoints, "/mnt/sdcard/convertLonLat/localLonLat.txt", "localLonLat");
		
		HashMap<Integer,PointLonLat> baiduMercator=ConvertLocalCoordToMercator(localPoints);
//		WriteHashMapToFile(baiduMercator, "/mnt/sdcard/convertLonLat/baiduMercator.txt", "baiduMercator");
		
		HashMap<Integer, PointLonLat> baiduLonLat=ConvertMercatorToLonLat(baiduMercator);
		WriteHashMapToFile(baiduLonLat, "/mnt/sdcard/convertLonLat/baiduLonLat", "baiduLonLat");
		
		HashMap<Integer, PointLonLat> gpsLonLat=ConvertLonLatToGPS(baiduLonLat);
		WriteHashMapToFile(gpsLonLat, "/mnt/sdcard/convertLonLat/gps.txt", "gps");
		return gpsLonLat;
	}
	/**
	 * 更新数据库中Poi的Gps坐标
	 * @param gpsLonLat
	 */
	public void updateGpsInPoi(HashMap<Integer, PointLonLat> gpsLonLat){
		Iterator<Integer> iterator=gpsLonLat.keySet().iterator();
		while(iterator.hasNext()){
			int poiId=(Integer) iterator.next();
			PointLonLat gpsPoint=gpsLonLat.get(poiId);
			updateGpsXYInPoi(poiId, gpsPoint);
		}
	}
	/**
	 * 更新Poi中的gps坐标
	 * @param poiId
	 * @param gpspoint
	 */
	public void updateGpsXYInPoi(int poiId,PointLonLat gpsPoint){
		poiService.updateGpsXYInPoi(poiId, gpsPoint);
	}
	/**
	 * 将百度的经纬度坐标转化为真实的Gps坐标
	 * @param baiduLonLat
	 * @return
	 */
	public HashMap<Integer, PointLonLat> ConvertLonLatToGPS(HashMap<Integer, PointLonLat> baiduLonLat){
		HashMap<Integer, PointLonLat> gpsLonLat=new HashMap<Integer, PointLonLat>();
		Iterator<Integer> iterator=baiduLonLat.keySet().iterator();
		String baiduURL="http://api.map.baidu.com/geoconv/v1/?coords=%s,%s&from=1&to=5&ak=AC197ef902d4d8cbdc86a22b568ec684";
		int i=0;
		while(iterator.hasNext()){
			int poiId=(Integer) iterator.next();
			PointLonLat oldLonLatPoint=baiduLonLat.get(poiId);
			String convertURL=String.format(baiduURL, String.valueOf(oldLonLatPoint.getX()),String.valueOf(oldLonLatPoint.getY()));
			Log.i("ConvertGPS", ""+(++i)+": "+convertURL);
			JSONObject convertResult=getConvertResultFromUrl(convertURL);
			PointLonLat newLonLatPoint=null;
			if(convertResult!=null){
				newLonLatPoint=getLonLatFromConvertResult(convertResult);
			}
			PointLonLat gpsPoint=convertGpsPoint(oldLonLatPoint, newLonLatPoint);
			if(gpsPoint!=null){
				gpsLonLat.put(poiId, gpsPoint);
			}
		}
		return gpsLonLat;
	}
	/**
	 * 转化为gps坐标(之后需要微调)
	 * @param oldPoint
	 * @param newPoint
	 * @return
	 */
	public PointLonLat convertGpsPoint(PointLonLat oldPoint,PointLonLat newPoint){
		double gps_x=oldPoint.getX()*2-newPoint.getX();
		double gps_y=oldPoint.getY()*2-newPoint.getY();
		PointLonLat gpsPoint=new PointLonLat(gps_x,gps_y);
		return gpsPoint;
	}
	/**
	 * 将百度的墨卡托坐标转化为百度的经纬度坐标
	 * @return
	 */
	public HashMap<Integer, PointLonLat> ConvertMercatorToLonLat(HashMap<Integer, PointLonLat> baiduMercator){
		HashMap<Integer, PointLonLat> baiduLonLat=new HashMap<Integer, PointLonLat>();
		Iterator<Integer> iterator=baiduMercator.keySet().iterator();
		String baiduURL="http://api.map.baidu.com/geoconv/v1/?coords=%s,%s&from=6&to=5&ak=AC197ef902d4d8cbdc86a22b568ec684";
		int i=0;
		while(iterator.hasNext()){
			int poiId=(Integer) iterator.next();
			PointLonLat mercatorPoint=baiduMercator.get(poiId);
			String convertURL=String.format(baiduURL, String.valueOf(mercatorPoint.getX()),String.valueOf(mercatorPoint.getY()));
			Log.i("ConvertLonLat", ""+(++i)+": "+convertURL);
			JSONObject convertResult=getConvertResultFromUrl(convertURL);
			
			PointLonLat lonlatPoint=null;
			if(convertResult!=null){
				lonlatPoint=getLonLatFromConvertResult(convertResult);
			}
			if(lonlatPoint!=null){
				baiduLonLat.put(poiId, lonlatPoint);
			}
		}
		return baiduLonLat;
	}
	/**
	 * 从返回的结果中获取经纬度信息
	 * @param convertResult
	 * @return
	 */
	public PointLonLat getLonLatFromConvertResult(JSONObject convertResult){
		PointLonLat lonLat=new PointLonLat();
		try{
			JSONArray coordArray=convertResult.getJSONArray("result");
			JSONObject lonlatObject=coordArray.getJSONObject(0);
			lonLat.setX((float)lonlatObject.getDouble("x"));
			lonLat.setY((float)lonlatObject.getDouble("y"));
		}catch(JSONException e){
			e.printStackTrace();
		}
		return lonLat;
	}
	/**
	 * 根据URL返回JSON串格式的转化结果
	 * @param convertURL
	 * @return
	 */
	public JSONObject getConvertResultFromUrl(String convertURL){
		JSONObject result=null;
		try{
			URL url=new URL(convertURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection(); 
			connection.connect();
			
			InputStream input=connection.getInputStream();
			BufferedReader br=new BufferedReader(new InputStreamReader(input));
			StringBuffer buffer=new StringBuffer();
			String temp=null;
			while((temp=br.readLine())!=null){
				buffer.append(temp);
			}
			br.close();
			input.close();
			result=new JSONObject(buffer.toString());
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
	/**
	 * 将本地坐标转换为百度的墨卡托坐标（经过偏移）
	 * @param local_x
	 * @param local_y
	 */
	public HashMap<Integer,PointLonLat> ConvertLocalCoordToMercator(HashMap<Integer, PointLonLat> localPoints){
		HashMap<Integer,PointLonLat> mercatorPoints=new HashMap<Integer,PointLonLat>();
		Iterator<Integer> localKeys=localPoints.keySet().iterator();
		while(localKeys.hasNext()){
			int poiId=localKeys.next();
			PointLonLat localPointLonLat=localPoints.get(poiId);
			
			double center_x=localPointLonLat.getX();
			double center_y=localPointLonLat.getY();
			
			center_x=(center_x-119.65f)*2+(12948960.43f - 239.3f);
			center_y=(center_y-188.7f)*2+(4838640.59f - 377.4f);
			
			PointLonLat mercatorPoint=new PointLonLat(center_x,center_y);
			mercatorPoints.put(poiId, mercatorPoint);
		}
		return mercatorPoints;
	}
	/**
	 * 从文件中读入Json来构建HashMap
	 * @param filePath
	 */
	public HashMap<Integer, PointLonLat> ReadHashMapFromFile(String filePath,String type){
	//	String fileStr=readStrFromFile(filePath);
		String fileStr=SDCardUtil.read(filePath);
		HashMap<Integer, PointLonLat> lonlatMap=new HashMap<Integer, PointLonLat>();
		try{
			JSONObject jsonObject=new JSONObject(fileStr);
			JSONArray lonlatArray=jsonObject.getJSONArray(type);
			for(int i=0;i<lonlatArray.length();i++){
				JSONObject lonlatObject=lonlatArray.getJSONObject(i);
				
				int poiId=lonlatObject.getInt("poiId");
				double x=lonlatObject.getDouble("x");
				double y=lonlatObject.getDouble("y");
				PointLonLat lonLatPoint=new PointLonLat(x, y);
				lonlatMap.put(poiId,lonLatPoint);
			}
		}catch(JSONException e){
			e.printStackTrace();
		}
		return lonlatMap;
	}
	
	/**
	 * 从文件中读取字符串
	 * @param filePath
	 * @return
	 */
	public String readStrFromFile(String filePath){
		File file = new File(filePath);
	    BufferedReader reader = null;
	    String laststr = "";
	    try {
		     reader = new BufferedReader(new FileReader(file));
		     String tempString = null;
		    
		     while ((tempString = reader.readLine()) != null) {
		    	 laststr = laststr+tempString;
		     }
		     reader.close();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    } finally {
		     if (reader != null) {
			      try {
			       reader.close();
			      } catch (IOException e1) {
			      }
		     }
	    }
	    return laststr;
	}
	/**
	 * 将HashMap以JSON格式写入到文件中
	 * @param map
	 * @param filePath
	 * @param type
	 */
	public void WriteHashMapToFile(HashMap<Integer, PointLonLat> map,String filePath,String type){
		
		Iterator<Integer> iterator=map.keySet().iterator();
		JSONArray jsonArray=new JSONArray();
		while(iterator.hasNext()){
			int poiId=iterator.next();
			PointLonLat lonLat=map.get(poiId);
			JSONObject jsonLonLat=makeJson(poiId, lonLat);
			jsonArray.put(jsonLonLat);
		}
		
		JSONObject result=new JSONObject();
		try{
			result.put(type, jsonArray);
		}catch(JSONException e){
			e.printStackTrace();
		}
		
	//	writeToFile(result.toString(),filePath);
		SDCardUtil.write(result.toString(), filePath);
		
	}
	/**
	 * 将字符串写入到文件中
	 * @param content
	 * @param filePath
	 */
	public void writeToFile(String content,String filePath){
		File file=new File(filePath);
		BufferedWriter bufferedWriter = null;
		  FileWriter fileWriter = null;
		  try
		  {
			   fileWriter = new FileWriter(file);
			   bufferedWriter = new BufferedWriter(fileWriter);
			   bufferedWriter.write(content);
			   bufferedWriter.flush();
		  } catch (IOException e){
			  e.printStackTrace();
		  } finally{
			   try
			   {
			    fileWriter.close();
			    bufferedWriter.close();
			   } catch (IOException e)
			   {
			    e.printStackTrace();
			   }
		  }
	}
	/**
	 * 组成JsonObject
	 * @param poiId
	 * @param lonLat
	 * @return
	 */
	public JSONObject makeJson(int poiId,PointLonLat lonLat){
		JSONObject jsonObject=new JSONObject();
		try{
			jsonObject.put("poiId", poiId);
			jsonObject.put("x", lonLat.getX());
			jsonObject.put("y", lonLat.getY());
		}catch(JSONException e){
			e.printStackTrace();
		}
		return jsonObject;
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
