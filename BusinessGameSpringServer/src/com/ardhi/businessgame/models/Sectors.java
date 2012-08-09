package com.ardhi.businessgame.models;

import java.util.ArrayList;

public class Sectors {
	private ArrayList<String> sectorList;
	private ArrayList<Double> priceList;
	private double propCost;
	
	public Sectors(ArrayList<String> s, ArrayList<Double> p, double pr){
		setSectorList(s);
		setPriceList(p);
		setPropCost(pr);
	}
	
	public void setSectorList(ArrayList<String> s){
		sectorList = s;
	}
	
	public void setPriceList(ArrayList<Double> p){
		priceList = p;
	}
	
	public void setPropCost(double pr){
		propCost = pr;
	}
	
	public ArrayList<String> getSectorList(){
		return sectorList;
	}
	
	public ArrayList<Double> getPriceList(){
		return priceList;
	}
	
	public double getPropCost(){
		return propCost;
	}
	
	
//	private String name;
//	private double cost;
//	
//	public Sectors(String n, double c){
//		setName(n);
//		setCost(c);
//	}
//	
//	public void setName(String n){
//		name = n;
//	}
//	
//	public void setCost(double c){
//		cost = c;
//	}
//	
//	public String getName(){
//		return name;
//	}
//	
//	public double getCost(){
//		return cost;
//	}
}
