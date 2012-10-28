package com.phionsoft.zentriumph.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.phionsoft.zentriumph.maps.IsoCam;
import com.phionsoft.zentriumph.maps.MyActionResolver;
import com.phionsoft.zentriumph.services.DBAccess;
import com.phionsoft.zentriumph.services.SystemService;
import com.phionsoft.zentriumph.services.TimeSync;

public class GDXSummoner extends AndroidApplication {
	private DBAccess db;
	private TimeSync timeSync;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGL20 = false;
        
        db = new DBAccess(this);
        timeSync = new TimeSync(db);
        
        bindService(new Intent(this, SystemService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        
        initialize(new IsoCam(new MyActionResolver(this), db, timeSync), cfg);
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
