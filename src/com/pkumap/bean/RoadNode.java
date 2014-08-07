package com.pkumap.bean;

public class RoadNode {
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
	
	
}
