package com.ardhi.businessgame.models;

public class StorageEquipment {
	private String id,equipment;
	private int quality;
	private double durability,size,operational;
	
	public StorageEquipment(String i, String e, int q, double d, double s, double o){
		setId(i);
		setEquipment(e);
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
}
