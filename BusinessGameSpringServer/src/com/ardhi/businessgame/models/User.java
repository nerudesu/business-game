package com.ardhi.businessgame.models;

import java.util.ArrayList;
import java.util.HashMap;

public class User {
	private String name, email, dob, about, zone;
	private double money, propCost;
	private long rep;
	private int level;
	private HashMap<String, String> storages, marketLicenses, sectorBlueprints;
	private HashMap<String, Double> sectorCosts; 
	private ArrayList<Installment> installments;
	
	
	public User(String n, String e, String d, String ab, double m, double p, long r, String z, int l, HashMap<String, String> s, HashMap<String, String> lic, HashMap<String, String> sb, HashMap<String, Double> sc, ArrayList<Installment> ins){
		setName(n);
		setEmail(e);
		setDob(d);
		setAbout(ab);
		setMoney(m);
		setPropCost(p);
		setRep(r);
		setZone(z);
		setLevel(l);
		setStorages(s);
		setMarketLicenses(lic);
		setSectorBlueprints(sb);
		setSectorCosts(sc);
		setInstallments(ins);
	}
	
	public void setName(String s){
		name = s;
	}
	
	public void setEmail(String s){
		email = s;
	}
	
	public void setDob(String s){
		dob = s;
	}
	
	public void setAbout(String s){
		about = s;
	}
	
	public void setMoney(double m){
		money = m;
	}
	
	public void setRep(long l){
		rep = l;
	}
	
	public void setZone(String s){
		zone = s;
	}
	
	public void setStorages(HashMap<String, String> s) {
		storages = s;
	}
	
	public void setLevel(int l) {
		level = l;
	}
	
	public String getName(){
		return name;
	}
	
	public String getEmail(){
		return email;
	}
	
	public String getDob(){
		return dob;
	}
	
	public String getAbout(){
		return about;
	}
	
	public double getMoney(){
		return money;
	}
	
	public long getRep(){
		return rep;
	}
	
	public String getZone(){
		return zone;
	}

	public HashMap<String, String> getStorages() {
		return storages;
	}

	public int getLevel() {
		return level;
	}

	public HashMap<String, String> getMarketLicenses() {
		return marketLicenses;
	}

	public void setMarketLicenses(HashMap<String, String> marketLicenses) {
		this.marketLicenses = marketLicenses;
	}

	public ArrayList<Installment> getInstallments() {
		return installments;
	}

	public void setInstallments(ArrayList<Installment> installments) {
		this.installments = installments;
	}
	
	public ArrayList<Installment> getInstallmentsBySector(String sector){
		ArrayList<Installment> specInstallments = new ArrayList<Installment>();
		for(Installment ins : installments){
			if(ins.getInstallment().equals(sector))
				specInstallments.add(ins);
		}
		return specInstallments;
	}

	public HashMap<String, String> getSectorBlueprints() {
		return sectorBlueprints;
	}

	public void setSectorBlueprints(HashMap<String, String> sectorBlueprints) {
		this.sectorBlueprints = sectorBlueprints;
	}

	public HashMap<String, Double> getSectorCosts() {
		return sectorCosts;
	}

	public void setSectorCosts(HashMap<String, Double> sectorCost) {
		this.sectorCosts = sectorCost;
	}

	public double getPropCost() {
		return propCost;
	}

	public void setPropCost(double propCost) {
		this.propCost = propCost;
	}
}
