package com.pkumap.bean;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class Building implements Parcelable{
	private int id;
	private String name;
	private String introduction;
	private String category;
	private int pointid;
	private ArrayList<Point> coordinates;
	private Point center;
	private String type;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIntroduction() {
		return introduction;
	}
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public int getPointid() {
		return pointid;
	}
	public void setPointid(int pointid) {
		this.pointid = pointid;
	}
	
	public ArrayList<Point> getCoordinates() {
		return coordinates;
	}
	public void setCoordinates(ArrayList<Point> coordinates) {
		this.coordinates = coordinates;
	}
	
	public Point getCenter() {
		return center;
	}
	public void setCenter(Point center) {
		this.center = center;
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
    public static final Parcelable.Creator<Building> CREATOR = new Creator<Building>() {  
        public Building createFromParcel(Parcel source) {  
        	Building building= new Building();
        	building.id=source.readInt();
        	building.name=source.readString();
        	building.introduction=source.readString();
        	building.category=source.readString();
        	building.pointid=source.readInt();
        	building.coordinates=source.readArrayList(Point.class.getClassLoader());
        	building.center=source.readParcelable(Point.class.getClassLoader());
        	building.type=source.readString();
            return building;  
        }  
  
        public Building[] newArray(int size) {  
            return new Building[size];  
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
		dest.writeString(name);
		dest.writeString(introduction);
		dest.writeString(category);
		dest.writeInt(pointid);
		dest.writeList(coordinates);
		dest.writeParcelable(center, 0);
		dest.writeString(type);
	}
	
	
}
