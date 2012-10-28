package com.phionsoft.zentriumph.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.phionsoft.zentriumph.R;
import com.phionsoft.zentriumph.models.User;
import com.phionsoft.zentriumph.services.DBAccess;
import com.phionsoft.zentriumph.services.SystemService;
import com.phionsoft.zentriumph.services.TimeSync;

@SuppressLint("NewApi")
public class MainBusinessGameActivity extends Activity {
	private ListView lv;
	private DBAccess db;
	private EditText zone, money, nextTurn;
	private User user;
	private Handler h;
	private Thread t;
	private TimeSync timeSync;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        lv = (ListView)findViewById(R.id.main_options);
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
            startService(new Intent(this, SystemService.class));
            bindService(new Intent(this, SystemService.class), serviceConnection, Context.BIND_AUTO_CREATE);
            h = new Handler();
            timeSync = new TimeSync(h, nextTurn, money, db);
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
    public void onDestroy(){
    	super.onDestroy();
    	db.deleteUserData();
    }
    
	@Override
    public void onBackPressed() {
    	dialog().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public AlertDialog dialog(){
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
    }

    private void doPositiveClickDialog(){
    	stopService(new Intent(getApplicationContext(), SystemService.class));
    	unbindService(serviceConnection);
    	Intent i = new Intent(getApplicationContext(), LoginActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(i);
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
	
	private AdapterView.OnItemClickListener onItemClickHandler = new AdapterView.OnItemClickListener() {
    	public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
			switch (pos) {
			case 0:
				startActivity(new Intent(MainBusinessGameActivity.this, GDXSummoner.class));
				break;
				
			case 1:
				startActivity(new Intent(MainBusinessGameActivity.this, MyProfileTabActivity.class));
				break;
				
			case 2:
				startActivity(new Intent(MainBusinessGameActivity.this, HelpActivity.class));
				break;
				
			case 3:
				dialog().show();
				break;
				
			default:
				break;
			}
		}
	};
}
