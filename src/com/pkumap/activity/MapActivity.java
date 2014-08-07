package com.pkumap.activity;


import com.pkumap.bean.Poi;
import com.pkumap.bean.Point;
import com.pkumap.eventlistener.MapOnClickListener;
import com.pkumap.eventlistener.SearchPoiOnClickListener;
import com.pkumap.util.ConvertCoordinate;
import com.zdx.pkumap.R;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.TextureView;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;


public class MapActivity extends Activity {
	private final static String TAG="MapActivity";
	private MapView mapView=null;
	private EditText edit_search=null;
	private RadioButton radio_near=null;
	private RadioButton radio_pathplan=null;
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
		
        mapView=(MapView) findViewById(R.id.mapView);
        edit_search=(EditText) findViewById(R.id.poi_edit_search);
        radio_near=(RadioButton) findViewById(R.id.nearby);
        radio_pathplan=(RadioButton) findViewById(R.id.pathplan);
        
 //       mapView.setOnClickListener(new MapOnClickListener(MapActivity.this));
        edit_search.setOnClickListener(new MapOnClickListener(MapActivity.this));
        radio_near.setOnClickListener(new MapOnClickListener(MapActivity.this));
        radio_pathplan.setOnClickListener(new MapOnClickListener(MapActivity.this));
        
        convertCoordinate=new ConvertCoordinate();
        
        fm=getFragmentManager();
        
        
		// 显示自定义的地图View
		//	mMapView.setFocusable(true);
//		setContentView(mapView);
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		
		switch (resultCode) {
		case RESULT_OK:
			showPoiInMap(data);
			break;
		case PathPlanActivity.RESULT_PATHPLAN:
			showPathInMap(data);
			
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
	 * 在地图上展示规划的路径
	 */
	private void showPathInMap(Intent data){
		Bundle pathBundle=data.getExtras();
		edit_search.setText(pathBundle.getString("start"));
	}
	/**
	 * 在地图上展示相应的POI
	 */
	private void showPoiInMap(Intent data){
		
		Poi poi=data.getParcelableExtra("poi");
		edit_search.setText(data.getExtras().getString("poi_name"));
		
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
/*	    ft.setCustomAnimations(android.R.animator.fade_in,  
                android.R.animator.fade_out);*/
	    PoiDetailFragment pdf=new PoiDetailFragment(poi);
	    if(fm.findFragmentByTag("PoiDetailFragment")!=null){
	    	ft.replace(R.id.poi_detail_layout, pdf, "PoiDetailFragment");
	    }else {
	    	 ft.add(R.id.poi_detail_layout, pdf,"PoiDetailFragment");
		}
	    
	    ft.commit();	
	}
}
