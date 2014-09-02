package com.pkumap.activity;


import java.util.ArrayList;

import com.pkumap.bean.Building;
import com.pkumap.bean.Poi;
import com.pkumap.bean.Point;
import com.pkumap.bean.PointLonLat;
import com.pkumap.bean.RoadNode;
import com.pkumap.eventlistener.GpsLocationListener;
import com.pkumap.eventlistener.MapOnClickListener;
import com.pkumap.manager.AudioManager;
import com.pkumap.manager.TimerManager;
import com.pkumap.util.ConvertCoordinate;
import com.pkumap.util.RoadPlan;
import com.zdx.pkumap.R;












import android.R.integer;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.Toast;


public class MapActivity extends FragmentActivity {
	private final static String TAG="MapActivity";
	
	public final static int RESULT_PATHPATH=1001;
	public final static int RESULT_NAVI=1002;
	private static final int REQ_TTS_STATUS_CHECK = 0;
	
	public MapView mapView=null;
	public EditText edit_search=null;
	private RadioButton radio_near=null;
	private RadioButton radio_pathplan=null;
	private RadioButton radio_navi=null;
	private RadioButton radio_tool=null;
	private RadioButton radio_layer_three=null;
	private RadioButton radio_layer_two=null;
	private ImageView zoom_in=null;
	private ImageView zoom_out=null;
	public ImageView layers=null;
	private ImageView navi=null;
	//屏幕弹出框
	public PopupWindow layers_window;
	//屏幕的透明度
	public int flag=0;
	
	public LocationManager locManager;
	/**
	 * 根据Gps获取当前的位置，在不断发生变化，全局供其他定位模块使用
	 */
//	public static PointLonLat gpsLonLat=new PointLonLat(116.306340,39.989003);
//	public static PointLonLat gpsLonLat=new PointLonLat(116.305243,39.988932);
//	public static PointLonLat gpsLonLat=new PointLonLat( 116.305538,39.992547);
	public static PointLonLat gpsLonLat;
	
	private MapOnClickListener mapOnClickListener;
	private GpsLocationListener gpsLocationListener;
	public ConvertCoordinate convertCoordinate;
	public AudioManager audioManager;
	public TimerManager timerManager;
	public RoadPlan roadPlan;
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
        radio_navi=(RadioButton) findViewById(R.id.navigation);
        radio_tool=(RadioButton) findViewById(R.id.tool);
        zoom_in=(ImageView) findViewById(R.id.zoom_in);
        zoom_out=(ImageView) findViewById(R.id.zoom_out);
        layers=(ImageView) findViewById(R.id.layers);
        navi=(ImageView) findViewById(R.id.naviImg);
       
        initPoPupWindow();
        mapOnClickListener=new MapOnClickListener(this);
        gpsLocationListener=new GpsLocationListener(this);
        edit_search.setOnClickListener(mapOnClickListener);
        radio_near.setOnClickListener(mapOnClickListener);
        radio_pathplan.setOnClickListener(mapOnClickListener);
        radio_navi.setOnClickListener(mapOnClickListener);
        radio_tool.setOnClickListener(mapOnClickListener);
        zoom_in.setOnClickListener(mapOnClickListener);
        zoom_out.setOnClickListener(mapOnClickListener);
        layers.setOnClickListener(mapOnClickListener);
        navi.setOnClickListener(mapOnClickListener);
//      mapView.setOnClickListener(mapOnClickListener);
        radio_layer_three.setOnClickListener(mapOnClickListener);
        radio_layer_two.setOnClickListener(mapOnClickListener);
      
        
        
        convertCoordinate=new ConvertCoordinate();
        
     // 检查TTS数据是否已经安装并且可用(暂时先不检查吧)
// 		Intent checkIntent = new Intent();
// 		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
// 		startActivityForResult(checkIntent, REQ_TTS_STATUS_CHECK);
        audioManager=AudioManager.getInstance(this);
     // 设置获取一次GPS的定位信息
     	locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0,
     			gpsLocationListener);
       
		// 显示自定义的地图View
		//	mMapView.setFocusable(true);
//		setContentView(mapView);
		
	}
	/**
	 * 初始化PopUpWindow
	 */
	private void initPoPupWindow(){
		View layerView=LayoutInflater.from(this).inflate(R.layout.layer_main,null);
        layers_window=new PopupWindow(layerView, 135,80);
//		layers_window.setFocusable(true);
		//点击PopUpWindow区域外的部分，PopUpWindow消失
//		layers_window.setOutsideTouchable(false);
//		layers_window.setAnimationStyle(anim.slide_in_left);
        Drawable pop_drawable=this.getResources().getDrawable(R.drawable.pop_border);
        layers_window.setBackgroundDrawable(pop_drawable);
       
        radio_layer_three=(RadioButton)layerView.findViewById(R.id.three_layer);
	    radio_layer_two=(RadioButton) layerView.findViewById(R.id.two_layer);
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
		case RESULT_PATHPATH:
			Bundle pathBundle=data.getExtras();
			ArrayList<RoadNode> roadNodes=pathBundle.getParcelableArrayList("path");
			showPathInMap(roadNodes);
			break;
		case RESULT_NAVI:
			Bundle pathNaviBundle=data.getExtras();
			ArrayList<RoadNode> naviNodes=pathNaviBundle.getParcelableArrayList("path");
			showPathInMap(naviNodes);
			startNaviFromCurLocation(naviNodes);
			break;
		case TextToSpeech.Engine.CHECK_VOICE_DATA_PASS:
			 //初始化语音功能
	        audioManager=AudioManager.getInstance(this);
			break;
		default:
			break;
		}
	}
	/**
	 * 开始导航
	 * @param naviNodes
	 */
	public void startNaviFromCurLocation(ArrayList<RoadNode> naviNodes){
		audioManager.mSpeech.speak("导航开始",TextToSpeech.QUEUE_ADD,null);
		timerManager.startTimer(naviNodes, roadPlan);
	}
	
	/**
	 * 调整背景的透明度
	 * @param flag
	 */
	public void windowDimOut(){
		WindowManager.LayoutParams layoutParams=this.getWindow().getAttributes();
		if(flag==1){
			layoutParams.alpha=0.5f;
		}else if(flag==0){
			layoutParams.alpha=1f;
		}
		this.getWindow().setAttributes(layoutParams);
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
		audioManager.close();
		locManager.removeUpdates(gpsLocationListener);
	}
	
}
