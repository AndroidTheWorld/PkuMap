package com.pkumap.manager;

import java.util.Timer;

import com.pkumap.task.InertiaTimerTask;

import android.os.Handler;

public class InertiaTimerManager {
	private Timer timer;
	private final long delay=0;
	private final long period=10;
	private Handler handler;
	public InertiaTimerManager(Handler handler){
		this.handler=handler;
		
	}
	/**
	 * 启动定时器
	 * @param speedX
	 * @param speedY
	 */
	public void startTimer(float speedX,float speedY){
		if(timer==null){
			timer=new Timer();
		}
		timer.schedule(new InertiaTimerTask(this,handler, speedX, speedY), delay, period);
	}
	/**
	 * 关闭定时器
	 */
	public void stopTimer(){
		if(timer!=null){
			timer.cancel();
			timer=null;
		}
	}
}
