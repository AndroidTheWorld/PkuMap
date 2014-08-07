package com.pkumap.bean;

public class Road {
	private int begin;
	private int end;
	private float distance;
	private String type;
	public Road(){}
	public Road(int begin,int end,float distance,String type){
		this.begin=begin;
		this.end=end;
		this.distance=distance;
		this.type=type;
	}
	public int getBegin() {
		return begin;
	}
	public void setBegin(int begin) {
		this.begin = begin;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public float getDistance() {
		return distance;
	}
	public void setDistance(float distance) {
		this.distance = distance;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}
