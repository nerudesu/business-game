package com.ardhi.businessgame.models;

public class TimePhase {
	private long currentTimeMillis;
	
	//Time in minutes
	private int workTime, idleTime;
	
	//Remember :
	//phase work as follows : Idle - Work - Idle - Work - etc...
	
	public TimePhase(long c, int w, int i){
		setCurrentTimeMillis(c);
		setWorkTime(w);
		setIdleTime(i);
	}

	public long getCurrentTimeMillis() {
		return currentTimeMillis;
	}

	public void setCurrentTimeMillis(long c) {
		currentTimeMillis = c;
	}

	public int getWorkTime() {
		return workTime;
	}

	public void setWorkTime(int w) {
		workTime = w;
	}

	public int getIdleTime() {
		return idleTime;
	}

	public void setIdleTime(int i) {
		idleTime = i;
	}
}
