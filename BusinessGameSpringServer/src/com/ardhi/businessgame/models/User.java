package com.ardhi.businessgame.models;

import java.util.HashMap;

public class User {
	private String name, email, dob, about, avatar, zone;
	private double money;
	private long rep;
	private HashMap<String, String> storages;
	private int level;
	
	public User(String n, String e, String d, String ab, String av, double m, long r, String z, HashMap<String, String> s, int l){
		setName(n);
		setEmail(e);
		setDob(d);
		setAbout(ab);
		setAvatar(av);
		setMoney(m);
		setRep(r);
		setZone(z);
		setStorages(s);
		setLevel(l);
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
	
	public void setAvatar(String s){
		avatar = s;
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
	
	public String getAvatar(){
		return avatar;
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
}
