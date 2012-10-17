package com.ardhi.businessgame.models;

public class InstallmentEmployee {
	private String id,employee;
	private int quality,draw;
	private double operational;
	
	public InstallmentEmployee(String i, String e, int q, double o, String d){
		setId(i);
		setEmployee(e);
		setQuality(q);
		setOperational(o);
		setDraw(d);
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

	public int getDraw() {
		return draw;
	}

	public void setDraw(String d) {
		draw = Integer.decode(d);
	}
}
