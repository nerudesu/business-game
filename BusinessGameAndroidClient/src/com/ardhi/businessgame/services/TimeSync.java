package com.ardhi.businessgame.services;

import com.ardhi.businessgame.models.TimePhase;

import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;

public class TimeSync implements Runnable {
	private boolean serviceBound, threadWork;
	private TimePhase timePhase;
	private GlobalServices service;
	private Handler h;
	private EditText nextTurn;
	
	public TimeSync(Handler ha, EditText next){
		h = ha;
		nextTurn = next;
		serviceBound = false;
		threadWork = false;
	}
	
	public void setGlobalServices(GlobalServices s){
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
//				Log.d("timemillis", ""+timeMillis);
//				if(timeMillis < 1001){
//					while(service.getTimeMillis() < 1){
//						Log.d("thread", "I locked? "+service.getTimeMillis());
//						Thread.sleep(1000);
//					}
//					timeMillis = service.getTimeMillis();
//					h.post(new Runnable() {
//						public void run() {
//							int tmp1 = (int)(timeMillis/60000),
//									tmp2 = (int)(timeMillis%60000)/1000;
//							nextTurn.setText((tmp1 < 10 ? ("0"+tmp1) : (""+tmp1))+":"+(tmp2 < 10 ? ("0"+tmp2) : (""+tmp2)));
//						}
//					});
//					Thread.sleep(timeMillis % 1000);
//					timeMillis -= timeMillis % 1000;
//				}
//				timeMillis -= 1000;
//				h.post(new Runnable() {
//					public void run() {
//						int tmp1 = (int)(timeMillis/60000),
//								tmp2 = (int)(timeMillis%60000)/1000;
//						nextTurn.setText((tmp1 < 10 ? ("0"+tmp1) : (""+tmp1))+":"+(tmp2 < 10 ? ("0"+tmp2) : (""+tmp2)));
//						nextTurn.setText(timeMillis/60000+":"+timeMillis%60000);
//					}
//				});
				Thread.sleep(1000);
			}
			Log.d("thread", "I Die?");
		} catch (InterruptedException e) {

		}
	}

}
