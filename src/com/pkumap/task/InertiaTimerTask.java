package com.pkumap.task;

import java.util.TimerTask;

import com.pkumap.manager.InertiaTimerManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class InertiaTimerTask extends TimerTask {
	private float speedX;
	private float speedY;
	private float rate;
	private float dxy;
	private Handler handler;
	private InertiaTimerManager inertiaTimerManager;
	public InertiaTimerTask(InertiaTimerManager inertiaTimerManager,Handler handler,float speedX,float speedY){
		this.inertiaTimerManager=inertiaTimerManager;
		this.handler=handler;
		this.speedX=speedX;
		this.speedY=speedY;
		this.rate=1f;
		if(speedX!=0f){
			this.dxy=speedY/speedX;
		}
		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		rate-=0.01;
		speedX*=rate;
		if(speedX!=0f){
			speedY=speedX*dxy;
		}else{
			speedY*=rate;
		}
		Log.i("SpeedXYZDX", "rate:"+rate+",dxy:"+dxy+",speedX:"+speedX+",speedY:"+speedY);
		if(Math.abs(speedX)<0.01&&Math.abs(speedY)<0.01){
			inertiaTimerManager.stopTimer();
		}else{
			Message msg=new Message();
			msg.what=0x123;
			Bundle bundle=new Bundle();
			bundle.putFloat("speedX", speedX);
			bundle.putFloat("speedY", speedY);
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}

}
