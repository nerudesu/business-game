package com.ardhi.businessgame.models;

public class MarketProduct {
	private String id,user,product;
	private int quality;
	private double price,size;
	
	public MarketProduct(String i, String u, String p, double pr, int q, double s){
		setId(i);
		setUser(u);
		setProduct(p);
		setPrice(pr);
		setQuality(q);
		setSize(s);
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
