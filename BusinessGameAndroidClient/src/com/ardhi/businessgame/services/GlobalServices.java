package com.ardhi.businessgame.services;

import com.ardhi.businessgame.R;
import com.ardhi.businessgame.activities.MainBusinessGameActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.ardhi.businessgame.models.TimePhase;
import com.ardhi.businessgame.models.User;
import com.google.gson.Gson;

public class GlobalServices extends Service {
	private NotificationManager mNM;
	private DBAccess db;
	private User user;
	long lastUpdateTimeInMillis;
	private TimePhase timePhase;
	private IBinder binder = new MyBinder();
	private Thread t;
	
	public class MyBinder extends Binder{
		public GlobalServices getService(){
			return GlobalServices.this;
		}
	}
	
	public TimePhase getTimePhase(){
		long tmp = System.currentTimeMillis() - lastUpdateTimeInMillis;
		while(timePhase.getCurrentTimeMillis() < tmp){
			timePhase.setCurrentTimeMillis(timePhase.getCurrentTimeMillis()+((timePhase.getIdleTime()+timePhase.getWorkTime())*60000));
			tmp = System.currentTimeMillis() - lastUpdateTimeInMillis;
		}
		tmp = System.currentTimeMillis() - lastUpdateTimeInMillis;
		lastUpdateTimeInMillis += tmp;
		timePhase.setCurrentTimeMillis(timePhase.getCurrentTimeMillis()-tmp);
		return new TimePhase(timePhase.getCurrentTimeMillis(), timePhase.getWorkTime(), timePhase.getIdleTime());
	}

	@Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        db = new DBAccess(this);
        user = db.getUser();

        // Display a notification about us starting.  We put an icon in the status bar.
        Notification notification = new Notification(R.drawable.ic_launcher, "Hello "+user.getName()+", welcome aboard..", System.currentTimeMillis());
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        Intent i = new Intent(this, MainBusinessGameActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i, 0);
        notification.setLatestEventInfo(this, "Welcome to BusinessGame, "+user.getName(), "Click here to go to your Dashboard", contentIntent);
        mNM.notify(100, notification);
        requestTimePhase();
        t = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					try {
						Thread.sleep(60000);
						refreshClientData();
					} catch (InterruptedException e) {
						
					}
				}
			}
		});
        t.start();
    }
	
	@Override
	public void onDestroy() {
        mNM.cancel(100);
        t.interrupt();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}
	
	private void refreshClientData(){
		try {
			String res = CustomHttpClient.executeHttpGet(CustomHttpClient.URL+CustomHttpClient.GET_REFRESH_CLIENT_DATA+"&user="+user.getName());
			res = res.toString().replaceAll("\\n+", "");
			db.updateUserData(new Gson().fromJson(res.toString(), User.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void requestTimePhase() {
		try {
			long tmp = System.currentTimeMillis();
			TimePhase time;
			String res = CustomHttpClient.executeHttpGet(CustomHttpClient.URL+CustomHttpClient.GET_GET_GAME_TIME);
			res = res.toString().replaceAll("\\n+", "");
			time = new Gson().fromJson(res, TimePhase.class);
			time.setCurrentTimeMillis(time.getCurrentTimeMillis() - (System.currentTimeMillis() - tmp));
			timePhase = time;
			lastUpdateTimeInMillis = System.currentTimeMillis();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
