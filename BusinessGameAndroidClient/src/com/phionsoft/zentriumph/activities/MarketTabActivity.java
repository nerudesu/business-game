package com.phionsoft.zentriumph.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.phionsoft.zentriumph.R;
import com.phionsoft.zentriumph.models.User;
import com.phionsoft.zentriumph.services.CommunicationService;
import com.phionsoft.zentriumph.services.DBAccess;
import com.phionsoft.zentriumph.services.SystemService;
import com.phionsoft.zentriumph.services.TimeSync;

@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class MarketTabActivity extends TabActivity {
	private ProgressDialog progressDialog;
	private DBAccess db;
	private EditText zone, money, nextTurn;
	private User user;
	private TimeSync timeSync;
	private Handler h;
	private Thread t;
	private String data;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_market);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        	getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        zone = (EditText)findViewById(R.id.zone);
        money = (EditText)findViewById(R.id.money);
        nextTurn = (EditText)findViewById(R.id.next_turn);
		db = new DBAccess(this);
		
		user = db.getUser();
		zone.setText(user.getZone());
        money.setText(user.getMoney()+" ZE");
        
        h = new Handler();
        timeSync = new TimeSync(h, nextTurn, money, db);
        bindService(new Intent(this, SystemService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        
        if(CommunicationService.isOnline(this)){
        	progressDialog = ProgressDialog.show(this, "", "Accessing market in zone "+user.getZone()+"..");
    		new LoadMarketContent().execute();
        } else {
        	Toast.makeText(getApplicationContext(), "Device is offline..", Toast.LENGTH_SHORT).show();
        	finish();
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
		zone.setText(user.getZone());
        money.setText(user.getMoney()+" ZE");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(serviceConnection);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_tab_market, menu);
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
	
	private void setLayout(){		
		TabHost.TabSpec spec;
		Intent intent;
		
		JsonParser parser = new JsonParser();
		JsonArray array = parser.parse(data).getAsJsonArray();
		
		intent = new Intent(this, MarketTabContentActivity.class);
		intent.putExtra("Tab", "Product");
		intent.putExtra("Data", new Gson().fromJson(array.get(0), String.class));
		spec = getTabHost().newTabSpec("Product").setIndicator("Product", getResources().getDrawable(R.drawable.ic_launcher)).setContent(intent);
        getTabHost().addTab(spec);
        
        intent = new Intent(this, MarketTabContentActivity.class);
		intent.putExtra("Tab", "Equipment");
		intent.putExtra("Data", new Gson().fromJson(array.get(1), String.class));
		spec = getTabHost().newTabSpec("Equipment").setIndicator("Equipment", getResources().getDrawable(R.drawable.ic_launcher)).setContent(intent);
        getTabHost().addTab(spec);
        
        intent = new Intent(this, MarketTabContentActivity.class);
		intent.putExtra("Tab", "Employee");
		intent.putExtra("Data", new Gson().fromJson(array.get(2), String.class));
		spec = getTabHost().newTabSpec("Employee").setIndicator("Employee", getResources().getDrawable(R.drawable.ic_launcher)).setContent(intent);
        getTabHost().addTab(spec);
        
        intent = new Intent(this, MarketTabContentActivity.class);
		intent.putExtra("Tab", "Bundle");
        spec = getTabHost().newTabSpec("Bundle").setIndicator("Bundle", getResources().getDrawable(R.drawable.ic_launcher)).setContent(intent);
        getTabHost().addTab(spec);
        
        parser = null;
        array = null;
	}
	
	private class LoadMarketContent extends AsyncTask<String, Void, Object>{

		@Override
		protected Object doInBackground(String... params) {
			try {
				return CommunicationService.get(CommunicationService.GET_LOAD_MARKET_CONTENT+"&zone="+user.getZone());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(Object res) {
			progressDialog.dismiss();
			if(res == null){
				Toast.makeText(getApplicationContext(), "No response from server. Try again later.", Toast.LENGTH_SHORT).show();
				finish();
			} else if(res.equals("-1")){
				Toast.makeText(getApplicationContext(), "Server is not ready..", Toast.LENGTH_SHORT).show();
				finish();
			} else if(res.equals("0")){
				Toast.makeText(getApplicationContext(), "Internal Error..", Toast.LENGTH_SHORT).show();
				finish();
			} else {
				data = res.toString();
				setLayout();
			}
		}
	}
}
