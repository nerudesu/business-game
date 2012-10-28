package com.phionsoft.zentriumph.services;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.google.gson.Gson;
import com.phionsoft.zentriumph.R;
import com.phionsoft.zentriumph.activities.MainBusinessGameActivity;
import com.phionsoft.zentriumph.models.TimePhase;
import com.phionsoft.zentriumph.models.User;

@SuppressLint("NewApi")
public class SystemService extends Service {
	private NotificationManager mNM;
	private DBAccess db;
	private User user;
	private long lastUpdateTimeInMillis;
	private TimePhase timePhase;
	private IBinder binder = new MyBinder();
	private Thread t;
	private boolean threadWork = false;
	
	public class MyBinder extends Binder{
		public SystemService getService(){
			return SystemService.this;
		}
	}
	
	public TimePhase getTimePhase(){
		long tmp = System.currentTimeMillis() - lastUpdateTimeInMillis;
		if(timePhase != null){
			while(timePhase.getCurrentTimeMillis() < tmp){
				timePhase.setCurrentTimeMillis(timePhase.getCurrentTimeMillis()+((timePhase.getIdleTime()+timePhase.getWorkTime())*60000));
				tmp = System.currentTimeMillis() - lastUpdateTimeInMillis;
			}
			tmp = System.currentTimeMillis() - lastUpdateTimeInMillis;
			lastUpdateTimeInMillis += tmp;
			timePhase.setCurrentTimeMillis(timePhase.getCurrentTimeMillis()-tmp);
			return new TimePhase(timePhase.getCurrentTimeMillis(), timePhase.getWorkTime(), timePhase.getIdleTime());
		} else return null;
	}

	@SuppressWarnings("deprecation")
	@Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        db = new DBAccess(this);
        user = db.getUser();
        threadWork = true;
        
		Notification notification = new Notification(R.drawable.ic_launcher, "Hello "+user.getName()+", welcome..", System.currentTimeMillis());
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        Intent i = new Intent(this, MainBusinessGameActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i, 0);
        notification.setLatestEventInfo(this, "Welcome, "+user.getName(), "Click here to go to your Dashboard", contentIntent);
        mNM.notify(100, notification);
        new RequestTimePhase().execute();
        t = new Thread(new Runnable() {
			@Override
			public void run() {
				while(threadWork){
					try {
						Thread.sleep(12000);
						new RefreshClientData().execute();
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
        threadWork = false;
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
	
	private class RefreshClientData extends AsyncTask<String, Void, Object>{
		@Override
		protected Object doInBackground(String... arg0) {
			try {
				return CommunicationService.get(CommunicationService.GET_REFRESH_CLIENT_DATA+"&user="+user.getName());
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(Object res) {
			if(res == null){
				Toast.makeText(getApplicationContext(), "No response from server..", Toast.LENGTH_SHORT).show();
//				Intent i = new Intent(getApplicationContext(), LoginActivity.class);
//				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//				startActivity(i);
//				onDestroy();
//				Intent i = new Intent(getApplicationContext(), MainBusinessGameActivity.class);
//				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//				startActivity(i);
//				stopSelf();
			} else if(res.toString().equals("-1")){
				Toast.makeText(SystemService.this, "Server is not ready..", Toast.LENGTH_SHORT).show();
			} else if(res.toString().equals("0")){
				Toast.makeText(SystemService.this, "Internal error..", Toast.LENGTH_SHORT).show();
			} else {
				User tmpUser = new Gson().fromJson(res.toString(), User.class);
				user.setMoney(tmpUser.getMoney());
				user.setPropCost(tmpUser.getPropCost());
				user.setRep(tmpUser.getRep());
				db.updateUserData(user);
			}
		}
	}
	
	private class RequestTimePhase extends AsyncTask<String, Void, Object>{

		@Override
		protected Object doInBackground(String... arg0) {
			try {
				return CommunicationService.get(CommunicationService.GET_GET_GAME_TIME);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			
		}
		
		@Override
		protected void onPostExecute(Object res) {
			if(res == null){
				Toast.makeText(SystemService.this, "No response from server..", Toast.LENGTH_SHORT).show();
			} else if(res.toString().equals("-1")){
				Toast.makeText(SystemService.this, "Server is not ready..", Toast.LENGTH_SHORT).show();
			} else if(res.toString().equals("0")){
				Toast.makeText(SystemService.this, "Internal error..", Toast.LENGTH_SHORT).show();
			} else {
				long tmp = System.currentTimeMillis();
				TimePhase time = new Gson().fromJson(res.toString(), TimePhase.class);
				time.setCurrentTimeMillis(time.getCurrentTimeMillis() - (System.currentTimeMillis() - tmp));
				timePhase = time;
				lastUpdateTimeInMillis = System.currentTimeMillis();
			}
		}
	}
}
