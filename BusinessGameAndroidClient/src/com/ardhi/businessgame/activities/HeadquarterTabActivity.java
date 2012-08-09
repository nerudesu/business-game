package com.ardhi.businessgame.activities;

import com.ardhi.businessgame.R;
import com.ardhi.businessgame.models.User;
import com.ardhi.businessgame.services.DBAccess;
import com.ardhi.businessgame.services.GlobalServices;
import com.ardhi.businessgame.services.TimeSync;

import android.app.TabActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.EditText;
import android.widget.TabHost;

public class HeadquarterTabActivity extends TabActivity {
	private DBAccess db;
	private EditText zone, money, nextTurn;
	private User user;
	private TimeSync timeSync;
	private Handler h;
	private Thread t;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.headquarter_tab);
		
		zone = (EditText)findViewById(R.id.zone);
        money = (EditText)findViewById(R.id.money);
        nextTurn = (EditText)findViewById(R.id.next_turn);
		db = new DBAccess(this);
		
		user = db.getUser();
		zone.setText(user.getZone());
        money.setText(user.getMoney()+" ZE");
        
        h = new Handler();
        timeSync = new TimeSync(h, nextTurn);
        Intent i = new Intent(getApplicationContext(), GlobalServices.class);
        bindService(i, serviceConnection, Context.BIND_AUTO_CREATE);
        
        TabHost.TabSpec spec;
		Intent intent;
		
		intent = new Intent(this, HeadquarterTabContentActivity.class);
		intent.putExtra("Tab", "Sector Unlocked");
		spec = getTabHost().newTabSpec("Sector Unlocked").setIndicator("Sector Unlocked", getResources().getDrawable(R.drawable.ic_launcher)).setContent(intent);
        getTabHost().addTab(spec);
        
        intent = new Intent(this, HeadquarterTabContentActivity.class);
		intent.putExtra("Tab", "Change Resident");
		spec = getTabHost().newTabSpec("Change Resident").setIndicator("Change Resident", getResources().getDrawable(R.drawable.ic_launcher)).setContent(intent);
        getTabHost().addTab(spec);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		timeSync.setThreadWork(false);
		t.interrupt();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		timeSync.setThreadWork(true);
		t = new Thread(timeSync);
		t.start();
		user = db.getUser();
		zone.setText(user.getZone());
        money.setText(user.getMoney()+" ZE");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(serviceConnection);
	}
	
	private ServiceConnection serviceConnection = new ServiceConnection() {
		public void onServiceDisconnected(ComponentName name) {
			timeSync.setGlobalServices(null);
			timeSync.setServiceBound(false);
		}
		
		public void onServiceConnected(ComponentName name, IBinder binder) {
			timeSync.setGlobalServices(((GlobalServices.MyBinder)binder).getService());
			timeSync.setServiceBound(true);
		}
	};
}
