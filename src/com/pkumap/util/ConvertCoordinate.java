package com.pkumap.util;

import android.util.Log;

import com.pkumap.activity.MapView;
import com.pkumap.bean.Point;

public class ConvertCoordinate {
	/**
	 * 将屏幕坐标转换为地图坐标
	 * @param screenPoint
	 * @return
	 */
/*	public Point getLonLatFromScreen(Point screenPoint,int flagScale,float scaleLevel,
			float mapDX,float mapDY,float ScreenWidth,float ScreenHeight){
		Point lonLatPoint=null;
		float x=screenPoint.getX();
		float y=screenPoint.getY();
		x=(x+(mapDX-ScreenWidth/2))/(flagScale*scaleLevel)-1024;
		y=1024-(y+(mapDY-ScreenHeight/2))/(flagScale*scaleLevel);
		Log.i("ZDX", "x:"+screenPoint.getX()+",y:"+screenPoint.getY()+"lon:"+x+",lat:"+y);
		lonLatPoint=new Point(x,y);
		return lonLatPoint;
	}*/
	public Point getLonLatFromScreen(Point screenPoint,MapView mapView){
		Point lonLatPoint=null;
		float x=screenPoint.getX();
		float y=screenPoint.getY();
		
		float mapDX=mapView.mapDX;
		float mapDY=mapView.mapDY;
		float ScreenWidth=mapView.ScreenWidth;
		float ScreenHeight=mapView.ScreenHeight;
		float flagScale=mapView.getScaleFlag(mapView.level);
		float scaleLevel=mapView.scaleLevel;
		if("3dmap".equals(mapView.map_type)){
			x=((x+(mapDX-ScreenWidth/2))/(flagScale*scaleLevel))*1.95f-2000;     //在3D上有点区别，需要注意
			y=2000-((y+(mapDY-ScreenHeight/2))/(flagScale*scaleLevel))*1.95f;
		}else{
			x=(x+(mapDX-ScreenWidth/2))/(flagScale*scaleLevel)-1024;
			y=1024-(y+(mapDY-ScreenHeight/2))/(flagScale*scaleLevel);
		}
		
		Log.i("ZDX","mapDX:"+mapDX+ ",flagScale:"+flagScale+",scaleLevel:"+scaleLevel+",x:"+screenPoint.getX()+",y:"+screenPoint.getY()+"lon:"+x+",lat:"+y);
		lonLatPoint=new Point(x,y);
		return lonLatPoint;
	}
	/**
	 * 将地图坐标转换为屏幕坐标
	 * @param lonlatPoint
	 * @return
	 */
/*	public Point getScreenPointFromLonLat(Point lonlatPoint,int flagScale,float scaleLevel,
			float mapDX,float mapDY,float ScreenWidth,float ScreenHeight){
		Point screenPoint=null;
		float x=lonlatPoint.getX();
		float y=lonlatPoint.getY();
		x=(x+1024)*flagScale*scaleLevel-(mapDX-ScreenWidth/2);
		y=Math.abs(y-1024)*flagScale*scaleLevel-(mapDY-ScreenHeight/2);
		screenPoint=new Point(x,y);
		return screenPoint;
	}*/
	public Point getScreenPointFromLonLat(Point lonlatPoint,MapView mapView){
		Point screenPoint=null;
		float x=lonlatPoint.getX();
		float y=lonlatPoint.getY();
		
		float mapDX=mapView.mapDX;
		float mapDY=mapView.mapDY;
		float ScreenWidth=mapView.ScreenWidth;
		float ScreenHeight=mapView.ScreenHeight;
		float flagScale=mapView.getScaleFlag(mapView.level);
		float scaleLevel=mapView.scaleLevel;
		if("3dmap".equals(mapView.map_type)){
			x=((x+2000)/1.95f)*flagScale*scaleLevel-(mapDX-ScreenWidth/2);
			y=(Math.abs(y-2000)/1.95f)*flagScale*scaleLevel-(mapDY-ScreenHeight/2);
		}else{
			x=(x+1024)*flagScale*scaleLevel-(mapDX-ScreenWidth/2);
			y=Math.abs(y-1024)*flagScale*scaleLevel-(mapDY-ScreenHeight/2);
		}
		
		screenPoint=new Point(x,y);
		return screenPoint;
	}
}
