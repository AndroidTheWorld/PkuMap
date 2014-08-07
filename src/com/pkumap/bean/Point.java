package com.pkumap.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class Point implements Parcelable{
	private float x;
	private float y;
	
	public Point(){}
	public Point(float x,float y) {
		this.x=x;
		this.y=y;
	}
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
	/** 
     * 序列化实体类 
     */  
    public static final Parcelable.Creator<Point> CREATOR = new Creator<Point>() {  
        public Point createFromParcel(Parcel source) {  
        	Point point = new Point();
        	point.x=source.readFloat();
        	point.y=source.readFloat();
            return point;  
        }  
  
        public Point[] newArray(int size) {  
            return new Point[size];  
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
		dest.writeFloat(x);
		dest.writeFloat(y);
	}
	
}
