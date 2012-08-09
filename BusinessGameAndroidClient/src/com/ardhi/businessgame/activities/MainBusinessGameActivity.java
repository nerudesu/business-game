package com.ardhi.businessgame.activities;

import com.ardhi.businessgame.R;
import com.ardhi.businessgame.models.User;
import com.ardhi.businessgame.services.DBAccess;
import com.ardhi.businessgame.services.GlobalServices;
import com.ardhi.businessgame.services.TimeSync;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainBusinessGameActivity extends Activity {
	private DBAccess db;
	private EditText zone, money, nextTurn;
	private User user;
	private Handler h;
	private Thread t;
	private TimeSync timeSync;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        ListView lv = (ListView)findViewById(R.id.main_options);
        lv.setTextFilterEnabled(true);
        lv.setOnItemClickListener(onItemClickHandler);
        
        db = new DBAccess(this);
        zone = (EditText)findViewById(R.id.zone);
        money = (EditText)findViewById(R.id.money);
        nextTurn = (EditText)findViewById(R.id.next_turn);
        
        user = db.getUser();
        if(user == null){
        	doPositiveClickDialog();
        } else {
        	zone.setText(user.getZone());
            money.setText(user.getMoney()+" ZE");
            Intent i = new Intent(getApplicationContext(), GlobalServices.class);
            startService(i);
            bindService(i, serviceConnection, Context.BIND_AUTO_CREATE);
            h = new Handler();
            timeSync = new TimeSync(h, nextTurn);
        }
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
		if(user == null){
        	doPositiveClickDialog();
		} else {
			zone.setText(user.getZone());
	        money.setText(user.getMoney()+" ZE");
		}
	}
	
    @Override
	public Dialog onCreateDialog(int id){
		switch (id) {
		case 1:
			LayoutInflater factory = LayoutInflater.from(this);
			final View textView = factory.inflate(R.layout.question_logout, null);
			return new AlertDialog.Builder(this)
				.setView(textView)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						doPositiveClickDialog();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						
					}
				})
				.create();

		default:
			break;
		}
		return null;
	}
    
    @Override
    public void onBackPressed() {
    	showDialog(1);
    }
    
    private void doPositiveClickDialog(){
    	stopService(new Intent(getApplicationContext(), GlobalServices.class));
    	unbindService(serviceConnection);
    	Intent i = new Intent(getApplicationContext(), LoginActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(i);
    }
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	db.deleteUserData();
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
	
	private AdapterView.OnItemClickListener onItemClickHandler = new AdapterView.OnItemClickListener() {
    	public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
			Toast.makeText(MainBusinessGameActivity.this, parent.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();
			
			switch (pos) {
			case 0:
				Intent i = new Intent(MainBusinessGameActivity.this, MyBusinessGameActivity.class);
				startActivity(i);
				break;

			default:
				break;
			}
		}
	};    
}