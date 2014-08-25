package com.pkumap.util;

import com.pkumap.activity.MapActivity;
import com.pkumap.bean.Point;
import com.pkumap.bean.RoadNode;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

public class NaviGpsHandler extends Handler {

	private MapActivity context;
	public NaviGpsHandler(MapActivity context){
		this.context=context;
	}
	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case 101:
			Bundle bundle=msg.getData();
			String naviSpeakTxt=bundle.getString("naviSpeakTxt");
			RoadNode curLocRoadNode=bundle.getParcelable("curLoc");
			
			context.audioManager.mSpeech.speak(naviSpeakTxt,TextToSpeech.QUEUE_ADD, null);
			if(curLocRoadNode!=null){
				context.mapView.curLocation=new Point(curLocRoadNode.getX(),curLocRoadNode.getY());
				context.mapView.currentStatus=context.mapView.STATUS_INIT;
				context.mapView.invalidate();
			}
			
	/*		if(MapActivity.gpsLonLat!=null){
				Toast.makeText(context, "当前坐标：X:"+MapActivity.gpsLonLat.getX()+",Y:"
							+MapActivity.gpsLonLat.getY(), Toast.LENGTH_SHORT).show();
			}else{
				context.audioManager.mSpeech.speak("GPS当前不可用",TextToSpeech.QUEUE_FLUSH, null);
			}*/
			break;
		case 102:
			context.mapView.invalidate();
			break;
			default:
				break;
		}
		super.handleMessage(msg);
	}
	
}
