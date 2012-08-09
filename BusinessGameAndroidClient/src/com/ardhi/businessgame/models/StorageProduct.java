package com.ardhi.businessgame.models;

public class StorageProduct {
	private String id,product;
	private int quality;
	private double size;
	private boolean offer;
	
	public StorageProduct(String i, String p, int q, double s, boolean of){
		setId(i);
		setProduct(p);
		setQuality(q);
		setSize(s);
		setOffer(of);
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

	public boolean isOffer() {
		return offer;
	}

	public void setOffer(boolean o) {
		offer = o;
	}
}
