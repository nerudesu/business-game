package com.ardhi.businessgame.models;

public class MarketEmployee {
	private String id,employee;
	private int quality;
	private double price,operational;
	
	public MarketEmployee(String i, String e, double pr, int q, double o){
		setId(i);
		setEmployee(e);
		setPrice(pr);
		setQuality(q);
		setOperational(o);
	}
	
	public String getId() {
		return id;
	}
	public void setId(String i) {
		id = i;
	}
	public String getEmployee() {
		return employee;
	}
	public void setEmployee(String e) {
		employee = e;
	}
	public int getQuality() {
		return quality;
	}
	public void setQuality(int q) {
		quality = q;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double p) {
		price = p;
	}
	public double getOperational() {
		return operational;
	}
	public void setOperational(double o) {
		operational = o;
	}
}
