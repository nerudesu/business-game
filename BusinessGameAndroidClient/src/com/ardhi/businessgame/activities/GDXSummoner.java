package com.ardhi.businessgame.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.ardhi.businessgame.maps.IsoCam;
import com.ardhi.businessgame.maps.MyActionResolver;
import com.ardhi.businessgame.models.User;
import com.ardhi.businessgame.services.DBAccess;
import com.ardhi.businessgame.services.SystemService;
import com.ardhi.businessgame.services.TimeSync;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class GDXSummoner extends AndroidApplication {
	private DBAccess db;
	private User user;
	private TimeSync timeSync;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGL20 = false;
        
        db = new DBAccess(this);
        user = db.getUser();
        timeSync = new TimeSync(db);
        
        bindService(new Intent(this, SystemService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        
        initialize(new IsoCam(new MyActionResolver(this), user, timeSync), cfg);
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
			timeSync.setGlobalServices(((SystemService.MyBinder)binder).getService());
			timeSync.setServiceBound(true);
		}
	};
}
