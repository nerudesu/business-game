package com.ardhi.businessgame.services;

import com.ardhi.businessgame.models.TimePhase;
import com.ardhi.businessgame.models.User;

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
	
	public TimeSync(Handler ha, EditText next, EditText money, DBAccess d){
		h = ha;
		nextTurn = next;
		serviceBound = false;
		threadWork = false;
		userMoney = money;
		db = d;
		user = db.getUser();
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
	
//	public void setTimePhase(TimePhase t){
//		timePhase = t;
//	}
	
	public void run() {
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
					h.post(new Runnable() {
						
						@Override
						public void run() {
							long tmp = timePhase.getCurrentTimeMillis()-(timePhase.getWorkTime()*60000);
							int tmp1 = (int)(tmp/60000),
									tmp2 = (int)(tmp%60000)/1000;
							nextTurn.setTextColor(Color.GREEN);
							nextTurn.setText((tmp1 < 10 ? ("0"+tmp1) : (""+tmp1))+":"+(tmp2 < 10 ? ("0"+tmp2) : (""+tmp2)));
						}
						
					});
				} else {
					h.post(new Runnable() {
						
						@Override
						public void run() {
							long tmp = timePhase.getCurrentTimeMillis();
							int tmp1 = (int)(tmp/60000),
									tmp2 = (int)(tmp%60000)/1000;
							nextTurn.setTextColor(Color.RED);
							nextTurn.setText((tmp1 < 10 ? ("0"+tmp1) : (""+tmp1))+":"+(tmp2 < 10 ? ("0"+tmp2) : (""+tmp2)));
						}
						
					});
				}
				timePhase.setCurrentTimeMillis(timePhase.getCurrentTimeMillis() - 1000);
				Thread.sleep(1000);
				if(counter < 30)
					counter++;
				else {
					counter = 0;
					h.post(new Runnable() {
						
						@Override
						public void run() {
							user = db.getUser();
							userMoney.setText(user.getMoney()+" ZE");
						}
						
					});
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
