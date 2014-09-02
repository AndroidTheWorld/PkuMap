package com.pkumap.handler;

import com.pkumap.activity.MapView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class InertiaHandler extends Handler {

	private MapView mapView;
	private float speedX;
	private float speedY;
	public InertiaHandler(MapView mapView){
		this.mapView=mapView;
	}
	@Override
	public void handleMessage(Message msg) {
		switch(msg.what){
		case 0x123:
			Bundle bundle=msg.getData();
			speedX=bundle.getFloat("speedX");
			speedY=bundle.getFloat("speedY");
			Log.i("HandlerSpeedXY","speedX:"+speedX+",speedY:"+speedY);
			mapView.moveDX=speedX*10;
			mapView.moveDY=speedY*10;
			mapView.currentStatus=mapView.STATUS_MOVE;
			mapView.invalidate();
			break;
		}
		super.handleMessage(msg);
	}
	
}
