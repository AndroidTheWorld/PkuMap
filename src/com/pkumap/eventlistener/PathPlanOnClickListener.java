package com.pkumap.eventlistener;

import java.util.ArrayList;

import com.pkumap.activity.MapActivity;
import com.pkumap.activity.PathPlanActivity;
import com.pkumap.bean.Poi;
import com.pkumap.bean.Point;
import com.pkumap.util.PoiManager;
import com.zdx.pkumap.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class PathPlanOnClickListener implements OnClickListener {
	private PathPlanActivity event_Activity;
	private EditText path_start_edit=null;
	private EditText path_end_edit=null;
	
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
		
		
		Bundle bundle=new Bundle();
		bundle.putString("start",path_start_edit.getText().toString());
		bundle.putString("end", path_end_edit.getText().toString());
		
		Intent intent=new Intent();
		intent.setClass(event_Activity,MapActivity.class);
		intent.putExtras(bundle);
		
		event_Activity.setResult(event_Activity.RESULT_PATHPLAN, intent);
		event_Activity.finish();
	}
	/**
	 * 根据起点和终点来获取路径上的点
	 * @param start
	 * @param end
	 * @return
	 */
	private ArrayList<Point> getRoadNodes(String start,String end){
		
	}
	private void importPoiData(){
		PoiManager poiManager=new PoiManager(event_Activity.getApplicationContext());
//		poiManager.importPoiFromMongo();
//		poiManager.updatePoiTable();
		poiManager.updatePoiAddPointID();
//		poiManager.importRoadInfo();
//		poiManager.importRoadNodeInfo();
//		Poi poi=poiManager.getPoiById(255);
//		path_start_edit.setText(poi.getName());
	}
}
