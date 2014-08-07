package com.pkumap.bean;

import android.os.Parcel;
import android.os.Parcelable;


public class Poi implements Parcelable{
	private int id;
	private String name;
	private String addr;
	private int layer;
	private String wd;
	private String desc;
	private Point center;
	
	public Poi(){}
	
	public Poi(int id,String name,String addr,int layer,
			String wd,String desc,Point center){
		this.id=id;
		this.name=name;
		this.addr=addr;
		this.layer=layer;
		this.wd=wd;
		this.desc=desc;
		this.center=center;
	}
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

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		if(null==addr||"".equals(addr)){
			this.addr="无";
		}else{
			this.addr = addr;
		}
	}

	public int getLayer() {
		return layer;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}

	public String getWd() {
		return wd;
	}

	public void setWd(String wd) {
		this.wd = wd;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		if(null==desc||"".equals(desc)){
			this.desc="无";
		}else{
			this.desc = desc;
		}
	}

	public Point getCenter() {
		return center;
	}

	public void setCenter(Point center) {
		this.center = center;
	}

	/** 
     * 序列化实体类 
     */  
    public static final Parcelable.Creator<Poi> CREATOR = new Creator<Poi>() {  
        public Poi createFromParcel(Parcel source) {  
        	Poi poi = new Poi();
        	poi.id=source.readInt();
        	poi.name=source.readString();
        	poi.addr=source.readString();
        	poi.desc=source.readString();
        	poi.layer=source.readInt();
        	poi.wd=source.readString();
        	poi.center=source.readParcelable(Point.class.getClassLoader());  
            return poi;  
        }  
  
        public Poi[] newArray(int size) {  
            return new Poi[size];  
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
		dest.writeString(addr);
		dest.writeString(desc);
		dest.writeInt(layer);
		dest.writeString(wd);
		dest.writeParcelable(center, 0);
	}
	
	
}
