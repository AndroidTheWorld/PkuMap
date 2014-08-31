package com.pkumap.eventlistener;

import java.util.ArrayList;

import com.pkumap.activity.MapActivity;
import com.pkumap.activity.MapView;
import com.pkumap.activity.PathPlanActivity;
import com.pkumap.activity.SearchActivity;
import com.pkumap.bean.Poi;
import com.pkumap.bean.Point;
import com.pkumap.bean.PointLonLat;
import com.pkumap.bean.RoadNode;
import com.pkumap.handler.NaviGpsHandler;
import com.pkumap.manager.NaviManager;
import com.pkumap.manager.TimerManager;
import com.pkumap.util.RoadPlan;
import com.zdx.pkumap.R;








import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.Toast;

public class MapOnClickListener implements OnClickListener {

	private MapActivity event_Activity;
	private EditText poi_edit_txt;
	private ImageView layers_img_view;
	private MapView mapView;
	private PopupWindow layers_window;
	private NaviGpsHandler naviGpsHandler;
	private NaviManager naviManager;
	public MapOnClickListener(MapActivity target_Activity) {
		this.event_Activity=target_Activity;
		poi_edit_txt=target_Activity.edit_search;
		layers_img_view=target_Activity.layers;
		mapView=target_Activity.mapView;
		layers_window=target_Activity.layers_window;
	
        event_Activity.roadPlan=new RoadPlan(mapView);
        
        naviGpsHandler=new NaviGpsHandler(event_Activity);

        event_Activity.timerManager=new TimerManager(naviGpsHandler,event_Activity);
        
        naviManager=NaviManager.getInstance();
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
//			Toast.makeText(event_Activity, "附近", Toast.LENGTH_SHORT).show();
			CancelTimer();
			break;
		case R.id.pathplan:
			confirmStartEndLocation(MapActivity.RESULT_PATHPATH);
			break;
		case R.id.navigation:
			confirmStartEndLocation(MapActivity.RESULT_NAVI);
			break;
		case R.id.tool:
			TestAudio();
//			TestTimer();
			break;
		}
	}
	/**
	 * 测试语音功能
	 */
	public void TestAudio(){
		//测试：从北大计算中心----->百年纪念讲堂  A-->B-->C-->D-->E-->F
//		PointLonLat srcLonLat=new PointLonLat(116.306427819824,39.9891760681152);   A
//		PointLonLat destLonLat=new PointLonLat(116.306443078613,39.988985333252);   B
		
//		PointLonLat srcLonLat=new PointLonLat(116.306443078613,39.988985333252);   B
//		PointLonLat destLonLat=new PointLonLat(116.305161340332,39.9889891479492); C
		
//		PointLonLat srcLonLat=new PointLonLat(116.305161340332,39.9889891479492);  C
//		PointLonLat destLonLat=new PointLonLat(116.305176599121,39.9886114929199); D
		
//		PointLonLat srcLonLat=new PointLonLat(116.305176599121,39.9886114929199);  D
//		PointLonLat destLonLat=new PointLonLat(116.304932458496,39.9886191223145); E
		
//		PointLonLat srcLonLat=new PointLonLat(116.304932458496,39.9886191223145);  E
//		PointLonLat destLonLat=new PointLonLat(116.304726464844,39.9884627197266); F
		
//		PointLonLat srcLonLat=new PointLonLat(116.3017425,39.97200388888889);
		PointLonLat srcLonLat=event_Activity.gpsLonLat;//有时候因为GPS受天气影响，会有一定的偏差
		PointLonLat destLonLat=new PointLonLat(116.30178083333334,39.97201527777778);
		
		String pathNavi=naviManager.GetOrientAndDistance(srcLonLat, destLonLat);
		event_Activity.audioManager.mSpeech.speak(pathNavi,TextToSpeech.QUEUE_FLUSH, null);
	}
	/**
	 * 测试定时器
	 */
	public void TestTimer(){
		event_Activity.timerManager.startTimer();
	}
	/**
	 * 取消定时器
	 */
	public void CancelTimer(){
		Toast.makeText(event_Activity, "关闭语音",Toast.LENGTH_SHORT ).show();
		event_Activity.timerManager.stopTimer();
//		naviGpsHandler.removeCallbacks(null);
	}
	/**
	 * 将地图移动到以当前位置为中心（由Gps获取）
	 */
	private void  getCurLocationInMap(){
//		MapActivity.gpsLonLat=new PointLonLat(116.305530,39.992543);
		if(MapActivity.gpsLonLat!=null){
			if(curLocIsInPku(MapActivity.gpsLonLat)){
//				Toast.makeText(event_Activity,"当前GPS坐标："+MapActivity.gpsLonLat.getX()+","+MapActivity.gpsLonLat.getY(), Toast.LENGTH_SHORT).show();
				poi_edit_txt.setText("当前GPS坐标："+MapActivity.gpsLonLat.getX()+","+MapActivity.gpsLonLat.getY());
				RoadNode near_node=event_Activity.roadPlan.getNearRoadNodeFromCurGps(MapActivity.gpsLonLat);
				if(null==near_node){
					Toast.makeText(event_Activity, "当前位置无法获取路口点信息，请到距路近的地方重试",Toast.LENGTH_SHORT ).show();
					return;
				}
				Point point=new Point(near_node.getX(),near_node.getY());
				Point screenPoint=event_Activity.convertCoordinate.getScreenPointFromLonLat(point, mapView);
				mapView.curLocation=point;
				mapView.moveDX=mapView.ScreenWidth/2-screenPoint.getX();
				mapView.moveDY=mapView.ScreenHeight/2-screenPoint.getY();
				mapView.currentStatus=mapView.STATUS_MOVE;
				mapView.invalidate();
			}else{
				Toast.makeText(event_Activity, "当前位置不在北大",Toast.LENGTH_SHORT ).show();
				poi_edit_txt.setText("当前GPS坐标："+MapActivity.gpsLonLat.getX()+","+MapActivity.gpsLonLat.getY());
			}
		}else{
			Toast.makeText(event_Activity, "当前GPS不可用", Toast.LENGTH_SHORT).show();
		}
	}
	/**
	 * 当前的位置是否在北大内部
	 */
	private boolean curLocIsInPku(PointLonLat curLonLat){
		//北大南门（39.985348,116.305478）,北大北门（39.997887,116.303368）
		//北大西门 （39.993367,116.298173）,北大东门（39.990860,116.309416）
		double x=curLonLat.getX();
		double y=curLonLat.getY();
		if(x>116.298173&&x<116.309416&&y>39.985348&&y<39.997887){
			return true;
		}
		return false;
	}
	/**
	 * 从当前位置到POI的位置
	 * @param poi
	 */
	private void goToPoi(Poi poi){
//		ArrayList<RoadNode> roadNodes=roadPlan.getRoadNodes("博雅塔", poi.getName(),mapView.map_type);
//		PointLonLat gpsLonLat=new PointLonLat(116.298708,39.993326);//北大西门
//		PointLonLat gpsLonLat=new PointLonLat(116.306478,39.989591);//理科一号楼(门前)
//		PointLonLat gpsLonLat=new PointLonLat(116.305530,39.992543);//博雅塔
//		PointLonLat gpsLonLat=new PointLonLat(116.304821,39.988406); //百年纪念讲堂
//		PointLonLat gpsLonLat=new PointLonLat(116.30689738,39.9894428837); 
		
		if(MapActivity.gpsLonLat==null){
			Toast.makeText(event_Activity, "当前GPS不可用", Toast.LENGTH_SHORT).show();
			return;
		}
		PointLonLat gpsLonLat=new PointLonLat(MapActivity.gpsLonLat.getX(),MapActivity.gpsLonLat.getY()); //Gps数据
		
		int curPointId=event_Activity.roadPlan.getPointIdFromCurGps(gpsLonLat);
		if(curPointId==-1){
			Toast.makeText(event_Activity, "当前路径不可达", Toast.LENGTH_SHORT).show();
			return;
		}
		ArrayList<RoadNode> roadNodes=event_Activity.roadPlan.getRoadNodeInPath(curPointId, poi.getPointId(), mapView.map_type);
//		Toast.makeText(event_Activity,roadNodes.size(), Toast.LENGTH_SHORT).show();
		event_Activity.showPathInMap(roadNodes);
		event_Activity.startNaviFromCurLocation(roadNodes);
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
		event_Activity.flag=0;
		event_Activity.windowDimOut();
	}
	/**
	 * 显示不同的地图图层的选择框PopWindow
	 */
	private void changeLayers(){
		event_Activity.flag=1;
		event_Activity.windowDimOut();
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
	private void confirmStartEndLocation(int resultCode){
		if(resultCode==MapActivity.RESULT_NAVI){
			if(MapActivity.gpsLonLat!=null){
				if(!curLocIsInPku(MapActivity.gpsLonLat)){
					Toast.makeText(event_Activity, "当前位置不在北大,无法导航！",Toast.LENGTH_SHORT ).show();
					return;
				}
			}else{
				Toast.makeText(event_Activity, "当前无法获取GPS信息", Toast.LENGTH_SHORT).show();
				return;
			}
		}
		Intent intent=new Intent();
		intent.setClass(event_Activity, PathPlanActivity.class);
		intent.putExtra("map_type",mapView.map_type);
		intent.putExtra("type", resultCode);
		event_Activity.startActivityForResult(intent, resultCode);
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
