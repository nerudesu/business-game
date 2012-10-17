package com.ardhi.businessgame.models;

public class StorageProduct {
	private String id,product;
	private int quality,draw;
	private double size;
	
	public StorageProduct(String i, String p, int q, double s, String d){
		setId(i);
		setProduct(p);
		setQuality(q);
		setSize(s);
		setDraw(d);
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String i) {
		id = i;
	}
	
	public String getProduct() {
		return product;
	}
	
	public void setProduct(String p) {
		product = p;
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

	public int getDraw() {
		return draw;
	}

	public void setDraw(String d) {
		draw = Integer.decode(d);
	}
}
