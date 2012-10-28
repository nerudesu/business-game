package com.phionsoft.zentriumph.models;

public class Contract {
	private String id,user,product,contractType,zone;
	private int quality;
	private double size,price;
	
	public Contract(String i,String u, String z, String c, String p, int q, double s, double pr){
		setId(i);
		setUser(u);
		setZone(z);
		setContractType(c);
		setProduct(p);
		setQuality(q);
		setSize(s);
		setPrice(pr);
	}
	
	public String getUser() {
		return user;
	}
	public void setUser(String u) {
		user = u;
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
	public double getPrice() {
		return price;
	}
	public void setPrice(double p) {
		price = p;
	}

	public String getContractType() {
		return contractType;
	}

	public void setContractType(String contractType) {
		this.contractType = contractType;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getId() {
		return id;
	}

	public void setId(String i) {
		id = i;
	}
}
