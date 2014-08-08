package com.pkumap.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class RoadNode implements Parcelable{
	private int id;
	private float x;
	private float y;
	private String type;
	
	public RoadNode(){}
	public RoadNode(int id,float x,float y,String type){
		this.id=id;
		this.x=x;
		this.y=y;
		this.type=type;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	/** 
     * 序列化实体类 
     */  
    public static final Parcelable.Creator<RoadNode> CREATOR = new Creator<RoadNode>() {  
        public RoadNode createFromParcel(Parcel source) {  
        	RoadNode roadNode= new RoadNode();
        	roadNode.id=source.readInt();
        	roadNode.x=source.readFloat();
        	roadNode.y=source.readFloat();
        	roadNode.type=source.readString();
            return roadNode;  
        }  
  
        public RoadNode[] newArray(int size) {  
            return new RoadNode[size];  
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
		dest.writeInt(id);
		dest.writeFloat(x);
		dest.writeFloat(y);
		dest.writeString(type);
	}
	
	
}
