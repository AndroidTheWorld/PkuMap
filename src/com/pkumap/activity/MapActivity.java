package com.pkumap.activity;


import java.util.ArrayList;

import com.pkumap.bean.Building;
import com.pkumap.bean.Poi;
import com.pkumap.bean.Point;
import com.pkumap.bean.PointLonLat;
import com.pkumap.bean.RoadNode;
import com.pkumap.eventlistener.GpsLocationListener;
import com.pkumap.eventlistener.MapOnClickListener;
import com.pkumap.eventlistener.SearchPoiOnClickListener;
import com.pkumap.util.BuildingManager;
import com.pkumap.util.ConvertCoordinate;
import com.pkumap.util.PoiManager;
import com.zdx.pkumap.R;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;


public class MapActivity extends FragmentActivity {
	private final static String TAG="MapActivity";
	private MapView mapView=null;
	private EditText edit_search=null;
	private RadioButton radio_near=null;
	private RadioButton radio_pathplan=null;
	private ImageView zoom_in=null;
	private ImageView zoom_out=null;
	private ImageView layers=null;
	private ImageView navi=null;
	public LocationManager locManager;
	
	/**
	 * 根据Gps获取当前的位置，在不断发生变化，全局供其他定位模块使用
	 */
	public static PointLonLat gpsLonLat;
	
	private MapOnClickListener mapOnClickListener;
	private GpsLocationListener gpsLocationListener;
	private ConvertCoordinate convertCoordinate;
	public FragmentManager fm;
	private final static String picurl="http://162.105.30.246:8080/pkumap/map?level=1&x=7&y=7&type=2dmap";
	private final static String picsrc="http://www.baidu.com";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//设置线程的策略  
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()     
        .detectDiskReads()     
        .detectDiskWrites()     
        .detectNetwork()   // or .detectAll() for all detectable problems     
        .penaltyLog()     
        .build());     
       //设置虚拟机的策略  
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()     
                .detectLeakedSqlLiteObjects()     
                .detectLeakedClosableObjects()     
                .penaltyLog()     
                .penaltyDeath()     
                .build());  
		super.onCreate(savedInstanceState);
		// 全屏显示窗口
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	/*	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
			WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
		setContentView(R.layout.map_main);
		
		fm=getFragmentManager();
		locManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
        mapView=(MapView) findViewById(R.id.mapView);
        edit_search=(EditText) findViewById(R.id.poi_edit_search);
        radio_near=(RadioButton) findViewById(R.id.nearby);
        radio_pathplan=(RadioButton) findViewById(R.id.pathplan);
        zoom_in=(ImageView) findViewById(R.id.zoom_in);
        zoom_out=(ImageView) findViewById(R.id.zoom_out);
        layers=(ImageView) findViewById(R.id.layers);
        navi=(ImageView) findViewById(R.id.naviImg);
       
        mapOnClickListener=new MapOnClickListener(this);
        gpsLocationListener=new GpsLocationListener(this);
        edit_search.setOnClickListener(mapOnClickListener);
        radio_near.setOnClickListener(mapOnClickListener);
        radio_pathplan.setOnClickListener(mapOnClickListener);
        zoom_in.setOnClickListener(mapOnClickListener);
        zoom_out.setOnClickListener(mapOnClickListener);
        layers.setOnClickListener(mapOnClickListener);
        navi.setOnClickListener(mapOnClickListener);
        
        convertCoordinate=new ConvertCoordinate();
        
     // 设置获取一次GPS的定位信息
     	locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
     			gpsLocationListener);
       
		// 显示自定义的地图View
		//	mMapView.setFocusable(true);
//		setContentView(mapView);
		
	}
	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		openGPSSettings();
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		
		switch (resultCode) {
		case RESULT_OK:
			edit_search.setText(data.getExtras().getString("poi_name"));
			if("3dmap".equals(mapView.map_type)){
				Building building=data.getParcelableExtra("building");
				showBuildingInMap(building);
			}else{
				Poi poi=data.getParcelableExtra("poi");
				showPoiInMap(poi);
			}
			break;
		case PathPlanActivity.RESULT_PATHPLAN:
			Bundle pathBundle=data.getExtras();
			ArrayList<RoadNode> roadNodes=pathBundle.getParcelableArrayList("path");
			showPathInMap(roadNodes);
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		Fragment curFragment=fm.findFragmentByTag("PoiDetailFragment");
		if(curFragment!=null&&curFragment.isVisible()){
			mapView.HidePoiMarkerAndDetail();
		}else{
			super.onBackPressed();
		}
		
	}
	/**
	 * 启动GPS
	 */
	private void openGPSSettings(){
		LocationManager alm=(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		if(alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)){
			Toast.makeText(this, "GPS已经开启", Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(this, "请开启GPS", Toast.LENGTH_SHORT).show();
			Intent myIntent=new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(myIntent);
		}
	}
	/**
	 * 在地图上展示规划的路径
	 */
	public void showPathInMap(ArrayList<RoadNode> roadNodes){
		//将起点移动到屏幕的中心
		RoadNode startNode=roadNodes.get(0);
		Point startPoint=new Point(startNode.getX(),startNode.getY());
		startPoint=convertCoordinate.getScreenPointFromLonLat(startPoint, mapView);
		mapView.moveDX=(mapView.ScreenWidth/2-startPoint.getX());
		mapView.moveDY=(mapView.ScreenHeight/2-startPoint.getY());
		
		mapView.currentStatus=mapView.STATUS_MOVE;
		mapView.roadPoints=roadNodes;
		mapView.invalidate();
	}
	/**
	 * 在地图上展示相应的Building
	 */
	private void showBuildingInMap(Building building){
		Point curLonLat=building.getCenter();
		Point screenPoint=convertCoordinate.getScreenPointFromLonLat(curLonLat, mapView);
		
		mapView.moveDX=mapView.ScreenWidth/2-screenPoint.getX();
		mapView.moveDY=mapView.ScreenHeight/2-screenPoint.getY();
		mapView.currentStatus=mapView.STATUS_MOVE;
		mapView.building=building;
		mapView.invalidate();
		
		
	    FragmentTransaction ft=fm.beginTransaction();
	    BuildingDetailFragment bdf=new BuildingDetailFragment(building);
	    if(fm.findFragmentByTag("PoiDetailFragment")!=null){
	    	ft.replace(R.id.poi_detail_layout, bdf, "PoiDetailFragment");
	    }else {
	    	 ft.add(R.id.poi_detail_layout, bdf,"PoiDetailFragment");
		}
	    
	    ft.commit();	
	}
	/**
	 * 在地图上展示相应的POI
	 */
	private void showPoiInMap(Poi poi){
		
		Point curLonLat=poi.getCenter();
		Point screenPoint=convertCoordinate.getScreenPointFromLonLat(curLonLat, mapView);
		
		
		int scaleFlag=mapView.getScaleFlag(poi.getLayer());
		int curScalFlag=mapView.getScaleFlag(mapView.level);
		float scale=(float)scaleFlag/(float)curScalFlag;
		mapView.mapDX*=scale;
		mapView.mapDY*=scale;
		Log.i("MAPDXY", "mapViewDX:"+mapView.mapDX+",mapViewDY:"+mapView.mapDY+",Scale:"+scale);
		mapView.level=poi.getLayer();
		mapView.moveDX=(mapView.ScreenWidth/2-screenPoint.getX())*scale;
		mapView.moveDY=(mapView.ScreenHeight/2-screenPoint.getY())*scale;
		mapView.currentStatus=mapView.STATUS_MOVE;
		mapView.poi=poi;
		mapView.invalidate();
		
		
	    FragmentTransaction ft=fm.beginTransaction();
	    PoiDetailFragment pdf=new PoiDetailFragment(poi);
	    if(fm.findFragmentByTag("PoiDetailFragment")!=null){
	    	ft.replace(R.id.poi_detail_layout, pdf, "PoiDetailFragment");
	    }else {
	    	 ft.add(R.id.poi_detail_layout, pdf,"PoiDetailFragment");
		}
	    ft.commit();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mapView.poiManager.close();
		mapView.buildingManager.close();
		mapView.pathPlanManager.close();
	}
	
}
