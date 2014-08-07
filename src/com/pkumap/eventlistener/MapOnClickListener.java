package com.pkumap.eventlistener;

import com.pkumap.activity.PathPlanActivity;
import com.pkumap.activity.SearchActivity;
import com.zdx.pkumap.R;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class MapOnClickListener implements OnClickListener {

	private Activity event_Activity;
	private EditText poi_edit_txt;
	public MapOnClickListener(Activity target_Activity) {
		this.event_Activity=target_Activity;
		poi_edit_txt=(EditText) event_Activity.findViewById(R.id.poi_edit_search);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.mapView:
			showOrHidePoi();
			break;
		case R.id.poi_edit_search:
			searchPoi();
			break;
		case R.id.nearby:
			Toast.makeText(event_Activity, "附近", Toast.LENGTH_SHORT).show();
			break;
		case R.id.pathplan:
			confirmStartEndLocation();
			break;
		}
	}
	/**
	 * 点击mapView显示或者隐藏POI
	 */
	private void showOrHidePoi(){
		Toast.makeText(event_Activity, "nihao", Toast.LENGTH_SHORT).show();
	}
	/**
	 * 确定路径规划的起点和终点
	 */
	private void confirmStartEndLocation(){
		Intent intent=new Intent();
		intent.setClass(event_Activity, PathPlanActivity.class);
	//	intent.putExtra("key","数字校园");
		event_Activity.startActivityForResult(intent, 0);
	}
	/**
	 * 跳转SearchActivity,进行POI的搜索
	 */
	private void searchPoi() {
		Intent intent=new Intent();
		intent.setClass(event_Activity, SearchActivity.class);
		intent.putExtra("poi_name_search",poi_edit_txt.getText().toString());
		event_Activity.startActivityForResult(intent, 0);
	}

}
