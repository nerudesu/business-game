package com.phionsoft.zentriumph.services;

import com.phionsoft.zentriumph.models.TimePhase;
import com.phionsoft.zentriumph.models.User;

import android.graphics.Color;
import android.os.Handler;
import android.widget.EditText;

public class TimeSync implements Runnable {
	private boolean serviceBound, threadWork;
	private TimePhase timePhase;
	private SystemService service;
	private Handler h;
	private EditText nextTurn, userMoney;
	private DBAccess db;
	private User user;
	private String time;
	private long tmpl;
	private int tmpi1,tmpi2;
	
	public TimeSync(Handler ha, EditText next, EditText money, DBAccess d){
		h = ha;
		nextTurn = next;
		serviceBound = false;
		threadWork = false;
		userMoney = money;
		db = d;
		user = db.getUser();
	}
	
	public TimeSync(DBAccess d){
		db = d;
		user = db.getUser();
		serviceBound = false;
		threadWork = false;
		time = "";
	}
	
	public void setGlobalServices(SystemService s){
		service = s;
	}
	
	public void setServiceBound(boolean s){
		serviceBound = s;
	}
	
	public void setThreadWork(boolean t){
		threadWork = t;
	}
	
	private void setTime(String t){
		time = t;
	}
	
	public String getTime(){
		return time;
	}
	
//	public void setTimePhase(TimePhase t){
//		timePhase = t;
//	}
	
	public void run() {
		android.util.Log.d("jalan?", "yey");
		int counter = 0;
		try {
			while(!serviceBound){
				Thread.sleep(50);
			}
			while(service.getTimePhase() == null){
				Thread.sleep(50);
			}
			timePhase = service.getTimePhase();
			Thread.sleep(timePhase.getCurrentTimeMillis() % 1000);
			timePhase.setCurrentTimeMillis(timePhase.getCurrentTimeMillis()-(timePhase.getCurrentTimeMillis() % 1000));
			while(threadWork){
				if(timePhase.getCurrentTimeMillis() < 1){
					timePhase.setCurrentTimeMillis((timePhase.getIdleTime()+timePhase.getWorkTime())*60000);
				}
				if(timePhase.getCurrentTimeMillis() > (timePhase.getWorkTime()*60000)){
					tmpl = timePhase.getCurrentTimeMillis()-(timePhase.getWorkTime()*60000);
					tmpi1 = (int)(tmpl/60000);
					tmpi2 = (int)(tmpl%60000)/1000;
					if(h != null){
						h.post(new Runnable() {
							
							@Override
							public void run() {
								
								nextTurn.setTextColor(Color.GREEN);
								nextTurn.setText((tmpi1 < 10 ? ("0"+tmpi1) : (""+tmpi1))+":"+(tmpi2 < 10 ? ("0"+tmpi2) : (""+tmpi2)));
							}
							
						});
					}
					setTime((tmpi1 < 10 ? ("0"+tmpi1) : (""+tmpi1))+":"+(tmpi2 < 10 ? ("0"+tmpi2) : (""+tmpi2)));
//					android.util.Log.d("time", "asd"+(tmpi1 < 10 ? ("0"+tmpi1) : (""+tmpi1))+":"+(tmpi2 < 10 ? ("0"+tmpi2) : (""+tmpi2)));
				} else {
					tmpl = timePhase.getCurrentTimeMillis();
					tmpi1 = (int)(tmpl/60000);
					tmpi2 = (int)(tmpl%60000)/1000;
					if(h != null){
						h.post(new Runnable() {
							
							@Override
							public void run() {
								nextTurn.setTextColor(Color.RED);
								nextTurn.setText((tmpi1 < 10 ? ("0"+tmpi1) : (""+tmpi1))+":"+(tmpi2 < 10 ? ("0"+tmpi2) : (""+tmpi2)));
							}
							
						});
					}
					setTime((tmpi1 < 10 ? ("0"+tmpi1) : (""+tmpi1))+":"+(tmpi2 < 10 ? ("0"+tmpi2) : (""+tmpi2)));
//					android.util.Log.d("time", "asd"+(tmpi1 < 10 ? ("0"+tmpi1) : (""+tmpi1))+":"+(tmpi2 < 10 ? ("0"+tmpi2) : (""+tmpi2)));
				}
				timePhase.setCurrentTimeMillis(timePhase.getCurrentTimeMillis() - 1000);
				Thread.sleep(1000);
				if(counter < 12)
					counter++;
				else {
					counter = 0;
					user = db.getUser();
					if(h != null){
						h.post(new Runnable() {
							
							@Override
							public void run() {
								userMoney.setText(user.getMoney()+" ZE");
							}
							
						});
					}
				}
//				Log.d("thread", "I Alive?");
			}
//			Log.d("thread", "Time for me to Die?");
		} catch (InterruptedException e) {
//			Log.d("thread", "I Die?");
		}
//		Log.d("thread", "Finally, I Die?");
	}
}
