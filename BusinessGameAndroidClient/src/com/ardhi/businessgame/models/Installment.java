package com.ardhi.businessgame.models;

public class Installment {
	private String id,installment,zone;
	private double efficiency,effectivity;
	private int draw;
	
	public Installment(String i, String in, String z, double efc, double eft, String d) {
		setId(i);
		setInstallment(in);
		setZone(z);
		setEfficiency(efc);
		setEffectivity(eft);
		setDraw(d);
	}
	
	public String getId() {
		return id;
	}
	public void setId(String i) {
		id = i;
	}
	public String getInstallment() {
		return installment;
	}
	public void setInstallment(String in) {
		installment = in;
	}
	public String getZone() {
		return zone;
	}
	public void setZone(String z) {
		zone = z;
	}
	public double getEfficiency() {
		return efficiency;
	}
	public void setEfficiency(double efc) {
		efficiency = efc;
	}
	public double getEffectivity() {
		return effectivity;
	}
	public void setEffectivity(double eft) {
		effectivity = eft;
	}
	public int getDraw() {
		return draw;
	}
	public void setDraw(String d) {
		draw = Integer.decode(d);
	}
}
