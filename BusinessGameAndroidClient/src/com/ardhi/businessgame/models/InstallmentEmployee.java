package com.ardhi.businessgame.models;

public class InstallmentEmployee {
	private String id,employee;
	private int quality;
	private double operational;
	
	public InstallmentEmployee(String i, String e, int q, double o){
		setId(i);
		setEmployee(e);
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
	public double getOperational() {
		return operational;
	}
	public void setOperational(double o) {
		operational = o;
	}
}
