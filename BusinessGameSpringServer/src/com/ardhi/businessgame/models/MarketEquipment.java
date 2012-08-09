package com.ardhi.businessgame.models;

public class MarketEquipment {
	private String id,user,equipment;
	private int quality;
	private double price,durability,size,operational;
	
	public MarketEquipment(String i, String u, String e, double pr, int q, double d, double s, double o){
		setId(i);
		setUser(u);
		setEquipment(e);
		setPrice(pr);
		setQuality(q);
		setDurability(d);
		setSize(s);
		setOperational(o);
	}
	
	public String getId() {
		return id;
	}
	public void setId(String i) {
		id = i;
	}
	public String getEquipment() {
		return equipment;
	}
	public void setEquipment(String e) {
		equipment = e;
	}
	public int getQuality() {
		return quality;
	}
	public void setQuality(int q) {
		quality = q;
	}
	public double getSize() {
		return size;
	}
	public void setSize(double s) {
		size = s;
	}

	public double getDurability() {
		return durability;
	}

	public void setDurability(double d) {
		durability = d;
	}

	public double getOperational() {
		return operational;
	}

	public void setOperational(double o) {
		operational = o;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String u) {
		user = u;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double p) {
		price = p;
	}
}
