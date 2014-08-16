package com.pkumap.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class PointLonLat implements Parcelable {
	private double x;
	private double y;
	
	public PointLonLat(){}
	
	public PointLonLat(double x,double y){
		this.x=x;
		this.y=y;
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
