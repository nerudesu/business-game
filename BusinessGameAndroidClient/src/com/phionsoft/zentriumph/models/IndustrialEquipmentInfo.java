package com.phionsoft.zentriumph.models;

public class IndustrialEquipmentInfo {
	private String type;
	private int quantity;
	private double basePrice;
	private double baseOp;
	
	public IndustrialEquipmentInfo(String t, int q, double p, double o){
		setType(t);
		setQuantity(q);
		setBasePrice(p);
		setBaseOp(o);
	}
	
	public void setType(String n){
		type = n;
	}
	
	public void setQuantity(int q){
		quantity = q;
	}
	
	public void setBasePrice(double p){
		basePrice = p;
	}
	
	public void setBaseOp(double o){
		baseOp = o;
	}
	
	public String getType(){
		return type;
	}
	
	public int getQuantity(){
		return quantity;
	}
	
	public double getBasePrice(){
		return basePrice;
	}
	
	public double getBaseOp(){
		return baseOp;
	}
}
