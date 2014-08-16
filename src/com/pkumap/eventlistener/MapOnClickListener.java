package com.pkumap.eventlistener;

import java.util.ArrayList;

import com.pkumap.activity.MapActivity;
import com.pkumap.activity.MapView;
import com.pkumap.activity.PathPlanActivity;
import com.pkumap.activity.SearchActivity;
import com.pkumap.bean.Poi;
import com.pkumap.bean.PointLonLat;
import com.pkumap.bean.RoadNode;
import com.pkumap.util.PathPlanManager;
import com.pkumap.util.RoadPlan;
import com.zdx.pkumap.R;

import android.R.anim;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

public class MapOnClickListener implements OnClickListener {

	private MapActivity event_Activity;
	private EditText poi_edit_txt;
	private ImageView layers_img_view;
	private MapView mapView;
	private View layerView;
	private RadioButton radio_layer_three=null;
	private RadioButton radio_layer_two=null;

	private PopupWindow layers_window;
	public MapOnClickListener(MapActivity target_Activity) {
		this.event_Activity=target_Activity;
		poi_edit_txt=(EditText) event_Activity.findViewById(R.id.poi_edit_search);
		layers_img_view=(ImageView) event_Activity.findViewById(R.id.layers);
		mapView=(MapView) event_Activity.findViewById(R.id.mapView);
		
		layerView=LayoutInflater.from(event_Activity).inflate(R.layout.layer_main,null);
		radio_layer_three=(RadioButton)layerView.findViewById(R.id.three_layer);
	    radio_layer_two=(RadioButton) layerView.findViewById(R.id.two_layer);
	    radio_layer_three.setOnClickListener(this);
        radio_layer_two.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.mapView:
			showOrHidePoi();
			break;
		case R.id.zoom_in:
			zoomMap(R.id.zoom_in);
			break;
		case R.id.zoom_out:
			zoomMap(R.id.zoom_out);
			break;
		case R.id.layers:
			changeLayers();
			break;
		case R.id.naviImg:
			getCurLocationInMap();
			break;
		case R.id.three_layer:
			showOtherMap(R.id.three_layer);
			break;
		case R.id.two_layer:
			showOtherMap(R.id.two_layer);
			break;
		case R.id.poi_edit_search:
			searchPoi();
			break;
		case R.id.poi_go:
//			Toast.makeText(event_Activity, mapView.poi.getName(), Toast.LENGTH_SHORT).show();
			goToPoi(mapView.poi);
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
	 * 将地图移动到以当前位置为中心（由Gps获取）
	 */
	private void  getCurLocationInMap(){
		if(MapActivity.gpsLonLat!=null){
			Toast.makeText(event_Activity,"当前GPS坐标："+MapActivity.gpsLonLat.getX()+","+MapActivity.gpsLonLat.getY(), Toast.LENGTH_SHORT).show();
			poi_edit_txt.setText("当前GPS坐标："+MapActivity.gpsLonLat.getX()+","+MapActivity.gpsLonLat.getY());
		}else{
			Toast.makeText(event_Activity, "当前GPS不可用", Toast.LENGTH_SHORT).show();
		}
	}
	/**
	 * 从当前位置到POI的位置
	 * @param poi
	 */
	private void goToPoi(Poi poi){
		RoadPlan roadPlan=new RoadPlan(mapView);
//		ArrayList<RoadNode> roadNodes=roadPlan.getRoadNodes("博雅塔", poi.getName(),mapView.map_type);
//		PointLonLat gpsLonLat=new PointLonLat(116.298708,39.993326);//北大西门
//		PointLonLat gpsLonLat=new PointLonLat(116.306478,39.989591);//理科一号楼(门前)
//		PointLonLat gpsLonLat=new PointLonLat(116.305530,39.992543);//博雅塔
//		PointLonLat gpsLonLat=new PointLonLat(116.304821,39.988406); //百年纪念讲堂
		
		if(MapActivity.gpsLonLat==null){
			Toast.makeText(event_Activity, "当前GPS不可用", Toast.LENGTH_SHORT).show();
			return;
		}
		PointLonLat gpsLonLat=new PointLonLat(MapActivity.gpsLonLat.getX(),MapActivity.gpsLonLat.getY()); //百年纪念讲堂
		
		int curPointId=roadPlan.getPointIdFromCurGps(gpsLonLat);
		if(curPointId==-1){
			Toast.makeText(event_Activity, "当前路径不可达", Toast.LENGTH_SHORT).show();
			return;
		}
		ArrayList<RoadNode> roadNodes=roadPlan.getRoadNodeInPath(curPointId, poi.getPointId(), mapView.map_type);
//		Toast.makeText(event_Activity,roadNodes.size(), Toast.LENGTH_SHORT).show();
		event_Activity.showPathInMap(roadNodes);
	}
	/**
	 * 切换地图
	 * @param id
	 */
	private void showOtherMap(int id){
		if(id==R.id.three_layer&&!"3dmap".equals(mapView.map_type)){
			mapView.map_type="3dmap";
			Toast.makeText(event_Activity, "三维地图", Toast.LENGTH_SHORT).show();
		}
		if(id==R.id.two_layer&&!"2dmap".equals(mapView.map_type)){
			mapView.map_type="2dmap";
			Toast.makeText(event_Activity, "二维地图", Toast.LENGTH_SHORT).show();
		}
		mapView.building=null;
		mapView.poi=null;
		mapView.roadPoints.clear();
		mapView.currentStatus=mapView.STATUS_INIT;
		mapView.invalidate();
		layers_window.dismiss();
	}
	/**
	 * 初始化PopUpWindow
	 */
	private void initPopUpWindow(){
		layers_window=new PopupWindow(layerView, 135,80);
		layers_window.setFocusable(true);  
		layers_window.setOutsideTouchable(true);
//		layers_window.setAnimationStyle(anim.slide_in_left);
		Drawable pop_drawable=event_Activity.getResources().getDrawable(R.drawable.pop_border);
        layers_window.setBackgroundDrawable(pop_drawable);
	}
	/**
	 * 显示不同的地图图层的选择框PopWindow
	 */
	private void changeLayers(){
		initPopUpWindow();
		int[] location = new int[2];  
		layers_img_view.getLocationOnScreen(location); 
		layers_window.showAtLocation(layers_img_view, Gravity.NO_GRAVITY, location[0]-layers_window.getWidth(), location[1]-10); 
	}
	/**
	 * 点击mapView显示或者隐藏POI
	 */
	private void showOrHidePoi(){
		Toast.makeText(event_Activity, "nihao", Toast.LENGTH_SHORT).show();
	}
	/**
	 * 地图缩放
	 * @param id
	 */
	private void zoomMap(int id){
		
		ImageView zoom_in=(ImageView) event_Activity.findViewById(R.id.zoom_in);
		ImageView zoom_out=(ImageView) event_Activity.findViewById(R.id.zoom_out);
		if(id==R.id.zoom_in){
			Toast.makeText(event_Activity, "ZoomIn", Toast.LENGTH_SHORT).show();
			if(mapView.level<3){
				mapView.singleScaleLevel=2f/mapView.scaleLevel;
				mapView.scaleLevel=2f;
			}else{
				return;
			}
			
		}
		if(id==R.id.zoom_out){
			Toast.makeText(event_Activity, "ZoomOut", Toast.LENGTH_SHORT).show();
			if(mapView.level>1){
				mapView.singleScaleLevel=0.5f/mapView.scaleLevel;
				mapView.scaleLevel=0;
			}else{
				return;
			}
			
		}
		mapView.currentStatus=mapView.STATUS_ZOOM;
		mapView.invalidate();
	}
	/**
	 * 确定路径规划的起点和终点
	 */
	private void confirmStartEndLocation(){
		Intent intent=new Intent();
		intent.setClass(event_Activity, PathPlanActivity.class);
		intent.putExtra("map_type",mapView.map_type);
		event_Activity.startActivityForResult(intent, 0);
	}
	/**
	 * 跳转SearchActivity,进行POI的搜索
	 */
	private void searchPoi() {
		Intent intent=new Intent();
		intent.setClass(event_Activity, SearchActivity.class);
		intent.putExtra("poi_name_search",poi_edit_txt.getText().toString());
		intent.putExtra("map_type", mapView.map_type);
		event_Activity.startActivityForResult(intent, 0);
	}

}
