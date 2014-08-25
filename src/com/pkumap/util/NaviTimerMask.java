package com.pkumap.util;

import java.util.ArrayList;
import java.util.TimerTask;

import com.pkumap.activity.MapActivity;
import com.pkumap.bean.PointLonLat;
import com.pkumap.bean.RoadNode;
import com.pkumap.manager.NaviManager;
import com.pkumap.manager.TimerManager;

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
	private TimerManager timerManager;
	public NaviTimerMask(Handler handler){
		this.handler=handler;
	}
	/**
	 * 传入当前路径规划中的点的信息
	 * @param handler
	 * @param roadNodes
	 */
	public NaviTimerMask(TimerManager timerManager,Handler handler,ArrayList<RoadNode> naviNodes,RoadPlan roadPlan){
		this.handler=handler;
		this.naviNodes=naviNodes;
		this.roadPlan=roadPlan;
		naviManager=NaviManager.getInstance();
		this.timerManager=timerManager;
	}
	@Override
	public void run() {
		getCurRoadNodeFromCurPath();
		String naviSpeakTxt="";
		Bundle bundle=new Bundle();
		Message msg=new Message();
		msg.what=101;
		if(curRoadNode!=null&&nextRoadNode!=null){
			PointLonLat srcLonLat=new PointLonLat(curRoadNode.getGps_x(), curRoadNode.getGps_y());
			PointLonLat destLonLat=new PointLonLat(nextRoadNode.getGps_x(), nextRoadNode.getGps_y());
			naviSpeakTxt=naviManager.GetOrientAndDistance(srcLonLat, destLonLat);
			bundle.putString("naviSpeakTxt", naviSpeakTxt);  //要进行导航的内容（方向和距离）
			bundle.putParcelable("curLoc", curRoadNode);   //当前所临近的路口点
			
		}else if(curRoadNode!=null&&nextRoadNode==null){
			naviSpeakTxt="到达终点,结束导航";
			bundle.putString("naviSpeakTxt", naviSpeakTxt);
			bundle.putParcelable("curLoc", curRoadNode);
			timerManager.stopTimer();
		}else if(curRoadNode==null&&nextRoadNode==null){
			naviSpeakTxt="无法获取附近路口点信息，请到离路近的地方重试";
			bundle.putString("naviSpeakTxt", naviSpeakTxt);
		}
		curRoadNode=null;
		nextRoadNode=null;
		msg.setData(bundle);
		handler.sendMessage(msg);
		
	}
	/**
	 * 获取当前Gps所临近的点在路劲规划中是哪个点
	 * @return
	 */
	private void getCurRoadNodeFromCurPath(){
		
		int curPointId=roadPlan.getPointIdFromCurGps(MapActivity.gpsLonLat);
		for(int i=0;i<naviNodes.size();i++){
			if(curPointId==naviNodes.get(i).getId()){
				curRoadNode=naviNodes.get(i);
				if(i+1==naviNodes.size()){
					nextRoadNode=null;
				}else{
					nextRoadNode=naviNodes.get(i+1);
				}
				return;
			}
		}
	}
}
