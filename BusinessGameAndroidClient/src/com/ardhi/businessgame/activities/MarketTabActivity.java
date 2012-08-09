package com.ardhi.businessgame.activities;

import com.ardhi.businessgame.R;
import com.ardhi.businessgame.models.User;
import com.ardhi.businessgame.services.CustomHttpClient;
import com.ardhi.businessgame.services.DBAccess;
import com.ardhi.businessgame.services.GlobalServices;
import com.ardhi.businessgame.services.TimeSync;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;

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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.market_tab);
		
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
        
		progressDialog = ProgressDialog.show(this, "", "Accessing market in zone "+user.getZone()+"..");
		new LoadMarketContent().execute();
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
	}
	
	private class LoadMarketContent extends AsyncTask<String, Void, Object>{

		@Override
		protected Object doInBackground(String... params) {
			try {
				String res = CustomHttpClient.executeHttpGet(CustomHttpClient.URL+CustomHttpClient.GET_LOAD_MARKET_CONTENT+"&zone="+user.getZone());
				res = res.toString().replaceAll("\\n+", "");
				return res.toString();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
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