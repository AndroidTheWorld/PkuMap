package com.pkumap.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class PointLonLat implements Parcelable {
	private double x;
	private double y;
	
	public PointLonLat(){}
	
	public PointLonLat(double x,double y){
		this.x=x;  //经度--lng
		this.y=y;  //维度--lat
	}
	
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	/**
	 * 判断当前点是否是否进入了某一个点的覆盖范围
	 * @param lonLat
	 * @return
	 */
	public boolean IsInAreaOfPoint(PointLonLat lonLat){
		//暂时设置在一个0.0004*0.0004范围内搜索一个路口的PointId
		double dx=0.0002;
		double dy=0.0002;
		if(x>(lonLat.getX()-dx)&&x<(lonLat.getX()+dx)&&y>(lonLat.getY()-dy)&&y<(lonLat.getX()+dy)){
			return true;
		}
		return false;
	}
	/**
	 * 判断当前点是否在两个点之间
	 * @param srcLonLat
	 * @param destLonLat
	 * @return
	 */
	public boolean isInAreaOfTwoRoadNode(RoadNode srcRoadNode,RoadNode destRoadNode){
		double minX,maxX,minY,maxY;
		if(srcRoadNode.getGps_x()>destRoadNode.getGps_x()){
			minX=destRoadNode.getGps_x();
			maxX=srcRoadNode.getGps_x();
		}else{
			minX=srcRoadNode.getGps_x();
			maxX=destRoadNode.getGps_x();
		}
		if(srcRoadNode.getGps_y()>destRoadNode.getGps_y()){
			minY=destRoadNode.getGps_y();
			maxY=srcRoadNode.getGps_y();
		}else{
			minY=srcRoadNode.getGps_y();
			maxY=destRoadNode.getGps_y();
		}
		double dx=0.0002,dy=0.0002;//扩展大概3米
		if(x>(minX-dx)&&x<(maxX+dx)&&y>(minY-dy)&&y<(maxY+dy)){
			return true;
		}
		return false;
	}
	/** 
     * 序列化实体类 
     */  
    public static final Parcelable.Creator<PointLonLat> CREATOR = new Creator<PointLonLat>() {  
        public PointLonLat createFromParcel(Parcel source) {  
        	PointLonLat gpsLonLat = new PointLonLat();
        	gpsLonLat.x=source.readDouble();
        	gpsLonLat.y=source.readDouble();
            return gpsLonLat;  
        }  
  
        public PointLonLat[] newArray(int size) {  
            return new PointLonLat[size];  
        }  
    };  
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeDouble(x);
		dest.writeDouble(y);
	}
	
}
