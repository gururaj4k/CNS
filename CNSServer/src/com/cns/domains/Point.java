package com.cns.domains;

import java.util.HashMap;
import java.util.List;

public class Point {

	String x;
	String y;
	String z;
	String partition;
	
	public String getPartition() {
		return partition;
	}
	public void setPartition(String partition) {
		this.partition = partition;
	}
	private HashMap<String, String> accessPoints; 
	public HashMap<String, String> getAccessPoints() {
		return accessPoints;
	}
	public void setAccessPoints(HashMap<String, String> accessPoints) {
		this.accessPoints = accessPoints;
	}
	public String getX() {
		return x;
	}
	public void setX(String x) {
		this.x = x;
	}
	public String getY() {
		return y;
	}
	public void setY(String y) {
		this.y = y;
	}
	public String getZ() {
		return z;
	}
	public void setZ(String z) {
		this.z = z;
	}
	
	
	public Point(){
		
	}
	public Point(String x,String y,String z){
		this.x=x;
		this.y=y;
		this.z=z;
	}
	@Override
	public String toString() {
	 return "("+x+","+y+","+z+")";
	}
}
