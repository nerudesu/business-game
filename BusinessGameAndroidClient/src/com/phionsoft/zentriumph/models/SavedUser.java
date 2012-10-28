package com.phionsoft.zentriumph.models;

public class SavedUser {
	private String user, pass;
	private boolean autoLog;
	
	public SavedUser(String u, String p, boolean a) {
		setUser(u);
		setPass(p);
		setAutoLog(a);
	}
	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	public boolean isAutoLog() {
		return autoLog;
	}
	public void setAutoLog(boolean autoLog) {
		this.autoLog = autoLog;
	}
}
