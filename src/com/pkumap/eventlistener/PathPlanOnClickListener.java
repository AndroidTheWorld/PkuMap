package com.pkumap.eventlistener;

import java.util.ArrayList;

import com.pkumap.activity.MapActivity;
import com.pkumap.activity.PathPlanActivity;
import com.pkumap.bean.Poi;
import com.pkumap.bean.Point;
import com.pkumap.bean.Road;
import com.pkumap.bean.RoadNode;
import com.pkumap.util.Dijkstra;
import com.pkumap.util.PathPlanManager;
import com.pkumap.util.PoiManager;
import com.zdx.pkumap.R;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PathMeasure;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class PathPlanOnClickListener implements OnClickListener {
	private PathPlanActivity event_Activity;
	private EditText path_start_edit=null;
	private EditText path_end_edit=null;
	private PathPlanManager pathPlanManager;
	private ArrayList<RoadNode> roadNodes;
	private ArrayList<Road> roads;
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
	public PathPlanOnClickListener(PathPlanActivity target_Activity){
		this.event_Activity=target_Activity;
		this.path_start_edit=(EditText) event_Activity.findViewById(R.id.path_start);
		this.path_end_edit=(EditText) event_Activity.findViewById(R.id.path_end);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.path_return:
			event_Activity.finish();
			break;
		case R.id.path_search:
			getStartEndPoint();
			break;
		case R.id.btn_import:
			importPoiData();
			break;
		}
	}
	/**
	 * 返回起点和终点信息，在地图上画出路径
	 */
	private void getStartEndPoint(){
		pathPlanManager=new PathPlanManager(event_Activity.getApplicationContext());
		initPathInfo();
		dijkstra=new Dijkstra(pathmap);
		Log.i("ZDX", "zzz");
		String startStr=path_start_edit.getText().toString();
		String endStr=path_end_edit.getText().toString();
		ArrayList<RoadNode> roadNodes=getRoadNodes(startStr,endStr);
		
		Bundle bundle=new Bundle();
		bundle.putParcelableArrayList("path", roadNodes);
		
		Intent intent=new Intent();
		intent.setClass(event_Activity,MapActivity.class);
		intent.putExtras(bundle);
		
		event_Activity.setResult(event_Activity.RESULT_PATHPLAN, intent);
		event_Activity.finish();
	}
	/**
	 * 初始化邻接表信息
	 * @return
	 */
	private void initPathInfo(){
		pathmap=new float[pCount][pCount];
		// 初始化地图为不可达
		for (int i = 0; i < pCount; i++){
			for (int j = 0; j < pCount; j++) {
				pathmap[i][j] = INF;
			}
		}
		//获取所有点和路的信息	
		String type="3dmap";//临时这样写,之后加入3d地图
		roadNodes=pathPlanManager.getAllPointInfoByType(type);
		roads=pathPlanManager.getAllRoadInfoByType(type);
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
	private ArrayList<RoadNode> getRoadNodes(String start,String end){
		
		int startId=event_Activity.poiManager.getPointIdByName(start);
		int endId=event_Activity.poiManager.getPointIdByName(end);
		startId=20;
		endId=157;
		ArrayList<RoadNode> roadNodes=new ArrayList<RoadNode>();
		ArrayList<Integer> pointids=dijkstra.getShortDistance(startId, endId);
		String ids="";
		for(int i=0;i<pointids.size();i++){
			ids+=pointids.get(i)+",";
			RoadNode roadNode=pathPlanManager.getRoadNodeById(pointids.get(i));
			roadNodes.add(roadNode);
			
		}
		Log.i("IDS", ids);
		return roadNodes;
	}
	private void importPoiData(){
		PoiManager poiManager=new PoiManager(event_Activity.getApplicationContext());
		PathPlanManager pathPlanManager=new PathPlanManager(event_Activity.getApplicationContext());
//		poiManager.importPoiFromMongo();
//		poiManager.updatePoiTable();
		poiManager.updatePoiAddPointID();
//		pathPlanManager.importRoadInfo();
//		pathPlanManager.importRoadNodeInfo();
//		Poi poi=poiManager.getPoiById(255);
//		path_start_edit.setText(poi.getName());
	}
}
