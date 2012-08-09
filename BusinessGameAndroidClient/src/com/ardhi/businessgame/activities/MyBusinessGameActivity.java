package com.ardhi.businessgame.activities;

import com.ardhi.businessgame.R;
import com.ardhi.businessgame.models.User;
import com.ardhi.businessgame.services.DBAccess;
import com.ardhi.businessgame.services.GlobalServices;
import com.ardhi.businessgame.services.TimeSync;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.EditText;

public class MyBusinessGameActivity extends Activity {
	private DBAccess db;
	private EditText zone, money, nextTurn;
	private User user;
	private TimeSync timeSync;
	private Handler h;
	private Thread t;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mybusiness);
        db = new DBAccess(this);
        zone = (EditText)findViewById(R.id.zone);
        money = (EditText)findViewById(R.id.money);
        nextTurn = (EditText)findViewById(R.id.next_turn);
        
        findViewById(R.id.btn_bank).setOnClickListener(onClickHandler);
        findViewById(R.id.btn_storage).setOnClickListener(onClickHandler);
        findViewById(R.id.btn_headquarter).setOnClickListener(onClickHandler);
        findViewById(R.id.btn_market).setOnClickListener(onClickHandler);
        
        user = db.getUser();
        zone.setText(user.getZone());
        money.setText(user.getMoney()+" ZE");
        
        h = new Handler();
        timeSync = new TimeSync(h, nextTurn);
        Intent i = new Intent(getApplicationContext(), GlobalServices.class);
        
        bindService(i, serviceConnection, Context.BIND_AUTO_CREATE);
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
	
	private View.OnClickListener onClickHandler = new View.OnClickListener(){
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_bank:
				Intent i = new Intent(MyBusinessGameActivity.this, BankLobbyActivity.class);
				startActivity(i);
				break;
				
			case R.id.btn_storage:
				startActivity(new Intent(MyBusinessGameActivity.this, StorageTabActivity.class));
				break;
				
			case R.id.btn_headquarter:
				startActivity(new Intent(MyBusinessGameActivity.this, HeadquarterTabActivity.class));
				break;
				
			case R.id.btn_market:
				startActivity(new Intent(MyBusinessGameActivity.this, MarketTabActivity.class));
				break;
				
			default:
				break;
			}
		}
    };
}