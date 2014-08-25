package com.pkumap.manager;

import java.util.ArrayList;
import java.util.Timer;

import com.pkumap.activity.MapActivity;
import com.pkumap.bean.RoadNode;
import com.pkumap.util.NaviTimerMask;
import com.pkumap.util.RoadPlan;

import android.content.Context;
import android.os.Handler;

public class TimerManager {
	private Timer timer;
	private final long delay=1000;
	private final long period=10000;//10秒
	private Handler handler;
	private MapActivity context;
	public TimerManager(Handler handler,MapActivity context){
		this.handler=handler;
		this.context=context;
	}

	/**
	 * 启动定时器1
	 */
	public void startTimer(){
		if(timer==null){
			timer=new Timer();
		}
		timer.schedule(new NaviTimerMask(handler), delay, period);
	}
	/**
	 * 启动定时器2
	 */
	public void startTimer(ArrayList<RoadNode> naviNodes,RoadPlan roadPlan){
		if(timer==null){
			timer=new Timer();
		}
		timer.schedule(new NaviTimerMask(TimerManager.this,handler,naviNodes,roadPlan,context), delay, period);
	}
	public void stopTimer(){
		if(timer!=null){
			timer.cancel();
			timer=null;
		}
	}
}
