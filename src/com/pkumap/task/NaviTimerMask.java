package com.pkumap.task;

import java.util.ArrayList;
import java.util.TimerTask;

import com.pkumap.activity.MapActivity;
import com.pkumap.bean.PointLonLat;
import com.pkumap.bean.RoadNode;
import com.pkumap.manager.NaviManager;
import com.pkumap.manager.TimerManager;
import com.pkumap.util.RoadPlan;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


public class NaviTimerMask extends TimerTask {

	private Handler handler;
	private ArrayList<RoadNode> naviNodes;
	private RoadPlan roadPlan;
	private NaviManager naviManager;
	private RoadNode curRoadNode;
	private RoadNode nextRoadNode;
	private RoadNode threeRoadNode;
	private RoadNode targetRoadNode; //当前路径规划的终点，为重新规划路径做准备
	private TimerManager timerManager;
	private RoadNode gpsRoadNode;
	private MapActivity context;
	public NaviTimerMask(Handler handler){
		this.handler=handler;
	}
	/**
	 * 传入当前路径规划中的点的信息
	 * @param handler
	 * @param roadNodes
	 */
	public NaviTimerMask(TimerManager timerManager,Handler handler,ArrayList<RoadNode> naviNodes,RoadPlan roadPlan,MapActivity context){
		this.handler=handler;
		this.naviNodes=naviNodes;
		this.roadPlan=roadPlan;
		naviManager=NaviManager.getInstance();
		this.timerManager=timerManager;
		this.context=context;
		this.targetRoadNode=naviNodes.get(naviNodes.size()-1);
	}
	@Override
	public void run() {
		getCurRoadNodeFromCurPath();
		String naviSpeakTxt="";
		Bundle bundle=new Bundle();
		Message msg=new Message();
		msg.what=101;
		if(curRoadNode!=null&&nextRoadNode!=null){
			//实时获取当前位置到下一个路口点的方向和距离
			if(MapActivity.gpsLonLat.isInAreaOfTwoRoadNode(curRoadNode, nextRoadNode)){
//				PointLonLat srcLonLat=new PointLonLat(curRoadNode.getGps_x(), curRoadNode.getGps_y());
				PointLonLat srcLonLat=new PointLonLat(MapActivity.gpsLonLat.getX(), MapActivity.gpsLonLat.getY());
				PointLonLat destLonLat=new PointLonLat(nextRoadNode.getGps_x(), nextRoadNode.getGps_y());
				PointLonLat threeLonLat=new PointLonLat(threeRoadNode.getGps_x(), threeRoadNode.getGps_y());
				naviSpeakTxt=naviManager.GetOrientAndDistance(srcLonLat, destLonLat,threeLonLat);
				bundle.putString("naviSpeakTxt", naviSpeakTxt);  //要进行导航的内容（方向和距离）
				bundle.putParcelable("curLoc", curRoadNode);   //当前所临近的路口点
			}else{
				naviSpeakTxt="重新规划路径";
				if(gpsRoadNode!=null){
					naviNodes=roadPlan.getRoadNodeInPath(gpsRoadNode.getId(), targetRoadNode.getId(),roadPlan.mapView.map_type);
					if(naviNodes!=null){
						naviSpeakTxt+=",路径规划成功";
						bundle.putParcelable("curLoc", gpsRoadNode);
						context.mapView.roadPoints=naviNodes;
				//		context.showPathInMap(naviNodes);
					}else{
						naviSpeakTxt+=",路径规划失败";
						bundle.putParcelable("curLoc", curRoadNode);
					}
				}else{
					naviSpeakTxt+="无法获取附近路口点信息，请到离路近的地方重试";
					bundle.putParcelable("curLoc", curRoadNode);
				}
				bundle.putString("naviSpeakTxt", naviSpeakTxt); 
			}	
		}else if(curRoadNode!=null&&nextRoadNode==null){
			naviSpeakTxt="到达终点,结束导航";
			bundle.putString("naviSpeakTxt", naviSpeakTxt);
			bundle.putParcelable("curLoc", curRoadNode);
			timerManager.stopTimer();
		}else if(curRoadNode==null&&nextRoadNode==null){
			
			naviSpeakTxt="无法获取附近路口点信息，请到离路近的地方重试";
			bundle.putString("naviSpeakTxt", naviSpeakTxt);
		}
//		curRoadNode=null;
//		nextRoadNode=null;
		msg.setData(bundle);
		handler.sendMessage(msg);
		
	}
	/**
	 * 获取当前Gps所临近的点在路劲规划中是哪个点
	 * @return
	 */
	private void getCurRoadNodeFromCurPath(){
//		MapActivity.gpsLonLat=new PointLonLat(116.305243,39.988932);
		gpsRoadNode=roadPlan.getNearRoadNodeFromCurGps(MapActivity.gpsLonLat);
//		curRoadNode=new RoadNode(869, 440.5f,127.75f,116.312172753906,39.9922545288086,"2dmap");
//		nextRoadNode=new RoadNode(867, 440.5f,127.75f,116.312172753906,39.9922545288086,"2dmap");
		if(gpsRoadNode!=null){
			for(int i=0;i<naviNodes.size();i++){
				if(gpsRoadNode.getId()==naviNodes.get(i).getId()){
					curRoadNode=naviNodes.get(i);
					if(i+1==naviNodes.size()){
						nextRoadNode=null;
						threeRoadNode=null;
					}else if(i+2==naviNodes.size()){
						nextRoadNode=naviNodes.get(i+1);
						threeRoadNode=null;	
					}else{
						nextRoadNode=naviNodes.get(i+1);
						threeRoadNode=naviNodes.get(i+2);
					}
					return;
				}
			}
		}
		
	}
}
