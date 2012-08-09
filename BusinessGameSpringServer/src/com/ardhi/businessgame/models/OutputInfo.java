package com.ardhi.businessgame.models;

public class OutputInfo {
	private String type;
	private double size;
	private double basePrice;
	
	public OutputInfo(String t, double s, double b){
		setType(t);
		setSize(s);
		setBasePrice(b);
	}
	
	public void setType(String n){
		type = n;
	}
	
	public void setSize(double s){
		size = s;
	}
	
	public void setBasePrice(double b){
		basePrice = b;
	}
	
	public String getType(){
		return type;
	}
	
	public double getSize(){
		return size;
	}
	
	public double getBasePrice(){
		return basePrice;
	}
}
